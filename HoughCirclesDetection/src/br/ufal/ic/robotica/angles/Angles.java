package br.ufal.ic.robotica.angles;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import br.ufal.ic.robotica.cluster.MyColors;

public class Angles {
	private double[] robot;//position, orientation
	private double[] destination;
	
	public void calculaAngulos(Point[] centers, Mat imgFrame){

		robot = new double[3];
		destination = new double[3];
		Mat imgTemp = new Mat();
		imgFrame.copyTo(imgTemp);
		Imgproc.cvtColor(imgTemp, imgTemp, Imgproc.COLOR_BGR2HSV);
		for (int i = 0; i < centers.length - 1; ++i) {
			for (int j = i + 1; j < centers.length; ++j) {
				double distance = euclideanDistance(centers[i], centers[j]);
				// Marcação detectada
				if (distance < 22 && distance > 16) {
					Imgproc.line(imgFrame, centers[i], centers[j], new Scalar(0, 0, 255), 2);
					
					double[] intensity1 = imgTemp.get((int) centers[i].y, (int) centers[i].x);
					double[] intensity2 = imgTemp.get((int) centers[j].y, (int) centers[j].x);
					int cor1 = color(intensity1);
					int cor2 = color(intensity2);
					
					if(cor1 == MyColors.RED && cor2 == MyColors.GREEN){
						robot[0] = centers[i].x;
						robot[1] = centers[i].y;
						robot[2] = Math.toDegrees(Math.atan2(centers[i].y-centers[j].y, centers[i].x-centers[j].x));
					}else if(cor1 == MyColors.GREEN && cor2 == MyColors.RED){
						robot[0] = centers[j].x;
						robot[1] = centers[j].y;
						robot[2] = Math.toDegrees(Math.atan2(centers[j].y-centers[i].y, centers[j].x-centers[i].x));
					}else if(cor1 == MyColors.BLUE && cor2 == MyColors.BLACK){
						destination[0] = centers[i].x;
						destination[1] = centers[i].y;
						destination[2] = Math.toDegrees(Math.atan2(centers[i].y-centers[j].y, centers[i].x-centers[j].x));
					}else if(cor1 == MyColors.BLACK && cor2 == MyColors.BLUE){
						destination[0] = centers[j].x;
						destination[1] = centers[j].y;
						destination[2] = Math.toDegrees(Math.atan2(centers[j].y-centers[i].y, centers[j].x-centers[i].x));
					}
					
			
					 
					 

				
					 
					/*if (intensityA[2] > 120) {// vermelho
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
					}*/
				}
			}
		}
	}
	private int color(double[] cor){
		double hue = cor[0];
		double saturation = cor[1];
		double value = cor[2];
		if(hue>=0 && hue <10 || hue> 160 && hue <180)
			return MyColors.RED;//red
		if(hue>=50 && hue <85)
			return MyColors.GREEN;//green
		if(hue>=93 && hue <125)
			return MyColors.BLUE; //blue
		if(hue<5 && saturation <5 && value <5)
			return MyColors.BLACK; //black
		return MyColors.INDEFINITE;
	}
	


	private double euclideanDistance(Point a, Point b) {
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
	public double[] getRobot() {
		return robot;
	}
	public double[] getDestination() {
		return destination;
	}
}
