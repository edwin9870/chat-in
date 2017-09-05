package com.edwin.android.chat_in.auth;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.util.AuthUtil;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AuthVerificationFragment extends Fragment {

    public static final String TAG = AuthVerificationFragment.class.getSimpleName();
    @BindView(R.id.edit_text_verification_code)
    EditText mVerificationCodeEditText;
    Unbinder unbinder;
    @BindView(R.id.button_verify_code)
    Button mVerifyCodeButton;
    @BindView(R.id.button_resend_verification_code)
    Button mResendVerificationCodeButton;
    private String phoneNumber;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private OnVerificationStateChangedCallbacks mCallbacksAuth;


    public AuthVerificationFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param phoneNumber Parameter 1.
     * @return A new instance of fragment AuthVerificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AuthVerificationFragment newInstance(String phoneNumber) {
        AuthVerificationFragment fragment = new AuthVerificationFragment();
        Bundle args = new Bundle();
        args.putString(AuthVerificationActivity.BUNDLE_PHONE_NUMBER, phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phoneNumber = getArguments().getString(AuthVerificationActivity.BUNDLE_PHONE_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_auth_verification, container,
                false);
        unbinder = ButterKnife.bind(this, view);



        mCallbacksAuth = new
                OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                        Log.d(TAG, "Calling signInWithPhoneAuthCredential from " +
                                "onVerificationCompleted");
                        AuthUtil.signInWithPhoneAuthCredential(getActivity(), phoneAuthCredential);

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.e(TAG, "onVerificationFailed", e);
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        Log.d(TAG, "onCodeSent.verificationId:" + verificationId);
                        showManualVerificationView();

                        mVerificationId = verificationId;
                        mResendToken = token;
                    }
                };

        /*if (mVerificationInProgress == null || mVerificationInProgress) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber, 2, TimeUnit.MINUTES, getActivity(),
                    mCallbacksAuth);
            mVerificationInProgress = true;
        }*/

        return view;
    }

    private void showManualVerificationView() {
        mVerificationCodeEditText.setVisibility(View.VISIBLE);
        mVerifyCodeButton.setVisibility(View.VISIBLE);
        mResendVerificationCodeButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @OnClick({R.id.button_verify_code, R.id.button_resend_verification_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.button_verify_code:

                if (mVerificationCodeEditText.getText() == null ||
                        mVerificationCodeEditText.getText().length() < 1) {
                    Toast.makeText(getActivity(), "Code field is required", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                final String verificationCode = mVerificationCodeEditText.getText().toString();
                Log.d(TAG, "mVerificationCode: " + verificationCode);
                final PhoneAuthCredential credential = PhoneAuthProvider.getCredential
                        (mVerificationId, verificationCode);
                Log.d(TAG, "Calling signInWithPhoneAuthCredential with manual credential");
                AuthUtil.signInWithPhoneAuthCredential(getActivity(), credential);
                break;
            case R.id.button_resend_verification_code:
                Log.d(TAG, "Re-send code button clicked");
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber, 2, TimeUnit.MINUTES, getActivity(),
                        mCallbacksAuth, mResendToken);
                break;
        }
    }



}
