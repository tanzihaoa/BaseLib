package com.tzh.myapplication.ui.dialog

import android.content.Context
import com.tzh.myapplication.R
import com.tzh.myapplication.databinding.DialogAddMobileBinding
import com.tzh.baselib.base.BaseBindingDialog
import com.tzh.myapplication.utils.ToastUtil
import com.tzh.baselib.util.isMobile
import com.tzh.baselib.util.setOnClickNoDouble

class AddMobileDialog(context : Context,val listener : AddMobileListener) : BaseBindingDialog<DialogAddMobileBinding>(context,R.layout.dialog_add_mobile){

    override fun initView() {

        binding.tvOk.setOnClickNoDouble {
            val phone = binding.etPhone.text.toString()
            if(phone.isMobile()){
                listener.mobile(phone)
                binding.etPhone.setText("")
                dismiss()
            }else{
                ToastUtil.show("请输入正确的手机号")
            }
        }
    }

    override fun initData() {

    }

    interface AddMobileListener{
        fun mobile(mobile : String)
    }

    /**
     * 设置手机号
     */
    fun show(mobile : String){
        binding.etPhone.setText(mobile)
        show()
    }
}