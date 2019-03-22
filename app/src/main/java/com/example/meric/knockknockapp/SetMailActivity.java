package com.example.meric.knockknockapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SetMailActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button getMailBtn;
    Context context = null;
    public javax.mail.Session session = null;
    ProgressDialog progressdialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_mail);

        ActivityCompat.requestPermissions(SetMailActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        //Yazilan email adresini almaya yarar.
        getMailBtn = (Button) findViewById(R.id.btnEmailSetter);
        getMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                getEmailFunc();
                sendEmail();
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(SetMailActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

//    public void getEmailFunc() {
//        EditText text = (EditText)findViewById(R.id.textEmailSetter) ;
//        MainActivity.emailAdress = text.getText().toString();
//
//        int duration = Toast.LENGTH_SHORT;
//        Context context = getApplicationContext();
//
//        Toast toast = Toast.makeText(context, MainActivity.emailAdress, duration);
//        toast.show();
//    }

    public void sendEmail() {
        //Creating SendMail object
        MailSend sm = new MailSend(this, "knockknockapplication@gmail.com", "Knock Knock App Daily Summary", "Here are your visitors today.", "false");

        //Executing sendmail to send email
        sm.execute();
    }

}
