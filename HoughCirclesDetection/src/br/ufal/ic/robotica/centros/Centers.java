package br.ufal.ic.robotica.centros;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import br.ufal.ic.robotica.camera.MyColors;

public class Centers {
	private double[] robot;// position, orientation
	private double[] destination;
	
	public Centers(){
		destination = null;
	}

	public void calculaCentros(Point[] centers, Mat imgFrame) {

		Mat imgTemp = new Mat();
		imgFrame.copyTo(imgTemp);
		Imgproc.cvtColor(imgTemp, imgTemp, Imgproc.COLOR_BGR2HSV);
		for (int i = 0; i < centers.length - 1; ++i) {
			double[] intensityI = imgTemp.get((int) centers[i].y, (int) centers[i].x);
			if (color(intensityI) == MyColors.BLUE && destination==null) {
				destination = new double[2];
				destination[0] = centers[i].x;
				destination[1] = imgTemp.rows() + 1 - centers[i].y;
			}

			for (int j = i + 1; j < centers.length; ++j) {
				double distance = euclideanDistance(centers[i], centers[j]);
				// Marcação detectada
				if (distance < 22 && distance > 12) {
					Imgproc.line(imgFrame, centers[i], centers[j], new Scalar(0, 0, 255), 2);

					double[] intensityJ = imgTemp.get((int) centers[j].y, (int) centers[j].x);
					int corI = color(intensityI);
					int corJ = color(intensityJ);

					if (corI == MyColors.RED && corJ == MyColors.GREEN) {
						robot = new double[4];
						robot[0] = centers[i].x;
						robot[1] = imgTemp.rows() + 1 - centers[i].y;
						robot[2] = centers[j].x;
						robot[3] = imgTemp.rows() + 1 - centers[j].y;
					} else if (corI == MyColors.GREEN && corJ == MyColors.RED) {
						robot = new double[4];
						robot[0] = centers[j].x;
						robot[1] = imgTemp.rows() + 1 - centers[j].y;
						robot[2] = centers[i].x;
						robot[3] = imgTemp.rows() + 1 - centers[i].y;
					}
				}
			}
		}
	}

	private int color(double[] cor) {
		double hue = cor[0];
		if (hue >= 0 && hue < 10 || hue > 160 && hue < 180)
			return MyColors.RED;// red
		if (hue >= 50 && hue < 90)
			return MyColors.GREEN;// green
		if (hue >= 100 && hue < 135)
			return MyColors.BLUE; // blue

		return MyColors.INDEFINITE;
	}

	public static double euclideanDistance(Point a, Point b) {
		double distance = 0.0;
		try {
			if (a != null && b != null) {
				double xDiff = a.x - b.x;
				double yDiff = a.y - b.y;
				distance = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return distance;
	}

	/**
	 * [0] x of the head , [1] y of the head , [2] x of the tall , [3] y of the
	 * tall
	 */
	public double[] getRobot() {
		return robot;
	}

	/**
	 * [0] x of the head , [1] y of the head
	 */
	public double[] getDestination() {
		return destination;
	}
}
