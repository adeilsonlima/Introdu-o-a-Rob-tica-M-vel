package br.ufal.ic.robotica.slam;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.remote.ev3.RemoteRequestPilot;
import lejos.robotics.RegulatedMotor;

public class MySLAM {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	private JFrame frame;
	private JLabel imageLabel;
	private Mat imgFrame;

	private RemoteRequestPilot pilot;
	private RegulatedMotor arm;
	private RMISampleProvider distanceProvider;
	private float[] distance;
	private final int VMAX = 255;

	private enum Direction {
		GO, BACK
	};

	public static void main(String[] args) {
		MySLAM app = new MySLAM();
		app.initGUI();

		try {
			app.initRobot();
			app.runMainLoop(args);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}

	}

	private void initRobot() throws IOException, NotBoundException {
		RemoteRequestEV3 ev3;
		ev3 = new RemoteRequestEV3("10.0.1.1");
		pilot = (RemoteRequestPilot) ev3.createPilot(56, 120, "B", "C");
		arm = ev3.createRegulatedMotor("A", 'N');// N => NXT
		// arm.rotate(90);
		pilot.setLinearSpeed(160);

		RemoteEV3 ev3_sensor = new RemoteEV3("10.0.1.1");
		distanceProvider = ev3_sensor.createSampleProvider("S1", "lejos.hardware.sensor.EV3UltrasonicSensor",
				"Distance");

		// initialise an array of floats for fetching samples
		distance = new float[1];

	}

	private void initGUI() {
		frame = new JFrame("SLAM");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		imageLabel = new JLabel();
		frame.add(imageLabel);
		frame.setVisible(true);
	}

	private void runMainLoop(String[] args) throws RemoteException {
		imgFrame = new Mat(500, 600, CvType.CV_8U);
		Image tempImage;
		Direction directionRobot = Direction.GO;
		Point positionRobot = new Point(20, 0);// posicao inicial
		float armAngle = 90;// virado para frente
		int imax = 6;
		int angle = 180 / imax;
		float[] distanceFront;

		while (true) {
			if (directionRobot == Direction.GO) {
				distanceFront = distanceProvider.fetchSample();
				System.out.println("Distance: " + distanceFront[0]);

				// gira o sensor para direita
				arm.rotate(-90 - angle);
				armAngle -= 90 - angle;

				for (int i = 0; i <= imax; ++i) {
					armAngle += angle;
					arm.rotate(angle);
					distance = distanceProvider.fetchSample();
					System.out.println("distancia " + armAngle + "° =" + distance[0]);

					if (distance[0] < 1.5) {// 1.5m
						imgFrame.put((int) (positionRobot.y + distance[0] * 100 * Math.sin(armAngle)),
								(int) (positionRobot.x + distance[0] * 100 * Math.cos(armAngle)), VMAX);
					}
					tempImage = toBufferedImage(imgFrame);
					ImageIcon imageIcon = new ImageIcon(tempImage, "map");
					imageLabel.setIcon(imageIcon);
					frame.pack(); // this will resize the window to fit the
									// image
				}
				if (distanceFront[0] > 0.30) {
					pilot.travel(100);// 10cm para frente
					positionRobot.y += 10;
					arm.rotate(-90);// 180 -> 90
					armAngle = 90;
				} else {// turn to right
					arm.rotate(-180);// 180 -> 0
					armAngle = 0;
					distance = distanceProvider.fetchSample();
					if (distance[0] > 0.30) {
						pilot.rotate(-90);
						pilot.travel(200);// 20cm para direita
						pilot.rotate(-90);
						positionRobot.x += 20;
						directionRobot = Direction.BACK;
						arm.rotate(90);
						armAngle = 90;
					} else {
						System.out.println("The End");
						break;
						// System.exit(0);
					}
				}

			} else {// Direction.BACK
				distanceFront = distanceProvider.fetchSample();
				System.out.println("Distance: " + distanceFront[0]);

				// gira o sensor para direita
				arm.rotate(90 + angle);
				armAngle -= 90 - angle;

				for (int i = 0; i <= imax; ++i) {
					armAngle += angle;
					arm.rotate(-angle);
					distance = distanceProvider.fetchSample();
					System.out.println("distancia " + armAngle + "° =" + distance[0]);

					if (distance[0] < 1.5) {// 1.5m
						imgFrame.put((int) (positionRobot.y + distance[0] * 100 * Math.sin(-armAngle)),
								(int) (positionRobot.x + distance[0] * 100 * Math.cos(armAngle)), VMAX);
					}
					tempImage = toBufferedImage(imgFrame);
					ImageIcon imageIcon = new ImageIcon(tempImage, "map");
					imageLabel.setIcon(imageIcon);
					frame.pack(); // this will resize the window to fit the
									// image
				}
				if (distanceFront[0] > 0.30) {
					pilot.travel(100);// 10cm para frente
					positionRobot.y -= 10;
					arm.rotate(-90);// 180 -> 90
					armAngle = 90;
				} else {// turn to right
					arm.rotate(180);// 180 -> 0
					armAngle = 0;
					distance = distanceProvider.fetchSample();
					if (distance[0] > 0.30) {
						pilot.rotate(90);
						pilot.travel(200);// 20cm para direita
						pilot.rotate(90);
						positionRobot.x += 20;
						directionRobot = Direction.GO;
						arm.rotate(-90);
						armAngle = 90;
					} else {
						System.out.println("The End");
						break;
						// System.exit(0);
					}
				}
			}

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
