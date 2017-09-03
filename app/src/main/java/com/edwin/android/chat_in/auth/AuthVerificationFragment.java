package com.edwin.android.chat_in.auth;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.edwin.android.chat_in.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AuthVerificationFragment extends Fragment {
    @BindView(R.id.edit_text_verification_code)
    EditText mVerificationCodeEditText;
    Unbinder unbinder;
    private String mParam1;


    public AuthVerificationFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment AuthVerificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AuthVerificationFragment newInstance(String param1) {
        AuthVerificationFragment fragment = new AuthVerificationFragment();
        Bundle args = new Bundle();
        args.putString(AuthVerificationActivity.BUNDLE_PHONE_NUMBER, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(AuthVerificationActivity.BUNDLE_PHONE_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_auth_verification, container,
                false);
        unbinder = ButterKnife.bind(this, view);

        

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
