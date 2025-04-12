package com.tzh.myapplication.utils.ffmpeg

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.tzh.baselib.util.LogUtils
import com.tzh.baselib.util.audio.GetAudioOrVideoUtil
import com.tzh.baselib.util.getFileName
import com.tzh.baselib.util.voice.PathUtil
import com.tzh.myapplication.base.MyApplication
import java.io.File


/**
 * 音视频格式转换工具类
 */
object FFmpegConvertUtil {

    fun convert(inputPath : String,outputPath : String,type : String,listener: ConvertListener){
        LogUtils.e("inputPath===", inputPath)
        LogUtils.e("outputPath===", outputPath)
        //判断是否存在，存在则删除
        outputPath.delete()
        when(type){
            "mp3"->{
                toMP3(inputPath,outputPath,listener)
            }
            "wav"->{
                toWAV(inputPath,outputPath,listener)
            }
            "m4a"->{
                toM4a(inputPath,outputPath,listener)
            }
            "amr"->{
                toAmr(inputPath,outputPath,listener)
            }
            "aac"->{
                toAac(inputPath,outputPath,listener)
            }
            "flac"->{
                toFlac(inputPath,outputPath,listener)
            }
            "ac3"->{
                toAc3(inputPath,outputPath,listener)
            }
            "aiff"->{
                toAiff(inputPath,outputPath,listener)
            }
            "ogg"->{
                toOgg(inputPath,outputPath,listener)
            }
            "m4r"->{
                toM4r(inputPath,outputPath,listener)
            }
            "mp2"->{
                toMp2(inputPath,outputPath,listener)
            }
            "wma"->{
                toWma(inputPath,outputPath,listener)
            }
            "mp4"->{
                toMp4(inputPath,outputPath,listener)
            }
            "avi"->{
                toAVI(inputPath,outputPath,listener)
            }
            "mkv"->{
                toMKV(inputPath,outputPath,listener)
            }
            "wmv"->{
                toWMV(inputPath,outputPath,listener)
            }
            "mpg"->{
                toMPG(inputPath,outputPath,listener)
            }
            "mov"->{
                toMOV(inputPath,outputPath,listener)
            }
            "rm"->{
                toRMVB(inputPath,outputPath,listener)
            }
            "f4v"->{
                toF4V(inputPath,outputPath,listener)
            }
            "flv"->{
                toFLV(inputPath,outputPath,listener)
            }
            "mpeg"->{
                toMPEG(inputPath,outputPath,listener)
            }
            "webm"->{
                toWebM(inputPath,outputPath,listener)
            }
        }
    }

