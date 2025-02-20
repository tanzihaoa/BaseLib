package com.tzh.baselib.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;

public abstract class XDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;

    private Context context;
    @ColorInt
    public int spaceColor = Color.TRANSPARENT;

    public XDividerItemDecoration(Context context) {
        this.context = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //left, top, right, bottom
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            int itemPosition = ((RecyclerView.LayoutParams) child.getLayoutParams()).getViewLayoutPosition();

            XDivider divider = getDivider(parent.getAdapter(), itemPosition);

            if (divider.getLeftSideLine().isHave()) {
                drawChildLeftVertical(child, c, parent, divider);
            }
            if (divider.getTopSideLine().isHave()) {
                drawChildTopHorizontal(child, c, parent, divider);
            }
            if (divider.getRightSideLine().isHave()) {
                drawChildRightVertical(child, c, parent, divider);
            }
            if (divider.getBottomSideLine().isHave()) {
                drawChildBottomHorizontal(child, c, parent, divider);
            }
        }
    }

    private void drawChildBottomHorizontal(View child, Canvas c, RecyclerView parent, XDivider divider) {
        int lineWidthPx = convert(context, divider.getBottomSideLine().getWidthDp());
        int startPaddingPx = convert(context, divider.getBottomSideLine().getStartPaddingDp());
        int endPaddingPx = convert(context, divider.getBottomSideLine().getEndPaddingDp());


        int leftPadding = 0;
        int rightPadding = 0;

        if (startPaddingPx <= 0) {
            //padding<0当作==0处理
            //上下左右默认分割线的两头都出头一个分割线的宽度，避免十字交叉的时候，交叉点是空白
            leftPadding = -lineWidthPx;
        } else {
            leftPadding = startPaddingPx;
        }

        if (endPaddingPx <= 0) {
            rightPadding = lineWidthPx;
        } else {
            rightPadding = -endPaddingPx;
        }

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        int left = child.getLeft() - params.leftMargin + leftPadding;
        int right = child.getRight() + params.rightMargin + rightPadding;
        int top = child.getBottom() + params.bottomMargin;
        int bottom = top + lineWidthPx;

        if (startPaddingPx > 0) {
            mPaint.setColor(spaceColor);
            c.drawRect(child.getLeft() - params.leftMargin - lineWidthPx, top, left, bottom, mPaint);
        }

        if (endPaddingPx > 0) {
            mPaint.setColor(spaceColor);
            c.drawRect(child.getRight() + params.rightMargin + lineWidthPx, top, right, bottom, mPaint);
        }

        mPaint.setColor(divider.getBottomSideLine().getColor());
        c.drawRect(left, top, right, bottom, mPaint);
    }

    private void drawChildTopHorizontal(View child, Canvas c, RecyclerView parent, XDivider divider) {
        int lineWidthPx = convert(context, divider.getTopSideLine().getWidthDp());
        int startPaddingPx = convert(context, divider.getTopSideLine().getStartPaddingDp());
        int endPaddingPx = convert(context, divider.getTopSideLine().getEndPaddingDp());

        int leftPadding = 0;
        int rightPadding = 0;

        if (startPaddingPx <= 0) {
            //padding<0当作==0处理
            //上下左右默认分割线的两头都出头一个分割线的宽度，避免十字交叉的时候，交叉点是空白
            leftPadding = -lineWidthPx;
        } else {
            leftPadding = startPaddingPx;
        }
        if (endPaddingPx <= 0) {
            rightPadding = lineWidthPx;
        } else {
            rightPadding = -endPaddingPx;
        }

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        int left = child.getLeft() - params.leftMargin + leftPadding;
        int right = child.getRight() + params.rightMargin + rightPadding;
        int bottom = child.getTop() - params.topMargin;
        int top = bottom - lineWidthPx;
        if (startPaddingPx > 0) {
            mPaint.setColor(spaceColor);
            c.drawRect(child.getLeft() - params.leftMargin - lineWidthPx, top, left, bottom, mPaint);
        }
        if (endPaddingPx > 0) {
            mPaint.setColor(spaceColor);
            c.drawRect(child.getRight() + params.rightMargin + lineWidthPx, top, right, bottom, mPaint);
        }

        mPaint.setColor(divider.getTopSideLine().getColor());
        c.drawRect(left, top, right, bottom, mPaint);

    }

    private void drawChildLeftVertical(View child, Canvas c, RecyclerView parent, XDivider divider) {
        int lineWidthPx = convert(context, divider.getLeftSideLine().getWidthDp());
        int startPaddingPx = convert(context, divider.getLeftSideLine().getStartPaddingDp());
        int endPaddingPx = convert(context, divider.getLeftSideLine().getEndPaddingDp());

        int topPadding = 0;
        int bottomPadding = 0;

        if (startPaddingPx <= 0) {
            //padding<0当作==0处理
            //上下左右默认分割线的两头都出头一个分割线的宽度，避免十字交叉的时候，交叉点是空白
            topPadding = -lineWidthPx;
        } else {
            topPadding = startPaddingPx;
        }
        if (endPaddingPx <= 0) {
            bottomPadding = lineWidthPx;
        } else {
            bottomPadding = -endPaddingPx;
        }

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        int top = child.getTop() - params.topMargin + topPadding;
        int bottom = child.getBottom() + params.bottomMargin + bottomPadding;
        int right = child.getLeft() - params.leftMargin;
        int left = right - lineWidthPx;

        if (startPaddingPx > 0) {
            mPaint.setColor(spaceColor);
            c.drawRect(left, child.getTop() - params.topMargin - lineWidthPx, right, top, mPaint);
        }
        if (endPaddingPx > 0) {
            mPaint.setColor(spaceColor);
            c.drawRect(left, child.getBottom() + params.bottomMargin + lineWidthPx, right, bottom, mPaint);
        }
        mPaint.setColor(divider.getLeftSideLine().getColor());
        c.drawRect(left, top, right, bottom, mPaint);

    }

    private void drawChildRightVertical(View child, Canvas c, RecyclerView parent, XDivider divider) {
        int lineWidthPx = convert(context, divider.getRightSideLine().getWidthDp());
        int startPaddingPx = convert(context, divider.getRightSideLine().getStartPaddingDp());
        int endPaddingPx = convert(context, divider.getRightSideLine().getEndPaddingDp());

        int topPadding = 0;
        int bottomPadding = 0;

        if (startPaddingPx <= 0) {
            //padding<0当作==0处理
            //上下左右默认分割线的两头都出头一个分割线的宽度，避免十字交叉的时候，交叉点是空白
            topPadding = -lineWidthPx;
        } else {
            topPadding = startPaddingPx;
        }
        if (endPaddingPx <= 0) {
            bottomPadding = lineWidthPx;
        } else {
            bottomPadding = -endPaddingPx;
        }

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
        int top = child.getTop() - params.topMargin + topPadding;
        int bottom = child.getBottom() + params.bottomMargin + bottomPadding;
        int left = child.getRight() + params.rightMargin;
        int right = left + lineWidthPx;

        if (startPaddingPx > 0) {
            mPaint.setColor(spaceColor);
            c.drawRect(left, child.getTop() - params.topMargin - lineWidthPx, right, top, mPaint);
        }
        if (endPaddingPx > 0) {
            mPaint.setColor(spaceColor);
            c.drawRect(left, child.getBottom() + params.bottomMargin + lineWidthPx, right, bottom, mPaint);
        }

        mPaint.setColor(divider.getRightSideLine().getColor());
        c.drawRect(left, top, right, bottom, mPaint);

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //outRect 看源码可知这里只是把Rect类型的outRect作为一个封装了left,right,top,bottom的数据结构,
        //作为传递left,right,top,bottom的偏移值来用的

        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();

        XDivider divider = getDivider(parent.getAdapter(), itemPosition);
        int left = divider.getLeftSideLine().isHave() ? convert(context, divider.getLeftSideLine().getWidthDp()) : 0;
        int top = divider.getTopSideLine().isHave() ? convert(context, divider.getTopSideLine().getWidthDp()) : 0;
        int right = divider.getRightSideLine().isHave() ? convert(context, divider.getRightSideLine().getWidthDp()) : 0;
        int bottom = divider.getBottomSideLine().isHave() ? convert(context, divider.getBottomSideLine().getWidthDp()) : 0;

        outRect.set(left, top, right, bottom);
    }


    public abstract XDivider getDivider(RecyclerView.Adapter adapter, int itemPosition);

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     */
//    private static int convert(Context context, float dpVal) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                dpVal, context.getResources().getDisplayMetrics());
//    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int convert(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}

