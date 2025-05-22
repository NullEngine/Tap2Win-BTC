package com.codekroy.tap2winbtc;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.codekroy.tap2winbtc.model.fetch_model;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codekroy.tap2winbtc.model.fetch_ultra;
import com.google.firebase.auth.FirebaseAuth;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Tap_Tap extends AppCompatActivity implements View.OnClickListener , IUnityAdsInitializationListener {


    ArrayList<fetch_model> arrayList=new ArrayList<>();
    ArrayList<com.codekroy.tap2winbtc.model.fetch_ultra> arrayList2=new ArrayList<>();

    long totalCountdownTime;
    private final TextView[] textViews = new TextView[20];
    private final Random random = new Random();
    private final Handler handler = new Handler();
    private int currentIndex = 0;
    Dialog dialog;
    FirebaseAuth mAuth;
    private String Email ;
    private int W_POINT ;
    TextView H_TEXT;
    ImageView MENU,REFER,FREE;
    int REFER_POINT;
    private final Timer timer = new Timer();
    int[] textview_list =
            {R.id.Point1,R.id.Point2,R.id.Point3,R.id.Point4,R.id.Point5,
                    R.id.Point6,R.id.Point7,R.id.Point8,R.id.Point9,R.id.Point10,R.id.Point11,
                    R.id.Point12,R.id.Point13,R.id.Point14,R.id.Point15,R.id.Point16,R.id.Point17,
                    R.id.Point18,R.id.Point19,R.id.Point20};
    TextView goButton,point_tv;
    boolean click=true;
    CountDownTimer countDownTimer;
    int CURRENT_POINTS;
    String REFER_CODE,PRIVACY,APPLINK;
    int testMODE;
    String unityGameID ;
    private Boolean adLoaded = false;
    private String adUnitId ;

   int WAITING_TIME=20000;


    private final IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {

            adLoaded=true;
        }



        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
        }
    };
    private final IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            Log.d("show filer",message);
            adLoaded=false;
            WAITING_TIME=20000;
            startCountdownTmer(WAITING_TIME,3,2);
        }
        @Override
        public void onUnityAdsShowStart(String placementId) {
        }
        @Override
        public void onUnityAdsShowClick(String placementId) {
        }
        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            if (state.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                GET_GAMA(-1,3,Integer.parseInt(textViews[currentIndex].getText().toString()));
                loadAads();
                startCountdownTmer(10000,2,-1);
                adLoaded=false;
                WAITING_TIME=20000;
            }
        }
    };
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_tap);
        loadAads();
        mAuth = FirebaseAuth.getInstance();
        goButton=findViewById(R.id.go_button);
        point_tv=findViewById(R.id.points_tv);
        H_TEXT=findViewById(R.id.history_text);
        MENU=findViewById(R.id.imageView2);
        REFER=findViewById(R.id.imageView);
        FREE=findViewById(R.id.promotional);
        goButton.setOnClickListener(this);
        FREE.setOnClickListener(this);
        MENU.setOnClickListener(this);
        REFER.setOnClickListener(this);
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            APPLINK= bundle.getString(BuildConfig.BETA_DELTA);
            PRIVACY=  bundle.getString(BuildConfig.BETA_GAMA);
            unityGameID=  bundle.getString("unityGameID");
            adUnitId=  bundle.getString("adUnitId");
            testMODE=  bundle.getInt("testmode");
            if (testMODE==1){
                UnityAds.initialize(this, unityGameID, this);
            } else {
                UnityAds.initialize(this, unityGameID, true, this);
            }
            if (bundle.getInt(BuildConfig.CM)==1){
                Progress();
                GET_GAMA(bundle.getInt(BuildConfig.CM),1,-1);
                GET_GAMA(-1,4,-1);
            } else {
                Progress();
                GET_GAMA(bundle.getInt(BuildConfig.CM),1,-1);
                GET_GAMA(-1,4,-1);
            }
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    GET_GAMA(-1,2,-1);
                }
            }, 0, 200000);
        }



        // Assign TextViews to the array
        for (int i = 0; i < textview_list.length; i++) {
            textViews[i]= findViewById(textview_list[i]);
        }






        goButton.setText("Go");




    }


    private void startSelectorAnimation() {
        handler.removeCallbacksAndMessages(null);
        final int randomStopIndex = random.nextInt(20);
        final int totalIterations = random.nextInt(64)+20;
        handler.postDelayed(new Runnable() {
            int iterations = 0;
            @Override
            public void run() {
                for (TextView tv : textViews) {
                    tv.setBackgroundResource(R.drawable.point_button);
                }
                textViews[currentIndex].setBackgroundResource(R.drawable.point_highlight);
                if (iterations >= totalIterations && currentIndex == randomStopIndex) {
                    startCountdownTmer(5000 ,1,-1);
                    return; //stop animation
                }
                currentIndex++;
                if (currentIndex >= textViews.length) {
                    currentIndex = 0;
                }
                iterations++;
                handler.postDelayed(this, 100);
            }
        }, 100);
    }
    public void pointRandom(ArrayList<com.codekroy.tap2winbtc.model.fetch_ultra> list){
        for (int i=0;i<list.size();i++){
            textViews[i].setText(list.get(i).getPoints());
        }
        dialog.dismiss();
            click=true;
    }
    @SuppressLint("SetTextI18n")
    public void TextviewUpdateCountdownTimer(int COM,int ERROR_COM) {
        long second = totalCountdownTime / 1000 % 60;
        String Timeformet = String.format(Locale.getDefault(),getResources().getString(R.string.time_formate),second);
        if (COM==1){

            goButton.setText("Show ads after:  "+Timeformet);
        } else if (COM==2) {
            goButton.setText("Wait... "+Timeformet);
        } else if ( COM==3) {
            if (ERROR_COM==1){
                goButton.setText("Load Failer! Wait... "+Timeformet);
            } else if (ERROR_COM==2) {
                goButton.setText("Show Failer! Wait... "+Timeformet);
            }


        } else {

            goButton.setText(Timeformet);
        }

    }
    public void startCountdownTmer(long time, int COM,int ERROR_COM) {

        countDownTimer = new CountDownTimer(time, 1) {
            @Override
            public void onTick(long l) {
                totalCountdownTime=  l;
                    TextviewUpdateCountdownTimer(COM,ERROR_COM);
            }
            @Override
            public void onFinish() {
               // TextviewUpdateCountdownTimer(-1);
                countDownTimer.cancel();
                if (COM==1){
                    showAd();
                } else if (COM==2){
                    Progress();
                    GET_GAMA(-1,4,-1);
                    goButton.setText(R.string.go);
                } else if (COM==3){
                    loadAads();
                    goButton.setText(R.string.go);
                    click=true;
                }


            }
        };
        countDownTimer.start();
    }
    private void startNumberAnimation(int start, int end, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            point_tv.setText(String.valueOf(animatedValue));
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                CURRENT_POINTS=end;
                super.onAnimationEnd(animation, isReverse);
            }
        });
        animator.start();
    }
    void  GET_GAMA(int COSMO,int COMAND,int POINTS) {
        RequestQueue req = Volley.newRequestQueue(getApplicationContext());
        NetworkUtil.checkInternetConnectionWithCallback(Tap_Tap.this, () -> {
            @SuppressLint("SetTextI18n") Response.Listener<String> resListener = response -> {
                try {
                    JSONObject gso = new JSONObject(response);
                    String THREE_DELTA = gso.getString(BuildConfig.THREE_DELTA);
                    String ULTRA_DELTA = gso.getString(BuildConfig.ULTRA_DELTA);
                    if (COMAND == 1) {
                        if (THREE_DELTA.equals(BuildConfig.COT_DELTA)) {
                            String user_success = gso.getString(BuildConfig.SU);
                            if (user_success.equals(BuildConfig.COT_DELTA)) {
                                JSONArray user_array = gso.getJSONArray(BuildConfig.UD);
                                for (int i = 0; i < user_array.length(); i++) {
                                    JSONObject user_ob = user_array.getJSONObject(i);
                                    REFER_POINT = user_ob.getInt(BuildConfig.CRP);
                                    CURRENT_POINTS = user_ob.getInt(BuildConfig.CP);
                                    REFER_CODE=user_ob.getString(BuildConfig.EXALM);
                                }
                                if (COSMO == 1) {
                                    startNumberAnimation(0,CURRENT_POINTS,3000);
                                } else {
                                    point_tv.setText(String.valueOf(CURRENT_POINTS));
                                }
                            }
                        }
                    } else if (COMAND == 2) {
                        if (THREE_DELTA.equals(BuildConfig.COT_DELTA)) {
                            arrayList.clear();
                            String list_success = gso.getString(BuildConfig.SW);
                            if (list_success.equals(BuildConfig.COT_DELTA)) {
                                JSONArray withdraw_array = gso.getJSONArray(BuildConfig.WD);
                                for (int i = 0; i < withdraw_array.length(); i++) {
                                    JSONObject user_ob = withdraw_array.getJSONObject(i);
                                    Email = user_ob.getString(BuildConfig.DELTA);
                                    W_POINT = user_ob.getInt(BuildConfig.P);
                                    arrayList.add(new fetch_model(Email,String.format(String.valueOf(W_POINT)),-1,"",0));
                                }
                                H_TEXT.setText(stringBuilder(arrayList).toString());
                            }

                        }
                    } else if (COMAND==3) {

                        if (THREE_DELTA.equals(BuildConfig.COT_DELTA)){

                            startNumberAnimation(CURRENT_POINTS,CURRENT_POINTS+Integer.parseInt(textViews[currentIndex].getText().toString()),
                                    3000);
                        } else {
                            Toast.makeText(Tap_Tap.this, ULTRA_DELTA, Toast.LENGTH_SHORT).show();
                        }
                    } else if (COMAND==4) {

                        if (THREE_DELTA.equals(BuildConfig.COT_DELTA)) {
                            String successPoint = gso.getString(BuildConfig.SP);
                            if (successPoint.equals(BuildConfig.COT_DELTA)) {
                                arrayList2.clear();
                                JSONArray points_array = gso.getJSONArray(BuildConfig.PD);
                                for (int i = 0; i < points_array.length(); i++) {
                                    JSONObject user_ob = points_array.getJSONObject(i);
                                    int point = user_ob.getInt(BuildConfig.PP);
                                    int id = user_ob.getInt(BuildConfig.PI);
                                    arrayList2.add(new fetch_ultra(String.valueOf(point),id));
                                }

                                pointRandom(arrayList2);
                            } else {
                                dialog.dismiss();

                            }

                        }else {
                            dialog.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Tap_Tap.this, "API Call Failed!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    if (COMAND==4){
                        startCountdownTmer(10000,2,-1);

                    }
                }
                req.getCache().clear();
            };
            Response.ErrorListener errListener = error -> {
                dialog.dismiss();
                if (COMAND==4){

                    startCountdownTmer(10000,2,-1);

                }
                if (error instanceof ServerError) {
                    Toast.makeText(Tap_Tap.this, "Server error! Please try again later.", Toast.LENGTH_SHORT).show();
                }
            };
            StringRequest stringrequest = new StringRequest(Request.Method.POST, BuildConfig.HOME, resListener, errListener) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> pram = new HashMap<>();
                    pram.put(BuildConfig.COMMAND, String.valueOf(COMAND));
                    pram.put(BuildConfig.DELTA, mAuth.getCurrentUser().getEmail());
                    pram.put(BuildConfig.NP, String.valueOf(POINTS));
                    if (COMAND==3){
                        pram.put(BuildConfig.getID, String.valueOf(arrayList2.get(currentIndex).getID()));

                    }

                    return pram;
                }
            };

            req.add(stringrequest);


        });
    }

    void Progress(){
        dialog=new Dialog(Tap_Tap.this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_layout);
        dialog.setCancelable(false);
        dialog.show();
    }

    public  StringBuilder stringBuilder(ArrayList<fetch_model> list){
        StringBuilder combinedString = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            combinedString.append(list.get(i).getEmail().replaceAll("(^[^@]{5}|(?!^)\\G)[^@]", "$1*"))
                    .append(" - ")
                    .append(list.get(i).getPoints())
                    .append(" Points\n");
        }


        return combinedString;
    }

    @Override
    public void onClick(View v) {

        if (v==goButton){
            if (click) {
                click = false;
                startSelectorAnimation();
            }
        } else if (v==MENU) {
            Intent intent=new Intent(this,Claim_History.class);
            intent.putExtra(BuildConfig.DELTA, Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
            intent.putExtra("bal",String.valueOf(CURRENT_POINTS));
            intent.putExtra(BuildConfig.BETA_DELTA,APPLINK);
            intent.putExtra(BuildConfig.BETA_GAMA,PRIVACY);
            startActivityForResult(intent, 1);

        }else if (v==FREE) {
            Intent intent=new Intent(this, Free_Free.class);
            startActivity(intent);

        }else if (v==REFER) {
            Intent intent=new Intent(this,Refer_Refer.class);
            intent.putExtra(BuildConfig.EXALM,REFER_CODE);
            intent.putExtra(BuildConfig.BETA_DELTA,APPLINK);
            intent.putExtra("crp",REFER_POINT);
            intent.putExtra(BuildConfig.CM,-1);
            startActivityForResult(intent, 2);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    if (data != null) {
                        CURRENT_POINTS = Integer.parseInt(Objects.requireNonNull(data.getStringExtra("backpoint")));
                        point_tv.setText(String.valueOf(CURRENT_POINTS));
                    }
                } else  if (requestCode==2){
                    if (data != null) {
                        if (CURRENT_POINTS!=CURRENT_POINTS+Integer.parseInt(Objects.requireNonNull(data.getStringExtra("backpoint")))){
                            REFER_POINT=0;
                            startNumberAnimation(CURRENT_POINTS,CURRENT_POINTS+Integer.parseInt(Objects.requireNonNull(data.getStringExtra("backpoint"))),3000);

                        }

                    }

                }

            }
        }


    @Override
    public void onInitializationComplete() {

    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
    }

    public void loadAads () {

        NetworkUtil.checkInternetConnectionWithCallback(Tap_Tap.this, () ->
                UnityAds.load(adUnitId, loadListener));
    }

    public void showAd() {

        if (adLoaded) {
            UnityAds.show(Tap_Tap.this, adUnitId, new UnityAdsShowOptions(), showListener);
        } else {
            startCountdownTmer(WAITING_TIME,3,1);
            WAITING_TIME=WAITING_TIME+10000;
        }
    }}

