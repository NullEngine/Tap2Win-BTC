package com.codekroy.tap2winbtc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class Splash_Splash extends AppCompatActivity {

    ImageView appLogo;
    TextView appName;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_splash);
        appLogo=findViewById(R.id.ic);
        appName=findViewById(R.id.appname);
        mAuth=FirebaseAuth.getInstance();
        animateLogoAndName();

    }
    private void animateLogoAndName() {
        ObjectAnimator scaleXLogo = ObjectAnimator.ofFloat(appLogo, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleYLogo = ObjectAnimator.ofFloat(appLogo, "scaleY", 0.5f, 1f);
        ObjectAnimator fadeInLogo = ObjectAnimator.ofFloat(appLogo, "alpha", 0f, 1f);
        ObjectAnimator fadeInName = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f);
        fadeInName.setStartDelay(500);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXLogo, scaleYLogo, fadeInLogo,fadeInName);
        animatorSet.setDuration(1500);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {}
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                NetworkUtil.checkInternetConnectionWithCallback
                        (Splash_Splash.this, new NetworkUtil.InternetCheckCallback() {
                    @Override
                    public void onInternetAvailable() {
                        ALFA_GET();
                    }
                });
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animation) {}
            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {}
        });
    }
    public void ALFA_GET() {
        RequestQueue req = Volley.newRequestQueue(getApplicationContext());
        Response.Listener<String> resListener = response -> {
            try {
                JSONObject gso = new JSONObject(response);
                String success = gso.getString(BuildConfig.THREE_DELTA);
                String message = gso.getString(BuildConfig.ULTRA_DELTA);
                JSONObject obg = gso.getJSONObject(BuildConfig.I_DELTA);
                if (success.equals(BuildConfig.COT_DELTA)) {
               int VERSION= obg.getInt(BuildConfig.BETA_ULFA);
               String applink=obg.getString(BuildConfig.BETA_DELTA);
               String policyLink=obg.getString(BuildConfig.BETA_GAMA);
                    String MSG=obg.getString(BuildConfig.BETA_BETA);
                    String unityGameID =obg.getString("unityGameID");
                    String adUnitId =obg.getString("adUnitId");
                    int testMODE= obg.getInt("testmode");

                    try {
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        int versionCode = pInfo.versionCode;
                        if (VERSION==versionCode){
                            if (mAuth.getCurrentUser()!=null){
                                Intent  i = new Intent(Splash_Splash.this, Tap_Tap.class);
                                i.putExtra(BuildConfig.BETA_DELTA,applink);
                                i.putExtra(BuildConfig.BETA_GAMA,policyLink);
                                i.putExtra(BuildConfig.CM,-1);
                                i.putExtra("unityGameID",unityGameID);
                                i.putExtra("adUnitId",adUnitId);
                                i.putExtra("testmode",testMODE);
                                startActivity(i);
                                finish();
                            } else {
                                Intent i = new Intent(Splash_Splash.this, User_Auth.class);
                                i.putExtra(BuildConfig.BETA_DELTA,applink);
                                i.putExtra(BuildConfig.BETA_GAMA,policyLink);
                                i.putExtra("unityGameID",unityGameID);
                                i.putExtra("adUnitId",adUnitId);
                                i.putExtra("testmode",testMODE);
                                startActivity(i);
                                finish();
                            }
                        } else {
                            AlertDialog.Builder builder= new AlertDialog.Builder(Splash_Splash.this);
                            builder.setCancelable(false);
                            builder.setTitle("New Version!");
                            builder.setMessage(MSG);
                            builder.setPositiveButton(
                                    "UPDATE",
                                    (dialog, id) -> {
                                        Uri uri1 = Uri.parse(applink);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri1);
                                        startActivity(intent);
                                        finish();
                                    });
                            builder.setNegativeButton(
                                    "CLOSE",
                                    (dialog, id) -> finish());
                            builder.create();
                            builder.show();
                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (success.equals("0")) {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "error 403", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {

                showRetryDialog();
            }

            req.getCache().clear();
        };
        Response.ErrorListener errListener = error ->
            showRetryDialog();

        StringRequest stringrequest
                = new StringRequest(Request.Method.GET, BuildConfig.SPLASH, resListener, errListener) {
        };

        req.add(stringrequest);
    }
    private void showRetryDialog() {
        new AlertDialog.Builder(this)
                .setTitle("API Call Failed!")
                .setMessage("Would you like to try again?")
                .setPositiveButton("Retry", (dialog, which) -> NetworkUtil.checkInternetConnectionWithCallback(Splash_Splash.this, this::ALFA_GET))
                .setNegativeButton("Cancel", (dialog, which) -> finishAffinity())
                .show();
    }

}
