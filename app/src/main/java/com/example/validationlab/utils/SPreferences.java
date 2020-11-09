package com.example.validationlab.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SPreferences {

    static public final String ANALYSIS_TYPE = "analysis_type";
    static public final String TYPE_COLOR = "type_color";
    static public final String TYPE_AREA = "type_area";
    static public final String RESULT = "result";
    static public final String FINAL_IMG = "final_img";


    public static SPreferences getInstance() {
        if (myPreferences == null) {
            canInstantiate = true;
            try {
                myPreferences = new SPreferences();
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
            canInstantiate = false;
        }
        return myPreferences;
    }

    private static SPreferences myPreferences;
    private static boolean canInstantiate = false;


    private long VALUE_H = 0;
    public long getVALUE_H() { return VALUE_H; }
    public void setVALUE_H(long VALUE_H) { this.VALUE_H = VALUE_H; }

    private long VALUE_S = 0;
    public long getVALUE_S() { return VALUE_S; }
    public void setVALUE_S(long VALUE_S) { this.VALUE_S = VALUE_S; }

    private long VALUE_V = 0;
    public long getVALUE_V() { return VALUE_V; }
    public void setVALUE_V(long VALUE_V) { this.VALUE_V = VALUE_V; }

    private long VALUE_A = 0;
    public long getVALUE_A() { return VALUE_A; }
    public void setVALUE_A(long VALUE_A) { this.VALUE_A = VALUE_A; }

    private final long DEFAULT_H = 25;
    private final long DEFAULT_S = 50;
    private final long DEFAULT_V = 50;
    private final long DEFAULT_A = 0;


    private long VALUE_R = 0;
    public long getVALUE_R() { return VALUE_H; }
    public void setVALUE_R(long VALUE_R) { this.VALUE_R = VALUE_R; }

    private long VALUE_G = 0;
    public long getVALUE_G() { return VALUE_S; }
    public void setVALUE_G(long VALUE_G) { this.VALUE_G = VALUE_G; }

    private long VALUE_B = 0;
    public long getVALUE_B() { return VALUE_B; }
    public void setVALUE_B(long VALUE_B) { this.VALUE_B = VALUE_B; }

    private long VALUE_RGB_A = 0;
    public long getVALUE_RGB_A() { return VALUE_RGB_A; }
    public void setVALUE_RGB_A(long VALUE_RGB_A) { this.VALUE_RGB_A = VALUE_RGB_A; }

    private final long DEFAULT_R = 255;
    private final long DEFAULT_G = 0;
    private final long DEFAULT_B = 0;
    private final long DEFAULT_RGB_A = 255;


    public SPreferences() throws InvalidClassException {
        if (!canInstantiate)
            throw new InvalidClassException("This class is a Singleton. Use its getInstance method");

        VALUE_H = DEFAULT_H;
        VALUE_S = DEFAULT_S;
        VALUE_V = DEFAULT_V;
        VALUE_A = DEFAULT_A;

        VALUE_R = DEFAULT_R;
        VALUE_G = DEFAULT_G;
        VALUE_B = DEFAULT_B;
        VALUE_RGB_A = DEFAULT_RGB_A;
    }

    public void resetDefaults() {
        VALUE_H = DEFAULT_H;
        VALUE_S = DEFAULT_S;
        VALUE_V = DEFAULT_V;
        VALUE_A = DEFAULT_A;

        VALUE_R = DEFAULT_R;
        VALUE_G = DEFAULT_G;
        VALUE_B = DEFAULT_B;
        VALUE_RGB_A = DEFAULT_RGB_A;
    }

}
