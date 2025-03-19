package com.tzh.myapplication.ui.activity.main

import android.content.Context
import android.content.Intent
import com.tzh.myapplication.R
import com.tzh.myapplication.base.AppBaseActivity
import com.tzh.myapplication.databinding.ActivitySelectFileBinding


/**
 * 选择文件 页面
 */
class SelectFileActivity : AppBaseActivity<ActivitySelectFileBinding>(R.layout.activity_select_file) {

    companion object{
        fun start(context: Context){
            context.startActivity(Intent(context,SelectFileActivity::class.java))
        }
    }

    override fun initView() {


    }

    override fun initData() {

    }
}