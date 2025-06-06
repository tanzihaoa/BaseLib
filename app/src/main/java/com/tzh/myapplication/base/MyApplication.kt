package com.tzh.myapplication.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import com.tzh.myapplication.utils.window.BaseActivityLifecycleCallbacks
import com.tzh.baselib.view.load.AppLoadLayout
import com.tzh.baselib.view.load.AppRefreshLayout


class MyApplication : Application() {

    companion object {
        init {
            //关闭彩蛋
            SmartRefreshLayout.setDefaultRefreshInitializer { _, layout -> layout.layout.tag = "close egg" }
            //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> //全局设置主题颜色
                //  layout.setEnableHeaderTranslationContent(true);
                AppRefreshLayout(context)
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> //  layout.setEnableFooterTranslationContent(false);
                //指定为经典Footer，默认是 BallPulseFooter
                AppLoadLayout(context)
            }
        }

        lateinit var mContext: Context

        var mLifecycleCallbacks: BaseActivityLifecycleCallbacks? = null
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this

        mLifecycleCallbacks = BaseActivityLifecycleCallbacks()
        registerActivityLifecycleCallbacks(mLifecycleCallbacks)
        MMKV.initialize(this)
    }

    /**
     * 这里会在onCreate之前被调用,可以做一些较早的初始化
     * 常用于 MultiDex 以及插件化框架的初始化
     *
     * @param base
     */
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun getLifecycleCallbacks(): BaseActivityLifecycleCallbacks? {
        return mLifecycleCallbacks
    }
}