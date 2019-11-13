package com.thesis.busticketing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class UpdateSubscriptionActivity extends AppCompatActivity implements View.OnClickListener {

    CarouselView carouselView;
    int[] sampleImages = {R.drawable.davao_bus1, R.drawable.davao_bus2, R.drawable.davao_bus3};
    //final String prices_url = "http://192.168.43.32/thesis/prices.php";
    //final String URL_UPDATE_SUBSCRIPTION = "http://192.168.43.32/thesis/update_subscription.php";
    final String URL_UPDATE_SUBSCRIPTION = "http://bus-ticketing.herokuapp.com/update_subscription.php";
    final String prices_url = "http://bus-ticketing.herokuapp.com/prices.php";
    Button btn7Day, btn15Day, btn30Day;
    TextView tvId,tvStatus;
    RequestQueue queue;
    String id, subscription, email, firstname,lastname,photo;
    String m_paypalCientid = "Aayerv3II9xvdH4KgrFb0Dw_aPJZCZF_AaElX2GH2jdroxdA2Rtu7bgHn9y2Da4TRvHjmfUY_UsfIT9C";
    Intent m_service;
    PayPalConfiguration m_configuration;
    int m_paypalRequestCode =9999;
    String Days7, Days15, Days30, Type7, Type15, Type30;
    String type, value, amount;
    TextView tv7Days, tv15Days, tv30Days;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_subscription);

        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(sampleImages[position]);
            }
        });


        queue = Volley.newRequestQueue(this);

        tv7Days = findViewById(R.id.tv7Days);
        tv15Days = findViewById(R.id.tv15Days);
        tv30Days = findViewById(R.id.tv30Days);
        btn7Day = findViewById(R.id.btn7Day);
        btn15Day = findViewById(R.id.btn15Day);
        btn30Day = findViewById(R.id.btn30Day);
        tvId = findViewById(R.id.tvId);
        tvStatus = findViewById(R.id.tvStatus);

        prices();

        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        email = intent.getExtras().getString("email");
        firstname = intent.getExtras().getString("firstname");
        lastname = intent.getExtras().getString("lastname");
        photo = intent.getExtras().getString("photo");

        btn7Day.setOnClickListener(this);
        btn15Day.setOnClickListener(this);
        btn30Day.setOnClickListener(this);


        m_configuration = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(m_paypalCientid);

        m_service = new Intent (this, PayPalService.class);
        m_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
        startService(m_service);


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
                                    tv7Days.setText("Subscribe for 7Days for \nPhp " + value);
                                }

                                if(type.contains("15")){
                                    Days15 = value;
                                    Type15 = type;
                                    tv15Days.setText("Subscribe for 15Days for \nPhp " +value);
                                }

                                if(type.contains("30")){
                                    Days30 = value;
                                    Type30 = type;
                                    tv30Days.setText("Subscribe for 30Days for \nPhp " +value);
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void toUpdate() {
        Log.d("Response5", "wew");
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_UPDATE_SUBSCRIPTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("subscription", subscription);
                params.put("uniqueID", id);
                params.put("amount", amount);
               // Log.d("Response66", subscription);

                return params;
            }
        };
        queue.add(postRequest);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == m_paypalRequestCode){

            if(resultCode == Activity.RESULT_OK){

                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if(confirmation != null){

                    String state = confirmation.getProofOfPayment().getState();

                    if(state.equals("approved")){

                        toUpdate();

                        SweetAlertDialog sDialog = new SweetAlertDialog(UpdateSubscriptionActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                sDialog.setTitleText("Success!");
                                sDialog.setContentText("Ticket validity updated. ");
                                sDialog.setCancelable(false);
                                sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        Intent intent = new Intent(UpdateSubscriptionActivity.this, UserHomeActivity.class);
                                        intent.putExtra("firstname",firstname);
                                        intent.putExtra("lastname",lastname);
                                        intent.putExtra("email",email);
                                        intent.putExtra("id", id);
                                        intent.putExtra("photo", photo);
                                        startActivity(intent);

                                    }
                                });
                                sDialog.show();

                    }else{

                        Toasty.error(this,"Payment Error", Toasty.LENGTH_SHORT).show();
                    }

                }else{

                    Toasty.error(this,"Payment Error", Toasty.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn7Day:
                subscription = "7Days";
                amount = Days7;
                if (subscription.equals("7Days")) {
                    PayPalPayment payment = new PayPalPayment(new BigDecimal(Days7), "PHP", "Pay 7Days Subscription for",
                            PayPalPayment.PAYMENT_INTENT_SALE);

                    Intent intent = new Intent(UpdateSubscriptionActivity.this, PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                    startActivityForResult(intent, m_paypalRequestCode);

                }

                break;

            case R.id.btn15Day:
                subscription = "15Days";
                amount = Days15;
                if (subscription.equals("15Days")) {
                    PayPalPayment payment = new PayPalPayment(new BigDecimal(Days15), "PHP", "Pay 15Days Subscription for",
                            PayPalPayment.PAYMENT_INTENT_SALE);

                    Intent intent = new Intent(UpdateSubscriptionActivity.this, PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                    startActivityForResult(intent, m_paypalRequestCode);

                }
                break;

            case R.id.btn30Day:
                subscription = "30Days";
                amount= Days30;
                if (subscription.equals("30Days")) {
                    PayPalPayment payment = new PayPalPayment(new BigDecimal(Days30), "PHP", "Pay 30Days Subscription for",
                            PayPalPayment.PAYMENT_INTENT_SALE);

                    Intent intent = new Intent(UpdateSubscriptionActivity.this, PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                    startActivityForResult(intent, m_paypalRequestCode);

                }
                break;

        }
    }
}