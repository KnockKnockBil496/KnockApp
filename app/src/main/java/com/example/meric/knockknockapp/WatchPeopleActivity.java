package com.example.meric.knockknockapp;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
/*
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;
import android.widget.ImageView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;*/


public class WatchPeopleActivity  extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase cameraManager;
    Button cameraClose;
    private BaseLoaderCallback baseLoaderCallback= new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            //super.onManagerConnected(status);
            //cameraManager.enableView();

            // !!!   Alt kısım çalıştırıldığında buton hata veriyor uygulama kapanıyor.
            // Buranın çalışması lazım kamerayı açabilmek için.  !!!!

            //Daha sonra bu kısma yüz tanıma için cascade'ler yüklenecek

           /* switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Toast.makeText(WatchPeopleActivity.this,"OpenCv loaded",Toast.LENGTH_LONG).show();
                   // cameraManager.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }*/
        }
    };


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);
       // setContentView(R.layout.face_rec);

        if(!OpenCVLoader.initDebug())
        {
            //opencv kütüphanelerini yükledi
           // OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11,this,baseLoaderCallback);

        }


       // cameraManager.enableView();
      /*  cameraManager = findViewById(R.id.camera);
        cameraManager.setVisibility(View.VISIBLE);
        cameraManager.setCvCameraViewListener(this);

        cameraManager.setCameraIndex(0); // 0 indeksi arka kameradır

        cameraClose = findViewById(R.id.close);
        cameraClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraManager.disableView();
            }
        });

*/


    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        return inputFrame.rgba();
        //return null;
    }
}
