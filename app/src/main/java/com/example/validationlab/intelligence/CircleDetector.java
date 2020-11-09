package com.example.validationlab.intelligence;

import android.util.Log;
import android.view.InputEvent;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CircleDetector implements IAlgorithm {

    private int numBlobs;
    private Mat mat;
    private Mat grayMat;


    @Override
    public void startup(int width, int height) {

    }


    @Override
    public void activate(Mat mat, float coordX, float coordY) {

        this.mat = mat;
        grayMat = new Mat(this.mat.cols(), this.mat.rows(), CvType.CV_8UC1);

        /* convert to grayscale */
        int colorChannels = (this.mat.channels() == 3) ? Imgproc.COLOR_BGR2GRAY
                : ((this.mat.channels() == 4) ? Imgproc.COLOR_BGRA2GRAY : 1);

        Imgproc.cvtColor(this.mat, grayMat, colorChannels);

        /* reduce the noise so we avoid false circle detection */
        Imgproc.GaussianBlur(grayMat, grayMat, new Size(9, 9), 2, 2);

        // accumulator value
        double dp = 1.2d;
        // minimum distance between the center coordinates of detected circles in pixels
        double minDist = 100;

        // min and max radii (set these values as you desire)
        int minRadius = 0, maxRadius = 0;

        // param1 = gradient value used to handle edge detection
        // param2 = Accumulator threshold value for the cv2.CV_HOUGH_GRADIENT method.
        // The smaller the threshold is, the more circles will be detected (including false circles).
        // The larger the threshold is, the more circles will potentially be returned.
        double param1 = 170;//70
        double param2 = 172;//72

        /* create a Mat object to store the circles detected */
        Mat circles = new Mat(this.mat.cols(), this.mat.rows(), CvType.CV_8UC1);

        /* find the circle in the image */
        Imgproc.HoughCircles(grayMat, circles,
                Imgproc.CV_HOUGH_GRADIENT, dp, minDist, param1,
                param2, minRadius, maxRadius);

        /* get the number of circles detected */
        numBlobs = (circles.rows() == 0) ? 0 : circles.cols();

        /* draw the circles found on the image */
        for (int i=0; i < numBlobs; i++) {
            /* get the circle details, circleCoordinates[0, 1, 2] = (x,y,r)
             * (x,y) are the coordinates of the circle's center
             */
            double[] circleCoordinates = circles.get(0, i);

            int x = (int) circleCoordinates[0], y = (int) circleCoordinates[1];
            Point center = new Point(x, y);
            int radius = (int) circleCoordinates[2];

            /* circle's outline */
            Imgproc.circle(this.mat, center, radius, new Scalar(0, 255, 0), 4);

            /* circle's center outline */
            Imgproc.rectangle(this.mat, new Point(x - 5, y - 5),
                    new Point(x + 5, y + 5),
                    new Scalar(0, 128, 255), -1);
        }

    }

    @Override
    public void calculate(Mat mat) {

    }

    @Override
    public void adjustCount(int difference) {
        numBlobs += difference;
    }

    @Override
    public int result() {
        return numBlobs;
    }

    @Override
    public void hold(Mat mat) {

    }
}
