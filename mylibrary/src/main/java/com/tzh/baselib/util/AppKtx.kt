package com.tzh.baselib.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.os.MessageQueue
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.regex.Pattern

object AppKtx {

    /**
     * 获取状态栏高度
     *
     * return px
     */
    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        var result = 24
        val resId =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        result = if (resId > 0) {
            context.resources.getDimensionPixelSize(resId)
        } else {
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                result.toFloat(),
                Resources.getSystem().displayMetrics
            ).toInt()
        }
        return result
    }

    /*底部导航栏*/
    fun getNavigationBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 复制到剪贴板
     *
     * @param context context
     * @param tip     标识语
     * @param text    内容
     */
    fun putTextIntoClip(
        context: Context,
        tip: String? = null,
        text: String? = null
    ) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //创建ClipData对象
        val clipData = ClipData.newPlainText(tip, text)
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData)
    }

    /**
     * 触摸点 是否在 rect范围内
     */
    fun isInRect(event: MotionEvent, rect: Rect): Boolean {
        return event.x >= rect.left && event.x <= rect.right && event.y >= rect.top && event.y <= rect.bottom
    }


}

/**
 *  databinding 通过 inflate 绑定  layout
 */
fun <T : ViewDataBinding> Context.bindingInflateLayout(@LayoutRes layoutId: Int): T {
    return DataBindingUtil.inflate(
        LayoutInflater.from(this),
        layoutId, null, false
    )
}


/**
 * 对某个对象 做非null处理
 */
//fun <T> T?.toDefault(default: T): T = this ?: default


fun <T> T?.toJsonString(): String? {
    this ?: return null
    return com.tzh.baselib.util.GsonUtil.GsonString(this)
}


/**
 * string 转列表
 */
inline fun <reified T> String?.stringToList(): MutableList<T> {
    if (this.isNullOrEmpty()) {
        return mutableListOf()
    }
    return com.tzh.baselib.util.GsonUtil.GsonToList(this, T::class.java).nullToEmpty()
}

/**
 * string 转对象
 */
