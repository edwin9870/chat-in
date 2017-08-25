package com.edwin.android.chat_in.contact;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.data.entity.Contact;
import com.edwin.android.chat_in.views.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Edwin Ramirez Ventura on 8/14/2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactAdapterViewHolder> {

    public static final String TAG = ContactAdapter.class.getSimpleName();
    private ContactListener mContactListener;
    private Context mContext;
    private List<Contact> mContacts;

    public ContactAdapter(ContactListener mContactListener) {
        this.mContactListener = mContactListener;
    }

    @Override
    public ContactAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int idLayout = R.layout.item_contact_list;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(idLayout, viewGroup, false);
        return new ContactAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactAdapterViewHolder holder, int position) {
        Contact contact = mContacts.get(position);

        Picasso picasso = Picasso.with(mContext);
        picasso.load(contact.getProfileImage()).fit().into(holder.mProfileImageView);

        holder.mContactNameTextView.setText(contact.getName());
    }

    @Override
    public int getItemCount() {
        if (null == mContacts) {
            return 0;
        }

        Log.d(TAG, "mContacts size:" + mContacts.size());
        return mContacts.size();
    }

    class ContactAdapterViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        @BindView(R.id.image_view_profile)
        RoundedImageView mProfileImageView;
        @BindView(R.id.text_view_contact_name)
        TextView mContactNameTextView;

        ContactAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mContactListener.onClickContact(mContacts.get(adapterPosition));
        }
    }

    public void setContacts(List<Contact> contacts) {
        this.mContacts = contacts;
        notifyDataSetChanged();
    }
}
