package com.example.meric.knockknockapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;


public class DetectFaceActivity extends AppCompatActivity {



    private final int ACTIVITY_CHOOSE_PHOTO = 1;
    public String downFotoName="yok"; // galerriden alınan dosyanın adı
    AddNewActivity addPer = new AddNewActivity();
    ImageView foto = addPer.foto;
    Button yuk;

    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_rec);
        yuk = findViewById(R.id.yukle2);

        yuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetectFaceActivity.this, downFotoName, Toast.LENGTH_SHORT).show();
                openGallery();
                if(!downFotoName.equals("yok")){ sqr();}
            }
        });


    }//onCreate


    //Galeriyi açıp foto seçmeyi sağlar
    public void openGallery()
    {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("image/*");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(chooseFile, "Choose a photo"), ACTIVITY_CHOOSE_PHOTO);
        Toast.makeText(DetectFaceActivity.this, downFotoName, Toast.LENGTH_SHORT).show();

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

    // galeriden gelen dfotodaki yüzleri tespit eder
    public void sqr()
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        //  ---- jpg'yi bitmap'e çevirmek için fotonun kaynaklardan okunması gerekiyormuş R.drawable gibi -----

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


}
