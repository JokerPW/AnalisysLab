package com.example.validationlab.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.validationlab.R;
import com.example.validationlab.intelligence.CircleDetector;
import com.example.validationlab.intelligence.ColorDetector;
import com.example.validationlab.intelligence.IAlgorithm;
import com.example.validationlab.utils.ImageTools;
import com.example.validationlab.utils.SPreferences;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class OpenFile extends Activity implements View.OnTouchListener, View.OnClickListener {

    private static final int READ_REQUEST_CODE = 42;

    private SPreferences sp;
    private ImageTools imgTools;
    private ViewHolder mViewHolder = new ViewHolder();
    private boolean hasImage;

    private Bitmap bitmap;
    private Bitmap currBmp;

    private String currAlgorithm;
    private IAlgorithm myAlgorithm;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    break;
                default:
                    super.onManagerConnected(status);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);

        hasImage = false;
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
                myAlgorithm = new CircleDetector();
        }

        this.mViewHolder.buttonConfirm = findViewById(R.id.button_confirm);
        this.mViewHolder.buttonConfirm.setOnClickListener(this);
        this.mViewHolder.myLayout = findViewById(R.id.imgvw);

        this.mViewHolder.buttonMinus = findViewById(R.id.buttonMinus);
        this.mViewHolder.buttonMinus.setOnClickListener(this);
        this.mViewHolder.buttonPlus = findViewById(R.id.buttonPlus);
        this.mViewHolder.buttonPlus.setOnClickListener(this);
        this.mViewHolder.partial = (TextView) findViewById(R.id.countBlobs);

    }


    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
            return true;

        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            //Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers, it would be "*/*".
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                try {
                    bitmap = getBitmapFromUri(resultData.getData());
                    this.mViewHolder.myLayout.setImageBitmap(bitmap);
                    if (myAlgorithm instanceof CircleDetector) {
                        countCircles();
                    } else {
                        this.mViewHolder.myLayout.setOnTouchListener(this);
                        myAlgorithm.startup(bitmap.getWidth(), bitmap.getHeight());

                        Scalar mColorRadius = new Scalar(
                                sp.getVALUE_H(),
                                sp.getVALUE_S(),
                                sp.getVALUE_V(),
                                sp.getVALUE_A()
                            );
                        ((ColorDetector)myAlgorithm).getmDetector().setColorRadius(mColorRadius);

                    }
                } catch (Exception e) {
                    System.out.println("*********** CATCH BITMAP");
                }
            } else {
                System.out.println("++++++++++++ FAIL TO OPEN FILE");
            }
        } else {
            System.out.println("============== FAIL TO OPEN FILE");
        }
    }

    private void countCircles(){
        Mat myMat = new Mat();
        Utils.bitmapToMat(bitmap, myMat);
        myAlgorithm.activate(myMat, 0, 0);
        this.mViewHolder.partial.setText(String.valueOf(myAlgorithm.result()));
    }//--- End: countCircles


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_confirm){
            if (!hasImage) {
                hasImage = true;
                performFileSearch();
            } else {
                hasImage = false;
                String fileName = this.imgTools.saveCurrent(currBmp);
                if (fileName.length() <= 0) {
                    this.mViewHolder.partial.setText("Fail to save the file");
                    return;
                }

                Intent intent = new Intent(this, Results.class);
                intent.putExtra(SPreferences.RESULT, Integer.parseInt(this.mViewHolder.partial.getText().toString()));
                intent.putExtra(SPreferences.FINAL_IMG, fileName);
                startActivity(intent);
            }
        } else {
            Integer blobs = Integer.parseInt(this.mViewHolder.partial.getText().toString());
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (myAlgorithm instanceof ColorDetector){
            Matrix inverse = new Matrix();
            this.mViewHolder.myLayout.getImageMatrix().invert(inverse);
            float[] pts = {
                    event.getX(), event.getY()
            };
            inverse.mapPoints(pts);

            Mat tmp = new Mat (bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
            Utils.bitmapToMat(bitmap, tmp);
            myAlgorithm.activate (tmp, pts[0], pts[1]);
            myAlgorithm.calculate(tmp);
            currBmp = bitmap.copy(bitmap.getConfig(), true);
            Utils.matToBitmap(tmp, currBmp);
            this.mViewHolder.myLayout.setImageBitmap(currBmp);
            this.mViewHolder.partial.setText(String.valueOf(myAlgorithm.result()));
        }
        return false;
    }

    private static class ViewHolder {
        Button buttonConfirm;
        ImageView myLayout;
        Button buttonMinus;
        Button buttonPlus;
        TextView partial;
    }
}
