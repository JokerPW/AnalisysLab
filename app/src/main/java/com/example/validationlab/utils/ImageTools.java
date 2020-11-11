package com.example.validationlab.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import androidx.core.app.ActivityCompat;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageTools {

    private static ImageTools myImgTools;
    private static boolean canInstantiate = false;
    public static ImageTools getInstance() {
        if (myImgTools == null) {
            canInstantiate = true;
            try {
                myImgTools = new ImageTools();
            } catch (Exception e) {
                e.printStackTrace();
            }
            canInstantiate = false;
        }
        return myImgTools;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean gotPermissions;

    public ImageTools() throws InvalidClassException {
        if (!canInstantiate)
            throw new InvalidClassException("This class is a Singleton. Use its getInstance method");

        gotPermissions = false;
    }

    public void verifyStoragePermissions(Activity activity) {
        if (gotPermissions)
            return;

        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        gotPermissions = true;
    }

    public String saveCurrent (Bitmap bmp) {
        FileOutputStream fileOutputStream = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "img" + date + ".jpeg";
        File file = getDisc (name);
        String file_name = file.getAbsolutePath();// + "/" + name;
        File new_file = new File(file_name);
        new_file.setWritable(true);

        try {
            fileOutputStream = new FileOutputStream(new_file);
            bmp.compress(Bitmap.CompressFormat.JPEG,100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
            return "";
        }
        return file_name;
    }

    private File getDisc (String name) {
        File file = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DCIM);
        return new File (file, name);
    }

    public Bitmap getImage(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public Bitmap loadImgByPath (String path) {
        File imgFile = new File (path);

        if (imgFile.exists()) {
            imgFile.setReadable(true);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return myBitmap;
        }

        return null;
    }

    public Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            return bmOut;
        }
        return bm;
    }

}
