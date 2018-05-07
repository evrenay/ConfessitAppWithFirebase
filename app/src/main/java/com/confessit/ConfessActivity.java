package com.confessit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import confessit.evren.com.confessit.R;

/**
 * Created by EVREN on 18.3.2018.
 */

public class ConfessActivity extends BaseActivity  {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG ="test33333";
    private int LoginTag;
    private FloatingActionButton fab;
    private ArrayList<Posts> userPostsFromFB;
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private RecyclerView recyclerView;
    private PostsAdapter adapter_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confess_main);
        Init();
        FirebaseConfig();
        getDataFromFirebase();
        EventHandlers();
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter_items);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void Init(){
        recyclerView = findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        userPostsFromFB = new ArrayList<Posts>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        recyclerView.setLayoutManager(layoutManager);
    }




    private void EventHandlers(){
            adapter_items = new PostsAdapter(userPostsFromFB, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("position", "Tıklanan Pozisyon:" + position);
                Posts posts = userPostsFromFB.get(position);
                Toast.makeText(getApplicationContext(),"pozisyon:"+" "+position+" "+"Ad:"+posts.getUserEmail(),Toast.LENGTH_SHORT).show();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConfessActivity.this,ConfessWriteActivity.class);
                startActivity(i);
            }
        });

    }
    private void FirebaseConfig(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            myRef = firebaseDatabase.getReference();
            for (UserInfo userInfo : user.getProviderData()) {
                if (userInfo.getProviderId().equals("facebook.com")) {
                    LoginTag=2;
                }

                if (userInfo.getProviderId().equals("google.com")) {
                    LoginTag=1;
                }
            }
        }
        else {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

        }
        hideProgressDialog();
    }

    private void getDataFromFirebase(){

        DatabaseReference newReference = firebaseDatabase.getReference("Posts");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    if(hashMap.get("downloadurl")==null){
                        userPostsFromFB.add(new Posts.Builder(Posts.TYPE_MESSAGE_COMMENT).userComment(hashMap.get("comment")).userEmail(hashMap.get("useremail")).build());
                        adapter_items.notifyDataSetChanged();
                    }
                    else {
                        userPostsFromFB.add(new Posts.Builder(Posts.TYPE_MESSAGE_PHOTO).userComment(hashMap.get("comment")).userEmail(hashMap.get("useremail")).imageUrl(hashMap.get("downloadurl")).build());
                        adapter_items.notifyDataSetChanged();
                    }


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ConfessActivity.this,"Bir Hata Oluştu...",Toast.LENGTH_LONG).show();
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(LoginTag==1){
                mAuth.signOut();

                // Google sign out
                mGoogleSignInClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ConfessActivity.this,"Hoşçakalın",Toast.LENGTH_LONG).show();
                                Intent i = new Intent(ConfessActivity.this,MainActivity.class);
                                startActivity(i);
                            }
                        });

            }
            else if(LoginTag ==2 ){
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                Intent i = new Intent(ConfessActivity.this,MainActivity.class);
                startActivity(i);

            }
        }

        return super.onOptionsItemSelected(item);
    }






}
