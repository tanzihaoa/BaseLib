package com.tzh.baselib.view.card;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SwipeTouchLayout extends FrameLayout {
    private boolean mFirstMove;
    private float mDownX;
    private float mDownY;

    public SwipeTouchLayout(@NonNull Context context) {
        super(context);
    }

    public SwipeTouchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeTouchLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        super.onInterceptTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstMove = false;
                mDownX = event.getX();
                mDownY = event.getY();
                if (mSwipeTouchListener != null) {
                    mSwipeTouchListener.onTouchDown(event);
                }

                return false;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - mDownX;
                float dy = event.getY() - mDownY;
                double value = Math.sqrt(dx * dx + dy * dy);
                if (!mFirstMove && Math.abs(value) < ViewConfiguration.get(
                        getContext()).getScaledTouchSlop()) {
                    return false;
                }
                mFirstMove = true;
                return true;
            case MotionEvent.ACTION_UP:
                if (mFirstMove) {
                    return true;
                }
                return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mSwipeTouchListener != null) {
                    mSwipeTouchListener.onTouchMove(this, event);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (mSwipeTouchListener != null) {
                    mSwipeTouchListener.onTouchUp(event);
                }
                if (mFirstMove) {
                    return true;
                }
                return false;
        }
        return super.onTouchEvent(event);
    }

    SwipeTouchListener mSwipeTouchListener;

    public void setSwipeTouchListener(SwipeTouchListener swipeTouchListener) {
        mSwipeTouchListener = swipeTouchListener;
    }

    public interface SwipeTouchListener {
        void onTouchDown(MotionEvent event);

        void onTouchUp(MotionEvent event);

        void onTouchMove(View v, MotionEvent event);

    }
}
