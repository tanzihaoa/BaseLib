package com.tzh.myapplication.utils.mask

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

class MaskView(context: Context) : View(context) {
    private val masks = mutableListOf<MaskRegion>()
    private var currentMaskType = MaskType.MOSAIC
    private var currentRect: Rect? = null
    private var startPoint = PointF()

    fun setMaskType(type: MaskType) {
        currentMaskType = type
    }

    fun addMask(rect: Rect, type: MaskType) {
        masks.add(MaskRegion(type, rect))
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制已有遮罩
        masks.forEach { region ->
            val paint = when (region.type) {
                MaskType.MOSAIC -> Paint().apply { color = Color.argb(100, 255, 0, 0) }
                MaskType.BLUR -> Paint().apply { color = Color.argb(100, 0, 255, 0) }
                MaskType.PIXELATE -> Paint().apply { color = Color.argb(100, 0, 0, 255) }
            }
            canvas.drawRect(region.startRect, paint)
        }

        // 绘制当前正在创建的遮罩
        currentRect?.let {
            val paint = Paint().apply {
                color = Color.argb(150, 200, 200, 0)
                style = Paint.Style.STROKE
                strokeWidth = 4f
            }
            canvas.drawRect(it, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startPoint.set(event.x, event.y)
                currentRect = Rect(event.x.toInt(), event.y.toInt(), event.x.toInt(), event.y.toInt())
            }
            MotionEvent.ACTION_MOVE -> {
                currentRect?.let {
                    it.left = min(startPoint.x, event.x).toInt()
                    it.top = min(startPoint.y, event.y).toInt()
                    it.right = max(startPoint.x, event.x).toInt()
                    it.bottom = max(startPoint.y, event.y).toInt()
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                currentRect?.let {
                    masks.add(MaskRegion(currentMaskType, it))
                    currentRect = null
                    invalidate()
                }
            }
        }
        return true
    }
}