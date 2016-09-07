package com.dorothy.v2ex.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dorothy on 16/9/7.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    protected Context context;
    protected List<T> dataList;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public BaseRecyclerAdapter(Context context, List<T> dataList) {
        this.context = context;
        this.dataList = (dataList == null) ? new ArrayList<T>() : dataList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerViewHolder viewHolder = new RecyclerViewHolder(context, inflater.inflate
                (getLayoutResId(), parent, false));
        viewHolder.getHolderView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(viewHolder.getLayoutPosition());
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        bindData(holder, dataList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public abstract void bindData(RecyclerViewHolder viewHolder, T data, int position);

    public abstract int getLayoutResId();

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

}
