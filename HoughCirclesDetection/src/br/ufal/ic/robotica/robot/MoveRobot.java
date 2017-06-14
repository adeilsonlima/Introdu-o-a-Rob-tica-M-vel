package br.ufal.ic.robotica.robot;

import java.io.IOException;

import org.opencv.core.Point;

import br.ufal.ic.robotica.centros.Centers;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.remote.ev3.RemoteRequestPilot;
import lejos.utility.Delay;

public class MoveRobot {
	private RemoteRequestPilot pilot;
	private static int X1 = 0;
	private static int Y1 = 1;
	private static int X2 = 2;
	private static int Y2 = 3;
	private boolean isMoving = false;

	public MoveRobot() {
		RemoteRequestEV3 ev3;
		try {
			ev3 = new RemoteRequestEV3("10.0.1.1");
			pilot = (RemoteRequestPilot) ev3.createPilot(56, 120, "B", "C");
			isMoving = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void drive() {
		pilot.setLinearSpeed(160);
		pilot.setAngularSpeed(120);
		// pilot.forward();
	}

	/**
	 * @param centers
	 *            Objeto que possue as posicoes do robo e do destino
	 */
	public void move(Centers centers) {
		double[] robot = centers.getRobot();
		double[] destination = centers.getDestination();
		try {
			double distance = Centers.euclideanDistance(new Point(robot[X1], robot[Y1]),
					new Point(destination[X1], destination[Y1]));
			System.out.println("distancia: "+distance);
			if (distance < 40) {
				// TODO
				System.out.println("Rotação final+stop");
				//pilot.rotate(90);
				pilot.travel(-distance);
				this.stop();
			} else {

				double vectorPathX = destination[X1] - robot[X2];
				double vectorPathY = destination[Y1] - robot[Y2];
				double vectorRobotX = robot[X1] - robot[X2];
				double vectorRobotY = robot[Y1] - robot[Y2];

				double dot = vectorPathX * vectorRobotX + vectorPathY * vectorRobotY;
				double det = vectorPathX * vectorRobotY - vectorPathY * vectorRobotX;
				double angle = Math.toDegrees(Math.atan2(det, dot));
				System.out.println("angulo: (" + angle + ")");
				if (Math.abs(angle) > 8) {
					pilot.rotate(-angle);
					pilot.forward();
				}
				// Thread.yield();
				// pilot.travel(100);
				// Delay.msDelay(2000);
			}
		} catch (Exception e) {
			System.out.println("Error!");
			e.printStackTrace();
		}

	}

	/**
	 * Stop the robot, and close the conection with the Ev3
	 */
	public void stop() {
		pilot.stop();
		pilot.close();
		this.isMoving = false;
	}

	public boolean isMoving() {
		return isMoving;
	}

}