inline fun <reified T> String?.stringToObj(): T? {
    if (this.isNullOrEmpty()) {
        return null
    }
    return try {
        com.tzh.baselib.util.GsonUtil.getGSON().fromJson(this, T::class.java)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 如果最后一位是某个字符，则删除
 */
fun String.deleteLastChar(default: Char): String {
    if (this.isEmpty()) {
        return this
    }
    if (this.lastIndexOf(default) == this.length - 1) {
        return this.substring(0, this.length - 1)
    }
    return this
}


fun String?.toFloat(default: Float): Float {
    this ?: return default
    return if (this.toFloatOrNull() == null) {
        default
    } else {
        this.toFloat()
    }
}

/**
 * 判断当前对象，是不是 null
 */
fun Any?.isNotNullKtx(): Boolean = this != null


/**
 * 主线程闲置后再处理新的
 *
 * @param handler 返回false代表在这个IdleHandler被回调一次后就会被销毁。true代表可以一直被回调。
 */
fun showMainIdle(handler: MessageQueue.IdleHandler) {
    Looper.myQueue().addIdleHandler(handler)
}


/**
 *  databinding 通过 inflate 绑定  layout
 */
fun <T : ViewDataBinding> ViewGroup.bindingInflateLayout(@LayoutRes layoutId: Int): T {
    return DataBindingUtil.inflate(
        LayoutInflater.from(this.context),
        layoutId, this, true
    )
}

/**
 * int 类型数据 前面增加+0
 * @param length 保证长度
 */
fun Int.supplement(length: Int, default: String = "0"): String {
    var thisStr = this.toString()
    if (thisStr.length < length) {
        for (i in 0 until length - thisStr.length) {
            thisStr = default + thisStr
        }
    }
    return thisStr
}

/**
 * long 类型数据 前面增加+0
 * @param length 保证长度
 */
fun Long.supplement(length: Int, default: String = "0"): String {
    var thisStr = this.toString()
    if (thisStr.length < length) {
        for (i in 0 until length - thisStr.length) {
            thisStr = default + thisStr
        }
    }
    return thisStr
}

/**
 * 判断是否是手机号
 */
fun String.isMobile() : Boolean{
    if(this.length != 11){
        return false
    }
    val pattern = "^(\\+?\\d{1,4}[-.\\s]?)?\\(?\\d{1,3}\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$"
    val regex = Pattern.compile(pattern)
    val matcher = regex.matcher(this)
    return matcher.matches()
}

/**
 * 截取短信
 */
fun String.divideMessage() : MutableList<String>{
    if(this.length <= 70){
        return mutableListOf<String>().apply {
            add(this@divideMessage)
        }
    }else{
        val list = mutableListOf<String>()
        val totalLength = this.length
        val chunkLength = 100
        val chunkCount = (totalLength + chunkLength - 1) / chunkLength // 计算需要截取多少个块
        for (i in 0 until chunkCount) {
            val start = i * chunkLength
            val end = (start + chunkLength).coerceAtMost(totalLength)
            val chunk: String = this.substring(start, end) // 从字符串中截取指定长度的子串
            list.add(chunk) // 将子串添加到数组中
        }
        return list
    }
}

/**
 * 截取短信
 */
fun String.divideMessageArray() : ArrayList<String>{
    if(this.length <= 70){
        return arrayListOf<String>().apply {
            add(this@divideMessageArray)
        }
    }else{
        val list = arrayListOf<String>()
        val totalLength = this.length
        val chunkLength = 67
        val chunkCount = (totalLength + chunkLength - 1) / chunkLength // 计算需要截取多少个块
        for (i in 0 until chunkCount) {
            val start = i * chunkLength
            val end = (start + chunkLength).coerceAtMost(totalLength)
            val chunk: String = this.substring(start, end) // 从字符串中截取指定长度的子串
            list.add(chunk) // 将子串添加到数组中
        }
        return list
    }
}



/**
 * 检查权限
 */
fun MutableList<String>.checkPhonePermission(context: Context): Boolean {
    //验证是否许可权限
    for (permission in this) {
        val permissionCode  = ContextCompat.checkSelfPermission(context,permission)
        if (permissionCode != PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()){
                return true
            }
            //没有这个权限
            LogUtils.e("没有这个权限====",permission + "======" +permissionCode)
            return false
        }
    }
    return true
}

/**
 * 检查权限
 * @return type 0同意了所有权限 -1没有这个权限 -2拒绝过了这个权限
 */
fun MutableList<String>.checkPhonePermissionType(activity : Activity): Int {
    //验证是否许可权限
    for (permission in this) {
        if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)){
                //用户曾经拒绝过权限请求
                -2
            }else{
                //没有这个权限
                -1
            }
        }
    }
    return 0
}

/**
 * 获取文件名的后缀
 */
fun String.getSuffixName() : String{
    if(this.contains(".") && this.indexOf(".") < this.length -1){
        return this.substring(this.indexOf(".") + 1)
    }

    return this
}

/**
 * 获取文件名的名字
 */
fun String.getFileName() : String{
    if(this.contains(".") && this.indexOf(".") < this.length -1){
        return this.substring(0,this.indexOf("."))
    }

    return this
}

/**
 * 时间戳转时分秒
 */
fun Long.toTime() : String{
    //秒
    val second = this / 1000 % 60
    //分
    val minute = this / 1000 / 60 % 60
    //时
    val hour = this / 1000 / 60 / 60 % 60
    var time = ""
    if(hour > 0){
        time += if(hour>=10) hour.toString() else "0$hour"
        time += ":"
    }

    time += if(minute>=10) minute.toString() else "0$minute"
    time += ":"
    time += if(second>=10) second.toString() else "0$second"

    return time
}

/**
 * 获取大小
 */
fun Long.getSize() : String{
    val size = this / 1000

    if(size > 1000 * 1000){
        return (size / 1000 * 1000f).numFloatReplace(1).toString() + "GB"
    }else if(size > 1000){
        return (size / 1000f).numFloatReplace(1).toString() + "MB"
    }else{
        return size.toString() + "KB"
    }
}

/**
 * float四舍五入保留两位小数
 * @param round 保留的小数位数
 */
fun Float.numFloatReplace(round : Int = 2) : Float{
    return try {
        var bd: BigDecimal = BigDecimal.valueOf(this.toDouble())
        bd = bd.setScale(round, RoundingMode.HALF_UP)
        bd.toFloat()
    }catch (e : Exception){
        this
    }
}