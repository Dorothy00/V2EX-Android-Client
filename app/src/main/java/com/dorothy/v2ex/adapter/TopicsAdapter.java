package com.dorothy.v2ex.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.CircleImageView;
import com.dorothy.v2ex.activity.NodeTopicsActivity;
import com.dorothy.v2ex.models.Topic;

import java.util.List;

/**
 * Created by dorothy on 16/8/17.
 */


public class TopicsAdapter extends BaseRecyclerAdapter<Topic> implements View.OnClickListener {

    public TopicsAdapter(Context context, List<Topic> dataList) {
        super(context, dataList);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        viewHolder.setOnClickListener(R.id.topic_node, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.getTextView(R.id.topic_node).setTag(holder.getLayoutPosition());
    }

    @Override
    public void bindData(RecyclerViewHolder viewHolder, Topic data, int position) {
        TextView tvTitle = viewHolder.getTextView(R.id.title);
        TextView tvAuthor = viewHolder.getTextView(R.id.author);
        TextView tvReplyNum = viewHolder.getTextView(R.id.reply_number);
        TextView tvNode = viewHolder.getTextView(R.id.topic_node);
        CircleImageView ivAvatar = viewHolder.getCircleImageView(R.id.avatar);

        tvTitle.setText(data.getTitle());
        tvAuthor.setText(data.getMember().getUsername());
        tvReplyNum.setText(data.getReplies() + "");
        if (TextUtils.isEmpty(data.getNode().getTitle())) {
            tvNode.setVisibility(View.GONE);
        } else {
            tvNode.setText(data.getNode().getTitle());
        }
        Glide.with(context).load("http:" + data.getMember().getAvatarNormal())
                .into(ivAvatar);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_topic;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof Integer) {
            int pos = (Integer) v.getTag();
            Topic topic = dataList.get(pos);
            context.startActivity(NodeTopicsActivity.newIntent((Activity) context, topic.getNode
                    ().getName(), topic.getNode().getTitle()));
        }
    }
}