package com.example.meric.knockknockapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Guest extends AppCompatActivity {

    Button goruntule;
    EditText dosya;
    ImageView foto;
    final int GALERY_INTENT=1;
    StorageReference mStorage;
    ProgressDialog mProgressDialog;
    String imgDecodableString;

    public boolean uploadDone = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foto = findViewById(R.id.imageViewGuest);
        dosya=findViewById(R.id.GuestDetail);
        setContentView(R.layout.activity_guest);
        mStorage = FirebaseStorage.getInstance().getReference();

        mProgressDialog = new ProgressDialog(this);
        goruntule = findViewById(R.id.goruntule);


        goruntule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALERY_INTENT);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==GALERY_INTENT && resultCode == RESULT_OK && uploadDone == false){

                    Uri selectedImage = data.getData();
                    dosya=findViewById(R.id.GuestDetail);
                    String detail= selectedImage.getPath().substring(selectedImage.getPath().lastIndexOf('/')+1);

                    //tarihnde _// saatinde _// _aybike aydemir geldi




                    dosya.setText(detail);
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    imgDecodableString = cursor.getString(columnIndex);

                    cursor.close();

                     foto = (ImageView) findViewById(R.id.imageViewGuest);

                     foto.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));




            Uri uri = selectedImage;

            StorageReference filepath = mStorage.child("Photos/").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Guest.this,"Upload is Done!",Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    uploadDone = true;
                }
            });
            uploadDone = true;



        }

    }
}