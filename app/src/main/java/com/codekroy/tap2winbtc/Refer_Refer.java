package com.codekroy.tap2winbtc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codekroy.tap2winbtc.adapter.History_Adapter;
import com.codekroy.tap2winbtc.model.fetch_model;
import com.codekroy.tap2winbtc.model.fetch_ultra;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Refer_Refer extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    LinearLayout INPUT_LIN,REFER_LIN;
    Dialog dialog;
    FirebaseAuth mAuth;
    TextView CHECK_TV,BACK_TEXT,Claim_Pts;
    EditText INPUT_TEXT;
    TextView refer_details,refer_code,shere_TV,back,REFER_POINTS,CRP_TV;
    View view,view2;
    String REFER_CODE,APPLINK,PRIVACY,adUnitId,unityGameID,BACK_POINT="0";
     int COSMO,testMODE,CURRENT_REFER_POINT;
    ArrayList<com.codekroy.tap2winbtc.model.fetch_model> arrayList=new ArrayList<>();
    boolean click=true;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_refer);
        mAuth = FirebaseAuth.getInstance();
        INPUT_TEXT=findViewById(R.id.input_text);
        listView=findViewById(R.id.list);
        INPUT_LIN=findViewById(R.id.input_lin);
        CHECK_TV=findViewById(R.id.check_tv);
        refer_code=findViewById(R.id.textView4);
        refer_details=findViewById(R.id.refer_details);
        shere_TV=findViewById(R.id.shere_tv);
        view=findViewById(R.id.view);
        view2=findViewById(R.id.view2);
        back=findViewById(R.id.back);
        BACK_TEXT=findViewById(R.id.textView3);
        REFER_POINTS=findViewById(R.id.total_referPoints);
        REFER_LIN=findViewById(R.id.refer_lin);
        Claim_Pts=findViewById(R.id.claim_tv);
        CRP_TV=findViewById(R.id.textView2);
        Claim_Pts.setOnClickListener(this);
        back.setOnClickListener(this);
        CHECK_TV.setOnClickListener(this);
        shere_TV.setOnClickListener(this);
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            if (bundle.getInt(BuildConfig.CM)==1){
                APPLINK= bundle.getString(BuildConfig.BETA_DELTA);
                PRIVACY=  bundle.getString(BuildConfig.BETA_GAMA);
                unityGameID=  bundle.getString("unityGameID");
                adUnitId=  bundle.getString("adUnitId");
                testMODE=  bundle.getInt("testmode");
                COSMO=bundle.getInt(BuildConfig.CM);
                INPUT_LIN.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                REFER_LIN.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                shere_TV.setVisibility(View.GONE);
                refer_details.setVisibility(View.GONE);
                refer_code.setVisibility(View.GONE);
                BACK_TEXT.setVisibility(View.GONE);
            }else {
                REFER_CODE=bundle.getString(BuildConfig.EXALM);
                APPLINK= bundle.getString(BuildConfig.BETA_DELTA);
                CURRENT_REFER_POINT=bundle.getInt("crp");
                Progress();
                GAMA_LOGIC(2,null);
            }
        }
    }
    void  GAMA_LOGIC(int COSMO,String ENTER_REFER_CODE) {
        RequestQueue req = Volley.newRequestQueue(getApplicationContext());
        NetworkUtil.checkInternetConnectionWithCallback(this, () -> {
            @SuppressLint("SetTextI18n") Response.Listener<String> resListener = (Response.Listener<String>) response -> {
                try {
                    JSONObject gso = new JSONObject(response);
                    String success = gso.getString(BuildConfig.THREE_DELTA);
                    String message = gso.getString(BuildConfig.ULTRA_DELTA);
                    if (COSMO==1){
                        if (success.equals(BuildConfig.COT_DELTA)) {
                            Toast.makeText(Refer_Refer.this, message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Intent i = new Intent(Refer_Refer.this, Tap_Tap.class);
                            i.putExtra(BuildConfig.CM,COSMO);
                            i.putExtra(BuildConfig.BETA_DELTA,APPLINK);
                            i.putExtra(BuildConfig.BETA_GAMA,PRIVACY);
                            i.putExtra("unityGameID",unityGameID);
                            i.putExtra("adUnitId",adUnitId);
                            i.putExtra("testmode",testMODE);
                            startActivity(i);
                            finishAffinity();
                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            click=true;
                        }
                    }   else if (COSMO==3) {
                        if (success.equals(BuildConfig.COT_DELTA)){
                            dialog.dismiss();
                            Toast.makeText(Refer_Refer.this, message, Toast.LENGTH_SHORT).show();
                            BACK_POINT=String.valueOf(CURRENT_REFER_POINT);
                            CURRENT_REFER_POINT=0;
                            CRP_TV.setText(CURRENT_REFER_POINT+" Pts");
                        } else {
                            dialog.dismiss();

                            Toast.makeText(Refer_Refer.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (success.equals(BuildConfig.COT_DELTA)){
                            refer_code.setText(REFER_CODE);
                            String comission_success = gso.getString(BuildConfig.SC);
                            String list_success = gso.getString(BuildConfig.SW);
                            if (comission_success.equals(BuildConfig.COT_DELTA)){
                                int total_commission = gso.getInt(BuildConfig.TC);
                                REFER_POINTS.setText(total_commission + " Points");

                            }else {
                                REFER_POINTS.setText("0 Points");
                            }
                            CRP_TV.setText(CURRENT_REFER_POINT+" Pts");
                            if (list_success.equals(BuildConfig.COT_DELTA)){
                                JSONArray jsonArray=gso.getJSONArray(BuildConfig.WD);
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject=  jsonArray.getJSONObject(i);
                                    String email= jsonObject.getString(BuildConfig.DELTA);
                                    int points= jsonObject.getInt(BuildConfig.TP);
                                    arrayList.add(new fetch_model(email,String.valueOf(points),-1,"",0));
                                }
                                History_Adapter historyAdapter=new History_Adapter(getApplicationContext(),arrayList,2);
                                listView.setAdapter(historyAdapter);
                                dialog.dismiss();
                            } else {
                                dialog.dismiss();
                            }
                        } else {
                            dialog.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(Refer_Refer.this, "API Call Failed!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    click=true;
                }
                req.getCache().clear();
            };
            Response.ErrorListener errListener = error -> {
                if (error instanceof ServerError) {
                    Toast.makeText(Refer_Refer.this, "Server error! Please try again later.", Toast.LENGTH_SHORT).show();

                }
                click=true;
            };
            StringRequest stringrequest = new StringRequest(Request.Method.POST, BuildConfig.REFER, resListener, errListener) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> pram = new HashMap<>();
                    pram.put(BuildConfig.DELTA,mAuth.getCurrentUser().getEmail());
                    if (COSMO==1){
                        pram.put(BuildConfig.FRC, ENTER_REFER_CODE);
                        pram.put(BuildConfig.COMMAND, String.valueOf(COSMO));
                    }else {
                        pram.put(BuildConfig.FRC, REFER_CODE);
                        pram.put(BuildConfig.COMMAND, String.valueOf(COSMO));
                    }
                    return pram;
                }
            };
            req.add(stringrequest);
        });
    }
    void Progress(){
        dialog=new Dialog(Refer_Refer.this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_layout);
        dialog.setCancelable(false);
        dialog.show();
    }
    @Override
    public void onClick(View v) {
            if (v == CHECK_TV) {
                if (click){
                    click=false;
                    Progress();
                    String INPUT = INPUT_TEXT.getText().toString();
                    String sanitizedInput = INPUT.replaceAll(BuildConfig.SINPUT, "").trim();
                    INPUT_TEXT.setText(sanitizedInput);
                    if (sanitizedInput.length() > 0) {
                        GAMA_LOGIC(COSMO, sanitizedInput);
                    } else {
                        Toast.makeText(this, "Please enter valid input!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        click=true;
                    }
                }
            }
            else if (v==back) {
            if (COSMO==1){
                Intent i = new Intent(Refer_Refer.this, Tap_Tap.class);
                i.putExtra(BuildConfig.CM,COSMO);
                i.putExtra(BuildConfig.BETA_DELTA,APPLINK);
                i.putExtra(BuildConfig.BETA_GAMA,PRIVACY);
                i.putExtra("unityGameID",unityGameID);
                i.putExtra("adUnitId",adUnitId);
                i.putExtra("testmode",testMODE);
                startActivity(i);
                finishAffinity();
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("backpoint", BACK_POINT);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        } else if (v==shere_TV) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Use my referral to get special reward, My referral code is: "+REFER_CODE + "\n" + APPLINK);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }else if (v==Claim_Pts) {
                new  AlertDialog.Builder(this).setMessage("Available Points: "+ CURRENT_REFER_POINT)
                        .setTitle("REFERRAL POINTS")
                        .setCancelable(true)
                        .setPositiveButton("CLAIM", (dialog, which) -> {
                            Progress();
                            GAMA_LOGIC(3,"");
                        })
                        .setNegativeButton("CANCEL", null).show();
            }
    }
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("backpoint", BACK_POINT);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

}