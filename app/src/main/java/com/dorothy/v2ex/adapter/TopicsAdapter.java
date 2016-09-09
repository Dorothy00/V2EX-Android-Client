package com.dorothy.v2ex.adapter;

import android.content.Context;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.CircleImageView;
import com.dorothy.v2ex.models.Topic;

import java.util.List;

/**
 * Created by dorothy on 16/8/17.
 */


public class TopicsAdapter extends BaseRecyclerAdapter<Topic> {

    public TopicsAdapter(Context context, List<Topic> dataList) {
        super(context, dataList);
    }

    @Override
    public void bindData(RecyclerViewHolder viewHolder, Topic data, int position) {
        TextView tvTitle = viewHolder.getTextView(R.id.title);
        TextView tvAuthor = viewHolder.getTextView(R.id.author);
        TextView tvReplyNum = viewHolder.getTextView(R.id.reply_number);
        CircleImageView ivAvatar = viewHolder.getCircleImageView(R.id.avatar);

        tvTitle.setText(data.getTitle());
        tvAuthor.setText(data.getMember().getUsername());
        tvReplyNum.setText(data.getReplies() + "");
        Glide.with(context).load("http:" + data.getMember().getAvatarNormal())
                .into(ivAvatar);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_topic;
    }
}