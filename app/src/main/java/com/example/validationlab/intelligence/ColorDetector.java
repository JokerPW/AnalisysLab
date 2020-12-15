package com.example.validationlab.intelligence;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class ColorDetector implements IAlgorithm {

    private boolean             mIsColorSelected = false;
    private Scalar              mBlobColorRgba;
    private Scalar              mBlobColorHsv;
    private ColorBlobDetector   mDetector;
    private Mat                 mSpectrum;
    private Size                SPECTRUM_SIZE;
    private Scalar              CONTOUR_COLOR;

    private int myWidth;
    private int myHeight;
    private int countBlobs;

    @Override
    public void startup(int width, int height) {
        myWidth = width;
        myHeight = height;

        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }

    @Override
    public void activate(Mat mat, float coordX, float coordY) {
        int cols = mat.cols();
        int rows = mat.rows();

        int xOffset = (myWidth - cols) / 2;
        int yOffset = (myHeight - rows) / 2;

        int x = (int) coordX - xOffset;
        int y = (int) coordY - yOffset;

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mat.submat(touchedRect);
        double[] rgb = mat.get(x, y);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);// Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++) {
            mBlobColorHsv.val[i] /= pointCount;
        }
        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
        mDetector.setHsvColor(mBlobColorHsv);
        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR_EXACT);
        mIsColorSelected = true;
    }

    @Override
    public void calculate(Mat mat) {
        if (mIsColorSelected) {
            mDetector.process(mat);
            List<MatOfPoint> contours = mDetector.getContours();

            countBlobs = contours.size();
            Imgproc.drawContours(mat, contours, -1, CONTOUR_COLOR);

//            Mat colorLabel = mat.submat(4, 68, 4, 68);
//            colorLabel.setTo(mBlobColorRgba);

//            Mat spectrumLabel = mat.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
//            mSpectrum.copyTo(spectrumLabel);
        }
    }

    @Override
    public void adjustCount(int difference) {
        countBlobs += difference;
    }

    @Override
    public int result() {
        return countBlobs;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    public ColorBlobDetector getmDetector(){
        return mDetector;
    }

    public void setCountour (Scalar color) {
        CONTOUR_COLOR = color;
    }

    @Override
    public void hold(Mat mat) {

    }

}
