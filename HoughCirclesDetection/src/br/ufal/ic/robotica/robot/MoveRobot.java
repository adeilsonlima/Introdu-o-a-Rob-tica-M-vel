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
	
	public MoveRobot(){
		RemoteRequestEV3 ev3;
		try {
			ev3 = new RemoteRequestEV3("10.0.1.1");
			pilot = (RemoteRequestPilot) ev3.createPilot(56, 120, "B", "C");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void drive() {
		pilot.setLinearSpeed(160);
		pilot.setAngularSpeed(120);
      //  pilot.forward();
    }

	/**
	 * @param centers Objeto que possue as posicoes do robo e do destino
	 * */
	public void move(Centers centers) {
		double[] robot = centers.getRobot();
		double[] destination = centers.getDestination();
		try {
			if(Centers.euclideanDistance(new Point(robot[X1],robot[Y1]), new Point(destination[X1],destination[Y1])) < 10){
				//TODO
				System.out.println("Rotação final+stop");
				pilot.stop();
			}else{
				
				double vectorPathX = destination[X1]-robot[X2];
				double vectorPathY = destination[Y1]-robot[Y2];
				double vectorRobotX = robot[X1] - robot[X2];
				double vectorRobotY = robot[Y1] - robot[Y2];
				
				double dot = vectorPathX*vectorRobotX + vectorPathY*vectorRobotY;
				double det = vectorPathX*vectorRobotY - vectorPathY*vectorRobotX;
				double angle = Math.toDegrees(Math.atan2(det, dot));
				System.out.println("angle to path: "+angle);
				pilot.rotate(-angle);
				pilot.forward();
			//	Delay.msDelay(500);
			}
		} catch (Exception e) {
			System.out.println("Error!");
			e.printStackTrace();
		}

	}
/**
 * Stop the robot, and close the conection with the Ev3
 * */
	public void stop() {
		pilot.stop();
		pilot.close();
	}

}
