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

public class SignUpFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = SignUpFragment.class.getSimpleName();

    EditText mNameText;
    EditText mEmailText;
    EditText mPass1Text;
    EditText mPass2Text;
    Button mSignUpButton;

    SignInteractor mInteractor;

    public static SignUpFragment newInstance() {
        return  new SignUpFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        mNameText = (EditText)view.findViewById(R.id.username);
        mEmailText = (EditText)view.findViewById(R.id.email);
        mPass1Text = (EditText)view.findViewById(R.id.password1);
        mPass2Text =  (EditText)view.findViewById(R.id.password2);

        mSignUpButton = (Button)view.findViewById(R.id.signup_btn);
        mSignUpButton.setOnClickListener(this);

        mInteractor = new SignInteractor(getContext());
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_btn:
                String name = mNameText.getText().toString();
                String email = mEmailText.getText().toString();
                String pass1 = mPass1Text.getText().toString();
                String pass2 = mPass2Text.getText().toString();

                if (pass1.equals(pass2)) {
                    mInteractor.signup(name, email, pass1, new SignInteractor.OnCallback() {
                        @Override
                        public void onCallback(Object obj) {
                            Boolean result = (Boolean)obj;
                            if (result) {
                                Toast.makeText(getActivity()
                                        , "회원가입을 축합니다. 로그인 하세요."
                                        , Toast.LENGTH_SHORT).show();
                                ((SignActivity) getActivity()).showSignInView();
                            } else {
                                Toast.makeText(getActivity()
                                        , "이미 가입된 정보입니다."
                                        , Toast.LENGTH_SHORT).show();

                                mNameText.setText("");
                                mEmailText.setText("");
                                mPass1Text.setText("");
                                mPass2Text.setText("");
                            }
                        }
                    });
                } else {
                    mPass1Text.setText("");
                    mPass2Text.setText("");

                    Toast.makeText(getActivity()
                            , "패스워드를 확인하세요."
                            , Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
