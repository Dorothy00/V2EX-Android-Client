package com.dorothy.v2ex.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.CircleImageView;
import com.dorothy.v2ex.interfaces.NotifyItemClickListener;
import com.dorothy.v2ex.models.Notification;

import java.util.List;

/**
 * Created by dorothy on 16/9/6.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter
        .NotificationViewHolder> {
    private Context context;
    private List<Notification> notificationList;
    private NotifyItemClickListener listener;

    public NotificationAdapter(Context context, NotifyItemClickListener listener,
                               List<Notification> notificationList) {
        this.context = context;
        this.listener = listener;
        this.notificationList = notificationList;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        NotificationViewHolder viewHolder = new NotificationViewHolder(v, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        Glide.with(context).load("http:" + notification.getMember().getAvatarMini()).into(holder
                .ciAvatar);
        holder.tvTitle.setText(notification.getReplyTitle());
        holder.tvReplyContent.setText(notification.getReplyContent());
        holder.tvTime.setText(notification.getTime());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cvCard;
        private CircleImageView ciAvatar;
        private TextView tvTitle;
        private TextView tvReplyContent;
        private TextView tvTime;
        private NotifyItemClickListener listener;

        public NotificationViewHolder(View v, NotifyItemClickListener listener) {
            super(v);
            cvCard = (CardView) v.findViewById(R.id.notification_card);
            ciAvatar = (CircleImageView) v.findViewById(R.id.avatar);
            tvTitle = (TextView) v.findViewById(R.id.title);
            tvReplyContent = (TextView) v.findViewById(R.id.reply_content);
            tvTime = (TextView) v.findViewById(R.id.reply_time);
            this.listener = listener;
            cvCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof CardView) {
                listener.notifyItemOnClick(notificationList.get(getAdapterPosition()));
            }
        }
    }
}
