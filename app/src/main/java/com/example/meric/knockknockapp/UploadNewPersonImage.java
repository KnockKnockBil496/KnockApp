package com.example.meric.knockknockapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadNewPersonImage {

    StorageReference mStorage;
    ProgressDialog mProgressDialog;
    Activity context;
    public void UploadImage(Uri uri){


        mStorage = FirebaseStorage.getInstance().getReference();


        mProgressDialog.setMessage("Uploading");
        mProgressDialog.show();
        StorageReference filepath = mStorage.child("NewPersonImage/").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast toast = Toast.makeText(context.getApplicationContext(), "Kişi Kaydedildi!", Toast.LENGTH_SHORT);
                mProgressDialog.dismiss();

            }
        });
    }
}
