package com.thesis.busticketing;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.WriterException;
import com.thesis.busticketing.ui.AccountFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //final String prices_url = "http://192.168.254.194/thesis/prices.php";
    //final String url = "http://192.168.254.194/thesis/check_email.php";
    final String url = "http://bus-ticketing.herokuapp.com/check_email.php";
    final String prices_url = "http://bus-ticketing.herokuapp.com/prices.php";
    TextInputLayout input_email, input_firstname, input_lastname, input_password, input_repassword;
    EditText etFirstname,etLastname, etPassword, etRepassword, etEmail;
    Button btnSubmit, btnCancel;
    TextView tvTerms;
    String firstname, lastname, password, repassword, email;
    CheckBox chbIagree;
    RequestQueue queue;
    RadioGroup radioGroup;
    RadioButton S7Days, S15Days,S30Days;
    String subscription="";
    String type,value="";
    String amount,ptype="";
    String Days7,Days15,Days30,Type7,Type15,Type30;
    String total, image;
    TextView tvUpload;
    int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    CircleImageView ivProfile;
    String picture;
    int formsuccess, agreement = 0;
    int verifyemail = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue = Volley.newRequestQueue(this);
        //inputlayout
        input_email = findViewById(R.id.input_email);
        input_firstname = findViewById(R.id.input_firstname);
        input_lastname = findViewById(R.id.input_lastname);
        input_password = findViewById(R.id.input_password);
        input_repassword = findViewById(R.id.input_repassword);
        tvUpload = findViewById(R.id.tvUpload);
        ivProfile = findViewById(R.id.ivProfile);

        tvUpload.setClickable(true);
        tvUpload.setOnClickListener(this);

        etEmail = findViewById(R.id.etRegEmail);
        etFirstname = findViewById(R.id.etRegFirstname);
        etLastname = findViewById(R.id.etRegLastname);
        etPassword = findViewById(R.id.etRegPassword);
        etRepassword = findViewById(R.id.etRegRePassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        S7Days = findViewById(R.id.rb7Days);
        S15Days = findViewById(R.id.rb15Days);
        S30Days = findViewById(R.id.rb30Days);
        chbIagree = findViewById(R.id.chbIagree);
        tvTerms = findViewById(R.id.tvTerms);
        radioGroup = findViewById(R.id.rgSub);


        tvTerms.setClickable(true);
        tvTerms.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        prices();

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(isValidEmail(etEmail.getText().toString().trim())&&editable.length()>0){
                    verifyemail=1;
                }else{
                    etEmail.setError("Invalid Email Address");
                    verifyemail--;
                }
            }
        });

    }

    private void prices() {

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, prices_url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONArray("prices");
                            Log.d("Response22", ""+result);

                            for(int x=0;x<result.length();x++) {
                                JSONObject collegeData = result.getJSONObject(x);
                                type = collegeData.getString("type");
                                value = collegeData.getString("price");
                                Log.d("Response24", value+"ioi");

                                if(type.contains("7")){
                                    Days7 = value;
                                    Type7 = type;
                                    S7Days.setText("7Days for Php " + value);
                                }

                                 if(type.contains("15")){
                                     Days15 = value;
                                     Type15 = type;
                                    S15Days.setText("15Days for Php " +value);
                                }

                                 if(type.contains("30")){
                                     Days30 = value;
                                    Type30 = type;
                                    S30Days.setText("30Days for Php " +value);
                                }

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return super.getBody();
            }
        };

        queue.add(getRequest);

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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
            case R.id.Bus_Information:
                Intent intent = new Intent(RegisterActivity.this, BusInformationActivity.class);
                startActivity(intent);
                break;

            case R.id.Exit:
                this.finishAffinity();
                break;
        }

        return super.onOptionsItemSelected(item);
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

    private void toRegister() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Please wait while sending verification code");
        pDialog.setCancelable(true);
        pDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response5",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            picture = jsonObject.getString("picture");
                            Log.d("Response5", message);
                            Log.d("Response5", picture);

                            if(message.equals("1")){

                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setTitleText("Email is already taken!");
                                    pDialog.setConfirmText("Ok");
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            pDialog.dismissWithAnimation();
                                        }
                                    });
                            }
                            else if(message.equals("0")) {

                                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setTitleText("Verify Email to proceed");
                                    pDialog.setContentText("A verification code has been sent to your email");
                                    //.setConfirmText("Delete!")
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            Intent intent = new Intent(RegisterActivity.this, VerifyEmailActivity.class);
                                            intent.putExtra("email",email);
                                            intent.putExtra("firstname",firstname);
                                            intent.putExtra("lastname",lastname);
                                            intent.putExtra("password", password);
                                            intent.putExtra("subscription", subscription);
                                            intent.putExtra("amount", amount);
                                            intent.putExtra("ptype",ptype);
                                            intent.putExtra("image",picture);

                                            Log.d("Response23,", amount+ptype);
                                            startActivity(intent);

                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pDialog.dismissWithAnimation();
                        Toasty.error(RegisterActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("image", image);
//                params.put("firstname", firstname);
//                params.put("lastname", lastname);
//                params.put("password", password);
//                params.put("subscription", subscription);


                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }


    @Override
    public void onClick(View view) {
//        SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
//        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//        pDialog.setTitleText("Loading ...");
//        pDialog.setCancelable(true);
//        pDialog.show();

        switch(view.getId()){
            case R.id.btnSubmit:
                formsuccess =5;
                agreement =1;

                email = etEmail.getText().toString();
                firstname = etFirstname.getText().toString();
                lastname = etLastname.getText().toString();
                password = etPassword.getText().toString();
                repassword = etRepassword.getText().toString();
                Log.d("Formsucces", ""+formsuccess);


                if(email.equals("")){
                    etEmail.setError("Field Required.");
                    formsuccess--;
                }
                if(firstname.equals("")){
                    etFirstname.setError("Field Required.");
                    formsuccess--;
                }
                if(lastname.equals("")){
                    etLastname.setError("Field Required.");
                    formsuccess--;
                }
                if(password.length()<6){
                    input_password.setError("Password must be 6 characters or more!");
                    formsuccess--;
                }
                else{
                    input_password.setError(null);
                }

                if(!repassword.equals(password)){
                    input_repassword.setError("Password does not match!");
                    formsuccess--;
                }
                else{
                    input_repassword.setError(null);
                }

                if(!chbIagree.isChecked()){
                    tvTerms.setError("Please click i agree to proceed.");
                    tvTerms.requestFocus();
                    agreement--;

                    chbIagree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if ( isChecked ) {
                                tvTerms.setError(null);
                            } else {
                                tvTerms.setError("Please click i agree to proceed.");
                                tvTerms.requestFocus();

                            }

                        }
                    });
                }

                if(formsuccess>=0 && formsuccess<4){

                    Toasty.error(RegisterActivity.this,"Please fill all fields.", Toasty.LENGTH_SHORT).show();

                }

                if(verifyemail<1){
                    Toasty.error(RegisterActivity.this, "Please input a valid email address.", Toasty.LENGTH_SHORT).show();
                }

                if(formsuccess==5 && agreement==1 && verifyemail==1) {

                    if (radioGroup.getCheckedRadioButtonId() == S7Days.getId()) {
                        subscription = "7Days";
                        amount= Days7;
                        ptype = Type7;
                        toRegister();
                    } else if (radioGroup.getCheckedRadioButtonId() == S15Days.getId()) {
                        subscription = "15Days";
                        amount= Days15;
                        ptype = Type15;
                        toRegister();
                    } else if (radioGroup.getCheckedRadioButtonId() == S30Days.getId()) {
                        subscription = "30Days";
                        amount= Days30;
                        ptype = Type30;
                        toRegister();
                    }
                }
                break;

            case R.id.tvUpload:
                showFileChooser();
                break;

            case R.id.tvTerms:
              Intent intent = new Intent(this, TermsAndConditions.class);
                startActivity(intent);
                break;

            case R.id.btnCancel:
           //     hasImage(ivProfile);
                this.finish();
                break;
    }

}
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                ivProfile.setImageBitmap(bitmap);
                image = getStringImage(bitmap);
                Log.d("Dats", image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

     boolean hasImage(@NonNull CircleImageView view){
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if(hasImage && (drawable instanceof BitmapDrawable)){
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
            Toast.makeText(this, "Naa", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "WaLA", Toast.LENGTH_SHORT).show();
        }
        return hasImage;
    }


}