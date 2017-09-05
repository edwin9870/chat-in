package com.edwin.android.chat_in.auth;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.util.AuthUtil;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.edwin.android.chat_in.auth.AuthVerificationActivity.BUNDLE_PHONE_NUMBER;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AuthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AuthFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = AuthFragment.class.getSimpleName();
    public static final String UNDEFINED = "N/A";
    @BindView(R.id.edit_text_phone_number)
    EditText mPhoneNumberEditText;
    Unbinder unbinder;
    @BindView(R.id.spinner_country)
    Spinner mCountrySpinner;
    @BindView(R.id.button_next_step_phone_number)
    Button mNextStepPhoneNumberButton;
    @BindView(R.id.fragment_auth_body)
    LinearLayout mAuthBodyLinearLayout;
    @BindView(R.id.progress_bar_auth)
    ProgressBar mAuthProgressBar;
    private String mCountryValueSelected;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacksAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    public AuthFragment() {
        // Required empty public constructor
    }

    public static AuthFragment newInstance() {
        AuthFragment fragment = new AuthFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        unbinder = ButterKnife.bind(this, view);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_country_number_items, R.layout.item_spinner);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCountrySpinner.setAdapter(typeAdapter);

        mCountrySpinner.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.button_next_step_phone_number)
    public void onViewClicked() {
        Log.d(TAG, "phone number clicked");
        if (mCountryValueSelected == null || mCountryValueSelected.isEmpty() ||
                mCountryValueSelected.equals(UNDEFINED)) {
            Toast.makeText(getActivity(), R.string.country_code_required, Toast.LENGTH_LONG).show();
            return;
        }

        if (mPhoneNumberEditText.getText() == null || mPhoneNumberEditText.getText().length() < 2) {
            Toast.makeText(getActivity(), R.string.phone_number_required, Toast.LENGTH_LONG).show();
        }

        final String phoneNumber = mCountryValueSelected + mPhoneNumberEditText.getText()
                .toString();
        Log.d(TAG, "Opening next windows, phone number: " + phoneNumber);
        final Intent intent = new Intent(getActivity(), AuthVerificationActivity.class);
        intent.putExtra(BUNDLE_PHONE_NUMBER, phoneNumber);
        intent.putExtra(AuthVerificationActivity.BUNDLE_TOKEN_VERIFICATION, mResendToken);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        final String[] countryValues = getResources().getStringArray(R.array
                .spinner_country_number_values);
        mCountryValueSelected = countryValues[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
