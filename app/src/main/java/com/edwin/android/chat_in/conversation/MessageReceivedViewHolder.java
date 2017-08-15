package com.edwin.android.chat_in.conversation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.views.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Edwin Ramirez Ventura on 8/15/2017.
 */
public class MessageReceivedViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_view_profile)
    RoundedImageView mProfileImageView;
    @BindView(R.id.text_message_sent)
    TextView mMessageReceivedTextView;

    public MessageReceivedViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
