package com.tzh.myapplication.utils.mask

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.*
import android.util.Log
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.math.min

class VideoProcessor(private val context: Context) {
    private lateinit var decoder: MediaCodec
    private lateinit var extractor: MediaExtractor
    private lateinit var encoder: MediaCodec
    private lateinit var muxer: MediaMuxer
    private var videoTrackIndex = -1
    private var muxerStarted = false
    private var frameCount = 0

    // 初始化视频处理
    fun initProcessing(inputPath: String, outputPath: String) {
        extractor = MediaExtractor()
        extractor.setDataSource(inputPath)

        // 查找视频轨道
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/") == true) {
                videoTrackIndex = i
                extractor.selectTrack(i)
                initDecoder(format)
                initEncoder(format, outputPath)
                break
            }
        }
    }

    // 初始化解码器
    private fun initDecoder(format: MediaFormat) {
        val mime = format.getString(MediaFormat.KEY_MIME) ?: "video/avc"
        decoder = MediaCodec.createDecoderByType(mime)

        // 配置解码器
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        decoder.configure(format, null, null, 0)
        decoder.start()
    }

    // 初始化编码器
    private fun initEncoder(inputFormat: MediaFormat, outputPath: String) {
        val mime = inputFormat.getString(MediaFormat.KEY_MIME) ?: "video/avc"
        encoder = MediaCodec.createEncoderByType(mime)

        // 配置编码参数
        val outputFormat = MediaFormat.createVideoFormat(
            mime,
            inputFormat.getInteger(MediaFormat.KEY_WIDTH),
            inputFormat.getInteger(MediaFormat.KEY_HEIGHT)
        )
        outputFormat.setInteger(MediaFormat.KEY_BIT_RATE, 2000000)
        outputFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        outputFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        outputFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)

        encoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        encoder.start()

        // 初始化复用器
        muxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    // 处理视频帧
    fun processFrames(maskRegions: List<MaskRegion>) {
        val inputBuffers = decoder.inputBuffers
        val outputBuffers = decoder.outputBuffers
        val bufferInfo = MediaCodec.BufferInfo()
        var isEos = false

        // 开始处理循环
        while (!isEos) {
            // 将数据输入解码器
            if (!isEos) {
                val inputBufferIndex = decoder.dequeueInputBuffer(10000)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = inputBuffers[inputBufferIndex]
                    val sampleSize = extractor.readSampleData(inputBuffer, 0)

                    if (sampleSize < 0) {
                        // 结束流
                        decoder.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            0,
                            0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        isEos = true
                    } else {
                        decoder.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            sampleSize,
                            extractor.sampleTime,
                            0
                        )
                        extractor.advance()
                    }
                }
            }

            // 从解码器获取输出
            val outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 10000)
            when {
                outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    // 没有输出可用
                }
                outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                    // 输出缓冲区已更改
                }
                outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    // 输出格式已更改
                }
                outputBufferIndex >= 0 -> {
                    // 获取解码后的帧
                    val frame = getFrameFromBuffer(decoder, outputBufferIndex)

                    // 应用遮罩效果
                    var processedFrame = frame
                    maskRegions.forEach { region ->
                        processedFrame = when (region.type) {
                            MaskType.MOSAIC -> MaskEffect().applyMosaic(
                                processedFrame,
                                region.getCurrentRect(frameCount)
                            )
                            MaskType.BLUR -> MaskEffect().applyBlur(
                                processedFrame,
                                region.getCurrentRect(frameCount)
                            )
                            MaskType.PIXELATE -> MaskEffect().applyPixelate(
                                processedFrame,
                                region.getCurrentRect(frameCount)
                            )
                        }
                    }

                    // 编码处理后的帧
                    encodeFrame(processedFrame, bufferInfo.presentationTimeUs)

                    // 释放解码器缓冲区
                    decoder.releaseOutputBuffer(outputBufferIndex, false)

                    frameCount++
                }
            }

            // 检查是否结束
            if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                break
            }
        }

        // 完成处理
        releaseResources()
    }

    // 从解码器缓冲区获取帧数据
    private fun getFrameFromBuffer(decoder: MediaCodec, bufferIndex: Int): Bitmap {
        val image = decoder.getOutputImage(bufferIndex)
        return if (image != null) {
            convertImageToBitmap(image)
        } else {
            // 备用方法：使用ByteBuffer
            val outputBuffer = decoder.getOutputBuffer(bufferIndex)
            convertBufferToBitmap(outputBuffer)
        }
    }

    // 将Image转换为Bitmap
    private fun convertImageToBitmap(image: Image): Bitmap {
        val width = image.width
        val height = image.height
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width

        // 创建Bitmap
        val bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()

        return bitmap
    }

    // 将ByteBuffer转换为Bitmap
    private fun convertBufferToBitmap(buffer: ByteBuffer?): Bitmap {
        buffer?.rewind()
        val data = ByteArray(buffer?.remaining() ?: 0)
        buffer?.get(data)

        return try {
            BitmapFactory.decodeByteArray(data, 0, data.size)
        } catch (e: Exception) {
            // 创建默认位图作为后备
            Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888)
        }
    }

    // 编码处理后的帧
    private fun encodeFrame(bitmap: Bitmap, presentationTimeUs: Long) {
        // 获取编码器输入缓冲区
        val inputBufferIndex = encoder.dequeueInputBuffer(10000)
        if (inputBufferIndex >= 0) {
            val inputBuffer = encoder.getInputBuffer(inputBufferIndex)

            // 将Bitmap转换为字节数组
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            val byteArray = stream.toByteArray()

            // 将数据复制到输入缓冲区
            inputBuffer?.put(byteArray)

            // 提交给编码器
            encoder.queueInputBuffer(
                inputBufferIndex,
                0,
                byteArray.size,
                presentationTimeUs,
                0
            )
        }

        // 处理编码器输出
        val bufferInfo = MediaCodec.BufferInfo()
        var outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, 10000)
        while (outputBufferIndex >= 0) {
            if (!muxerStarted) {
                // 等待格式更改
                if (encoder.outputFormat != null) {
                    videoTrackIndex = muxer.addTrack(encoder.outputFormat)
                    muxer.start()
                    muxerStarted = true
                }
            }

            if (muxerStarted) {
                val outputBuffer = encoder.getOutputBuffer(outputBufferIndex)
                muxer.writeSampleData(videoTrackIndex, outputBuffer!!, bufferInfo)
            }

            encoder.releaseOutputBuffer(outputBufferIndex, false)
            outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, 0)
        }
    }

    // 释放资源
    private fun releaseResources() {
        try {
            decoder.stop()
            decoder.release()
            encoder.stop()
            encoder.release()
            extractor.release()
            if (muxerStarted) {
                muxer.stop()
            }
            muxer.release()
        } catch (e: Exception) {
            Log.e("VideoProcessor", "Error releasing resources", e)
        }
    }
}

// 遮罩区域定义
data class MaskRegion(
    val type: MaskType,
    val startRect: Rect,
    val endRect: Rect? = null, // 用于动画
    val duration: Int = 0 // 动画持续时间（帧数）
) {
    // 获取当前帧的区域
    fun getCurrentRect(frameIndex: Int): Rect {
        if (endRect == null || duration == 0) return startRect

        val progress = min(1f, frameIndex.toFloat() / duration)
        return Rect(
            lerp(startRect.left, endRect.left, progress).toInt(),
            lerp(startRect.top, endRect.top, progress).toInt(),
            lerp(startRect.right, endRect.right, progress).toInt(),
            lerp(startRect.bottom, endRect.bottom, progress).toInt()
        )
    }

    private fun lerp(start: Int, end: Int, progress: Float): Float {
        return start + (end - start) * progress
    }
}

enum class MaskType { MOSAIC, BLUR, PIXELATE }