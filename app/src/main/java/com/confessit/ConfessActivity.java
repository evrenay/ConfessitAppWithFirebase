package com.confessit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import confessit.evren.com.confessit.R;

/**
 * Created by EVREN on 18.3.2018.
 */

public class ConfessActivity extends BaseActivity implements View.OnClickListener {
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG ="test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confess_main);
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        Bundle extras = getIntent().getExtras();
        findViewById(R.id.sign_out_button).setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }
    private void gmailSignOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        LoginManager.getInstance().logOut();
        ConfessActivity.this.finish();}


    private void updateUI(FirebaseUser user) {

        if (user != null) {
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
    startActivity(intent);
        }
        hideProgressDialog();
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_out_button) {
          gmailSignOut();
        }
    }
}
