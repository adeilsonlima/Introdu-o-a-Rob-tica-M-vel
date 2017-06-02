package br.ufal.ic.robotica.robot;

import org.opencv.core.Point;
import br.ufal.ic.robotica.angles.Angles;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.remote.ev3.RemoteRequestPilot;
import lejos.utility.Delay;

public class MoveRobot {
	private RemoteRequestPilot pilot;
	private static int X1 = 0;
	private static int Y1 = 1;
	private static int X2 = 2;
	private static int Y2 = 3;
	

	public MoveRobot(RemoteRequestEV3 ev3) {
		pilot = (RemoteRequestPilot) ev3.createPilot(56, 120, "B", "C");
	}
	
	//DELETAR
	public MoveRobot(){
		
	}

	/**
	 * @param robot array x, y, theta of the robot
	 * @param destination array x, y, theta of the destination
	 * */
	public void move(double[] robot, double[] destination) {
		try {
	//		System.out.println("Destino: h("+destination[0]+","+destination[1]+"), t("+destination[2]+","+destination[3]+")");

			//System.out.println("Connected");
			/*if (!pilot.isMoving()) {
				pilot.setLinearSpeed(160); // 16cm/s
				pilot.setAngularSpeed(120);
			}*/
			/*if(robot[1] < destination[1]){//robo abaixo do destino
				double difference = destination[2] - robot[2];System.out.println("diff theta "+difference);
				if(Math.abs(difference) >10){
					
				}
			}*/
			
			///TEST
			/*double destination[] = new double[5];
			destination[X2] = 239.0;
			destination[Y2] = 207.0;*/
			///
			if(Angles.euclideanDistance(new Point(robot[X1],robot[Y1]), new Point(destination[X1],destination[Y1])) < 10){
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
				Delay.msDelay(500);
			}

			// pilot.travel(distance);
			// pilot.rotate(angle);

			// Delay.msDelay(2000);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
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
