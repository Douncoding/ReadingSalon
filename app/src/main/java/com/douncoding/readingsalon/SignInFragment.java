package com.douncoding.readingsalon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.douncoding.readingsalon.controller.SignInteractor;

public class SignInFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = SignInFragment.class.getSimpleName();

    EditText mEmailText;
    EditText mPassText;
    Button mLoginButton;
    Button mSinUpView;

    SignInteractor mInteractor;

    public static SignInFragment newInstance() {
        return  new SignInFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        mEmailText = (EditText)view.findViewById(R.id.login_email_edit);
        mPassText = (EditText)view.findViewById(R.id.login_password_edit);

        mLoginButton = (Button)view.findViewById(R.id.signin_btn);
        mLoginButton.setOnClickListener(this);
        mSinUpView = (Button)view.findViewById(R.id.signup_btn);
        mSinUpView.setOnClickListener(this);

        mInteractor = new SignInteractor(getContext());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_btn:
                ((SignActivity)getActivity()).showSignUpView();
                break;
            case R.id.signin_btn:
                String email = mEmailText.getText().toString();
                String password = mPassText.getText().toString();
                mInteractor.login(email, password, new SignInteractor.OnCallback() {
                    @Override
                    public void onCallback(Object obj) {
                        Boolean result = (Boolean)obj;
                        if (result) {
                            getActivity().finish();
                        } else {
                            mEmailText.setText("");
                            mPassText.setText("");

                            Toast.makeText(getActivity()
                                    , "잘못된 계정정보 입니다. 다시 입력하세요."
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }
}
