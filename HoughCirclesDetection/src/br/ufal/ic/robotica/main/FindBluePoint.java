package br.ufal.ic.robotica.main;

import org.opencv.core.Core;

import br.ufal.ic.robotica.camera.Camera;
import br.ufal.ic.robotica.centros.Centers;
import br.ufal.ic.robotica.robot.MoveRobot;
import lejos.hardware.Button;

public class FindBluePoint {
	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		Camera camera = new Camera();
		camera.start();
		System.out.println("camera started");

		MoveRobot pilot = new MoveRobot();
		pilot.drive();
		System.out.println("Pilot started");

		while (pilot.isMoving()) {
			long timer = System.currentTimeMillis();
			Centers centers = camera.getCenters();
			if (centers != null) {
				pilot.move(centers);
				/*
				 * double error = eye.getError(centers);
				 * System.out.println("Found error: " + error);
				 * pilot.setError(error); pilot.doPID();
				 */
				System.out.println("Time: " + (System.currentTimeMillis() - timer));
			} else {
				System.out.println("Camera not ready...");
			}
		}

		//pilot.stop();
		camera.close();
		
		System.exit(0);

	}
}
