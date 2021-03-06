package com.example.meric.knockknockapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


//import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.google.android.gms.vision.Frame;
//import com.google.android.gms.vision.face.Face;
//import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity {

    public static String emailAdress;
    Button addNew1;
    Button setMailBtn;
    public static final int Camera_Req = 9999;
    public static final int GALERY_INTENT = 2;
    private static final int OPEN_CAMERA = 1;
    public boolean uploadDone = false;
    Button mSelectImage;
    Button watchPeople;
    Button speechToText;
    ImageView foto;
    StorageReference mStorage;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //Yeni kişi ekleyebileceği sayfayı açar
        addNew1 = findViewById(R.id.addNew);
        addNew1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewFunc();
            }
        });

        //Email adresini set edebilmek icin yeni bir aktivite baslatir.
        setMailBtn = (Button) findViewById(R.id.setEmail);
        setMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEmailFunc();
            }
        });

        //show  image in Galery
        mStorage = FirebaseStorage.getInstance().getReference();
        mSelectImage = findViewById(R.id.viewhistory);
        mProgressDialog = new ProgressDialog(this);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALERY_INTENT);
            }
        });

        // gelenleri izlemek için kamerayı aç
        watchPeople=findViewById(R.id.videoCamera);
        watchPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  showGuests();
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, OPEN_CAMERA);*/

                Intent swap = new Intent(MainActivity.this, RecognizeActivity.class);
                startActivity(swap);


            }
        });

        speechToText = findViewById(R.id.textToSpeech);
        speechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakNameFunc();
            }
        });
    }


    public void showGuests(){
        startActivity(new Intent(MainActivity.this, WatchPeopleActivity.class));
    }
    public void addNewFunc(){
        startActivity(new Intent(MainActivity.this,AddNewActivity.class));
    }

    public void speakNameFunc(){
        startActivity(new Intent(MainActivity.this,TextToSpeechActivity.class));
    }

    public void setEmailFunc() {
        Intent emailSetter = new Intent(this, SetMailActivity.class);
        startActivity(emailSetter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==Camera_Req){
            Bitmap image=(Bitmap)data.getExtras().get("data");//Çekilen resim id olarak bitmap şeklinde alındı ve imageview'e atandı
            foto.setImageBitmap(image);

            /*
             BURDA 'FOTO' KULLANMA ACTIVITY MAIN'E YENİ BİR IMAGE VIEW EKLE
             ('FOTO' ACTIVITY_ADD_NEW.XML'DE  )
             */
        }

        // photos are downloaded  https://console.firebase.google.com/project/knockapp-bf55d/storage/knockapp-bf55d.appspot.com/files~2FPhotos~2F
        if(requestCode==GALERY_INTENT && resultCode == RESULT_OK && uploadDone == false){
            Uri uri = data.getData();
            // Uri uri = Uri.fromFile(new File(pathArray.get(array_position)));
            mProgressDialog.setMessage("Uploading");
            mProgressDialog.show();
            StorageReference filepath = mStorage.child("Photos/").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this,"Upload is Done!",Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    uploadDone = true;
                }
            });
            uploadDone = true;
        }
        // super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}