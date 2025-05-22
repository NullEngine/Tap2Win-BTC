package com.codekroy.tap2winbtc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codekroy.tap2winbtc.adapter.History_Adapter;
import com.codekroy.tap2winbtc.model.fetch_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class History_History extends AppCompatActivity {
    Bundle bundle;
    ListView listView;
    Dialog dialog;
    TextView noyet;
    ArrayList<fetch_model> arrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_history);
        bundle=getIntent().getExtras();
        listView=findViewById(R.id.list);
        noyet=findViewById(R.id.textView8);
        Progress();
        GET_BITA();
    }
    void  GET_BITA() {
        RequestQueue req = Volley.newRequestQueue(getApplicationContext());

        NetworkUtil.checkInternetConnectionWithCallback(this, new NetworkUtil.InternetCheckCallback() {
            @Override
            public void onInternetAvailable() {
                Response.Listener<String> resListener = response -> {
                    try {
                        JSONObject gso = new JSONObject(response);
                        String success = gso.getString(BuildConfig.THREE_DELTA);
                        if (success.equals(BuildConfig.COT_DELTA)) {
                            arrayList.clear();
                            String list_success = gso.getString(BuildConfig.SW);
                            if (list_success.equals(BuildConfig.COT_DELTA)) {
                                JSONArray withdraw_array = gso.getJSONArray(BuildConfig.WD);
                                for (int i = 0; i < withdraw_array.length(); i++) {
                                    JSONObject user_ob = withdraw_array.getJSONObject(i);
                                    String  Email = user_ob.getString(BuildConfig.DELTA);
                                    int  W_POINT = user_ob.getInt(BuildConfig.P);
                                    int STATUS = user_ob.getInt(BuildConfig.STATUS);
                                    String time  = user_ob.getString("time");
                                    double btc_amount = user_ob.getDouble("btc");
                                    arrayList.add(new fetch_model(Email,String.format(String.valueOf(W_POINT)),STATUS,time,btc_amount));
                                }
                                History_Adapter historyAdapter=new History_Adapter(getApplicationContext(),arrayList,1);
                                listView.setAdapter(historyAdapter);
                                dialog.dismiss();

                            } else {
                                dialog.dismiss();
                                listView.setVisibility(View.GONE);
                                noyet.setVisibility(View.VISIBLE);
                            }
                        } else {
                            dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(History_History.this, "API Call Failed!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    req.getCache().clear();
                };
                Response.ErrorListener errListener = error -> {
                    dialog.dismiss();
                    if (error instanceof ServerError) {
                        Toast.makeText(History_History.this, "Server error! Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                };
                StringRequest stringrequest = new StringRequest(Request.Method.POST,BuildConfig.HISTORY, resListener, errListener) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> pram = new HashMap<>();
                        pram.put(BuildConfig.COMMAND, String.valueOf(2));
                        if (bundle!=null){
                            pram.put(BuildConfig.DELTA, bundle.getString(BuildConfig.DELTA));
                        }
                        return pram;
                    }
                };
                req.add(stringrequest);

            }
        });
    }
    void Progress(){
        dialog=new Dialog(History_History.this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_layout);
        dialog.setCancelable(false);
        dialog.show();
    }
}