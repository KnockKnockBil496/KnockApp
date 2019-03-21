package com.example.meric.knockknockapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_DCIM;

import android.graphics.drawable.BitmapDrawable;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


/*
    Bu class'ta fotoğraf çek butonuyla eklenecek kişinin fotoğrafı çekilir,
    kullanacının gireceği isim dosyanın adı olarak
    kaydedilir.
 */

public class AddNewActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    ImageView foto;
    Button captr,cancel;
    EditText dosya;
    Button kaydet;
    Button yukle;
    private static Uri fileUri = null;
    boolean dirExists = true;
    private static final int CAMERA_IMAGE_REQUEST=1;
    private static int RESULT_LOAD_IMG = 1;
    StorageReference mStorage;
    ProgressDialog mProgressDialog;
    public static final int GALERY_INTENT = 2;
    private static final int OPEN_CAMERA = 1;
    public boolean uploadDone = false;
    public static final int Camera_Req = 9999;
    public static boolean save=false;
    private final int ACTIVITY_CHOOSE_PHOTO = 1;
    private String downFotoName="";
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


//        mStorage = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_add_new);
        foto = findViewById(R.id.imageView);//küçük resim olarak gösterildiği yer
        captr = findViewById(R.id.capture);//fotoğraf çekme butonu
        cancel = findViewById(R.id.cancel_btn);//yeni kişi ekleme sayfasından çıkılır


        final DetectFaceActivity faceDet = new DetectFaceActivity();

        captr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Intent kamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Resim çekme isteği
//                Toast.makeText(AddNewActivity.this,"Kamera açıldı!",Toast.LENGTH_LONG).show();
//                startActivityForResult(kamera, Camera_Req);
                Intent swap = new Intent(AddNewActivity.this, TrainActivity.class);
                startActivity(swap);



            }
        });


        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }//onCreate

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return null;
    }


}