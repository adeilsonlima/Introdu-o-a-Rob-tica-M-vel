package br.ufal.ic.robotica.angles;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import br.ufal.ic.robotica.cluster.MyColors;

public class Angles {
	private double[] robot;// position, orientation
	private double[] destination;

	public void calculaAngulos(Point[] centers, Mat imgFrame) {
		
		Mat imgTemp = new Mat();
		imgFrame.copyTo(imgTemp);
		Imgproc.cvtColor(imgTemp, imgTemp, Imgproc.COLOR_BGR2HSV);
		for (int i = 0; i < centers.length - 1; ++i) {
			for (int j = i + 1; j < centers.length; ++j) {
				double distance = euclideanDistance(centers[i], centers[j]);
				// Marcação detectada
				if (distance < 22 && distance > 16) {
					Imgproc.line(imgFrame, centers[i], centers[j], new Scalar(0, 0, 255), 2);

					double[] intensityI = imgTemp.get((int) centers[i].y, (int) centers[i].x);
					double[] intensityJ = imgTemp.get((int) centers[j].y, (int) centers[j].x);
					int corI = color(intensityI);
					int corJ = color(intensityJ);

					if (corI == MyColors.RED && corJ == MyColors.GREEN) {
						robot = new double[5];
						robot[0] = centers[i].x;
						robot[1] = imgTemp.rows() + 1 - centers[i].y;
						robot[2] = centers[j].x;
						robot[3] = imgTemp.rows() + 1 - centers[j].y;
						robot[4] = Math.toDegrees(Math.atan2(robot[1] - robot[3], robot[0] - robot[2]));
						System.out.println("angle robot: " + robot[4]);
						System.out.println("R(" + robot[0] + "," + robot[1] + "),G(" + robot[2] + "," + robot[3] + ")");
					} else if (corI == MyColors.GREEN && corJ == MyColors.RED) {
						robot = new double[5];
						robot[0] = centers[j].x;
						robot[1] = imgTemp.rows() + 1 - centers[j].y;
						robot[2] = centers[i].x;
						robot[3] = imgTemp.rows() + 1 - centers[i].y;
						robot[4] = Math.toDegrees(Math.atan2(robot[1] - robot[3], robot[0] - robot[2]));
						System.out.println("angle robot: " + robot[4]);
						System.out.println("R(" + robot[0] + "," + robot[1] + "),G(" + robot[2] + "," + robot[3] + ")");
					} else if (corI == MyColors.BLUE && corJ == MyColors.BLACK) {
						destination = new double[5];
						destination[0] = centers[i].x;
						destination[1] = imgTemp.rows() + 1 - centers[i].y;
						destination[2] = centers[j].x;
						destination[3] = imgTemp.rows() + 1 - centers[j].y;
						destination[4] = Math.toDegrees(
								Math.atan2(destination[1] - destination[3], destination[0] - destination[2]));
					} else if (corI == MyColors.BLACK && corJ == MyColors.BLUE) {
						destination = new double[5];
						destination[0] = centers[j].x;
						destination[1] = imgTemp.rows() + 1 - centers[j].y;
						destination[2] = centers[i].x;
						destination[3] = imgTemp.rows() + 1 - centers[i].y;
						destination[4] = Math.toDegrees(
								Math.atan2(destination[1] - destination[3], destination[0] - destination[2]));
					}
				} // System.out.println("robot: "+robot[4]);//System.out.println("dest "+destination[4]);
			}
		}
	}

	private int color(double[] cor) {
		double hue = cor[0];
		double saturation = cor[1];
		double value = cor[2];
		if (hue >= 0 && hue < 10 || hue > 160 && hue < 180)
			return MyColors.RED;// red
		if (hue >= 50 && hue < 85)
			return MyColors.GREEN;// green
		if (hue >= 93 && hue < 125)
			return MyColors.BLUE; // blue
		if (hue < 5 && saturation < 5 && value < 5)
			return MyColors.BLACK; // black
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
	 * tall , [4] angle
	 */
	public double[] getRobot() {
		double [] copy = robot;
		robot = null;
		return copy;
	}

	/**
	 * [0] x of the head , [1] y of the head , [2] x of the tall , [3] y of the
	 * tall , [4] angle
	 */
	public double[] getDestination() {
		return destination;
	}
}
