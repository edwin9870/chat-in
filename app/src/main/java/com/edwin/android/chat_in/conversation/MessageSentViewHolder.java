package com.edwin.android.chat_in.conversation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edwin.android.chat_in.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Edwin Ramirez Ventura on 8/15/2017.
 */
public class MessageSentViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.text_message_sent)
    TextView mMessageSentViewText;
    @BindView(R.id.text_message_sent_date)
    TextView mMessageSentDateTextView;

    public MessageSentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
