package br.ufal.ic.robotica.videocapture;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

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

import br.ufal.ic.robotica.angles.Angles;
import br.ufal.ic.robotica.robot.MoveRobot;
import lejos.remote.ev3.RemoteRequestEV3;

public class FindPath {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private JFrame frame;
	private JLabel imageLabel;
	private Mat imgFrame;
	private HoughCirclesDetection circles;
	private RemoteRequestEV3 ev3;

	public static void main(String[] args) {
		FindPath app = new FindPath();
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

	
	

	private void runMainLoop(String[] args) {
		imgFrame = new Mat();
		circles = new HoughCirclesDetection();
	/*	try {
			ev3 = new RemoteRequestEV3("10.0.1.1");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		MoveRobot moveRobot = new MoveRobot(ev3);*/
		MoveRobot moveRobot = new MoveRobot();
		Angles angles = new Angles();
		
		Image tempImage;
		VideoCapture capture = new VideoCapture("/home/adeilson/Downloads/programaOpencv/robotlocalization.avi");

		if (capture.isOpened()) {
			while (true) {
				capture.read(imgFrame);
				if (!imgFrame.empty()) {
					Point[] centers = circles.houghCircles(imgFrame);
					angles.calculaAngulos(centers, imgFrame);
					double[] robot = angles.getRobot();
					double[] destination = angles.getDestination();
					tempImage = toBufferedImage(imgFrame);
					ImageIcon imageIcon = new ImageIcon(tempImage, "Captured video");
					imageLabel.setIcon(imageIcon);
					frame.pack(); // this will resize the window to fit the
									// image
					if(robot!=null){
						moveRobot.move(robot, destination);
					}
				} else {
					System.out.println(" -- Frame not captured -- Break!");
					break;
				}

				try {
					Thread.sleep(70);//20
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Couldn't open capture.");
		//	moveRobot.stop();
		}
	}
	


	private Image toBufferedImage(Mat m) {
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