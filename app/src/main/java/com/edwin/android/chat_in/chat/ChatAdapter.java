package com.edwin.android.chat_in.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.entity.dto.Chat;
import com.squareup.picasso.Picasso;

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
    private List<Chat> mChats;

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
        Chat contact = mChats.get(position);

        Picasso picasso = Picasso.with(mContext);
        picasso.load(contact.getProfileImage()).fit().into(holder.mProfileImageView);

        holder.mContactNameTextView.setText(contact.getUserName());

        CharSequence dateMessage = DateFormat.format(mContext.getString(R.string.time_format), contact.getMessageDate());
        holder.mContactMessageDateTextView.setText(dateMessage);
        holder.mContactMessageTextView.setText(contact.getLastMessage());

    }

    @Override
    public int getItemCount() {
        if (null == mChats) {
            Log.d(TAG, "mChats is null at getItemCount");
            return 0;
        }

        Log.d(TAG, "mChats size:" + mChats.size());
        return mChats.size();
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
            mChatListener.onClickContact(mChats.get(adapterPosition));
        }
    }


    public void setChats(List<Chat> chats) {
        this.mChats = chats;
        notifyDataSetChanged();
    }
}
