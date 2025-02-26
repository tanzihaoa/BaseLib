package com.tzh.myapplication.ui.activity.wallper

import com.tzh.myapplication.R
import com.tzh.myapplication.databinding.AdapterWallpaperListBinding
import com.tzh.baselib.adapter.XRvBindingHolder
import com.tzh.baselib.adapter.XRvBindingPureDataAdapter
import com.tzh.baselib.util.LoadImageUtil
import com.tzh.baselib.util.toDefault

/**
 * 壁纸列表 适配器
 */
class WallpaperListAdapter : XRvBindingPureDataAdapter<WallpaperInfo>(R.layout.adapter_wallpaper_list){
    override fun onBindViewHolder(holder: XRvBindingHolder, position: Int, data: WallpaperInfo) {
        holder.getBinding<AdapterWallpaperListBinding>().run {
            LoadImageUtil.loadImageUrl(this.ivImage,data.thumbnail.toDefault("ic_launcher"),8f)

        }
    }
}