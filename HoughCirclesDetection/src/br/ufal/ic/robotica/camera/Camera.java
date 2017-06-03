package br.ufal.ic.robotica.camera;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import br.ufal.ic.robotica.centros.Centers;

public class Camera {

	private final VideoCapture capture;
	private final Mat imgFrame = new Mat();
	private final HoughCirclesDetection circles;

	public Camera() {
		//capture = new VideoCapture("/home/adeilson/Downloads/programaOpencv/quatrocores.avi");
		capture = new VideoCapture("rtsp://192.168.0.20:554/live1.sdp");
		circles = new HoughCirclesDetection();
	}

	public void close() {
		capture.release();
	}

	public Centers getCenters() {
		if (!capture.isOpened()) {
			return null;
		}

		capture.read(imgFrame);

	//	if (!imgFrame.empty()) {
			Centers coresCentros = new Centers();
			Point[] centers = circles.houghCircles(imgFrame);
			coresCentros.calculaCentros(centers, imgFrame);
			double[] robot = coresCentros.getRobot();
			double[] destination = coresCentros.getDestination();

			if (robot != null && destination != null) {
				return coresCentros;
			}
	//	}
		return null;
	}
}
