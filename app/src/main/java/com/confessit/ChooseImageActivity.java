package com.confessit;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import confessit.evren.com.confessit.R;

/**
 * Created by EVREN on 13.4.2018.
 */

public class ChooseImageActivity extends AppCompatActivity {
    private static final int REQUESTCODE_GALLERY=1;
    private static final int REQUESTCODE_PERMISSION=2;
    private ImageButton backFragment;
    private ImageView chooseImage;
    private FirebaseAuth mAuth;
    private Button upload;
    private String filename,userName = "";;
    private Bitmap bitmap;
    private Uri selected;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confess_photo);
        initComponent();
        eventHandlers();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


    }

    private void initComponent() {
        backFragment = findViewById(R.id.left_arrow_choose);
        chooseImage = findViewById(R.id.img_select_image);
        upload = findViewById(R.id.btn_select_image);
    }

    private void eventHandlers() {
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String FirebaseUserName = user.getDisplayName().toString().trim();
                    String[] words = FirebaseUserName.split(" ");// boşlukları parçala
                    for(int i = 0; i<words.length; i++){

                        userName += words[i];
                    }
                    String userId = user.getUid().toString().trim();
                    String date = getDate();
                    UUID uuid = UUID.randomUUID();
                    String uuidString = uuid.toString();
                    filename = uuidString+"_"+userName+"_"+userId+"_"+date+".jpg";
                    FileOutputStream stream = ChooseImageActivity.this.openFileOutput(filename, Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    //Cleanup
                    stream.close();
                    bitmap.recycle();


                    //Pop intent
                    Intent in1 = new Intent(ChooseImageActivity.this, ConfessWriteActivity.class);
                    in1.putExtra("image", filename);
                    in1.putExtra("imageUri", selected);
                    Log.d("uriiiiiii", String.valueOf(selected));
                    setResult(RESULT_OK,in1);
                    ChooseImageActivity.this.finish();
                }

                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},REQUESTCODE_PERMISSION); // izin vermemişse izin iste
                }
                else {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i,REQUESTCODE_GALLERY);


                }
            }
        });
         backFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImageActivity.this.finish();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUESTCODE_PERMISSION ){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,REQUESTCODE_GALLERY);

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUESTCODE_GALLERY && resultCode == RESULT_OK && data != null){
            selected = data.getData();
            try {
               bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selected);
                chooseImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private String getDate(){
        Date simdikiZaman = new Date();
        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        String tarih = df.format(simdikiZaman);
        return tarih;
    }
}
