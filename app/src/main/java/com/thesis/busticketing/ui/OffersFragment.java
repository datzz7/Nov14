package com.thesis.busticketing.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.synnapps.carouselview.CarouselView;
import com.thesis.busticketing.LoginActivity;
import com.thesis.busticketing.R;
import com.thesis.busticketing.RegisterActivity;
import com.thesis.busticketing.UpdateSubscriptionActivity;
import com.thesis.busticketing.UserHomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OffersFragment extends Fragment implements View.OnClickListener {

    TextView tv7Days, tv15Days, tv30Days;
    Button sub7Day, sub15Day, sub30Day;
    RequestQueue queue;
    final String prices_url = "http://bus-ticketing.herokuapp.com/prices.php";
    String type, value;
    String Days7, Days15, Days30;
    String Type7, Type15, Type30;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_offers, container, false);

        queue = Volley.newRequestQueue(getActivity());

        sub7Day = v.findViewById(R.id.sub7Day);
        sub15Day = v.findViewById(R.id.sub15Day);
        sub30Day = v.findViewById(R.id.sub30Day);
        tv7Days = v.findViewById(R.id.tv7Days);
        tv15Days = v.findViewById(R.id.tv15Days);
        tv30Days = v.findViewById(R.id.tv30Days);

        sub7Day.setOnClickListener(this);
        sub15Day.setOnClickListener(this);
        sub30Day.setOnClickListener(this);

        prices();
        return v;
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
                                    tv7Days.setText("Subscribe for 7Days for \\Php " + value);
                                }

                                if(type.contains("15")){
                                    Days15 = value;
                                    Type15 = type;
                                    tv15Days.setText("Subscribe for 15Days for \\Php " +value);
                                }

                                if(type.contains("30")){
                                    Days30 = value;
                                    Type30 = type;
                                    tv30Days.setText("Subscribe for 30Days for \\Php " +value);
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
    public void onClick(View view) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Please log in to proceed")
                        .setContentText("Log in to proceed")
                        .setConfirmText("Login")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
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
    }
}
