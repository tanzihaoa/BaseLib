package com.tzh.baselib.divider;

import androidx.annotation.ColorInt;

public class XSideLine {

    public boolean isHave = false;


    /**
     * A single color value in the form 0xAARRGGBB.
     **/
    public int color;
    /**
     * 单位dp
     */
    public float widthDp;
    /**
     * startPaddingDp,分割线起始的padding，水平方向左为start，垂直方向上为start
     * endPaddingDp,分割线尾部的padding，水平方向右为end，垂直方向下为end
     */
    public float startPaddingDp;
    public float endPaddingDp;

    public XSideLine(boolean isHave, @ColorInt int color, float widthDp, float startPaddingDp, float endPaddingDp) {
        this.isHave = isHave;
        this.color = color;
        this.widthDp = widthDp;
        this.startPaddingDp = startPaddingDp;
        this.endPaddingDp = endPaddingDp;
    }

    public float getStartPaddingDp() {
        return startPaddingDp;
    }

    public void setStartPaddingDp(float startPaddingDp) {
        this.startPaddingDp = startPaddingDp;
    }

    public float getEndPaddingDp() {
        return endPaddingDp;
    }

    public void setEndPaddingDp(float endPaddingDp) {
        this.endPaddingDp = endPaddingDp;
    }

    public boolean isHave() {
        return isHave;
    }

    public void setHave(boolean have) {
        isHave = have;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getWidthDp() {
        return widthDp;
    }

    public void setWidthDp(float widthDp) {
        this.widthDp = widthDp;
    }
}
