package com.codekroy.tap2winbtc;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TimerTask;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class User_Auth extends AppCompatActivity implements View.OnClickListener {

    TextView privacy;
    Dialog dialog;
    String APPLINK,PRIVACY,adUnitId,unityGameID;

    GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private CardView cardView;
    int testMODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            APPLINK= bundle.getString(BuildConfig.BETA_DELTA);
            PRIVACY=  bundle.getString(BuildConfig.BETA_GAMA);
            unityGameID=  bundle.getString("unityGameID");
            adUnitId=  bundle.getString("adUnitId");
            testMODE=  bundle.getInt("testmode");
        }
        mAuth = FirebaseAuth.getInstance();
        privacy = findViewById(R.id.privacy_text);
        String text = "<a href=''>Privacy Policy</a>";
        privacy.setText(Html.fromHtml(text));
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.HEXA_HEXA)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        cardView = findViewById(R.id.signin_card);
        cardView.setOnClickListener(this);
        privacy.setOnClickListener(this);
    }
    public void OnSignInButtonClick() {
        NetworkUtil.checkInternetConnectionWithCallback
                (User_Auth.this, new NetworkUtil.InternetCheckCallback() {
                    @Override
                    public void onInternetAvailable() {
                        try {
                            Intent choose = mGoogleSignInClient.getSignInIntent();
                            startActivityForResult(choose, RC_SIGN_IN);
                        } catch (Exception e) {
                          //  Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }

                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
               // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        YYYY_GAMA(user);
                    } else {
                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }
    public String refer_code_generate(int length) {
        char[] chars = BuildConfig.CODE_HEXA.toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
    public void YYYY_GAMA(final FirebaseUser user) {
        RequestQueue req = Volley.newRequestQueue(User_Auth.this);
        Response.Listener<String> resListener = response -> {
            try {
                JSONObject gso = new JSONObject(response);
                String THREE_DELTA = gso.getString(BuildConfig.THREE_DELTA);
                String ULTRA_DELTA = gso.getString(BuildConfig.ULTRA_DELTA);
                if(THREE_DELTA.equals(BuildConfig.N_DELTA)) {
                    Intent i = new Intent(User_Auth.this, Refer_Refer.class);
                    i.putExtra(BuildConfig.CM,1);
                    i.putExtra(BuildConfig.BETA_DELTA,APPLINK);
                    i.putExtra(BuildConfig.BETA_GAMA,PRIVACY);
                    i.putExtra("unityGameID",unityGameID);
                    i.putExtra("adUnitId",adUnitId);
                    i.putExtra("testmode",testMODE);
                    startActivity(i);
                    finishAffinity();
                } else if (THREE_DELTA.equals(BuildConfig.P_DELTA)) {
                    Toast.makeText(User_Auth.this, ULTRA_DELTA, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    mAuth.signOut();
                } else if (THREE_DELTA.equals(BuildConfig.COT_DELTA)) {
                    Intent i = new Intent(User_Auth.this, Tap_Tap.class);
                    i.putExtra(BuildConfig.CM,-1);
                    i.putExtra(BuildConfig.BETA_DELTA,APPLINK);
                    i.putExtra(BuildConfig.BETA_GAMA,PRIVACY);
                    i.putExtra("unityGameID",unityGameID);
                    i.putExtra("adUnitId",adUnitId);
                    i.putExtra("testmode",testMODE);
                    startActivity(i);
                    finishAffinity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(User_Auth.this, "Api call Failed!", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                dialog.dismiss();
            }
            req.getCache().clear();
        };
        Response.ErrorListener errListener = error -> {
            Toast.makeText(User_Auth.this,  error.getMessage(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            mAuth.signOut();
        };
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BuildConfig.YELLOW, resListener, errListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(BuildConfig.HEXA, user.getDisplayName());
                params.put(BuildConfig.DELTA, user.getEmail());
                params.put(BuildConfig.EXALM, "T" + refer_code_generate(5));
                params.put(BuildConfig.TEXA_DELTA, user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
                return params;
            }
        };
        req.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v == cardView) {
            Progress();
            OnSignInButtonClick();
        } else if (v==privacy) {
            Uri uri=Uri.parse(PRIVACY);
            Intent intent= new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);

        }
    }

   void Progress(){
        dialog=new Dialog(User_Auth.this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_layout);
        dialog.setCancelable(false);
        dialog.show();
    }

}
