package com.example.meric.knockknockapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetMailActivity extends AppCompatActivity {
    Button getMailBtn;
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
}
