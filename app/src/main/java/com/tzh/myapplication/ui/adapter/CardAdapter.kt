package com.tzh.myapplication.ui.adapter

import com.tzh.myapplication.R
import com.tzh.myapplication.databinding.AdapterCardBinding
import com.tzh.baselib.adapter.XRvBindingHolder
import com.tzh.baselib.adapter.XRvBindingPureDataAdapter
import com.tzh.myapplication.ui.dto.CardDto
import com.tzh.baselib.util.LoadImageUtil

class CardAdapter : XRvBindingPureDataAdapter<CardDto>(R.layout.adapter_card){
    override fun onBindViewHolder(holder: XRvBindingHolder, position: Int, data: CardDto) {
        holder.getBinding<AdapterCardBinding>().run {
            this.dto = data
            this.cardTxt.text = data.title
            com.tzh.baselib.util.LoadImageUtil.loadImageUrl(this.cardImg,data.url)
        }
    }
}