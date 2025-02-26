package com.tzh.myapplication.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.googlecode.tesseract.android.TessBaseAPI
import com.tzh.baselib.util.LogUtils
import com.tzh.baselib.view.SearchView
import com.tzh.myapplication.R
import com.tzh.myapplication.base.AppBaseActivity
import com.tzh.myapplication.databinding.ActivitySearchBinding
import com.tzh.myapplication.utils.ToastUtil
import com.tzh.myapplication.utils.screen.ScreenshotObserver


class SearchActivity : AppBaseActivity<ActivitySearchBinding>(R.layout.activity_search) {
    companion object {
        @JvmStatic
        fun start(context: AppCompatActivity) {
            context.startActivity(Intent(context, SearchActivity::class.java))
        }
    }

    val tessBaseAPI by lazy {
        TessBaseAPI()
    }

    override fun initView() {
        binding.activity = this
        val dataPath = filesDir.absolutePath + "/tesseract/"
        tessBaseAPI.init(dataPath, "chi_sim") // 使用中文语言包

        binding.searchView.setSvSearchListener(object : SearchView.SvSearchListener{
            override fun search(text: String): Boolean {
                ToastUtil.show(text)
                return true
            }

            override fun clear() {

            }
        })

        val handler = Handler()
        val observer = ScreenshotObserver(handler, this,object : ScreenshotObserver.ImageListener{
            override fun image(bitmap: Bitmap) {
                binding.ivImage.setImageBitmap(bitmap)
                tessBaseAPI.setImage(bitmap)
                val text = tessBaseAPI.utF8Text
                LogUtils.e("识别结果=====",text)
            }
        })
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
    }

    override fun initData() {

    }

    override fun onDestroy() {
        super.onDestroy()
        tessBaseAPI.end()
    }
}