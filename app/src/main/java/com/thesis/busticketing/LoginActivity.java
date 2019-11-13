package com.thesis.busticketing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

   TextInputLayout textinput_email,textinput_password;
   TextView tvLogRegister, tvForgotPassword;
   EditText etEmail, etPassword;
   String email, password;
   Button btnLogin;
   //final String URL_LOGIN = "http://192.168.254.194/thesis/users_login.php";
   final String URL_LOGIN = "http://bus-ticketing.herokuapp.com/users_login.php";
   RequestQueue queue;
   int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        textinput_password = findViewById(R.id.textinput_password);
        textinput_email = findViewById(R.id.textinput_email);

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvLogRegister = findViewById(R.id.tvLogRegister);
        tvLogRegister.setClickable(true);
        tvForgotPassword.setClickable(true);

        btnLogin.setOnClickListener(this);
        tvLogRegister.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);

 }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:

                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                i=2;

                if(email.isEmpty()){
                    textinput_email.setError("Please insert username");
                    i--;
                }
                if(!email.isEmpty()){
                    textinput_email.setError(null);
                }
                if(password.isEmpty()){
                    textinput_password.setError("Please insert password");
                    i--;
                }
                if(!password.isEmpty()){
                    textinput_password.setError(null);

                }
                if(i==2){
                    Login(email,password);
                }
                break;

            case R.id.tvLogRegister:

                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Create account?")
                        .setConfirmText("Create")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
                break;

            case R.id.tvForgotPassword:

                Intent intent = new Intent(LoginActivity.this , ForgotPasswordActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        menu.findItem(R.id.Logout).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.Exit:
                this.finishAffinity();
                break;
            case R.id.Bus_Information:
                Intent iintent = new Intent(LoginActivity.this, BusInformationActivity.class);
                startActivity(iintent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void Login(final String email, final String password) {
      //  Log.d("Response", "login");
        final SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");

                    //Log.d("Message", "" +jsonArray);
                    if(success.equals("1")){
                        for (int i = 0; i<jsonArray.length(); i++){

                            JSONObject object =jsonArray.getJSONObject(i);

                            String firstname = object.getString("firstname").trim();
                            String lastname = object.getString("lastname").trim();
                            String email = object.getString("email").trim();
                            String id = object.getString("id").trim();
                            String photo = object.getString("photo").trim();

                            final Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                            intent.putExtra("firstname",firstname);
                            intent.putExtra("lastname",lastname);
                            intent.putExtra("email",email);
                            intent.putExtra("id", id);
                            intent.putExtra("photo", photo);


                            pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            pDialog.setTitleText("Success!");
                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    finish();
                                    pDialog.dismissWithAnimation();
                                    startActivity(intent);
                                }
                            });


                        }
                    }
                    else{
                        pDialog.dismissWithAnimation();
                        Toasty.error(LoginActivity.this, "Incorrect Email or password!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    pDialog.dismissWithAnimation();
                    e.printStackTrace();
                    Toasty.error(LoginActivity.this, "Incorrect Email or password!", Toast.LENGTH_SHORT).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismissWithAnimation();
                        Toasty.error(LoginActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}




