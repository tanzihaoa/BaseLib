package com.tzh.myapplication.ui.activity.tool

import android.content.Context
import android.content.Intent
import com.tzh.baselib.util.GsonUtil
import com.tzh.baselib.util.audio.GetAudioOrVideoUtil
import com.tzh.baselib.util.gradDivider
import com.tzh.baselib.util.grid
import com.tzh.baselib.util.initAdapter
import com.tzh.baselib.util.toDefault
import com.tzh.myapplication.R
import com.tzh.myapplication.base.AppBaseActivity
import com.tzh.myapplication.databinding.ActivityAudioConvertBinding
import com.tzh.myapplication.ui.activity.tool.adapter.ConvertFormatAdapter
import com.tzh.myapplication.ui.activity.tool.dto.ConvertFormatDto
import com.tzh.myapplication.ui.activity.tool.util.ConvertDateUtil
import com.tzh.myapplication.utils.ToastUtil
import com.tzh.myapplication.utils.ffmpeg.ConvertListener
import com.tzh.myapplication.utils.ffmpeg.FFmpegConvertUtil
import com.tzh.myapplication.utils.ffmpeg.getSaveName

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

    val mType by lazy {
        intent.getStringExtra("type")
    }

    var mDto : ConvertFormatDto?= null
    val mAdapter by lazy {
        ConvertFormatAdapter(object : ConvertFormatAdapter.ConvertListener{
            override fun ok(data: ConvertFormatDto) {
                mDto = data
            }
        })
    }

    override fun initView() {
        binding.activity = this
        binding.tvFileName.text = file[0].name
        binding.recycleView.grid(4).initAdapter(mAdapter).gradDivider(10f,4)

        if(mType == "audio"){
            mAdapter.setDatas(ConvertDateUtil.getAudioConvertList())
        }else{
            mAdapter.setDatas(ConvertDateUtil.getVideoConvertList())
        }
    }

    override fun initData() {

    }

    fun startConvert(){
        if(mDto!=null){
            FFmpegConvertUtil.convert(file[0].path,file[0].getSaveName(mDto?.type.toDefault("mp3")),mDto?.type.toDefault("mp3"),object : ConvertListener{
                override fun ok(filePath : String) {

                }

                override fun error() {

                }
            })
        }else{
            ToastUtil.show("请选择转换类型")
        }
    }
}