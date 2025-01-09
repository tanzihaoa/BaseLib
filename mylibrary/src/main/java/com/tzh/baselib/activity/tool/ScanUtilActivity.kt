package com.tzh.baselib.activity.tool

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.zxing.integration.android.IntentIntegrator
import com.tzh.baselib.R
import com.tzh.baselib.base.XBaseBindingActivity
import com.tzh.baselib.databinding.ActivityScanUtilBinding
import com.tzh.baselib.livedata.ActivityCloseLiveData
import com.tzh.baselib.livedata.observeForeverNoBack

class ScanUtilActivity : XBaseBindingActivity<ActivityScanUtilBinding>(R.layout.activity_scan_util) {
    companion object {
        @JvmStatic
        fun start(context: Context,listener : ScanListener) {
            mListener = listener
            context.startActivity(Intent(context, ScanUtilActivity::class.java))
        }

        var mListener : ScanListener?= null
    }

    override fun initView() {
        ScanActivity.start(this)
        ActivityCloseLiveData.instance.observeForeverNoBack {

        }
    }

    override fun initData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Log.e("=====","扫码取消")
                mListener?.cancel()
            } else {
                mListener?.sure(result.contents)
                Log.e("=====",result.contents)
            }
            finish()
        } else {
            super.onActivityResult(requestCode, resultCode,data)
        }
    }

    override fun onCloseActivity() {

    }

    interface ScanListener{
        fun sure(text : String)

        fun cancel()
    }
}