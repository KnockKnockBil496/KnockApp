package com.example.meric.knockknockapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Message;
import android.se.omapi.Session;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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

        //Yazilan email adresini almaya yarar.
        getMailBtn = (Button) findViewById(R.id.btnEmailSetter);
        getMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEmailFunc();
                sendEmail();
//                MailSend mail = new MailSend(null, "burakshn96@gmail.com", "KnockKnock Deneme", "Merhaba merhaba asdasdasdasd...");
//                mail.execute();
//                mai
//                mail.onPreExecute();
//                mail.doInBackground();
            }
        });
    }

    public void getEmailFunc() {
        EditText text = (EditText)findViewById(R.id.textEmailSetter) ;
        MainActivity.emailAdress = text.getText().toString();

        int duration = Toast.LENGTH_SHORT;
        Context context = getApplicationContext();

        Toast toast = Toast.makeText(context, MainActivity.emailAdress, duration);
        toast.show();
    }

    public void sendEmail() {
        //Creating SendMail object
        MailSend sm = new MailSend(this, "knockknockapplication@gmail.com", "TEST", "TESTEEEEEE");

        //Executing sendmail to send email
        sm.execute();
    }

}
