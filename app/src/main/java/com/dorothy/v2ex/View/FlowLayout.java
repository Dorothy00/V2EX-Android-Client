package com.dorothy.v2ex.View;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dorothy on 16/9/18.
 */
public class FlowLayout extends ViewGroup {


    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        int lineWidth = 0; // 行宽
        int lineHeight = 0;// 行高

        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            if (i == cCount - 1) {
                height += lineHeight;
                width = Math.max(width, lineWidth);
            }
        }

        if (modeWidth == MeasureSpec.EXACTLY) {
            width = sizeWidth;
        } else {
            width = width + getPaddingLeft() + getPaddingRight();
        }
        if (modeHeight == MeasureSpec.EXACTLY) {
            height = sizeHeight;
        } else {
            height = height + getPaddingBottom() + getPaddingTop();
        }

        setMeasuredDimension(width, height);

    }

    private List<List<View>> mViewList = new ArrayList<>();
    private List<Integer> mLineHeights = new ArrayList<>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mViewList.clear();
        mLineHeights.clear();

        int width = getWidth();
        int lineWidth = 0;
        int lineHeight = 0;
        List<View> lineViews = new ArrayList<>();
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 换行
            if (lineWidth + childWidth + lp.rightMargin + lp.leftMargin > width - getPaddingRight
                    () - getPaddingLeft()) {
                mLineHeights.add(lineHeight);
                mViewList.add(lineViews);

                lineWidth = 0;
                lineHeight = childHeight + lp.bottomMargin + lp.topMargin;

                lineViews = new ArrayList<>();
            }

            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
        }
        // 处理最后一行
        mLineHeights.add(lineHeight);
        mViewList.add(lineViews);

        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (int i = 0; i < mViewList.size(); i++) {
            List<View> lineViewList = mViewList.get(i);
            lineHeight = mLineHeights.get(i);
            for (int j = 0; j < lineViewList.size(); j++) {
                View child = lineViewList.get(j);
                if (child.getVisibility() == View.GONE)
                    continue;
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int lc = left + lp.leftMargin;
                int rc = lc + child.getMeasuredWidth();
                int tc = top + lp.topMargin;
                int bc = tc + child.getMeasuredHeight();
                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
}
