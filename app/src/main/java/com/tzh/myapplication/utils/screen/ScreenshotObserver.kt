package com.tzh.myapplication.utils.screen

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import com.googlecode.tesseract.android.TessBaseAPI
import com.tzh.baselib.util.LogUtils
import com.tzh.baselib.util.toDefault
import com.tzh.baselib.util.voice.PathUtil
import com.tzh.myapplication.base.MyApplication


class ScreenshotObserver(handler: Handler, context: Context) : ContentObserver(handler) {
    private val context: Context

    init {
        this.context = context
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        if(uri != null){
            val screenshotPath = getScreenshotPath(uri)
            LogUtils.e("截屏====",screenshotPath.toDefault(""))
            if (screenshotPath?.contains("screenshot",true).toDefault(false)) {
                // 检测到截屏
                LogUtils.e("截屏====","检测到截屏")
                screenshotPath?.let { processScreenshot(it) }
            }
        }
    }

    private fun getScreenshotPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                return cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return null
    }

    private fun processScreenshot(screenshotPath: String) {
        val bitmap = BitmapFactory.decodeFile(screenshotPath)
        val tessBaseAPI = TessBaseAPI()
        tessBaseAPI.init(PathUtil.getTessData(MyApplication.mContext).absolutePath, "eng")
        tessBaseAPI.setImage(bitmap)
        val result = tessBaseAPI.utF8Text
        tessBaseAPI.end()
        LogUtils.e("解析结果====",result)
    }

}