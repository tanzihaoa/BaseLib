package com.tzh.myapplication.utils.ffmpeg

import android.content.Context
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Tensor
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class VoiceSeparator(context: Context) {
    private var interpreter: Interpreter? = null

    init {
        val modelFile = loadModelFile(context, "fssd_25_8bit_gray_v2.tflite")
        val options = Interpreter.Options().apply { setNumThreads(2) }
        interpreter = Interpreter(modelFile, options)
    }

    fun separate(inputAudio: ShortArray): Pair<FloatArray, FloatArray> {
        // 1. 预处理音频为模型输入格式
        val inputBuffer = preprocessAudio(inputAudio)

        // 2. 获取输入张量信息
        val inputTensor = interpreter!!.getInputTensor(0)
        val inputShape = inputTensor.shape()
        val expectedSize = inputShape.reduce { acc, dim -> acc * dim }

        // 3. 验证输入数据大小
        if (inputBuffer.size != expectedSize) {
            throw IllegalArgumentException("输入数据大小 ${inputBuffer.size} 与模型预期 $expectedSize 不匹配")
        }

        // 4. 根据数据类型填充缓冲区
        val inputs: Array<Any> = when (inputTensor.dataType()) {
            DataType.UINT8 -> {
                val byteBuffer = ByteBuffer.allocateDirect(expectedSize)
                inputBuffer.forEach { value ->
                    val scaledValue = (value * 255).toInt().coerceIn(0, 255)
                    byteBuffer.put(scaledValue.toByte())
                }
                byteBuffer.rewind()
                arrayOf(byteBuffer)
            }
            DataType.FLOAT32 -> {
                val floatBuffer = FloatBuffer.allocate(expectedSize)
                floatBuffer.put(inputBuffer)
                floatBuffer.rewind()
                arrayOf(floatBuffer)
            }
            else -> throw IllegalStateException("不支持的输入类型")
        }

        // 5. 运行推理
        val outputMap = HashMap<Int, Any>()
        val v1 = Array(1) { FloatArray(4096) } // 人声输出
        val v2 = Array(1) { FloatArray(4096) } // 伴奏输出
        outputMap[0] = Array(1) { FloatArray(4096) } // 人声输出
        outputMap[1] = Array(1) { FloatArray(4096) } // 伴奏输出
        interpreter!!.runForMultipleInputsOutputs(inputs, outputMap)

        return Pair(v1[0], v2[0])
    }

    private fun preprocessAudio(audio: ShortArray): FloatArray {
        // 实现音频到频谱图的转换（需根据模型调整）
        return FloatArray(160 * 160 * 4) // 占位符
    }

    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelName)
        return FileInputStream(assetFileDescriptor.fileDescriptor).use { inputStream ->
            val channel = inputStream.channel
            channel.map(
                FileChannel.MapMode.READ_ONLY,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.declaredLength
            )
        }
    }
}
