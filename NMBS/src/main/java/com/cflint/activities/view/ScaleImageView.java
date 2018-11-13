package com.cflint.activities.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.cflint.R;

public class ScaleImageView extends android.support.v7.widget.AppCompatImageView {
    /**
     * 类型宽
     */
    private static final int WIDTH = 1;
    /**
     * 类型高
     */
    private static final int HEIGHT = 2;
    /**
     * 缩放的类型
     */
    private int mScaleType = 0;
    /**
     * 缩放的倍数
     */
    private float mScaleRadio;

    public enum ImageScaleType {
        WIDTH, HEIGHT
    }

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView);
        mScaleRadio = array.getFloat(R.styleable.ScaleImageView_viewScaleRadio, 0);
        mScaleType = array.getInt(R.styleable.ScaleImageView_viewScaleType, 0);
        array.recycle();
    }

    /**
     * 测量控件的代码
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 不设置任何属性
        if (mScaleType == 0 || mScaleRadio == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 如果子类设置了精确的宽高
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY
                && (widthSize != 0 && heightSize != 0)) {
            setMeasuredDimension(widthSize, heightSize);
            return;
        }
        // 如果是按宽度来缩放
        if (mScaleType == WIDTH && widthMode == MeasureSpec.EXACTLY && widthSize != 0) {
            heightSize = (int) (widthSize / mScaleRadio);
            setMeasuredDimension(widthSize, heightSize);
            return;
        }
        // 如果是按高度来缩放
        if (mScaleType == HEIGHT && heightMode == MeasureSpec.EXACTLY && heightSize != 0) {
            widthSize = (int) (heightSize / mScaleRadio);
            setMeasuredDimension(widthSize, heightSize);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置缩放类型
     */
    public void setScaleType(ImageScaleType scaleType) {
        switch (scaleType) {
            case WIDTH:
                mScaleType = WIDTH;
                break;
            case HEIGHT:
                mScaleType = HEIGHT;
                break;
        }
        invalidate();
    }

    /**
     * 设置缩放倍数
     */
    public void setScaleRadio(float radio) {
        this.mScaleRadio = radio;
        invalidate();
    }
}
