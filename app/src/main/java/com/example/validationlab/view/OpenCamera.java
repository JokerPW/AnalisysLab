package com.example.validationlab.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.example.validationlab.R;
import com.example.validationlab.intelligence.CircleDetector;
import com.example.validationlab.intelligence.ColorBlobDetector;
import com.example.validationlab.intelligence.ColorDetector;
import com.example.validationlab.intelligence.IAlgorithm;
import com.example.validationlab.utils.ImageTools;
import com.example.validationlab.utils.SPreferences;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;


public class OpenCamera extends Activity implements View.OnClickListener, View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OpenCamera";

    private SPreferences sp;
    private ImageTools imgTools;
    private ViewHolder mViewHolder = new ViewHolder();

    private Mat mRgba;
    private CameraBridgeViewBase mOpenCvCameraView;
    private String currAlgorithm;
    private IAlgorithm myAlgorithm;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(OpenCamera.this);
                    mOpenCvCameraView.setCameraPermissionGranted();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.sp = SPreferences.getInstance();
        this.imgTools = ImageTools.getInstance();
        this.imgTools.verifyStoragePermissions(this);

        Intent myIntent = getIntent();
        currAlgorithm = myIntent.getStringExtra(SPreferences.ANALYSIS_TYPE);
        switch (currAlgorithm){
            case SPreferences.TYPE_COLOR:
                myAlgorithm = new ColorDetector();
                break;
            case SPreferences.TYPE_AREA:
                myAlgorithm = new CircleDetector();
                break;

            default:
                myAlgorithm = new ColorDetector();
        }

        this.mViewHolder.buttonConfirm = findViewById(R.id.button_confirm);
        this.mViewHolder.buttonConfirm.setOnClickListener(this);

        this.mViewHolder.buttonMinus = findViewById(R.id.buttonMinus);
        this.mViewHolder.buttonMinus.setOnClickListener(this);
        this.mViewHolder.buttonPlus = findViewById(R.id.buttonPlus);
        this.mViewHolder.buttonPlus.setOnClickListener(this);
        this.mViewHolder.partial = (TextView) findViewById(R.id.countBlobs);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        checkForPermission();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkForPermission() {
        int permissionCheck = checkSelfPermission(Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Granted");
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Log.d(TAG, "Camera Permission Required!!");
            }
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_TAKE_PHOTO);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        myAlgorithm.startup(width, height);
        if (myAlgorithm instanceof ColorDetector) {
            Scalar mColorRadius = new Scalar(
                    sp.getVALUE_H(),
                    sp.getVALUE_S(),
                    sp.getVALUE_V(),
                    sp.getVALUE_A()
            );
            ((ColorDetector)myAlgorithm).getmDetector().setColorRadius(mColorRadius);

            Scalar mColorContour = new Scalar(
                    sp.getVALUE_R(),
                    sp.getVALUE_G(),
                    sp.getVALUE_B(),
                    sp.getVALUE_RGB_A()
            );
            ((ColorDetector)myAlgorithm).setCountour(mColorRadius);
        }
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        myAlgorithm.activate (mRgba, event.getX(), event.getY());
        Integer blobs = myAlgorithm.result();
        this.mViewHolder.partial.setText(blobs.toString());
        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        myAlgorithm.calculate(mRgba);
        return mRgba;
    }


    @Override
    public void onClick(View view) {
        String value = this.mViewHolder.partial.getText().toString();

        // It checks if value is numeric so it could be confident in parsing it to integer
        if (value.matches("-?(0|[1-9]\\d*)")) {
            Integer blobs = Integer.parseInt(this.mViewHolder.partial.getText().toString());

            if(view.getId() == R.id.button_confirm){
                String fileName = this.imgTools.saveCurrent(this.imgTools.rotate(this.imgTools.getImage(mRgba), 90));
                if (fileName.length() <= 0) {
                    this.mViewHolder.partial.setText("Fail to save the file");
                    return;
                }

                Intent intent = new Intent(this, Results.class);
                intent.putExtra(SPreferences.RESULT, blobs);
                intent.putExtra(SPreferences.FINAL_IMG, fileName);
                mOpenCvCameraView.disableView();
                startActivity(intent);
            } else {
                if (view.getId() == R.id.buttonMinus) {
                    blobs--;
                    myAlgorithm.adjustCount(-1);
                } else if (view.getId() == R.id.buttonPlus){
                    blobs++;
                    myAlgorithm.adjustCount(1);
                }
                this.mViewHolder.partial.setText(blobs.toString());
            }
        }
    }

    private static class ViewHolder {
        Button buttonConfirm;
        Button buttonMinus;
        Button buttonPlus;
        TextView partial;
    }

}
