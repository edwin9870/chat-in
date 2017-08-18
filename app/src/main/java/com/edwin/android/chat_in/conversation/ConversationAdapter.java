package com.edwin.android.chat_in.conversation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.entity.Contact;
import com.edwin.android.chat_in.entity.Message;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Edwin Ramirez Ventura on 8/15/2017.
 */

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_MESSAGE_RECEIVED = 5454;
    public static final int VIEW_TYPE_MESSAGE_SENT = 8877;
    public static final String TAG = ConversationAdapter.class.getSimpleName();
    private Context mContext;
    private List<Message> mContacts;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder viewHolder;

        if(viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            View view = inflater.inflate(R.layout.item_message_received, parent, false);
            viewHolder = new MessageReceivedViewHolder(view);
        }else {
            View view = inflater.inflate(R.layout.item_message_sent, parent, false);
            viewHolder = new MessageSentViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = mContacts.get(position);
        Log.d(TAG, "holder.getItemViewType(): " + holder.getItemViewType());
        if(holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED) {
            bindReceivedMessage((MessageReceivedViewHolder) holder, message);
        } else {
            bindSentMessage((MessageSentViewHolder) holder, message);
        }

    }

    private void bindReceivedMessage(MessageReceivedViewHolder holder, Message message) {
        holder.mMessageReceivedTextView.setText(message.getMessage());

        Picasso picasso = Picasso.with(mContext);
        picasso.load(R.drawable.ic_women_image).fit().into(holder.mProfileImageView);
    }

    private void bindSentMessage(MessageSentViewHolder holder, Message message) {
        holder.mMessageSentViewText.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        if (mContacts == null) {
            return 0;
        }

        Log.d(TAG, "Messages size: " + mContacts.size());
        return mContacts.size();
    }

    @Override
    public int getItemViewType(int position) {
        boolean isMessageReceived = mContacts.get(position).isMessageReceived();
        Log.d(TAG, "Messages: " + mContacts);
        if (isMessageReceived) {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        } else {
            return VIEW_TYPE_MESSAGE_SENT;
        }

    }

    public void setContact(List<Message> contacts) {
        this.mContacts = contacts;
        notifyDataSetChanged();
    }

    public void message(Message message) {
        if(mContacts == null) {
            return;
        }
        this.mContacts.add(message);
        notifyDataSetChanged();
    }
}
