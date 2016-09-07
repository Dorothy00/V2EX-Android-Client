package com.dorothy.v2ex.View;

/**
 * Created by dorothy on 16/9/2.
 */

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Asus on 2016/5/17.
 */
public class WrapLinearLayoutManager extends LinearLayoutManager {
    public WrapLinearLayoutManager(Context context) {
        super(context);
    }

    public WrapLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public WrapLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                                   int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("problem", "meet a IOOBE in RecyclerView");
        }
    }


}
