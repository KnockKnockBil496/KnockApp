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
        dosya = findViewById(R.id.dosyaAdi);//fotoğrafın adı olark kaydedilecek
        kaydet = findViewById(R.id.saveBtn);//foto galeriye kaydedilir  !!?? DB'ye kaydetmeli !!!??
        cancel = findViewById(R.id.cancel_btn);//yeni kişi ekleme sayfasından çıkılır
        yukle = findViewById(R.id.yukle);//var olan fotoyu kullanmak için


        final DetectFaceActivity faceDet = new DetectFaceActivity();

        captr.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {


                Intent kamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Resim çekme isteği
                Toast.makeText(AddNewActivity.this,"Kamera açıldı!",Toast.LENGTH_LONG).show();
                startActivityForResult(kamera, Camera_Req);

            }
        });

       /* yukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  openGallery();
            }
        });*/

        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // save = true;
                try {
                    createImageFile(dosya.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Toast.makeText(AddNewActivity.this, "Kaydedildi!", Toast.LENGTH_SHORT).show();
               // save = false;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        //                --- YÜKLENEN FOTOĞRAFLA KİŞİYİ KAYDETME ---


        yukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AddNewActivity.this, DetectFaceActivity.class));
               /* openGallery();
                if(!downFotoName.equals("")){ redSqr();}*/
            }
        });

    }//onCreate

    //Galeriyi açıp foto seçmeyi sağlamalı
    public void openGallery()
    {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("image/*");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(chooseFile, "Choose a photo"), ACTIVITY_CHOOSE_PHOTO);
        Toast.makeText(AddNewActivity.this, downFotoName, Toast.LENGTH_SHORT).show();

    }

    public String parseUriToFilename(Uri uri) {
        String selectedImagePath = null;
        String filemanagerPath = uri.getPath();
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
    // Here you will get a null pointer if cursor is null
    // This can be if you used OI file manager for picking the media
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            selectedImagePath = cursor.getString(column_index);
        }

        if (selectedImagePath != null) {
            return selectedImagePath;
        }
        else if (filemanagerPath != null) {

            return filemanagerPath;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ACTIVITY_CHOOSE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    String filename = parseUriToFilename(data.getData());
                    if (filename != null) {
                        downFotoName = filename;
                       // moustachify(filename, null);
                    }
                }
                break;
            }
        }
    }

    public void redSqr()
    {
        // galeriden gelen dosyanın adını okumalı  ( openGalley metod )

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap myBitmap = (Bitmap) BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                Integer.parseInt(downFotoName),
                options);

        FaceDetector faceDetector= new FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(true).build();


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
        foto.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));


    }


    public void detectFace(View view){

        //Intent intent = new Intent(this,VideoFaceDetectionActivity.class);
        //startActivity(intent);

    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==Camera_Req){

            Bitmap image=(Bitmap)data.getExtras().get("data");//Çekilen resim id olarak bitmap şeklinde alındı ve imageview'e atandı
            foto.setImageBitmap(image);// fotoğrafı uygulamada gösterir
        }




        //çekilen fotoğrafın DB'ye atılması   ?????
       /* if(save == true) {
            if (requestCode == GALERY_INTENT && resultCode == RESULT_OK && uploadDone == false) {
                Uri uri = data.getData();
                mProgressDialog.setMessage("Uploading");
                mProgressDialog.show();
                StorageReference filepath = mStorage.child("Photos/").child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AddNewActivity.this, "Upload is Done!", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                        uploadDone = true;
                    }
                });
                uploadDone = true;
            }
        }*/
   // }

    String mCurrentPhotoPath;

    private void createImageFile( String labelOfImage) throws IOException {//return type was File
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = labelOfImage+"_" + timeStamp + "_"; // kullanıcının girdiği isim fotoğrafın adı olacak
        File storageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);
       /* String path = Environment.getExternalStorageDirectory().toString();
        File storageDir = new File(path);*/
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */  // .bmp olarak mı kaydedilsin??
                storageDir      /* directory */
        );
        //Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Toast.makeText(AddNewActivity.this, mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
        galleryAddPic(mCurrentPhotoPath);
        //return image;
    }

    private void galleryAddPic(String mCurrentPhotoPath) {
        // setPic();
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        Uri uri = Uri.fromFile(new File(mCurrentPhotoPath));
        mediaScanIntent.setData(contentUri);
        mProgressDialog.setMessage("Uploading");
        mProgressDialog.show();
        StorageReference filepath = mStorage.child("Photos/").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddNewActivity.this,"Upload is Done!",Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                uploadDone = true;
            }
        });



        this.sendBroadcast(mediaScanIntent);
    }


    //Alınan görüntü açılmıyor galeride !!
    private void setPic() {
        // Get the dimensions of the View
        int targetW = foto.getWidth();
        int targetH = foto.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        foto.setImageBitmap(bitmap);
    }


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