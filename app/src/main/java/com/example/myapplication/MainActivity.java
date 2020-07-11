package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.String;

public class MainActivity extends AppCompatActivity {

    int request_code_for_taking_img = 0;
    int imgrow , imgcol;
    Button btn_add_image , btn_analyze ;
    ImageView imgv_floures;
    //this Variable is set to true when opencv lib is loaded
    Boolean opencvIsLoad = false;
    private Bitmap bmp;
    final String TAG = "Hello World";
    //this variable hold pixel values of middle row in gray scale image and is sent to graph activity to draw a line chart
    double [] grayValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_add_image = (Button) findViewById(R.id.btn_add_image);
        btn_analyze = (Button) findViewById(R.id.btn_analyze);
        imgv_floures = (ImageView) findViewById(R.id.imgv_floures);
        //Get picture from gallery
        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent ,request_code_for_taking_img );
            }
        });
        //Open new Activity for show  analysis result in line chart
        btn_analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this,graph.class);
                intent.putExtra("grayValues",grayValues);
                startActivity(intent);
            }
        });
    }
    //load opencv lib into project
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //set opencvIsLoad to true
                    opencvIsLoad = true;
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == request_code_for_taking_img && resultCode == RESULT_OK && data != null
                && opencvIsLoad)
        {
            Log.d(TAG , "enter to set opencv" + resultCode);
            Uri imageUri = data.getData();
           // String path = getPath(imageUri, this);
            grayValues = loadDisplayImage(this, imageUri);
        }
    }
    //load image and display it in image view
    private double [] loadDisplayImage(Context context,Uri uri)
    {

        Bitmap bitmap;
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is);
            Mat originalImg = new Mat();
            bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
            Utils.bitmapToMat(bitmap, originalImg);
            Mat grayImg = new Mat();
            Imgproc.cvtColor(originalImg,grayImg,Imgproc.COLOR_BGR2GRAY);
            int channels = grayImg.channels();
            if(channels == 1) {
                Log.i(TAG, "Im gray");
            }
            imgrow = grayImg.rows();
            imgcol = grayImg.cols();
            //This variable hold the pixels values in middle row of grayImg
            Log.i(TAG, "rows" + imgrow);
            Log.i(TAG , "cols" + imgcol);
            Log.i(TAG,"total" + grayImg.total());

            double[] pixelValues = new double[imgcol];
           /* byte[] imgData = new byte[(int) (grayImg.total() * grayImg.channels())];
            grayImg.get(0, 0, imgData);*/
            for(int i = 0 ; i<imgcol;i++)
            {
                //get pixels values in middle row
                double [] grayValue = grayImg.get((imgrow/2),i);
                pixelValues [i] = grayValue[0];
                /*double  grayValue = imgData[(imgrow/2) * grayImg.cols() + i];
                pixelValues [i] = grayValue;*/
            }
            Bitmap bitmap2 = Bitmap.createBitmap(originalImg.cols(),originalImg.rows(),Bitmap.Config.RGB_565);
            Utils.matToBitmap(grayImg , bitmap);
            imgv_floures.setImageBitmap(bitmap);
            return pixelValues;
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        ///load image
        //Mat originalImg = Imgcodecs.imread(path);
        //This mat object hold the rgb format of Original Image to display in image view
    //    Mat rgbImg = new Mat();
        //this mat object hold the gray scale format of Original Image to calculate the pixel intensities
    //    Mat grayImg = new Mat();
    //    Log.d(TAG,"originalImage" + originalImg.height());
        //Set rgbImg content
    //    Imgproc.cvtColor(originalImg,rgbImg,Imgproc.COLOR_BGR2RGB);
        //set grayImg content
    //    Imgproc.cvtColor(originalImg,grayImg,Imgproc.COLOR_BGR2GRAY);
        //Get the image dimensions
    //    imgrow = grayImg.rows();
    //    imgcol = grayImg.cols();
        //This variable hold the pixels values in middle row of grayImg
    //    double[] pixelValues = new double[imgcol];

    //    for(int i = 0 ; i<imgcol;i++)
    //    {
            //get pixels values in middle row
    //        double [] grayValue = grayImg.get((imgrow/2),i);
    //        pixelValues [i] = grayValue[0];
    //    }
    //    Log.i(TAG,"finish");
        ///display image
    //    Bitmap bitmap = Bitmap.createBitmap(rgbImg.cols(),rgbImg.rows(),Bitmap.Config.RGB_565);
    //    Utils.matToBitmap(rgbImg , bitmap);
    //    imgv_floures.setImageBitmap(bitmap);
        double[] pixelValues = new double[10];
        return  pixelValues;
    }
    //Get image path
    private String getPath(Uri uri, Context context) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return  "success";
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return  "success";
    }
}


