package com.edwin.android.chat_in.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.util.FileUtil;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Edwin Ramirez Ventura on 8/14/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatAdapterViewHolder> {

    public static final String TAG = ChatAdapter.class.getSimpleName();
    private ChatListener mChatListener;
    private Context mContext;
    private List<ConversationWrapper> mConversationDTOS;

    public ChatAdapter(ChatListener chatListener) {
        this.mChatListener = chatListener;
    }

    @Override
    public ChatAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int idLayout = R.layout.item_chat_list;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(idLayout, viewGroup, false);
        return new ChatAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatAdapterViewHolder holder, int position) {
        ConversationWrapper conversation = mConversationDTOS.get(position);

        Picasso picasso = Picasso.with(mContext);

        Log.d(TAG, "conversation contact: "+ conversation.getContact());
        if(conversation.getContact().getProfileImagePath() == null || conversation.getContact().getProfileImagePath().isEmpty()) {
            picasso.load(R.drawable.ic_faceless_man).fit().into(holder.mProfileImageView);
        } else {
            picasso.load(FileUtil.getImageFile(mContext, conversation.getContact().getProfileImagePath()))
                    .fit().into(holder.mProfileImageView);
        }

        if(conversation.getContact().getUserName() != null && !conversation.getContact().getUserName().isEmpty()) {
            holder.mContactNameTextView.setText(conversation.getContact().getUserName());
        } else {
            holder.mContactNameTextView.setText(String.valueOf(conversation.getContact().getNumber()));
        }

        final Date messageDate = new Date(conversation.getConversation().getMessageDate());
        Log.d(TAG, "Message date: " + messageDate);
        CharSequence dateMessage;
        if(DateUtils.isToday(messageDate.getTime())) {
            dateMessage = DateFormat.format(mContext.getString(R.string.time_format), messageDate);
        } else {
            dateMessage = DateFormat.format(mContext.getString(R.string.date_format), messageDate);
        }
        holder.mContactMessageDateTextView.setText(dateMessage);
        holder.mContactMessageTextView.setText(conversation.getConversation().getMessage());

    }

    @Override
    public int getItemCount() {
        if (null == mConversationDTOS) {
            Log.d(TAG, "mConversationDTOS is null at getItemCount");
            return 0;
        }

        Log.d(TAG, "mConversationDTOS size:" + mConversationDTOS.size());
        return mConversationDTOS.size();
    }

    class ChatAdapterViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {

        @BindView(R.id.image_view_profile)
        ImageView mProfileImageView;
        @BindView(R.id.text_view_contact_name)
        TextView mContactNameTextView;
        @BindView(R.id.text_view_contact_message_date)
        TextView mContactMessageDateTextView;
        @BindView(R.id.text_view_contact_message)
        TextView mContactMessageTextView;

        ChatAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mChatListener.onClickContact(mConversationDTOS.get(adapterPosition).getContact().getId());
        }
    }


    public void setChats(List<ConversationWrapper> conversations) {
        this.mConversationDTOS = conversations;
        notifyDataSetChanged();
    }
}
