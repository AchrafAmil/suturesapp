package com.suturesapp.peditor.peditor;

import android.Manifest;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author ahlam
 */

public class MainActivity extends AppCompatActivity {

    ImageButton btnLoadImage, btnSaveImage, backButton;
    ImageButton btnRed, btnGreen,btnYellow,btnDark,btnBlue,btnWhite;
    ImageView imageResult;
    //SeekBar mSolorSlider;

    final int RQS_IMAGE1 = 1;

    Uri source;
    Bitmap bitmapMaster;
    Canvas canvasMaster;

    int prvX, prvY;

    Paint paintDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                0);

        btnLoadImage = (ImageButton)findViewById(R.id.loadimage);
        btnSaveImage = (ImageButton)findViewById(R.id.saveimage);
        backButton = (ImageButton)findViewById(R.id.backButton);
        btnRed = (ImageButton)findViewById(R.id.btnRed);
        btnGreen = (ImageButton)findViewById(R.id.btnGreen);
        btnYellow = (ImageButton)findViewById(R.id.btnYellow);
        btnDark = (ImageButton)findViewById(R.id.btnDark);
        btnBlue = (ImageButton)findViewById(R.id.btnBlue);
        btnWhite = (ImageButton)findViewById(R.id.btnWhite);


        imageResult = (ImageView)findViewById(R.id.result);

        paintDraw = new Paint();

        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmapMaster != null){
                    saveBitmap(bitmapMaster);
                }
            }
        });

        imageResult.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        prvX = x;
                        prvY = y;
                        drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                        prvX = x;
                        prvY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        drawOnProjectedBitMap((ImageView) v, bitmapMaster, prvX, prvY, x, y);
                        break;
                }
    /*
     * Return 'true' to indicate that the event have been consumed.
     * If auto-generated 'false', your code can detect ACTION_DOWN only,
     * cannot detect ACTION_MOVE and ACTION_UP.
     */
                return true;
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // init color ImageButtons
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewColor(Color.RED);

            }
        });
        btnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewColor(Color.WHITE);
            }
        });
        btnDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewColor(Color.BLACK);
            }
        });
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewColor(Color.BLUE);
            }
        });
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewColor(Color.YELLOW);
            }
        });
        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewColor(0xFF177b17);

            }
        });

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");

        // TODO: 05/03/17 don't hardcode that!
        imagePath = "/Sutures/pica.png";

        try {
            String fullPath = Environment.getExternalStorageDirectory()+imagePath;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap tmpBitmap = BitmapFactory.decodeFile(fullPath, options);
            Bitmap.Config config;
            if(tmpBitmap.getConfig() != null){
                config = tmpBitmap.getConfig();
            }else{
                config = Bitmap.Config.ARGB_8888;
            }

            //bitmapMaster is Mutable bitmap
            bitmapMaster = Bitmap.createBitmap(
                    tmpBitmap.getWidth(),
                    tmpBitmap.getHeight(),
                    config);

            canvasMaster = new Canvas(bitmapMaster);
            canvasMaster.drawBitmap(tmpBitmap, 0, 0, null);

            imageResult.setImageBitmap(bitmapMaster);
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }

        setNewColor(Color.BLUE);

    }


    /*
    Project position on ImageView to position on Bitmap draw on it
     */

    private void drawOnProjectedBitMap(ImageView iv, Bitmap bm,
                                       float x0, float y0, float x, float y){
        if(x<0 || y<0 || x > iv.getWidth() || y > iv.getHeight()){
            //outside ImageView
            return;
        }else{
            float ratioWidth = (float)bm.getWidth()/(float)iv.getWidth();
            float ratioHeight = (float)bm.getHeight()/(float)iv.getHeight();

            canvasMaster.drawLine(
                    x0 * ratioWidth,
                    y0 * ratioHeight,
                    x * ratioWidth,
                    y * ratioHeight,
                    paintDraw);
            imageResult.invalidate();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap tempBitmap;

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case RQS_IMAGE1:
                    source = data.getData();

                    try {
                        //tempBitmap is Immutable bitmap,
                        //cannot be passed to Canvas constructor
                        tempBitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(source));

                        Bitmap.Config config;
                        if(tempBitmap.getConfig() != null){
                            config = tempBitmap.getConfig();
                        }else{
                            config = Bitmap.Config.ARGB_8888;
                        }

                        //bitmapMaster is Mutable bitmap
                        bitmapMaster = Bitmap.createBitmap(
                                tempBitmap.getWidth(),
                                tempBitmap.getHeight(),
                                config);

                        canvasMaster = new Canvas(bitmapMaster);
                        canvasMaster.drawBitmap(tempBitmap, 0, 0, null);

                        imageResult.setImageBitmap(bitmapMaster);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }


    private void saveBitmap(Bitmap bm){
        File file = Environment.getExternalStorageDirectory();
        File newFile = new File(file, "test.jpg");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            MediaStore.Images.Media.insertImage(getContentResolver() , bm , "TitleName" , "") ;
            Toast.makeText(MainActivity.this,
                    "Annotations saved",
                    Toast.LENGTH_SHORT).show();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,
                    "Something wrong: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,
                    "Something wrong: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setNewColor(int color){
        paintDraw.setStyle(Paint.Style.FILL);
        paintDraw.setColor(color);
        paintDraw.setStrokeWidth(4);
    }

    private void setNewPaintWidth(int width){
        paintDraw.setStrokeWidth(width);
    }
}