    fun toMP3(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-vn", "-acodec", "libmp3lame",
            "-q:a", "2",
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toWAV(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-i", inputPath,
            "-acodec", "pcm_s16le",
            "-ar", "44100",
            "-ac", "2",
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toM4a(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-i", inputPath,
            "-c:a", "aac",
            "-b:a", "192k",
            "-movflags", "+faststart",
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toAmr(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-i", inputPath,
            "-ar", "8000",
            "-ac", "1",
            "-ab", "12.2k",
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toAac(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-i", inputPath,
            "-vn", // 忽略视频流
            "-c:a", "aac", // 使用通用 AAC 编码器
            "-b:a", "192k", // 明确指定比特率
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toFlac(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-i", inputPath,
            "-c:a", "flac",
            "-compression_level", "8",
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toAc3(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-i", inputPath,
            "-c:a", "ac3",
            "-b:a", "640k",
            "-ar", "48000",
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toAiff(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-i", inputPath,
            "-c:a", "pcm_s16be",
            "-b:a", "640k",
            "-ar", "48000",
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toOgg(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-i", inputPath,
            "-c:a", "libvorbis",
            "-q:a", "6",//质量调节：-q:a 6（0-10，10最佳）
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toM4r(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-i", inputPath,
            "-c:a", "aac",
            "-b:a", "128k",
            "-f", "ipod",//-f ipod：兼容iOS格式
            "-ss", "0",
            "-t", "40",//-t 40：限制40秒（苹果铃声要求）
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toMp2(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:a", "mp2",
            "-b:a", "192k",
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toWma(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:a", "wmav2",
            "-b:a", "192k",
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toMp4(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:v", "libx264",//-c:v libx264：H.264 视频编码
            "-crf", "23",//-crf 23：质量范围（0-51，值越小质量越高）
            "-preset", "medium",//-preset medium：编码速度与压缩率的平衡
            "-c:a", "aac",//AAC 音频编码
            "-b:a", "128k",//音频比特率
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {
                // CANCEL
                LogUtils.e("FFmpeg===", "转换取消！")
            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toAVI(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:v", "mpeg4",//-c:v libx264：兼容 Xvid 编码
            "-vtag", "xvid",//-vtag xvid：强制标记为 Xvid
            "-qscale:v", "3",//-qscale:v 3：视频质量（1-31，值越小质量越高）
            "-c:a", "libmp3lame",//
            "-qscale:a", "2",//
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {
                // CANCEL
                LogUtils.e("FFmpeg===", "转换取消！")
            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toMKV(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:v", "libx264",  // 视频编码器（H.264）
            "-crf", "23",       // 质量参数（18-28，值越小质量越高）
            "-preset", "fast",  // 编码速度/压缩率平衡
            "-c:a", "aac",      // 音频编码器
            "-b:a", "192k",     // 音频比特率
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {
                // CANCEL
                LogUtils.e("FFmpeg===", "转换取消！")
            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toWMV(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:v", "wmv2",//
            "-b:v", "5000k",//
            "-c:a", "wmav2",//
            "-b:a", "192k",//
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toMPG(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:v", "mpeg1video",//
            "-b:v", "1500k",//
            "-c:a", "mp2",//
            "-b:a", "192k",//
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toMOV(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:v", "libx264",//
            "-profile:v", "high",//
            "-pix_fmt", "yuv420p",//
            "-movflags", "+faststart",//
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toRMVB(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:v", "rv20",       // RealVideo 1.0 编码器
            "-c:a", "libmp3lame", // RM 通常使用 MP3 音频
            "-f", "rm",          // 强制输出为 RealMedia 格式
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toF4V(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:v", "libx264",
            "-profile:v", "main",
            "-preset", "fast", // 平衡速度与质量
            "-c:a", "aac",
            "-b:a", "192k", // 明确指定音频比特率
            "-movflags", "+faststart", // 流媒体优化
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toFLV(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:v", "libx264",//
            "-profile:v", "main",//
            "-c:a", "aac",//
            "-strict", "experimental",//
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toMPEG(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:v", "mpeg2video",//
            "-b:v", "4000k",//
            "-c:a", "mp2",//
            "-b:a", "256k",//
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }

    fun toWebM(inputPath : String,outputPath : String,listener: ConvertListener){
        val cmd = arrayOf<String>(
            "-y",
            "-i", inputPath,
            "-c:v", "libvpx-vp9",//
            "-crf", "30",//质量控制（0-63）
            "-b:v", "0",//启用 CRF 模式
            "-c:a", "libopus",//
            "-b:a", "128k",//
            outputPath
        )

        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session  ->
            if (ReturnCode.isSuccess(session.returnCode)) {
                // SUCCESS
                LogUtils.e("FFmpeg===", "转换完成！")
                listener.ok(outputPath)
            } else if (ReturnCode.isCancel(session.returnCode)) {

                // CANCEL

            } else {
                listener.error()
                // FAILURE
                LogUtils.d("转换错误====", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            }
        }
    }
}

interface ConvertListener{
    fun ok(filePath : String)

    fun error()
}

/**
 * 获取输出地址
 */
fun GetAudioOrVideoUtil.VideoFile.getSaveName(type : String) : String{
    return PathUtil.getAudio(MyApplication.mContext).absolutePath + "/" + this.name.getFileName() + "_转换." + type
}

/**
 * 判断文件是否存在  存在则删除
 */
fun String.delete(){
    val outputFile = File(this)
    if (outputFile.exists()) {
        outputFile.delete()
    }
}