package com.tzh.baselib.util.img

import android.content.Context
import com.tzh.baselib.util.AppPathManager
import java.io.File

object KtFileUtil {

    /**
     * 获取 图片文件下载目录
     *
     */
    fun getImageCacheFolder(context : Context): File? {
        val file = context.getExternalFilesDir("image/")
        com.tzh.baselib.util.AppPathManager.ifFolderExit(file?.absolutePath)
        return file
    }
}