package com.edwin.android.chat_in.auth;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.mainview.MainViewActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AuthVerificationFragment extends Fragment {


    public static final String VERIFICATION_IN_PROGRESS = "VERIFICATION_IN_PROGRESS";
    public static final String TAG = AuthVerificationFragment.class.getSimpleName();
    @BindView(R.id.edit_text_verification_code)
    EditText mVerificationCodeEditText;
    Unbinder unbinder;
    @BindView(R.id.button_verify_number)
    Button mVerifyNumberButton;
    @BindView(R.id.button_resend_verification_code)
    Button mResendVerificationCodeButton;
    private String phoneNumber;
    private Boolean mVerificationInProgress;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private OnVerificationStateChangedCallbacks mCallbacksAuth;


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
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        if (savedInstanceState.containsKey(VERIFICATION_IN_PROGRESS)) {
            mVerificationInProgress = savedInstanceState.getBoolean(VERIFICATION_IN_PROGRESS);
        }
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
                        mVerificationInProgress = false;
                        Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                        Log.d(TAG, "Calling signInWithPhoneAuthCredential from " +
                                "onVerificationCompleted");
                        signInWithPhoneAuthCredential(phoneAuthCredential);

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

        if (mVerificationInProgress == null || mVerificationInProgress) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber, 2, TimeUnit.MINUTES, getActivity(),
                    mCallbacksAuth);
            mVerificationInProgress = true;
        }

        return view;
    }

    private void showManualVerificationView() {
        mVerificationCodeEditText.setVisibility(View.VISIBLE);
        mVerifyNumberButton.setVisibility(View.VISIBLE);
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
        outState.putBoolean(VERIFICATION_IN_PROGRESS, mVerificationInProgress);
    }

    @OnClick({R.id.button_verify_number, R.id.button_resend_verification_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.button_verify_number:

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
                signInWithPhoneAuthCredential(credential);
                break;
            case R.id.button_resend_verification_code:
                Log.d(TAG, "Re-send code button clicked");
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber, 2, TimeUnit.MINUTES, getActivity(),
                        mCallbacksAuth, mResendToken);
                mVerificationInProgress = true;
                break;
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.d(TAG, "Calling signInWithPhoneAuthCredential");
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();
                        final Intent intent = new Intent(getActivity(), MainViewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof
                                FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                });
    }


}
