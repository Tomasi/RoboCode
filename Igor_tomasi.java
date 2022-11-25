package igor_tomasi;

import robocode.*;
import robocode.HitRobotEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import java.awt.Color;

public class Igor_tomasi extends AdvancedRobot {
		
		private byte moveDirection = 1;
	
		public void run(){

			// Colors
			setBodyColor(Color.gray);
			setGunColor(Color.gray);
			setRadarColor(Color.red);
			setBulletColor(Color.orange);
			setScanColor(Color.gray);		
			
			setTurnGunRightRadians(Double.POSITIVE_INFINITY);
			
			while (true) {				
				
				if (_scannedRobot == null){
					System.out.println("Deve procurar novos inimigos ...");
					setTurnGunRightRadians(Double.POSITIVE_INFINITY);
				}				
				
				_scannedRobot = null;
				
				scan();
			}
		}

		private ScannedRobotEvent _scannedRobot;
		public void onScannedRobot(ScannedRobotEvent e){			

			_scannedRobot = e;

			System.out.println("Enemy name: " + e.getName());
			System.out.println("Enemy life: " + e.getLife());			

			if (getVelocity() == 0) {
				moveDirection *= -1;
				setAhead(150 * moveDirection);
			}
			
			setTurnRight(e.getBearing() + 90);
			
			if (getTime() % 15 == 0) {
				moveDirection *= -1;
				setAhead(150 * moveDirection);
			}
	
			double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
			double radarTurn = Utils.normalRelativeAngle(angleToEnemy - getRadarHeadingRadians());
			double extraTurn = Math.min(Math.atan(36.0 / e.getDistance()), Rules.RADAR_TURN_RATE_RADIANS);
			double absoluteBearing = e.getBearingRadians() + getHeadingRadians();
			
			radarTurn += (radarTurn < 0 ? -extraTurn : extraTurn);
			setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
			setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians() + Math.max((1 - e.getDistance() / (radarTurn+100)),0) * Math.asin(e.getVelocity() / 11) * Math.sin(e.getHeadingRadians() - absoluteBearing) ));	
			
			if (e.getDistance() <= 250) {
				fire(3); 
			} else if ((e.getDistance() > 251) && (e.getDistance() < 350)) {
				fire(2);
			} else if ((e.getDistance() < 400) || (e.getLife() < 10)) {
				fire(1);
			}			
							
		}
		
		private HitByBulletEvent bulletEvent;
		public void onHitByBullet(HitByBulletEvent e) {
			bulletEvent = e;
			
			if ((_scannedRobot != null) && (e.getName() != _scannedRobot.getName())) {
				
				int tentativas = 0;
				
				do {
					setTurnGunRightRadians(Double.POSITIVE_INFINITY);
					tentativas += 1;
				}while(_scannedRobot.getName() != bulletEvent.getName() && tentativas < 5);
					
				System.out.println("Deve procurar inimigo diferente que causou danos ...");
			}
		}		

		public void onHitWall(HitWallEvent e) {
		    moveDirection *= -1;
		}
		public void onHitRobot(HitRobotEvent e) {
		    moveDirection *= -1;
		}
		
		
}
