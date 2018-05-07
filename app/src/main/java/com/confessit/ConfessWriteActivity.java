package com.confessit;




import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.UUID;

import confessit.evren.com.confessit.R;

/**
 * Created by EVREN on 12.4.2018.
 */

public class ConfessWriteActivity extends AppCompatActivity {
    private EditText editText;
    private ImageView imageView;
    private Button uploadFirebase;
    private ImageButton leftArrow,goChoose;
    private static int REQUESTCODE_GETPHOTO = 3;
    private StorageReference mStorageRef;
    private String filename;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Bitmap btm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confess_write);
        initComponent();
        eventHandlers();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    private void eventHandlers() {
        uploadFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filename==null){
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userEmail = user.getEmail();
                    String userComment = editText.getText().toString();
                    UUID uuid = UUID.randomUUID();
                    String uuidString = uuid.toString();
                    uuidString ="-"+uuidString;
                    myRef.child("Posts").child(uuidString).child("useremail").setValue(userEmail);
                    myRef.child("Posts").child(uuidString).child("comment").setValue(userComment);
                    myRef.child("Posts").child(uuidString).child("downloadurl").setValue(null);

                    Toast.makeText(ConfessWriteActivity.this,"Post Gönderme Başarılı",Toast.LENGTH_SHORT);

                    Intent i = new Intent(ConfessWriteActivity.this, ConfessActivity.class);
                    startActivity(i);

                }
                else {
                    filename = "images/"+filename;
                    //Log.d("uuuid",selected.toString());
                    StorageReference storageReference = mStorageRef.child(filename);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    btm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // sıkıştırma
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = storageReference.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String dowlandURL = taskSnapshot.getDownloadUrl().toString();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userEmail = user.getEmail();
                            String userComment = editText.getText().toString();
                            UUID uuid = UUID.randomUUID();
                            String uuidString = uuid.toString();
                            uuidString ="-"+uuidString;
                            myRef.child("Posts").child(uuidString).child("useremail").setValue(userEmail);
                            myRef.child("Posts").child(uuidString).child("comment").setValue(userComment);
                            myRef.child("Posts").child(uuidString).child("downloadurl").setValue(dowlandURL);

                            Toast.makeText(ConfessWriteActivity.this,"Post Gönderme Başarılı",Toast.LENGTH_SHORT);

                            Intent i = new Intent(ConfessWriteActivity.this, ConfessActivity.class);
                            startActivity(i);
                        }
                    });

                }







            }
        });

        goChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ConfessWriteActivity.this,ChooseImageActivity.class);
                startActivityForResult(i,REQUESTCODE_GETPHOTO);

            }
        });
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfessWriteActivity.this.finish();
            }
        });
        Drawable drawable = getResources().getDrawable(R.drawable.logo);
        Drawable smallDrawble = getResources().getDrawable(R.drawable.logo_small);
        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            imageView.setImageDrawable(smallDrawble);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                            params.gravity = Gravity.CENTER;
                            imageView.setLayoutParams(params);
                        } else {
                            imageView.setImageDrawable(drawable);
                        }
                    }
                });
    }


    private void initComponent(){
        uploadFirebase = findViewById(R.id.btn_uploadfirebase);
        goChoose = findViewById(R.id.go_choose);
        leftArrow = findViewById(R.id.left_arrow);
        editText = findViewById(R.id.edtconfess_write);
        imageView = findViewById(R.id.confess_logo);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUESTCODE_GETPHOTO && resultCode == RESULT_OK && data != null){
            Bitmap bmp = null;
            filename = data.getStringExtra("image");
            imageUri =(Uri) data.getParcelableExtra("imageUri");
            Log.d("uriiiiiii", String.valueOf(imageUri));

            try {
                FileInputStream is = this.openFileInput(filename);
                btm = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else {
            filename=null;
            Toast.makeText(ConfessWriteActivity.this,"Resim Seçme İşlemi Başarısız..!",Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }


}
