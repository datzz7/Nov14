package com.thesis.busticketing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class ForgotPasswordActivity extends AppCompatActivity {

    String URL_FORGOTPASSWORD = "http://bus-ticketing.herokuapp.com/forgot_password.php";
    //String URL_FORGOTPASSWORD = "http://192.168.43.32/thesis/forgot_password.php";
    Button btnForgotPassword;
    EditText etForgotPasswordEmail;
    String email;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue = Volley.newRequestQueue(this);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        etForgotPasswordEmail = findViewById(R.id.etForgotPasswordEmail);

        btnForgotPassword.setEnabled(false);


        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etForgotPasswordEmail.getText().toString();
                forgotPassword();
            }
        });

        etForgotPasswordEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                btnForgotPassword.setEnabled(!TextUtils.isEmpty(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    private void forgotPassword() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(ForgotPasswordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        Log.d("response99", "dats");

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_FORGOTPASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response5",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");

                            if(result.equals("Sent")){

                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText("Success!");
                                pDialog.setContentText("A verification code has been sent to your email.");
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        pDialog.dismissWithAnimation();
                                        Intent intent = new Intent(ForgotPasswordActivity.this, UpdatePasswordActivity.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);
                                    }
                                });
                            }

                            if(result.equals("Does Not Exists")){

                                pDialog.dismissWithAnimation();
                                Toasty.error(ForgotPasswordActivity.this,"Email does not exists",Toasty.LENGTH_SHORT).show();
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
                        Toasty.error(ForgotPasswordActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }
}
