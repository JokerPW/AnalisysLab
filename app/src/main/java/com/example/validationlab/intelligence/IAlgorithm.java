package com.example.validationlab.intelligence;

import android.graphics.Canvas;
import org.opencv.core.Mat;

public interface IAlgorithm {

    void startup(int width, int height);
    void activate(Mat mat, float coordX, float coordY);
    void calculate(Mat mat);
    void adjustCount(int difference);
    int result();
    void hold (Mat mat);
}
