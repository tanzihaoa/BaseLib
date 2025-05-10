package com.tzh.myapplication.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import com.ven.assists.AssistsCore


object SkUtil {
    /**
     * 判断无障碍服务是否开启
     * @param mContext
     * @return
     */
    @JvmStatic
    fun isAccessibilitySettingsOn(mContext: Context): Boolean {
        return AssistsCore.isAccessibilityServiceEnabled()
    }

    /**
     * 去开启无障碍模式
     */
    fun start(mContext: Context){
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        mContext.startActivity(intent)
    }
}