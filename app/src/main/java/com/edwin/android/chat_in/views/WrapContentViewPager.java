package com.edwin.android.chat_in.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Edwin Ramirez Ventura on 8/7/2017.
 */

public class WrapContentViewPager extends ViewPager {

    public static final String TAG = WrapContentViewPager.class.getSimpleName();
    private int mCurrentPagePosition = 0;

    public WrapContentViewPager(Context context) {
        super(context);
    }

    public WrapContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            View child = getChildAt(mCurrentPagePosition);
            if (child != null) {
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                Log.d(TAG, "widthMeasureSpec: "+widthMeasureSpec);
                Log.d(TAG, "heightMeasureSpec: "+heightMeasureSpec);
                Log.d(TAG, "child height: "+h);
                if(h > heightMeasureSpec) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void reMeasureCurrentPage(int position) {
        mCurrentPagePosition = position;
        requestLayout();
    }
}
