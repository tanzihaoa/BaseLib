package com.tzh.myapplication.ui.activity.tool

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import com.tzh.baselib.util.GsonUtil
import com.tzh.baselib.util.audio.GetAudioOrVideoUtil
import com.tzh.baselib.util.toDefault
import com.tzh.baselib.util.voice.PathUtil
import com.tzh.myapplication.R
import com.tzh.myapplication.base.AppBaseActivity
import com.tzh.myapplication.databinding.ActivityAudioConvertBinding
import com.tzh.myapplication.utils.ToastUtil
import com.tzh.myapplication.utils.mask.MaskRegion
import com.tzh.myapplication.utils.mask.MaskType
import com.tzh.myapplication.utils.mask.VideoProcessor

/**
 * 音视频转换页面
 */
class AudioConvertActivity : AppBaseActivity<ActivityAudioConvertBinding>(R.layout.activity_audio_convert){

    companion object{
        /**
         * @param file 需要转换的数据
         * @param type 类型 audiO音频 video视频
         */
        fun start(context: Context,file : MutableList<GetAudioOrVideoUtil.VideoFile>,type : String){
            context.startActivity(Intent(context,AudioConvertActivity::class.java).apply {
                putExtra("file",GsonUtil.GsonString(file))
                putExtra("type",type)
            })
        }
    }

    val file by lazy {
        GsonUtil.GsonToList(intent.getStringExtra("file").toDefault("[]"),GetAudioOrVideoUtil.VideoFile::class.java)
    }

    val videoProcessor by lazy {
        VideoProcessor(this)
    }

    override fun initView() {
        binding.activity = this
        // 初始化视频处理器
        val fileName = "合成视频_" + System.currentTimeMillis() + ".mp4"

        // 设置输入输出路径
        val outputPath = PathUtil.getVideo(this).absolutePath + "/${fileName}"
        videoProcessor.initProcessing(file[0].path, outputPath)

    }

    override fun initData() {

    }

    fun startConvert(){
        // 定义遮罩区域
        val maskRegions = listOf(
            MaskRegion(
                MaskType.MOSAIC,
                Rect(40,40, 50, 50) // 马赛克区域
            ),
//            MaskRegion(
//                MaskType.PIXELATE,
//                Rect(50, 50, 200, 200) // 模糊区域
//            )
        )

        // 开始处理
        Thread {
            videoProcessor.processFrames(maskRegions)
            runOnUiThread {
                // 处理完成，更新UI
                ToastUtil.show("视频处理完成")
            }
        }.start()
    }

}