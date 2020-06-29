package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import org.opencv.*;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import android.annotation.SuppressLint;

import java.lang.String;
import java.util.ArrayList;

//import java.net.URI;

public class MainActivity extends AppCompatActivity {

    int request_code_for_taking_img = 0;
    int imgrow , imgcol;
    Button btn_add_image , btn_analyze ;
    ImageView imgv_floures;
    Boolean opencvIsLoad = false;
    double [] grayValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_add_image = (Button) findViewById(R.id.btn_add_image);
        btn_analyze = (Button) findViewById(R.id.btn_analyze);
        imgv_floures = (ImageView) findViewById(R.id.imgv_floures);
        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent ,request_code_for_taking_img );
            }
        });
        btn_analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this,graph.class);
                intent.putExtra("grayValues",grayValues);
                startActivity(intent);
            }
        });
    }
    private Bitmap bmp;
    final String TAG = "Hello World";
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
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
            String path = getpath(imageUri, this);
            grayValues = loadDisplayImage(path);
        }
    }
    private double [] loadDisplayImage(String path)
    {
        ///load image
        Mat originalImg = Imgcodecs.imread(path);
        Mat rgbImg = new Mat();
        Mat grayImg = new Mat();
        Log.d(TAG,"originalImage" + originalImg.height());
        Imgproc.cvtColor(originalImg,rgbImg,Imgproc.COLOR_BGR2RGB);
        Imgproc.cvtColor(originalImg,grayImg,Imgproc.COLOR_BGR2GRAY);
        imgrow = grayImg.rows();
        imgcol = grayImg.cols();
        int channel = grayImg.channels();
        if(channel == 1)
        {
            Log.i(TAG,"im gray");
        }
        double[] pixelValues = new double[imgcol];
        for(int i = 0 ; i<imgcol;i++)
        {
            double [] grayValue = grayImg.get((imgrow/2),i);
            pixelValues [i] = grayValue[0];
        }
        Log.i(TAG,"finish");
        ///display image
        Bitmap bitmap = Bitmap.createBitmap(rgbImg.cols(),rgbImg.rows(),Bitmap.Config.RGB_565);
        Utils.matToBitmap(rgbImg , bitmap);
        imgv_floures.setImageBitmap(bitmap);
        return pixelValues;
    }

    private String getpath(Uri uri, Context context) {
        return RealPathUtil.getRealPath(context, uri);
    }
}


