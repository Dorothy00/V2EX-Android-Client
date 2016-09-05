package com.dorothy.v2ex.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.CircleImageView;
import com.dorothy.v2ex.interfaces.TopicOnItemClickListener;
import com.dorothy.v2ex.models.Topic;

import java.util.List;

/**
 * Created by dorothy on 16/8/17.
 */

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicViewHolder> {
    private Context context;
    private List<Topic> topicList;
    private TopicOnItemClickListener topicOnItemClickListener;

    public TopicsAdapter(Context context, List<Topic> topicList, TopicOnItemClickListener
            listener) {
        this.context = context;
        this.topicList = topicList;
        topicOnItemClickListener = listener;
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic, parent,
                false);
        TopicViewHolder viewHolder = new TopicViewHolder(view, topicOnItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        final Topic topic = topicList.get(position);
        holder.tvTitle.setText(topic.getTitle());
        holder.btnNode.setText(topic.getNode().getName());
        holder.tvAuthor.setText(topic.getMember().getUsername());
        holder.tvReplyNum.setText(Integer.toString(topic.getReplies()));
        Glide.with(context).load("http:" + topic.getMember().getAvatarNormal())
                .into(holder.ivAvatar);
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cvTopic;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvReplyNum;
        Button btnNode;
        CircleImageView ivAvatar;
        TopicOnItemClickListener topicOnItemClickListener;

        public TopicViewHolder(View view, TopicOnItemClickListener topicOnItemClickListener) {
            super(view);
            this.topicOnItemClickListener = topicOnItemClickListener;
            cvTopic = (CardView) view.findViewById(R.id.topic_card);
            tvTitle = (TextView) view.findViewById(R.id.title);
            tvAuthor = (TextView) view.findViewById(R.id.author);
            tvReplyNum = (TextView) view.findViewById(R.id.reply_number);
            btnNode = (Button) view.findViewById(R.id.topic_node);
            ivAvatar = (CircleImageView) view.findViewById(R.id.avatar);
            cvTopic.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof CardView) {
                topicOnItemClickListener.onItemClick(topicList.get(getAdapterPosition()));
            }
        }
    }
}
