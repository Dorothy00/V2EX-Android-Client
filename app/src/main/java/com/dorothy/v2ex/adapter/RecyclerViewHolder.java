package com.dorothy.v2ex.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dorothy.v2ex.View.CircleImageView;

/**
 * Created by dorothy on 16/9/7.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private View holder;
    private Context context;
    private SparseArray<View> childViews;

    public RecyclerViewHolder(Context cxt, View view) {
        super(view);
        holder = view;
        context = cxt;
        childViews = new SparseArray<>();
    }

    private <T extends View> T findViewById(int viewId) {
        View view = childViews.get(viewId);
        if (view == null) {
            view = holder.findViewById(viewId);
            childViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getHolderView() {
        return holder;
    }

    public View getView(int viewId) {
        return findViewById(viewId);
    }

    public TextView getTextView(int viewId) {
        return (TextView) findViewById(viewId);
    }

    public CircleImageView getCircleImageView(int viewId) {
        return (CircleImageView) findViewById(viewId);
    }

    public Button getButton(int viewId) {
        return (Button) findViewById(viewId);
    }

    public EditText getEditText(int viewId) {
        return (EditText) getView(viewId);
    }


    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = findViewById(viewId);
        view.setOnClickListener(listener);
    }
}
