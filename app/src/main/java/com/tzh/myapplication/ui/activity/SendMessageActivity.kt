package com.tzh.myapplication.ui.activity

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.tzh.myapplication.R
import com.tzh.myapplication.base.AppBaseActivity
import com.tzh.myapplication.databinding.ActivitySendMessageBinding
import com.tzh.myapplication.livedata.SmsLiveData
import com.tzh.baselib.livedata.observeNoBack
import com.tzh.myapplication.network.NetWorkApi
import com.tzh.myapplication.service.MessageService
import com.tzh.myapplication.ui.adapter.SmsListAdapter
import com.tzh.myapplication.ui.dialog.AddMobileDialog
import com.tzh.myapplication.ui.dto.SmsDto
import com.tzh.myapplication.utils.AndroidUtil
import com.tzh.myapplication.utils.ConfigUtil
import com.tzh.myapplication.utils.DateTime
import com.tzh.myapplication.utils.ObservableUtil
import com.tzh.myapplication.utils.OnPermissionCallBackListener
import com.tzh.myapplication.utils.PermissionXUtil
import com.tzh.myapplication.utils.ToastUtil
import com.tzh.baselib.util.LogUtils
import com.tzh.baselib.util.divideMessageArray
import com.tzh.baselib.util.initAdapter
import com.tzh.baselib.util.linear
import com.tzh.baselib.util.toDefault
import com.tzh.baselib.util.verDivider


class SendMessageActivity : AppBaseActivity<ActivitySendMessageBinding>(R.layout.activity_send_message) {
    companion object {
        @JvmStatic
        fun start(context: AppCompatActivity) {
            if(!ConfigUtil.isMobile()){
                AddMobileDialog(context,object : AddMobileDialog.AddMobileListener{
                    override fun mobile(mobile: String) {
                        ConfigUtil.setMobile(mobile)
                        start(context)
                    }
                }).show()
            }else{
                PermissionXUtil.requestAnyPermission(context, mutableListOf<String>().apply {
                    add(Manifest.permission.SEND_SMS)
                    add(Manifest.permission.CALL_PHONE)
                    add(Manifest.permission.READ_SMS)
                },object : OnPermissionCallBackListener{
                    override fun onAgree() {
                        context.startActivity(Intent(context, SendMessageActivity::class.java))
                    }

                    override fun onDisAgree() {

                    }
                })
            }
        }
    }

    val mAdapter by lazy {
        SmsListAdapter()
    }

    val mIntent by lazy {
        Intent(this, MessageService::class.java)
    }

    var SENT = "SMS_SENT"

    override fun initView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.titleBar.setRightTitleTxt(AndroidUtil.getVersionName(this))
        binding.activity = this
        binding.recyclerView.linear().initAdapter(mAdapter).verDivider(10f)

        /**
         * 收到的短信
         */
        SmsLiveData.instance.observeNoBack(this) {
            it?.apply {
                mAdapter.addData20(this)
                upSms(this)
            }
        }

        //注册短信发送监听
        registerReceiver(smsSentReceiver, IntentFilter(SENT))
        //开启短信读取监听
        startService(mIntent)
        getTask()
    }

    override fun initData() {

    }

    /**
     * 当前发送的短信
     */
    var mDto : SmsDto ?= null

    /**
     * 获取短信任务
     * 15988301228
     */
    fun getTask(){
        NetWorkApi.httpSmsTaskGet(this,ConfigUtil.getMobile()).subscribe({
            mDto = it.getDataDto()
            it.getDataDto().status = "待发送"
            it.getDataDto().time = DateTime.getNowTime()
            mAdapter.addData20(it.getDataDto())
            send()
        },{
            ToastUtil.show(it.message)
            ObservableUtil.startTimer(10*1000,SENT){
                ObservableUtil.stopTimer(SENT)
                getTask()
            }
        })
    }

    fun send(){
        if(mDto == null){
            ToastUtil.show("当前没有发送短信")
        }else{
            val sentIntent = Intent(SENT)
            val sentPendingIntent = PendingIntent.getBroadcast(this@SendMessageActivity, 0, sentIntent, PendingIntent.FLAG_IMMUTABLE)
            binding.root.post {
                val manager = SmsManager.getDefault()
                val list = mDto?.content.toDefault("").divideMessageArray()
//                if(list.size > 1){
//
//                }else{
//                    manager.sendTextMessage(mDto?.mobile.toDefault(""), null,mDto?.content.toDefault(""),sentPendingIntent, null)
//                }
                manager.sendMultipartTextMessage(mDto?.mobile.toDefault(""), null,list, arrayListOf<PendingIntent?>().apply {
                    add(sentPendingIntent)
                }, null)
            }
        }
    }

    private val smsSentReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            LogUtils.d("MyBroadcastReceiver", "Received broadcast: " + intent.action)
            LogUtils.d("code", resultCode.toString())
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // 短信发送成功
                    LogUtils.e("signal", "成功")
                    ToastUtil.show("短信发送成功yyyyy")
                    taskRet(1,resultCode.toString())
                }

                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                    // 短信发送失败
                    LogUtils.e("signal", "失败")
                    ToastUtil.show("短信发送失败")
                    taskRet(2,resultCode.toString())
                }

                SmsManager.RESULT_ERROR_NO_SERVICE -> {
                    // 手机没有信号，无法发送短信
                    LogUtils.e("signal", "失败")
                    ToastUtil.show("手机无信号，无法发送短信")
                    taskRet(2,resultCode.toString())
                }

                else->{
                    taskRet(2,resultCode.toString())
                }
            }
        }
    }

    /**
     * 反馈发送任务结果
     * @param status 1表示发送成功，2表示发送失败 3表示是接收的短信
     */
    fun taskRet(status : Int,statusNote : String){
        mDto?.apply {
            for((index,dto) in mAdapter.listData.withIndex()){
                if(this.id == dto.id){
                    dto.status = if(status == 1) "发送成功" else "发送失败"
                    mAdapter.notifyItemChanged(index)
                }
            }
        }
        NetWorkApi.httpSmsTaskRet(this,mDto?.id.toDefault(""),status,statusNote).subscribe({
            binding.root.postDelayed({
                mDto = null
                getTask()
            },10*1000)
        },{
            binding.root.postDelayed({
                mDto = null
                getTask()
            },10*1000)
        })
    }

    /**
     * 上传接收的短信
     */
    fun upSms(dto : SmsDto){
        NetWorkApi.httpSmsInbox(this,dto.mobile.toDefault(""),dto.content.toDefault(""),dto.time.toDefault("")).subscribe({

        },{
            ToastUtil.show(it.message)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消注册广播接收器
        unregisterReceiver(smsSentReceiver)
        //关闭短信监听
        stopService(mIntent)
        ObservableUtil.stopTimer(SENT)
    }
}