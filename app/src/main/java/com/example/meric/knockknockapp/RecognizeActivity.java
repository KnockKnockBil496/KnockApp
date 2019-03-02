package com.example.meric.knockknockapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.stetho.Stetho;
import android.speech.tts.TextToSpeech.OnInitListener;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;


public class RecognizeActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2,OnInitListener {
    private static String TAG = TrainActivity.class.getSimpleName();
    private CameraBridgeViewBase openCVCamera;
    private Mat rgba,gray;
    private CascadeClassifier classifier;
    private MatOfRect faces;
    private ArrayList<String> imagesLabels;
    private int label[] = new int[1];
    private double predict[] = new double[1];
    private Storage local;
    private FaceRecognizer recognize;
    private boolean stopper = false;
    ArrayList<String> names = new ArrayList<>();
    private TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;

    private BaseLoaderCallback callbackLoader = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status) {
                case BaseLoaderCallback.SUCCESS:
                    faces = new MatOfRect();
                    openCVCamera.enableView();
                    recognize = LBPHFaceRecognizer.create(3,8,8,8,200);
                    imagesLabels = local.getListString("imagesLabels");

                    if(loadData())
                        Log.i(TAG, "Trained data loaded successfully");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };
    private boolean loadData() {
        String filename;
        for(int i=0; i<imagesLabels.size(); i++) {
            filename = FileUtils.loadTrained();
            names.add(filename);
        }
        if (names.isEmpty())
            return false;
        else {
            for(int i=0; i<imagesLabels.size(); i++) {
                recognize.read(names.get(i));
                return true;
            }
            return false;
        }

    }
    private void recognizeImage(Mat mat) {
        Rect rect_Crop=null;
        for(Rect face: faces.toArray()) {
            rect_Crop = new Rect(face.x, face.y, face.width, face.height);
        }
        Mat croped = new Mat(mat, rect_Crop);
        recognize.predict(croped, label, predict);
        if(label[0] != -1 && (int)predict[0] < 125) {
            Toast.makeText(getApplicationContext(), "Welcome " + imagesLabels.get(label.length-1) + "", Toast.LENGTH_SHORT).show();
            String name = "Merhaba"+imagesLabels.get(label.length-1);
            speakWords(name);
            stopper = true;
        }
        else
            Toast.makeText(getApplicationContext(), "You're not the right person", Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            }
            else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recognize_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Stetho.initializeWithDefaults(this);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        openCVCamera = (CameraBridgeViewBase) findViewById(R.id.java_camera_view2);
        openCVCamera.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        openCVCamera.setVisibility(SurfaceView.VISIBLE);
        openCVCamera.setCvCameraViewListener(this);
        local = new Storage(this);

        //bu thread yardimi ile her 0.5 saniyede bir yüz taninmaya calisiyor
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!stopper) {
                    try {
                        Thread.sleep(500);  //1000ms = 1 sec
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (gray.total() == 0)
                                    Toast.makeText(getApplicationContext(), "Can't Detect Faces", Toast.LENGTH_SHORT).show();
                                classifier.detectMultiScale(gray, faces, 1.1, 3, 0 | CASCADE_SCALE_IMAGE, new Size(30, 30));
                                if (!faces.empty()) {
                                    if (faces.toArray().length > 1)
                                        Toast.makeText(getApplicationContext(), "Mutliple Faces Are not allowed", Toast.LENGTH_SHORT).show();
                                    else {
                                        if (gray.total() == 0) {
                                            Log.i(TAG, "Empty gray image");
                                            return;
                                        }
                                        recognizeImage(gray);
                                    }
                                } else
                                    Toast.makeText(getApplicationContext(), "Unknown Face", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();

        final Button recognize = (Button) findViewById(R.id.recognize_button);
        recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopper = false;
            }
        });
    }
////////  recognize butonu artik gerekli degil. Onun yerine yukarida thread yazildi.

//        final Button recogniz = (Button)findViewById(R.id.recognize_button);
//        recogniz.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(gray.total() == 0)
//                    Toast.makeText(getApplicationContext(), "Can't Detect Faces", Toast.LENGTH_SHORT).show();
//                classifier.detectMultiScale(gray,faces,1.1,3,0|CASCADE_SCALE_IMAGE, new Size(30,30));
//                if(!faces.empty()) {
//                    if(faces.toArray().length > 1)
//                        Toast.makeText(getApplicationContext(), "Mutliple Faces Are not allowed", Toast.LENGTH_SHORT).show();
//                    else {
//                        if(gray.total() == 0) {
//                            Log.i(TAG, "Empty gray image");
//                            return;
//                        }
//                        recognizeImage(gray);
//                    }
//                }else
//                    Toast.makeText(getApplicationContext(), "Unknown Face", Toast.LENGTH_SHORT).show();
//            }
//        });


    @Override
    protected void onPause() {
        super.onPause();
        if(openCVCamera != null)
            openCVCamera.disableView();


    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(openCVCamera != null)
            openCVCamera.disableView();
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(OpenCVLoader.initDebug()) {
            Log.i(TAG, "System Library Loaded Successfully");
            callbackLoader.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Log.i(TAG, "Unable To Load System Library");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, callbackLoader);
        }
    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        rgba = new Mat();
        gray = new Mat();
        classifier = FileUtils.loadXMLS(this, "lbpcascade_frontalface_improved.xml");
    }

    @Override
    public void onCameraViewStopped() {
        rgba.release();
        gray.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mGrayTmp = inputFrame.gray();
        Mat mRgbaTmp = inputFrame.rgba();

        int orientation = openCVCamera.getScreenOrientation();
        if (openCVCamera.isEmulator()) // Treat emulators as a special case
            Core.flip(mRgbaTmp, mRgbaTmp, 1); // Flip along y-axis
        else {
            switch (orientation) { // RGB image
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    Core.flip(mRgbaTmp, mRgbaTmp, 0); // Flip along x-axis
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    Core.flip(mRgbaTmp, mRgbaTmp, 1); // Flip along y-axis
                    break;
            }
            switch (orientation) { // Grayscale image
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    Core.transpose(mGrayTmp, mGrayTmp); // Rotate image
                    Core.flip(mGrayTmp, mGrayTmp, -1); // Flip along both axis
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    Core.transpose(mGrayTmp, mGrayTmp); // Rotate image
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    Core.flip(mGrayTmp, mGrayTmp, 1); // Flip along y-axis
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    Core.flip(mGrayTmp, mGrayTmp, 0); // Flip along x-axis
                    break;
            }
        }
        gray = mGrayTmp;
        rgba = mRgbaTmp;
        Imgproc.resize(gray, gray, new Size(200,200.0f/ ((float)gray.width()/ (float)gray.height())));
        return rgba;
    }

    public void speakWords(String speech) {

        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }
}

