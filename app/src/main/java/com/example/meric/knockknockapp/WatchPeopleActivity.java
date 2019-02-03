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
import com.google.android.gms.vision.face.FaceDetector;


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
        //setContentView(R.layout.camera_view);
        setContentView(R.layout.face_rec);

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


        //   --- Kamera açılmadığı için bir foto üstünde denedim ---


        // Fotoğraf yükleyip fotodaki yüzleri kare içine alana kod;

        ImageView myImageView = (ImageView) findViewById(R.id.imgview);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
         Bitmap myBitmap = BitmapFactory.decodeResource(
                    getApplicationContext().getResources(),
                    R.drawable.test2,
                    options);
            //okunacak dosya adı test2


        FaceDetector faceDetector= new FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(true).build();
           /* if (!faceDetector.isOperational()) {
                new AlertDialog.Builder(.getContext()).setMessage("Could not set up the face detector!").show();
                return;
            }*/

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();//görüntü bitmap olmalı

        SparseArray<Face> faces = faceDetector.detect(frame);

        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);

        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);


        // Yüz'ün etrafında kırmızı kare çizmek için;

        for(int i=0; i<faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
        }
        myImageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

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
