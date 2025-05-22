package com.codekroy.tap2winbtc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Claim_History extends AppCompatActivity implements View.OnClickListener {


TextView EMAIL,CLAIM,HIST,POLICY,pts;
EditText CURRENCY_EDIT,PTS_EDIT;
    Bundle bundle;
    String email,bal_,APPLINK,PRIVACY,CONVET_VALUE,INPUT_POINTS;
    Handler handler = new Handler();
    Runnable runnable;
    ProgressBar progressBar;
    boolean isClaiming=false;
    Dialog Pdialog;
    AlertDialog.Builder builder;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_claim);

        HIST=findViewById(R.id.History_button);
        CLAIM=findViewById(R.id.Claim_button);
        POLICY=findViewById(R.id.policy_text);
        CURRENCY_EDIT=findViewById(R.id.currency_edit);
        PTS_EDIT=findViewById(R.id.pts_edit);
        progressBar=findViewById(R.id.progress);
        pts=findViewById(R.id.pts);
        HIST.setOnClickListener(this);
        CLAIM.setOnClickListener(this);
        POLICY.setOnClickListener(this);
        builder = new AlertDialog.Builder(Claim_History.this);
        bundle=getIntent().getExtras();
        String text = "<a href=''>Privacy Policy</a>";
        POLICY.setText(Html.fromHtml(text));
        if (bundle!=null){
            EMAIL=findViewById(R.id.email_Po);
            bal_=bundle.getString("bal");
            email=bundle.getString(BuildConfig.DELTA);
            APPLINK= bundle.getString(BuildConfig.BETA_DELTA);
            PRIVACY=  bundle.getString(BuildConfig.BETA_GAMA);
            EMAIL.setText(email);
            pts.setText(" Your Points: "+bal_);
        }
        PTS_EDIT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                progressBar.setVisibility(View.VISIBLE);
                CURRENCY_EDIT.setVisibility(View.GONE);
                isClaiming=false;
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                runnable = () ->
                    DEYAGON_LOGIC(2,s.toString());
                handler.postDelayed(runnable, 1000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==CLAIM){

            if (PTS_EDIT.getText().length()>0){

                if (isClaiming) {
                    DEYAGON_SHOW();
                } else {
                    Toast.makeText(this,
                            "Converting...",
                            Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(this,
                        "Points Required!",
                        Toast.LENGTH_SHORT).show();

            }


        } else if (v==HIST) {
            Intent intent=new Intent(this,History_History.class);
            intent.putExtra(BuildConfig.DELTA, bundle.getString(BuildConfig.DELTA));
            startActivity(intent);

        } else if(v==POLICY){
            Uri uri=Uri.parse(PRIVACY);
            Intent intent= new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("backpoint", bal_);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    void DEYAGON_LOGIC(int COMMAND,String INPUT_AMOUNT) {
        RequestQueue req =
                Volley.newRequestQueue(getApplicationContext());
        NetworkUtil.checkInternetConnectionWithCallback(this, () -> {
            @SuppressLint({"DefaultLocale", "SetTextI18n"}) Response.Listener<String> resListener = response -> {
                try {
                    JSONObject gso = new JSONObject(response);
                    String success = gso.getString(BuildConfig.THREE_DELTA);
                    String message = gso.getString(BuildConfig.ULTRA_DELTA);


                    if (COMMAND==1){

                        if (success.equals(BuildConfig.COT_DELTA)) {
                            Pdialog.dismiss();
                            bal_ = String.valueOf(Integer.parseInt(bal_)-Integer.parseInt(INPUT_AMOUNT));
                            CURRENCY_EDIT.setText(null);
                            PTS_EDIT.setText(null);
                            pts.setText(" Your Points: "+bal_);
                            builder.setCancelable(false);
                            builder.setTitle("CONGRATULATION!");
                            builder.setMessage(message);
                            builder.setPositiveButton
                                    ("RATE US", (dialog, which) -> {
                                        Uri appuri = Uri.parse(APPLINK);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, appuri);
                                        startActivity(intent);
                                    });
                            builder.setNegativeButton
                                    ("LATER", (dialog, which) -> {
                                        dialog.cancel();
                                    });
                            builder.create();
                            builder.show();
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            Pdialog.dismiss();
                        }

                    } else  if (COMMAND==2){
                        if (success.equals(BuildConfig.COT_DELTA)){
                            Double convert_value = gso.getDouble("convert");
                            CURRENCY_EDIT.setText(String.format("%.10f",convert_value));
                            progressBar.setVisibility(View.GONE);
                            CURRENCY_EDIT.setVisibility(View.VISIBLE);
                            CONVET_VALUE=String.format("%.10f",convert_value);
                            INPUT_POINTS=INPUT_AMOUNT;
                            isClaiming=true;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Claim_History.this, e.toString(), Toast.LENGTH_SHORT).show();
                    Pdialog.dismiss();
                }
                req.getCache().clear();
            };
            Response.ErrorListener errListener = error -> {
                if (error instanceof ServerError) {
                    Toast.makeText(Claim_History.this,
                            "Server error! Please try again later.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            StringRequest stringrequest
                    = new StringRequest
                    (Request.Method.POST, BuildConfig.CLAIM, resListener, errListener) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> pram = new HashMap<>();
                    pram.put(BuildConfig.DELTA, email);
                    pram.put(BuildConfig.COMMAND,String.valueOf(COMMAND));
                        pram.put("input",INPUT_AMOUNT);

                    return pram;
                }
            };

            req.add(stringrequest);

        });
    }
    void Progress() {
        Pdialog = new Dialog(Claim_History.this);
        Objects.requireNonNull(Pdialog.getWindow()).
                setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Pdialog.setContentView(R.layout.progress_layout);
        Pdialog.setCancelable(false);
        Pdialog.show();
    }

    void DEYAGON_SHOW() {
        builder.setCancelable(false);
        builder.setMessage("You are now claiming '" + INPUT_POINTS + "' points = '"+CONVET_VALUE+"' BTC to\n" + email +
                ".\n\nPlease remember:\n- Points are not equal to Satoshis.\n- We only pay to users with an acceptable Binance account.");
        builder.setTitle("CLAIMING");
        builder.setPositiveButton("CLAIM",
                (dialog, which) -> {
                    Progress();
                    dialog.dismiss();
                    DEYAGON_LOGIC(1,INPUT_POINTS);
                });
        builder.setNegativeButton("CANCEL",
                (dialog, which) -> dialog.cancel());
        builder.create();
        builder.show();
    }

}