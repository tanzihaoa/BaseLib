package com.tzh.myapplication.livedata

import androidx.lifecycle.MutableLiveData
import com.tzh.myapplication.service.auto.AutoDataDto
import com.tzh.myapplication.ui.dto.SmsDto

/**
 * 登陆状态处理
 */
class LoginStateLiveData : MutableLiveData<Boolean>() {
    companion object {
        val instance by lazy { LoginStateLiveData() }
    }
}

/**
 * 收到的短信监听
 */
class SmsLiveData : MutableLiveData<SmsDto>() {
    companion object {
        val instance by lazy { SmsLiveData() }
    }
}

/**
 * 收到自动记账监听
 */
class AutoDataLiveData : MutableLiveData<AutoDataDto>() {
    companion object {
        val instance by lazy { AutoDataLiveData() }
    }
}
