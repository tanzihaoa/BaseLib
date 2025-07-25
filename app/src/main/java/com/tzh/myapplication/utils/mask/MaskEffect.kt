package com.tzh.myapplication.utils.mask

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.wonderkiln.blurkit.BlurKit
import kotlin.math.min
import androidx.core.graphics.scale
import com.tzh.myapplication.base.MyApplication

class MaskEffect {

    // 马赛克效果
    fun applyMosaic(input: Bitmap, region: Rect, blockSize: Int = 15): Bitmap {
        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)

        for (x in region.left until region.right step blockSize) {
            for (y in region.top until region.bottom step blockSize) {
                val block = Rect(
                    x,
                    y,
                    min(x + blockSize, region.right),
                    min(y + blockSize, region.bottom)
                )

                val color = getAverageColor(input, block)
                val paint = Paint().apply { this.color = color }
                canvas.drawRect(block, paint)
            }
        }
        return output
    }

    // 高斯模糊（水滴效果）
    fun applyBlur(input: Bitmap, region: Rect, radius: Float = 25f): Bitmap {
        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        BlurKit.init(MyApplication.mContext)
        val blurKit = BlurKit.getInstance()

        // 只模糊选定区域
        val cropped = Bitmap.createBitmap(
            input, region.left, region.top, region.width(), region.height()
        )
        val blurred = blurKit.blur(cropped, radius.toInt())

        val canvas = Canvas(output)
        canvas.drawBitmap(blurred, region.left.toFloat(), region.top.toFloat(), null)
        return output
    }

    // 像素化效果
    fun applyPixelate(input: Bitmap, region: Rect, pixelSize: Int = 10): Bitmap {
        val small = Bitmap.createBitmap(input, region.left, region.top, region.width(), region.height())
                .scale(region.width() / pixelSize, region.height() / pixelSize, false)

        val pixelated = small.scale(region.width(), region.height(), false)

        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)
        canvas.drawBitmap(pixelated, region.left.toFloat(), region.top.toFloat(), null)
        return output
    }

    private fun getAverageColor(bitmap: Bitmap, rect: Rect): Int {
        var r = 0
        var g = 0
        var b = 0
        var count = 0

        for (x in rect.left until rect.right) {
            for (y in rect.top until rect.bottom) {
                val color = bitmap.getPixel(x, y)
                r += Color.red(color)
                g += Color.green(color)
                b += Color.blue(color)
                count++
            }
        }

        return Color.rgb(r / count, g / count, b / count)
    }
}