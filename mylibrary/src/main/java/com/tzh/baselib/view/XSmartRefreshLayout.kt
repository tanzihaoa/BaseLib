package com.tzh.baselib.view

import android.content.Context
import android.util.AttributeSet
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.scwang.smart.refresh.layout.listener.ScrollBoundaryDecider
import com.scwang.smart.refresh.layout.simple.SimpleBoundaryDecider
import com.tzh.baselib.adapter.XRvBindingPureDataAdapter

class XSmartRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SmartRefreshLayout(context, attrs) {

    private var oldPageIndex = 1

    /**
     * 页码
     */
    var pageIndex = 1

    /**
     * 是否是刷新状态
     * 这里的 isRefresh 用来 判断 请求 数据的 状态
     * 跟 smart isRefreshing 不同，
     * smart isRefreshing 是用来 判断 刷新动画的 状态
     */
    var isRefresh = true

    /**
     * 总页数
     */
    var pageCount = 0

    fun onRefresh() {
        mRefreshListener?.onRefresh(this)
    }

    fun setOnRefreshLoadMoreListener(block: ((Layout: RefreshLayout) -> Unit?)? = null): RefreshLayout {
        return if (block != null) {
            super.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageIndex = 1
                    isRefresh = true
                    onRefreshStatus()
                    block(refreshLayout)
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    isRefresh = false
                    if (pageIndex >= pageCount) {
                        loadNoData()
                    } else {
                        pageIndex++
                        block(refreshLayout)
                    }
                }
            })
        } else {
            super.setOnRefreshLoadMoreListener(null)
        }
    }

    override fun setOnRefreshLoadMoreListener(listener: OnRefreshLoadMoreListener?): RefreshLayout {
        return if (listener != null) {
            super.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    pageIndex = 1
                    isRefresh = true
                    onRefreshStatus()
                    listener.onRefresh(refreshLayout)
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    isRefresh = false
                    if (pageIndex >= pageCount) {
                        loadNoData()
                    } else {
                        pageIndex++
                        listener.onLoadMore(refreshLayout)
                    }
                }
            })
        } else {
            super.setOnRefreshLoadMoreListener(listener)
        }
    }


    /**
     * 加载数据 成功 使用这个
     */
    fun loadSuccess(
        adapter: XRvBindingPureDataAdapter<*>? = null,
        isShowNoData: Boolean = true,
        noDataTip: String? = null,
        noDataMinTip: String? = null
    ) {
        oldPageIndex = pageIndex
        if (this.isRefreshing) {
            finishRefresh()
        }
        if (this.isLoading) {
            finishLoadMore()
        }
        if (pageIndex > 0 && pageCount > 0) {
            adapter?.run {
                if (pageIndex >= pageCount) {
                    adapter.showNoMoreData(true)
                    setEnableLoadMore(false)
                } else {
                    adapter.showNoMoreData(false)
                    setEnableLoadMore(true)
                }

            }
        } else {
            adapter?.showNoMoreData(false)
        }
    }


    /**
     * 加载失败
     */
    fun loadError(
        throwable: Throwable? = null,
        isReLoad: Boolean = true
    ) {
        pageIndex = oldPageIndex
        //如果是 刷新操作 的加载失败，那么pageIndex也需要+1，比
        if (this.isLoading) {
            finishLoadMore(300, false, false)
        }
        if (this.isRefreshing) {
            finishRefresh(300, false, false)
            //处理数据加载错误的缺省状态
            //  loadView?.loadingError(throwable, isReLoad)
        }

    }

    /**
     * 没有更多数据
     */
    fun loadNoData(adapter: XRvBindingPureDataAdapter<*>? = null) {
        adapter?.run {
            adapter.showNoMoreData(true)
            setEnableLoadMore(false)
        }
        finishLoadMore()
    }


    /**
     * 刷新时，允许加载更多
     */
    fun onRefreshStatus(adapter: XRvBindingPureDataAdapter<*>? = null) {
        adapter?.showNoMoreData(false)
        setNoMoreData(false)
        setEnableLoadMore(true)
    }


    /**
     * 立即停止刷新加载
     */
    fun stopRefreshLoad(
        refreshDelayed: Int = 300,
        loadDelayed: Int = 300
    ) {
        if (state == RefreshState.Refreshing) {
            finishRefresh(refreshDelayed)
        }
        if (state == RefreshState.Loading) {
            finishLoadMore(loadDelayed)
        }
    }

    /**
     * 禁用刷新加载 带阻尼效果
     * @param enable 是否启用
     */
    @JvmOverloads
    fun dampingStyle(enable: Boolean = false) {
        this.setEnableLoadMore(enable)
        this.setEnableRefresh(enable)
        this.setEnableOverScrollBounce(!enable)
        this.setEnableOverScrollDrag(!enable)
        this.setEnablePureScrollMode(!enable)
    }

    /**
     * 禁用加载更多 带阻尼效果
     */
    fun dampingRefreshStyle() {
        this.setEnableLoadMore(false)
        this.setEnableRefresh(true)
        this.setEnableOverScrollBounce(true)
        this.setEnableOverScrollDrag(true)
        this.setEnablePureScrollMode(true)
    }


    /**
     * 滚动边界
     * Created by scwang on 2017/7/8.
     */
    open class ScrollBoundaryDeciderAdapter : SimpleBoundaryDecider(), ScrollBoundaryDecider {

    }
}