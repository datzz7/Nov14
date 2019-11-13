package com.thesis.busticketing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class UpdatePasswordActivity extends AppCompatActivity {

    //String URL_NEW_PASSWORD= "http://192.168.43.32/thesis/change_password.php";
    String URL_NEW_PASSWORD= "http://bus-ticketing.herokuapp.com/change_password.php";
    EditText etNewPassword, etReNewPassword, etCode;
    TextInputLayout input_password, input_repassword;
    Button btnChangePassword;
    String newpassword, renewpassword, code, email;
    RequestQueue queue;
    int formsuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue = Volley.newRequestQueue(this);
        etNewPassword = findViewById(R.id.etNewPassword);
        etReNewPassword = findViewById(R.id.etReNewPassword);
        etCode = findViewById(R.id.etCode);
        input_password = findViewById(R.id.input_password);
        input_repassword = findViewById(R.id.input_repassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        Intent intent = getIntent();
        email = intent.getExtras().getString("email");

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formsuccess =2;

                code = etCode.getText().toString();
                newpassword = etNewPassword.getText().toString();
                renewpassword = etReNewPassword.getText().toString();

                if(code.equals("")){
                    etCode.setError("Code Required");
                    formsuccess--;
                }

                if(newpassword.length()<6){
                    input_password.setError("Password must be 6 characters or more!");
                    formsuccess--;
                }
                else{
                    input_password.setError(null);
                }

                if(!renewpassword.equals(newpassword)){

                    input_repassword.setError("Password does not match!");
                    formsuccess--;
                }else {
                    input_repassword.setError(null);
                }

                if(formsuccess==2){
                    changePassword();
                }

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private void changePassword() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_NEW_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response5",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");
                            Log.d("response99", result);

                            if(result.equals("Updated")){

                                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setCancelable(false);
                                    pDialog.setTitleText("Password Changed!");
                                    pDialog.setContentText("Log in now using your new password.");
                                    pDialog.setConfirmText("Login");
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    pDialog.dismissWithAnimation();
                                                    Intent intent = new Intent(UpdatePasswordActivity.this,LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                    pDialog.show();
                            }

                            if(result.equals("Code is invalid")){
                                pDialog.dismissWithAnimation();
                                etCode.setError("Code is Invalid");
                                Toasty.error(UpdatePasswordActivity.this, "Code is invalid!", Toasty.LENGTH_SHORT).show();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismissWithAnimation();
                        Toasty.error(UpdatePasswordActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("password", newpassword);
                params.put("code", code);
                params.put("email", email);
                return params;
            }
        };
        queue.add(postRequest);
    }
}
