package com.example.meric.knockknockapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.getExternalStoragePublicDirectory;


/*
    Bu class'ta fotoğraf çek butonuyla eklenecek kişinin fotoğrafı çekilir,
    kullanacının gireceği isim dosyanın adı olarak
    kaydedilir.
 */

public class AddNewActivity extends AppCompatActivity {

    ImageView foto;
    Button captr;
    EditText dosya;
    Button kaydet;
    private static Uri fileUri = null;
    boolean dirExists = true;
    private static final int CAMERA_IMAGE_REQUEST=1;
    private static int RESULT_LOAD_IMG = 1;

    public static final int Camera_Req = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        foto = findViewById(R.id.imageView);//küçük resim olarak gösterildiği yer
        captr = findViewById(R.id.capture);//fotoğraf çekme butonu
        dosya = findViewById(R.id.dosyaAdi);//fotoğrafın adı olark kaydedilecek
        kaydet = findViewById(R.id.saveBtn);//foto galeriye kaydedilir

        captr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Resim çekme isteği
                startActivityForResult(kamera, Camera_Req);
            }
        });



        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createImageFile(dosya.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // Toast.makeText(AddNewActivity.this, "Kaydedildi!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==Camera_Req){
            Bitmap image=(Bitmap)data.getExtras().get("data");//Çekilen resim id olarak bitmap şeklinde alındı ve imageview'e atandı
             foto.setImageBitmap(image);// fotoğrafı uygulamada gösterir
        }
        // super.onActivityResult(requestCode, resultCode, data);
    }
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
        mediaScanIntent.setData(contentUri);
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




}//class
