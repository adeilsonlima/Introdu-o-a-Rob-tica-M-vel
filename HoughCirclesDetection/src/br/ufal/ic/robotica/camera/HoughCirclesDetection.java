package br.ufal.ic.robotica.camera;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class HoughCirclesDetection {
	public Point[] houghCircles(Mat imgFrame) {
		Mat src_gray = new Mat();
		Imgproc.cvtColor(imgFrame, src_gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(src_gray, src_gray, new Size(9, 9), 2, 2);
		Mat circles = new Mat();
		double minDist = 10;// (double)src_gray.rows()/8;
		// If unknown the min and max radius, put zero as default.
		int minRadius = 5, maxRadius = 10;
		Imgproc.HoughCircles(src_gray, circles, Imgproc.HOUGH_GRADIENT, 1.0, minDist, 64.0, 19.0, minRadius, maxRadius);// 70,20

		Point[] centers = new Point[circles.cols()];

		for (int i = 0; i < circles.cols(); i++) {
			double[] c = circles.get(0, i);

			Point center = new Point(Math.round(c[0]), Math.round(c[1]));

			centers[i] = center;

			int radius = (int) Math.round(c[2]);
			Imgproc.circle(imgFrame, center, radius, new Scalar(0, 0, 255), 2, 8, 0);
		}

		return centers;
	}

}
