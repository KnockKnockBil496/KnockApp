package com.example.meric.knockknockapp;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.example.meric.knockknockapp.MainActivity.imgCounter;
import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;



public class TrainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static String TAG = TrainActivity.class.getSimpleName();
    private CameraBridgeViewBase openCVCamera;
    private Mat rgba,gray;
    private CascadeClassifier classifier;
    private MatOfRect faces;
    private static final int PERMS_REQUEST_CODE = 123;
    private ArrayList<Mat> images;
    public static ArrayList<String> imagesLabels;
    public static ArrayList<String> Isimler;
    private Storage local;
    private String[] uniqueLabels;
    FaceRecognizer recognize;
    AddNewActivity object;
    TextView isim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Stetho.initializeWithDefaults(this);

        if (hasPermissions()){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Permission Granted Before");

        }
        else {
            requestPerms();
        }

        openCVCamera = (CameraBridgeViewBase)findViewById(R.id.java_camera_view);
        openCVCamera.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        openCVCamera.setVisibility(SurfaceView.VISIBLE);
        openCVCamera.setCvCameraViewListener(this);
        local = new Storage(this);
        isim = findViewById(R.id.save_name);

        Button detect = (Button)findViewById(R.id.take_picture_button);
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (gray.total() == 0)
                    Toast.makeText(getApplicationContext(), "Can't Detect Faces", Toast.LENGTH_SHORT).show();
                classifier.detectMultiScale(gray, faces, 1.1, 3, 0 | CASCADE_SCALE_IMAGE, new Size(30, 30));
                if (!faces.empty()) {
                    if (gray.total() == 0) {
                        Log.i(TAG, "Empty gray image");
                        return;
                    }
                    cropedImages(gray);
                    addLabel(isim.getText().toString());
                    //addLabel("DetectedFace" + imgCounter);
                    //imgCounter++;
                    Toast.makeText(getApplicationContext(), "Face Detected: " + /*imagesLabels.get(imagesLabels.size() - 1)*/isim.getText(), Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(getApplicationContext(), "Unknown Face", Toast.LENGTH_SHORT).show();
            }
        });

        // Burayı otomatik yapsın; 3 fotodan sonra kaydetsin direkt;

        Button putFaces = (Button)findViewById(R.id.save_img);
        putFaces.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){

                if(trainfaces()) {
                    Toast.makeText(getApplicationContext(), "Person Saved: " + isim.getText().toString(), Toast.LENGTH_SHORT).show();
                    images.clear();
                    imagesLabels.clear();
                    // Birden fazla kişi eklenebilmesi için bu 2 array temizlenir.
                }
            }
        });

    }

    @SuppressLint("WrongConstant")
    private boolean hasPermissions(){
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.CAMERA};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }
    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,PERMS_REQUEST_CODE);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;
        switch (requestCode){
            case PERMS_REQUEST_CODE:
                for (int res : grantResults){
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }
        if (allowed){
            //user granted all permissions we can perform our task.
            Log.i(TAG, "Permission has been added");
        }
        else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private BaseLoaderCallback callbackLoader = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status) {
                case BaseLoaderCallback.SUCCESS:
                    faces = new MatOfRect();
                    openCVCamera.enableView();

                    images = local.getListMat("images");
                    imagesLabels = local.getListString("imagesLabels");

                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(openCVCamera != null)
            openCVCamera.disableView();


    }
    @Override
    protected void onStop() {
        super.onStop();
        if (images != null && imagesLabels != null) {
            local.putListMat("images", images);
            local.putListString("imagesLabels", imagesLabels);
            Log.i(TAG, "Images have been saved");

          /*  if(trainfaces()) {
                images.clear();
                imagesLabels.clear();
            }*/
        }
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

    public boolean SaveImage() { // xml dosyasını kişinin adıyla oluştur
        File path = new File(Environment.getExternalStorageDirectory(), "TrainedData");
        path.mkdirs();
        String filename =isim.getText().toString()+".xml";
        File file = new File(path, filename);
        recognize.write(file.toString());// save de olabülü
        if(file.exists())
            return true;
        return false;


      /*  File path = new File(Environment.getExternalStorageDirectory(), "TrainedData");
        path.mkdirs();
        String filename ;
        File file;
      for(int i=0; i<imagesLabels.size()-1; i++)
        {
            if(!imagesLabels.get(i).equals(imagesLabels.get(i+1)))
            {
                filename = imagesLabels.get(i)+"_trained_data.xml";
                file = new File(path, filename);
                recognize.save(file.toString());
                if(file.exists())
                    return true;
            }
        }
        return false;*/
    }

    public void cropedImages(Mat mat) {
        Rect rect_Crop=null;
        for(Rect face: faces.toArray()) {
            rect_Crop = new Rect(face.x, face.y, face.width, face.height);
        }
        Mat croped = new Mat(mat, rect_Crop);
        images.add(croped);
    }
    private void addLabel(String string) {
        String label = string.substring(0, 1).toUpperCase(Locale.US) + string.substring(1).trim().toLowerCase(Locale.US); // Make sure that the name is always uppercase and rest is lowercase
        imagesLabels.add(label); // Add label to list of labels
        Log.i(TAG, "Label: " + label);

    }
    private boolean trainfaces() {
        if(images.isEmpty())
            return false;
        List<Mat> imagesMatrix = new ArrayList<>();
        for (int i = 0; i < images.size(); i++)
            imagesMatrix.add(images.get(i));
        Set<String> uniqueLabelsSet = new HashSet<>(imagesLabels); // Get all unique labels
        uniqueLabels = uniqueLabelsSet.toArray(new String[uniqueLabelsSet.size()]); // Convert to String array, so we can read the values from the indices

        int[] classesNumbers = new int[uniqueLabels.length];
        for (int i = 0; i < classesNumbers.length; i++)
            classesNumbers[i] = i; // Create incrementing list for each unique label starting at 1
        int[] classes = new int[imagesLabels.size()];
        for (int i = 0; i < imagesLabels.size(); i++) {
            String label = imagesLabels.get(i);
            for (int j = 0; j < uniqueLabels.length; j++) {
                if (label.equals(uniqueLabels[j])) {
                    classes[i] = classesNumbers[j]; // Insert corresponding number
                    break;
                }
            }
        }
        Mat vectorClasses = new Mat(classes.length, 1, CvType.CV_32SC1); // CV_32S == int
        vectorClasses.put(0, 0, classes); // Copy int array into a vector
        try {
            recognize = LBPHFaceRecognizer.create(3, 8, 8, 8, 200);
            Log.i(TAG, "IMAGES MATRIX: " + imagesMatrix.toString() + "VECTORCLASSES: " + vectorClasses.toString());
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),  "LBPH HATASI", Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            recognize.train(imagesMatrix, vectorClasses);
        }catch(Exception e){

            Toast.makeText(getApplicationContext(), "XML oluşmadı", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(SaveImage())
            return true;

        return false;
    }
}
