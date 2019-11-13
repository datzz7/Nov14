package com.thesis.busticketing.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.thesis.busticketing.LoginActivity;
import com.thesis.busticketing.R;
import com.thesis.busticketing.RegisterActivity;
import com.thesis.busticketing.UserHomeActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AccountFragment extends Fragment implements View.OnClickListener {

    TextView tvCreateAccount;
    Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_account, container, false);

        btnLogin = v.findViewById(R.id.btnLogin);
        tvCreateAccount = v.findViewById(R.id.tvCreateAccount);
        tvCreateAccount.setClickable(true);
        tvCreateAccount.setOnClickListener(this);
        btnLogin.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.tvCreateAccount:
                Intent next = new Intent(getActivity(), RegisterActivity.class);
                startActivity(next);
                break;

            case R.id.btnLogin:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
        }

    }
}
