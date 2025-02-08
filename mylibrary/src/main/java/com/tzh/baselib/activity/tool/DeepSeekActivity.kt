package com.tzh.baselib.activity.tool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.gyf.immersionbar.ImmersionBar
import com.tzh.baselib.R
import com.tzh.baselib.base.XBaseBindingActivity
import com.tzh.baselib.databinding.ActivityDeepSeekBinding
import com.tzh.baselib.databinding.ActivityTranslateBinding
import com.tzh.baselib.dialog.SelectTranslateDialog
import com.tzh.baselib.dto.LanguageDto
import com.tzh.baselib.network.LibNetWorkApi
import com.tzh.baselib.util.GsonUtil
import com.tzh.baselib.util.LogUtils
import com.tzh.baselib.util.toDefault

/**
 * DeepSeek页面
 */
class DeepSeekActivity : XBaseBindingActivity<ActivityDeepSeekBinding>(R.layout.activity_deep_seek) {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, DeepSeekActivity::class.java))
        }
    }

    override fun initView() {
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).init()
        binding.activity = this

    }

    override fun initData() {

    }

    override fun onCloseActivity() {

    }

    /**
     * 翻译
     */
    @SuppressLint("AutoDispose")
    fun translate(){
        val text = binding.etText1.text.toString()
        if(text.isEmpty()){
            Toast.makeText(this,"请输入要问的内容",Toast.LENGTH_LONG).show()
        }else{
            LibNetWorkApi.sendRequest(this,text).subscribe({
                LogUtils.e("返回=====",GsonUtil.GsonString(it.choices))
            },{
                LogUtils.e("错误=====",GsonUtil.GsonString(it))
            })
        }
    }
}