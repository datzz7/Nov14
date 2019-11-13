package com.thesis.busticketing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class Pay extends AppCompatActivity {

    //final String url = "http://192.168.254.194/thesis/register.php";
    final String url = "http://bus-ticketing.herokuapp.com/register.php";
    PayPalConfiguration m_configuration;
    //paypal id
    String m_paypalCientid = "Aayerv3II9xvdH4KgrFb0Dw_aPJZCZF_AaElX2GH2jdroxdA2Rtu7bgHn9y2Da4TRvHjmfUY_UsfIT9C";
    String  email, firstname, lastname, password, subscription, amount, ptype,image;
    //String uniqueID = UUID.randomUUID().toString().replaceAll("-","").toLowerCase();
    RequestQueue queue;
    Intent m_service;
    int m_paypalRequestCode =9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue = Volley.newRequestQueue(this);


        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        firstname = intent.getExtras().getString("firstname");
        lastname = intent.getExtras().getString("lastname");
        password = intent.getExtras().getString("password");
        subscription = intent.getExtras().getString("subscription");
        amount = intent.getExtras().getString("amount");
        ptype = intent.getExtras().getString("ptype");
        image = intent.getExtras().getString("image");
        Log.d("Response6",amount+ptype);


        onLoad();

//                PayPalPayment payment = new PayPalPayment(new BigDecimal(50), "PHP", "Test Payment with paypal",
//                        PayPalPayment.PAYMENT_INTENT_SALE);
//
//                Intent intent = new Intent(Pay.this, PaymentActivity.class);
//                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
//                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
//                startActivityForResult(intent, m_paypalRequestCode);


        m_configuration = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(m_paypalCientid);

        m_service = new Intent (this, PayPalService.class);
        m_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
        startService(m_service);
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void onLoad() {

        if (subscription.equals("7Days")) {
            PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "PHP", "Pay 7Days Subscription for",
                    PayPalPayment.PAYMENT_INTENT_SALE);

            Intent intent = new Intent(Pay.this, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            startActivityForResult(intent, m_paypalRequestCode);
        }

        if(subscription.equals("15Days")){

            PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "PHP", "Pay 15Days Subscription for",
                    PayPalPayment.PAYMENT_INTENT_SALE);

            Intent intent = new Intent(Pay.this, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            startActivityForResult(intent, m_paypalRequestCode);
        }
        if(subscription.equals("30Days")){

            PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "PHP", "Pay 30Days Subscription for",
                    PayPalPayment.PAYMENT_INTENT_SALE);

            Intent intent = new Intent(Pay.this, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            startActivityForResult(intent, m_paypalRequestCode);
        }
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

                        toRegister();
                        SweetAlertDialog Dialog = new SweetAlertDialog(Pay.this, SweetAlertDialog.SUCCESS_TYPE);
                                Dialog.setTitleText("Success!");
                                Dialog.setContentText("Log in to view ticket. ");
                                Dialog.setCancelable(false);
                                Dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                Intent intent = new Intent(Pay.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                Dialog.show();

                    }//else{

                        //test.setText("Error in payment");
                  //  }

                }//else{

                    //test.setText("Confirrm is ");
                //}
            }
        }
    }

    private void toRegister() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response5",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
               // params.put("uniqueID", uniqueID);
                //Log.d("message", uniqueID);
                params.put("email", email);
                params.put("firstname", firstname);
                params.put("lastname", lastname);
                params.put("password", password);
                params.put("subscription", subscription);
                params.put("amount", amount);
                params.put("ptype", ptype);
                params.put("image", image);
                Log.d("dats",amount);

                return params;
            }
        };
        queue.add(postRequest);

    }


}
