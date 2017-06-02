package br.ufal.ic.robotica.videocapture;

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
			// circle center
			// Imgproc.circle(imgFrame, center, 3, new Scalar(100, 0, 0), -1, 8,
			// 0);
			// circle outline
			Imgproc.circle(imgFrame, center, radius, new Scalar(0, 0, 255), 2, 8, 0);
		}
		
		return centers;
		
		

		/*for (int i = 0; i < centers.length - 1; ++i) {
			for (int j = i + 1; j < centers.length; ++j) {
				double distance = euclideanDistance(centers[i], centers[j]);
				// Marcação detectada
				if (distance < 22 && distance > 16) {
					Imgproc.line(imgFrame, centers[i], centers[j], new Scalar(0, 0, 255), 2);
					
				//	Imgproc.cvtColor(imgTemp, imgTemp, Imgproc.COLOR_BGR2HSV);

		//			double[] intensityA = imgTemp.get((int) centers[i].y, (int) centers[i].x);
		//			double[] intensityB = imgTemp.get((int) centers[j].y, (int) centers[j].x);
				//	System.out.println((int) intensityA[0] + ", " + (int) intensityA[1] + ", " + (int) intensityA[2]);//hsv
			
					 
					 

				
					 
					if (intensityA[2] > 120) {// vermelho
						// System.out.println("theta: " +
						// Math.toDegrees(Math.atan2(centers[i].y-centers[j].y,
						// centers[i].x-centers[j].x)));
						Imgproc.putText(imgFrame,
								String.valueOf(Math.round(Math.toDegrees(
										Math.atan2(centers[j].y - centers[i].y, centers[j].x - centers[i].x)))),
								centers[i], 3, 1.0, new Scalar(141, 11, 234), 2);
					} else {
						// System.out.println("theta: " +
						// Math.toDegrees(Math.atan2(centers[j].y-centers[i].y,
						// centers[j].x-centers[i].x)));
						Imgproc.putText(imgFrame,
								String.valueOf(Math.round(Math.toDegrees(
										Math.atan2(centers[i].y - centers[j].y, centers[i].x - centers[j].x)))),
								centers[j], 3, 1.0, new Scalar(141, 11, 234), 2);
					}
				}
			}
		}*/
	}
	

}
