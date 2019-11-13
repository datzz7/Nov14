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

public class VerifyEmailActivity extends AppCompatActivity {

    String URL_VERIFY_EMAIL ="http://bus-ticketing.herokuapp.com/verify_email.php";
    //String URL_VERIFY_EMAIL = "http://192.168.254.194/thesis/verify_email.php";
    EditText etVerifyCode;
    String  email, firstname, lastname, password, subscription, amount, code, ptype, image;
    Button btnSubmit;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etVerifyCode = findViewById(R.id.etVerifyCode);
        btnSubmit = findViewById(R.id.btnSubmit);
        queue = Volley.newRequestQueue(this);

        btnSubmit.setEnabled(false);

        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        firstname = intent.getExtras().getString("firstname");
        lastname = intent.getExtras().getString("lastname");
        password = intent.getExtras().getString("password");
        subscription = intent.getExtras().getString("subscription");
        amount = intent.getExtras().getString("amount");
        ptype = intent.getExtras().getString("ptype");
        image = intent.getExtras().getString("image");
        Log.d("Dats",amount+ptype+""+image);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = etVerifyCode.getText().toString();
                verifyEmail();
            }
        });

        etVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnSubmit.setEnabled(!TextUtils.isEmpty(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void verifyEmail() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(VerifyEmailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        Log.d("response99", "dats");

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_VERIFY_EMAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response5",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");

                            if (result.equals("Valid")){

                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText("Verified");
                                pDialog.setContentText("You may now proceed with payment");
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        pDialog.dismissWithAnimation();
                                        Intent intent = new Intent(VerifyEmailActivity.this, Pay.class);
                                        intent.putExtra("email",email);
                                        intent.putExtra("firstname",firstname);
                                        intent.putExtra("lastname",lastname);
                                        intent.putExtra("password", password);
                                        intent.putExtra("subscription", subscription);
                                        intent.putExtra("amount", amount);
                                        intent.putExtra("ptype", ptype);
                                        intent.putExtra("image", image);
                                        startActivity(intent);
                                    }
                                });
                            }
                            if(result.equals("Code is invalid")){
                                pDialog.dismissWithAnimation();
                                etVerifyCode.setError("Invalid Code");
                                Toasty.error(VerifyEmailActivity.this, "Invalid Code", Toasty.LENGTH_SHORT).show();
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
                        Toasty.error(VerifyEmailActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("code", code);
                params.put("email", email);
                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

