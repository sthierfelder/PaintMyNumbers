package com.example.suzy.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Display;
import android.content.Intent;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.widget.ImageView;
import android.widget.Button;
import android.util.Log;
import android.net.Uri;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.content.pm.ActivityInfo;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;


public class MainActivity extends Activity{

    // Take a picture

    int TAKE_PHOTO_CODE = 0;
    String mCurrentPhotoPath;

    boolean mbImageAvailable = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create picture folder
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/PaintMyNumbers/";
        File newdir = new File(dir);
        newdir.mkdirs();


        // Button listener: take a picture
        Button capture = (Button) findViewById(R.id.btnCapture);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String file = dir+timeStamp+".jpg";

                mCurrentPhotoPath = file;

                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                } catch (IOException e) {}

                Uri outputFileUri = Uri.fromFile(newfile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        });

        // Button listener: analyse
        capture = (Button) findViewById(R.id.btnAnalyse);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(mbImageAvailable){


                    Log.d("onClick", "Analyse image " + mCurrentPhotoPath);


                }


            }
        });


    }

    private void setPic() {

        Log.d("setPic", "Display image " + mCurrentPhotoPath);

        ImageView imageView = (ImageView) findViewById(R.id.cameraImage);

        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        Log.d("setPic", "layout ( " + targetW + " * " + targetH + " )");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Log.d("setPic", "display ( " + width + " * " + height + " )");


        imageView.getLayoutParams().width = ((width / 100) * 80);;
        imageView.getLayoutParams().height = ((height / 100) * 80);
        imageView.requestLayout();

        targetW = imageView.getWidth();
        targetH = imageView.getHeight();

        Log.d("setPic", "new layout ( " + targetW + " * " + targetH + " )");

/*
// working for local image
        Bitmap bmp;
        bmp=BitmapFactory.decodeResource(getResources(),R.drawable.miau);//image is your image
        bmp=Bitmap.createScaledBitmap(bmp, targetW,targetH, true);
        imageView.setImageBitmap(bmp);
*/

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.d("setPic", "photo ( " + photoW + " * " + photoH + " )");

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");

            // Show picture
            setPic();

            mbImageAvailable = true;

            // Enable analyse button
            Button myButton = (Button) findViewById(R.id.btnAnalyse);
            myButton.setEnabled(true);
        }

    }


    // Original Functions

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
