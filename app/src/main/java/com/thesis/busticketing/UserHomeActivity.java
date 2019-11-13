package com.thesis.busticketing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.WriterException;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserHomeActivity extends AppCompatActivity {

    final Context context = this;
    TextView tvId, tvEmail, tvFullname, tvSubno, tvDateSubscribed, tvValidity, tvValidityUntil, tvStatus;
    Button btnUpdate, btn7Day, btn15Day, btn30Day ;
    RequestQueue queue;
    String id,email,firstname,lastname,subno, date_subscribed, validity, qr_code, photo;
    String status, days_left;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    ImageView ivQrcode;
    CircleImageView ivProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);


        ivProfile = findViewById(R.id.ivProfile);
        ivQrcode = findViewById(R.id.ivQrcode);
        tvId = findViewById(R.id.tvId);
        tvEmail = findViewById(R.id.tvEmail);
        tvFullname = findViewById(R.id.tvFullname);
        tvSubno = findViewById(R.id.tvSubno);
        tvDateSubscribed = findViewById(R.id.tvDateSubscribed);
        tvValidityUntil = findViewById(R.id.tvValidityUntil);
        tvValidity = findViewById(R.id.tvValidity);
        tvStatus = findViewById(R.id.tvStatus);
        btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setVisibility(View.INVISIBLE);

        queue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        email = intent.getExtras().getString("email");
        firstname = intent.getExtras().getString("firstname");
        lastname = intent.getExtras().getString("lastname");
        photo = intent.getExtras().getString("photo");
        Picasso.get().load(photo).into(ivProfile);
        Log.d("Dats",photo);

        fetchData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(status.equals(("Active"))){

                    new SweetAlertDialog(UserHomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("You still have a valid ticket!")
                            .setContentText("You still have "+days_left+" day(s) left.")
                            .setConfirmText("Proceed")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    Intent intent = new Intent(UserHomeActivity.this, UpdateSubscriptionActivity.class);
                                    intent.putExtra("id", id);
                                    intent.putExtra("email", email);
                                    intent.putExtra("firstname", firstname);
                                    intent.putExtra("lastname", lastname);
                                    intent.putExtra("photo", photo);
                                    startActivity(intent);

                                }
                            })
                            .setCancelText("Cancel")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
                else if(status.equals("Expired")){

                    new SweetAlertDialog(UserHomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Your ticket validity has expired!")
                            .setContentText("Update ticket now?")
                            .setConfirmText("Update")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    Intent intent = new Intent(UserHomeActivity.this, UpdateSubscriptionActivity.class);
                                    intent.putExtra("id", id);
                                    intent.putExtra("email", email);
                                    intent.putExtra("firstname", firstname);
                                    intent.putExtra("lastname", lastname);
                                    intent.putExtra("photo", photo);
                                    startActivity(intent);

                                }
                            })
                            .setCancelText("Cancel")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();

                }

            }
        });
    }


    @Override
    public void onBackPressed(){

        return;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        menu.findItem(R.id.Exit).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.Bus_Information:
                Intent intent = new Intent(UserHomeActivity.this, BusInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.Logout:
                new SweetAlertDialog(UserHomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure you want to log out?")
                        .setConfirmText("Logout!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setVisibility(View.GONE);
                                sDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                sDialog.setTitleText("Logged out.");
                                sDialog.setConfirmText("Ok");
                                sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        Intent intent = new Intent(UserHomeActivity.this, MainActivity.class);
                                        finish();
                                        startActivity(intent);

                                    }
                                });

                            }
                        })
                        .setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchData() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        Log.d("Response2", "niagi");
        //String URL_SUBSCRIPTION ="http://192.168.254.194/thesis/my_subscription.php?id="+id;
        String URL_SUBSCRIPTION ="http://bus-ticketing.herokuapp.com/my_subscription.php?id="+id;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL_SUBSCRIPTION, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response2", ""+response);
                        String value="";

                        try {
                            days_left = response.getString("days_left");
                            status = response.getString("status");
                            JSONArray result = response.getJSONArray("subscriptions");
                            Log.d("Response11", status);
                            if(days_left.equals("0")&& status.equals("Active")){
                                days_left="30";
                            }

                            for(int x=0;x<result.length();x++) {
                                JSONObject collegeData = result.getJSONObject(x);
                                subno = collegeData.getString("subno");
                                date_subscribed = collegeData.getString("date_subscribed");
                                validity = collegeData.getString("validity");
                                qr_code = collegeData.getString("qr_code");


                                //tvId.setText("Id no: "+id);

                                tvEmail.setText("Email: "+ email);
                                tvFullname.setText("Name: " +firstname+" "+lastname);
                                btnUpdate.setVisibility(View.VISIBLE);

                                pDialog.dismissWithAnimation();

                                value = qr_code;

                                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                                Display display = manager.getDefaultDisplay();
                                Point point = new Point();
                                display.getSize(point);
                                int width = point.x;
                                int height = point.y;
                                int smallerDimension = width < height ? width : height;
                                smallerDimension = smallerDimension * 3 / 4;

                                qrgEncoder = new QRGEncoder(
                                        value, null,
                                        QRGContents.Type.TEXT,
                                        smallerDimension);

                                try {
                                    bitmap = qrgEncoder.encodeAsBitmap();
                                    ivQrcode.setImageBitmap(bitmap);
                                } catch (WriterException e) {
                                    Log.d("response", e.toString());
                                }

                                tvSubno.setText("Subscription no: "+subno);
                                tvDateSubscribed.setText("Date Subscrbied: " +date_subscribed);
                                tvValidity.setText(validity);
                                tvValidityUntil.setText("Valid until: ");

                            }
                            if(status.equals("Active")){

                                tvStatus.setText("Active");
                                tvStatus.setTextColor(Color.GREEN);
                                tvValidity.setTextColor(Color.GREEN);

                            }else if (status.equals("Expired")){

                                tvStatus.setText("Expired");
                                tvStatus.setTextColor(Color.RED);
                                tvValidity.setTextColor(Color.RED);

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
}