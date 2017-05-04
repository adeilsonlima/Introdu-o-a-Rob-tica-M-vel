package br.ufal.ic.robotica.videocapture;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class HoughCirclesDetection {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private JFrame frame;
	private JLabel imageLabel;
	private Mat imgFrame;

	public static void main(String[] args) {
		HoughCirclesDetection app = new HoughCirclesDetection();
		app.initGUI();
		app.runMainLoop(args);
	}

	private void initGUI() {
		frame = new JFrame("Detectando Círculos");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		imageLabel = new JLabel();
		frame.add(imageLabel);
		frame.setVisible(true);
	}

	private void houghCircles() {
		Mat src_gray = new Mat();
		Mat imgTemp = new Mat();
		imgFrame.copyTo(imgTemp);
		Imgproc.cvtColor(imgFrame, src_gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(src_gray, src_gray, new Size(9, 9), 2, 2);
		Mat circles = new Mat();
		double minDist = 10;// (double)src_gray.rows()/8;
		// If unknown the min and max radius, put zero as default.
		int minRadius = 5, maxRadius = 10;
		Imgproc.HoughCircles(src_gray, circles, Imgproc.HOUGH_GRADIENT, 1.0, minDist, 67.0, 22.0, minRadius, maxRadius);// 70,20

		Point[] centers = new Point[circles.cols()];

		for (int x = 0; x < circles.cols(); x++) {
			double[] c = circles.get(0, x);

			/*
			 * int centerY = (int) Math.round(c[0]); int centerX = (int)
			 * Math.round(c[1]);
			 * 
			 * // System.out.println("("+centerX+", "+centerY+")"); double[]
			 * intensity = imgFrame.get(centerX, centerY);
			 * System.out.println((int)intensity[2]+", "+(int)intensity[1]+", "+
			 * (int)intensity[0]);
			 */

			Point center = new Point(Math.round(c[0]), Math.round(c[1]));

			centers[x] = center;

			int radius = (int) Math.round(c[2]);
			// circle center
			Imgproc.circle(imgFrame, center, 3, new Scalar(100, 0, 0), -1, 8, 0);
			// circle outline
			Imgproc.circle(imgFrame, center, radius, new Scalar(0, 100, 0), 2, 8, 0);
		}
		for (int i = 0; i < centers.length - 1; ++i) {
			for (int j = i + 1; j < centers.length; ++j) {
				double distance = euclideanDistance(centers[i], centers[j]);
				// System.out.println(distance);
				if (distance < 22 && distance > 16) {
					Imgproc.line(imgFrame, centers[i], centers[j], new Scalar(0, 0, 255), 3);
					// System.out.println(distance);

					double[] intensityA = imgTemp.get((int) centers[i].y, (int) centers[i].x);
					double[] intensityB = imgTemp.get((int) centers[j].y, (int) centers[j].x);
				//System.out.println((int) intensityA[2] + ", " + (int) intensityA[1] + ", " + (int) intensityA[0]);//RGB
					if (intensityA[2] > 120) {//vermelho
						System.out.println("theta: " + Math.toDegrees(Math.atan2(centers[i].y-centers[j].y, centers[i].x-centers[j].x)));
						Imgproc.putText(imgFrame, String.valueOf(Math.round(Math.toDegrees(Math.atan2(centers[j].y-centers[i].y, centers[j].x-centers[i].x)))), centers[i], 3, 1.0, new Scalar(141,11,234),3);
					} else {
						System.out.println("theta: " + Math.toDegrees(Math.atan2(centers[j].y-centers[i].y, centers[j].x-centers[i].x)));
						Imgproc.putText(imgFrame, String.valueOf(Math.round(Math.toDegrees(Math.atan2(centers[i].y-centers[j].y, centers[i].x-centers[j].x)))), centers[j], 3, 1.0, new Scalar(141,11,234),3);
					}
				}
			}
		}
	}

	public double euclideanDistance(Point a, Point b) {
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

	private void runMainLoop(String[] args) {
		imgFrame = new Mat();
		Image tempImage;
		VideoCapture capture = new VideoCapture("/home/adeilson/Downloads/programaOpencv/robotlocalization.avi");
		// capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 320);
		// capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 240);

		if (capture.isOpened()) {
			while (true) {
				capture.read(imgFrame);
				if (!imgFrame.empty()) {
					houghCircles();
					tempImage = toBufferedImage(imgFrame);
					ImageIcon imageIcon = new ImageIcon(tempImage, "Captured video");
					imageLabel.setIcon(imageIcon);
					frame.pack(); // this will resize the window to fit the
									// image
				} else {
					System.out.println(" -- Frame not captured -- Break!");
					break;
				}

				try {
					Thread.sleep(20);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Couldn't open capture.");
		}
	}

	public Image toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

}