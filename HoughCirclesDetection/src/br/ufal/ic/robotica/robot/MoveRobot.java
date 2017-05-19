package br.ufal.ic.robotica.robot;

import lejos.remote.ev3.RemoteRequestEV3;
import lejos.remote.ev3.RemoteRequestPilot;
import lejos.utility.Delay;

public class MoveRobot {
	private RemoteRequestPilot pilot;

	public MoveRobot(RemoteRequestEV3 ev3) {
		pilot = (RemoteRequestPilot) ev3.createPilot(56, 120, "B", "C");
	}

	/**
	 * @param robot array x, y, theta of the robot
	 * @param destination array x, y, theta of the destination
	 * */
	public void move(double[] robot, double[] destination) {
		try {

			System.out.println("Connected");
			if (!pilot.isMoving()) {
				pilot.setLinearSpeed(160); // 16cm/s
			}
			if(robot[1] < destination[1]){//robo abaixo do destino
				double difference = destination[2] - robot[2];System.out.println("diff theta "+difference);
				if(Math.abs(difference) >10){
					
				}
			}

			// pilot.travel(distance);
			// pilot.rotate(angle);

			// Delay.msDelay(2000);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}

	}

	public void stop() {
		pilot.stop();
		pilot.close();
	}

}
