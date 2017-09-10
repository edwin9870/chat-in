package com.edwin.android.chat_in.conversation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.chat.ConversationWrapper;
import com.edwin.android.chat_in.data.dto.ConversationDTO;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.util.FileUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Edwin Ramirez Ventura on 8/15/2017.
 */

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_MESSAGE_RECEIVED = 5454;
    public static final int VIEW_TYPE_MESSAGE_SENT = 8877;
    public static final String TAG = ConversationAdapter.class.getSimpleName();
    private Context mContext;
    private List<ConversationWrapper> mConversations;

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
        final ConversationWrapper conversationWrapper = mConversations.get(position);
        Log.d(TAG, "holder.getItemViewType(): " + holder.getItemViewType());
        if(holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED) {
            bindReceivedMessage((MessageReceivedViewHolder) holder, conversationWrapper);
        } else {
            bindSentMessage((MessageSentViewHolder) holder, conversationWrapper);
        }

    }

    private void bindReceivedMessage(MessageReceivedViewHolder holder, ConversationWrapper conversationWrapper) {
        holder.mMessageReceivedTextView.setText(conversationWrapper.getConversation().getMessage());

        Picasso picasso = Picasso.with(mContext);
        if(conversationWrapper.getContact().getProfileImagePath() == null ||
                conversationWrapper.getContact().getProfileImagePath().isEmpty()) {
            picasso.load(R.drawable.ic_faceless_man).fit().into(holder.mProfileImageView);
        } else {
            picasso.load(FileUtil.getImageFile(mContext, conversationWrapper.getContact().getProfileImagePath()))
                    .fit()
                    .into(holder.mProfileImageView);
        }

        final Date messageDate = new Date(conversationWrapper.getConversation().getMessageDate());
        String messageDateFormated = DateFormat.format(mContext.getString(R.string.message_date), messageDate).toString();
        holder.mMessageReceivedDateTextView.setText(messageDateFormated);

    }

    private void bindSentMessage(MessageSentViewHolder holder, ConversationWrapper conversationWrapper) {
        holder.mMessageSentViewText.setText(conversationWrapper.getConversation().getMessage());
    }

    @Override
    public int getItemCount() {
        if (mConversations == null) {
            return 0;
        }

        Log.d(TAG, "Messages size: " + mConversations.size());
        return mConversations.size();
    }

    @Override
    public int getItemViewType(int position) {
        ConversationDTO conversation = mConversations.get(position).getConversation();
        Log.d(TAG, "Messages: " + mConversations);
        if (conversation.getRecipientContactId() == ContactRepository.OWNER_CONTACT_ID) {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        } else {
            return VIEW_TYPE_MESSAGE_SENT;
        }

    }

    public void setConversations(List<ConversationWrapper> conversations) {
        this.mConversations = conversations;
        notifyDataSetChanged();
    }

    public void addConversation(ConversationWrapper conversation) {
        if(mConversations  == null) {
            mConversations = new ArrayList<>();
        }
        mConversations.add(conversation);
        notifyDataSetChanged();
    }
}
