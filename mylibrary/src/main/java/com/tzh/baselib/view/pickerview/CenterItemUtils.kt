package com.tzh.baselib.view.pickerview

object CenterItemUtils {
    /**
     * 计算距离中间最近的一个ItemView
     * @param itemHeights
     * @return
     */
    fun getMinDifferItem(itemHeights: MutableList<CenterViewItem>): CenterViewItem {
        var minItem = itemHeights.get(0) //默认第一个是最小差值
        for (i in itemHeights.indices) {
            //遍历获取最小差值
            if (itemHeights.get(i).differ <= minItem.differ) {
                minItem = itemHeights.get(i)
            }
        }
        return minItem
    }

    //    public static void main(String[] a){
    //
    //        CenterViewItem i = getMinDifferItem(Arrays.asList(
    //                new CenterViewItem(2 , 39)
    //                ,new CenterViewItem(3 , 3)
    //                ,new CenterViewItem(1 , 9)
    //                ,new CenterViewItem(4 , 449)));
    //       System.out.println("position:"+i.position+"   height:"+i.differ);
    //    }
    class CenterViewItem //当前Item索引
    //当前item和居中位置的差值
        (var position: Int, var differ: Int)
}