package com.example.meric.knockknockapp;

//import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


//import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static String emailAdress;
    Button addNew1;
    Button setMailBtn;

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
    }

    public void addNewFunc(){
        startActivity(new Intent(MainActivity.this,AddNewActivity.class));
    }

    public void setEmailFunc() {
        Intent emailSetter = new Intent(this, SetMailActivity.class);
        startActivity(emailSetter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
