package com.edwin.android.chat_in.auth;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.edwin.android.chat_in.R;

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
    private String mCountryValueSelected;

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

        if (mPhoneNumberEditText.getText() == null || mPhoneNumberEditText.getText().length() < 5) {
            Toast.makeText(getActivity(), R.string.phone_number_required, Toast.LENGTH_LONG).show();
            return;
        }

        if(!TextUtils.isDigitsOnly(mPhoneNumberEditText.getText())) {
            Toast.makeText(getActivity(), R.string.only_digits_allowance, Toast.LENGTH_LONG).show();
            return;
        }


        final String phoneNumber = mCountryValueSelected + mPhoneNumberEditText.getText()
                .toString();
        Log.d(TAG, "Opening next windows, phone number: " + phoneNumber);
        final Intent intent = new Intent(getActivity(), AuthVerificationActivity.class);
        intent.putExtra(BUNDLE_PHONE_NUMBER, phoneNumber);
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
