package com.tzh.myapplication.utils.screen

import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.tzh.baselib.util.LogUtils

class ImageProcess {
    fun processScreenshot(screenshotPath: String,listener : ScreenshotObserver.ImageListener) {
        Thread{
            val bitmap = BitmapFactory.decodeFile(screenshotPath)
            if(bitmap != null){
                LogUtils.e("解析====","开始识别")
                // 从 Bitmap 创建 InputImage
                val image = InputImage.fromBitmap(bitmap, 0)

                // 初始化识别器（支持中文）
                val recognizer = TextRecognition.getClient(
                    ChineseTextRecognizerOptions.Builder()
                        .build())
                Handler(Looper.getMainLooper()).post {
                    listener.image(bitmap)
                }
                // 处理识别结果
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val fullText = visionText.text
                        Log.d("OCR====", "识别结果：$fullText")
                    }
                    .addOnFailureListener { e ->
                        Log.e("OCR===", "识别失败：${e.message}")
                    }
            }else{
                LogUtils.e("解析结果====","图片为空")
            }
        }.start()

    }
}