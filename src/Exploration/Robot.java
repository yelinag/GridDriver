package Exploration;

import java.util.concurrent.TimeUnit;

public class Robot {
	
	public static final String SENSOR_LEFT = "left";
	public static final String SENSOR_RIGHT = "right";
	public static final String SENSOR_FRONTLEFT = "frontleft";
	public static final String SENSOR_FRONTRIGHT = "frontright";
	public static final String SENSOR_FRONT = "front";
	public static final int INFRA_SHORT_RANGE = 4;
	public static final int FRONTLEFT_SENSOR_XOFFSET = -10;
	public static final int FRONTLEFT_SENSOR_YOFFSET = 2;
	public static final int FRONTRIGHT_SENSOR_XOFFSET = 10;
	public static final int FRONTRIGHT_SENSOR_YOFFSET = 2;
	public static final int FRONT_SENSOR_YOFFSET = 10;
	public static final int LEFT_SENSOR_XOFFSET = -10;
	public static final int RIGHT_SENSOR_XOFFSET = 10;
	public static final int TURNING_DURATION = 3;
	public static final int MOVING_DURATION = 3;
	
	private int xPos, yPos;
	//int[] direction = new int[2];
	private Directions direction;
	private SensorsValue sensors;
	// N = (0, 1), S = (0, -1), E = (1, 0), W = (-1, 0)
	public RobotMap robotMap;
	public Map map;
	protected int[][] visitedCount;
	private String msgFromRpi;
	
	
	static Directions dtd[] = {Directions.W,Directions.N,Directions.E,Directions.S};
	
	public Robot(int x, int y, Directions dir){
		this.xPos = x;
		this.yPos = y;
		this.direction = dir;
		robotMap = new RobotMap();
		String str = "0xC0000000020000000072000000000001C00000002000000007A00001C0000000000000000003";
		map = new Map();
		sensors = new SensorsValue();
		
		visitedCount = new int[Exploration.TOTAL_HORIZONTAL_TILES][Exploration.TOTAL_VERTICAL_TILES];
		for(int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++){
			for (int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++){
				visitedCount[i][j] = 0;
			}
		}
		
		/*updateRobotSensors();
		updateRobotMap();*/
	}
	
	//--------------------- PERFORM ROBOT MOVEMENTS -------------------------
	
	public Robot getNextMove(){
		Robot robot = new Robot(this.xPos, this.yPos, this.direction);
		if(robot.direction == Directions.E){
			robot.xPos += Exploration.OBSTACLE_SIZE;
		}else if(robot.direction == Directions.N){
			robot.yPos += Exploration.OBSTACLE_SIZE;
		}else if(robot.direction == Directions.S){
			robot.yPos -= Exploration.OBSTACLE_SIZE;
		}else{
			robot.xPos-= Exploration.OBSTACLE_SIZE;
		}
			
		return robot;
	}
	
	public int rotateRobot(Directions direction){
		int goalD = direction.ordinal();
		int curD = this.direction.ordinal();
		int diff = goalD - curD;
		if(diff == 0)
			return 0;
		if(diff == -2 || diff == 2){
			turnLeft();
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			turnLeft();
			return TURNING_DURATION * 2;
		}
		
		if(diff == -3 || diff == 1){
			turnRight();
			return TURNING_DURATION;
		}
		
		if(diff == 3 || diff == -1){
			turnLeft();
			return TURNING_DURATION;
		}
		System.out.println("Rotating Robot");
		return 0;
	}
	
	public int turnLeft(){
		int dir = this.direction.ordinal();
		dir = (dir - 1) % 4;
		if(dir < 0){
			dir += 4;
		}
		this.direction = dtd[dir];
		System.out.println("Turning Left");
		SimulationMain.sendMsgToArduino("A");
		return TURNING_DURATION;
	}
	
	public int turnRight(){
		int dir = this.direction.ordinal();
		dir = (dir + 1) % 4;
		this.direction = dtd[dir];
		System.out.println("Turning Right");
		SimulationMain.sendMsgToArduino("D");
		return TURNING_DURATION;
	}
	
	public int turnBackward(){
		turnLeft();
		try {
			TimeUnit.MILLISECONDS.sleep(25);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		turnLeft();
		return TURNING_DURATION;
	}
	
	/*public boolean checkB4Move(){
		int frontV = sensors.front + 10;
		int frontleftV = sensors.frontLeft;
		int frontRightV = sensors.frontRight;
		
		int min = Math.min(frontV, Math.min(frontleftV, frontRightV));
		if(min < 15)
			return false;
		return true;
	}*/
	
	public int moveForward(){
		Robot robot = this.getNextMove();
//		if(robotMap.isOkaytoMove(Exploration.coordinatesToGrid(robot.xPos),
//				Exploration.coordinatesToGrid(robot.yPos)) && checkB4Move()){
		this.xPos = validateXCoordinates(robot.xPos);
		this.yPos = validateYCoordinates(robot.yPos);
		visitedCount[Exploration.coordinatesToGrid(xPos)]
				[Exploration.coordinatesToGrid(yPos)]++;
		System.out.println("Moving Forward");
		SimulationMain.sendMsgToArduino("W");
		return MOVING_DURATION;
	}
	
	
	public int moveForwardsimu(){
		Robot robot = this.getNextMove();
//		if(robotMap.isOkaytoMove(Exploration.coordinatesToGrid(robot.xPos),
//				Exploration.coordinatesToGrid(robot.yPos)) && checkB4Move()){
		this.xPos = validateXCoordinates(robot.xPos);
		this.yPos = validateYCoordinates(robot.yPos);
		visitedCount[Exploration.coordinatesToGrid(xPos)]
				[Exploration.coordinatesToGrid(yPos)]++;
		System.out.println("Moving Forward");
		return MOVING_DURATION;
	}
	
	public int turnLeftsimu(){
		int dir = this.direction.ordinal();
		dir = (dir - 1) % 4;
		if(dir < 0){
			dir += 4;
		}
		this.direction = dtd[dir];
		System.out.println("Turning Left");
		return TURNING_DURATION;
	}
	
	public int turnRightsimu(){
		int dir = this.direction.ordinal();
		dir = (dir + 1) % 4;
		this.direction = dtd[dir];
		System.out.println("Turning Right");
		return TURNING_DURATION;
	}
	
	
	
	
	//---------------- UPDATE ROBOT MAP ---------------
	public void updateRobotMap(){
		updateRobotMapForEachSensor(SENSOR_FRONT);
		updateRobotMapForEachSensor(SENSOR_LEFT);
		updateRobotMapForEachSensor(SENSOR_RIGHT);
		updateRobotMapForEachSensor(SENSOR_FRONTLEFT);
		updateRobotMapForEachSensor(SENSOR_FRONTRIGHT);
	}
	
	private void updateRobotMapForEachSensor(String sensorName){
		
		int[] ary = getSensorPosition(sensorName);
		int x = ary[0];
		int y = ary[1];
		
		int[] obstacleGrid = getObstacleGridNoFromSensor(sensorName);
		int[] sensorGrid = Exploration.coordinatesToGrid(x, y);
		int[] offsets = getSensorOffsets(this.direction, sensorName);
		
		
		if(obstacleGrid[0] == -1 && obstacleGrid[1] == -1){
			for(int i = 0; i < INFRA_SHORT_RANGE; i++){
				if(!isGridWithinCurRobotGrid(sensorGrid[0], sensorGrid[1]))
					robotMap.updateRobotMap(sensorGrid[0], sensorGrid[1], 0);
				sensorGrid[0] += offsets[0];
				sensorGrid[1] += offsets[1];
				
			}
			return;
		}
//		if(obstacleGrid[0] < 0 || obstacleGrid[1] < 0)
//			return;
		while(sensorGrid[0] != obstacleGrid[0] || sensorGrid[1] != obstacleGrid[1]){
			if(!isGridWithinCurRobotGrid(sensorGrid[0], sensorGrid[1]))
				robotMap.updateRobotMap(sensorGrid[0], sensorGrid[1], 0);
			sensorGrid[0] += offsets[0];
			sensorGrid[1] += offsets[1];
		}
		if(!isGridWithinCurRobotGrid(sensorGrid[0], sensorGrid[1]))
			robotMap.updateRobotMap(sensorGrid[0], sensorGrid[1], 1);
		
	}
	
	
	//---------------- SENSING ---------------
	
	/**
	 * this function will return the actual block grid number of the map
	 * index 0 is horizontal blocks, index 1 is vertical blocks
	 * @param sensorName
	 * @return
	 */
	private int[] getObstacleGridNoFromSensor(String sensorName){
		int[] detectedGrids = new int[2];
		Directions sensorDir = getSensorDirection(this.direction, sensorName);
		int sensorValue;
		int[] sensorPos = getSensorPosition(sensorName);
		int[] obstaclePos = new int[2];
		
		if(sensorName == SENSOR_FRONT){
			sensorValue = sensors.front;
		}else if(sensorName == SENSOR_FRONTLEFT){
			sensorValue = sensors.frontLeft;
		}else if(sensorName == SENSOR_FRONTRIGHT){
			sensorValue = sensors.frontRight;
		}else if(sensorName == SENSOR_LEFT){
			sensorValue = sensors.left;
		}else{
			sensorValue = sensors.right;
		}
		
		if(sensorValue <= 0 || sensorValue > INFRA_SHORT_RANGE * Exploration.OBSTACLE_SIZE)
		{
			int[] tempGrids = {-1, -1};
			return tempGrids;
		}
		
		
		if(sensorDir == Directions.N){
			obstaclePos[0] = sensorPos[0];
//			obstaclePos[1] = sensorPos[1] + sensorValue;
			obstaclePos[1] = sensorPos[1] + sensorValue + 5;
		}else if(sensorDir == Directions.E){
//			obstaclePos[0] = sensorPos[0] + sensorValue;
			obstaclePos[0] = sensorPos[0] + sensorValue + 5;
			obstaclePos[1] = sensorPos[1];
		}else if(sensorDir == Directions.S){
			obstaclePos[0] = sensorPos[0];
//			obstaclePos[1] = sensorPos[1] - sensorValue;
			obstaclePos[1] = sensorPos[1] - sensorValue - 5;
		}else{
//			obstaclePos[0] = sensorPos[0] - sensorValue;
			obstaclePos[0] = sensorPos[0] - sensorValue - 5;
			obstaclePos[1] = sensorPos[1];
		}
		if(Exploration.isOutofArenaByCoord(obstaclePos[0], obstaclePos[1])){
			int[] tempGrids = {-1, -1};
			return tempGrids;
		}
		
		detectedGrids = Exploration.coordinatesToGrid(obstaclePos[0], obstaclePos[1]);		
		return detectedGrids;
	}
	
	private boolean isGridWithinCurRobotGrid(int rowGrid, int colGrid){
		int[] curGrid = Exploration.coordinatesToGrid(this.xPos, this.yPos);
		if(rowGrid >= curGrid[0] - 1 && rowGrid <= curGrid[0] + 1 &&
				colGrid >= curGrid[1] - 1 && colGrid <= curGrid[1] + 1)
			return true;
		return false;
	}
	
	public static Directions getSensorDirection(Directions robotDirection, String sensorName){
		int dir;
		if(sensorName == SENSOR_FRONT || sensorName == SENSOR_FRONTLEFT || sensorName == SENSOR_FRONTRIGHT){
			return robotDirection;
		}
		else if(sensorName == SENSOR_LEFT){
			dir = robotDirection.ordinal();
			dir = (dir - 1) % 4;
			if(dir < 0)
				dir += 4;
		}else{
			dir = robotDirection.ordinal();
			dir = (dir + 1) % 4;
		}
		return dtd[dir];
		
	}
	
	public void updateRobotSensors(String msg){
		//range for short range sensor
		
//		this.sensors.front = retrieveSensor(SENSOR_FRONT);
//		this.sensors.frontLeft = retrieveSensor(SENSOR_FRONTLEFT);
//		this.sensors.frontRight = retrieveSensor(SENSOR_FRONTRIGHT);
//		this.sensors.left = retrieveSensor(SENSOR_LEFT);
//		this.sensors.right = retrieveSensor(SENSOR_RIGHT);
		msgFromRpi = msg;
		if(msg.length() < 0)
			return;
		if(msg.charAt(msg.length() - 1) != 'q')
			return;
		this.sensors.front = retrieveRealSensor(SENSOR_FRONT);
		this.sensors.frontLeft = retrieveRealSensor(SENSOR_FRONTLEFT);
		this.sensors.frontRight = retrieveRealSensor(SENSOR_FRONTRIGHT);
		this.sensors.left = retrieveRealSensor(SENSOR_LEFT);
		this.sensors.right = retrieveRealSensor(SENSOR_RIGHT);
//		System.out.println("Front Sensor: " + this.sensors.front);
//		System.out.println("FrontLeft Sensor: " + this.sensors.frontLeft);
//		System.out.println("FrontRight Sensor: " + this.sensors.frontRight);
//		System.out.println("Left Sensor: " + this.sensors.left);
//		System.out.println("Right Sensor: " + this.sensors.right);
		
	}
	
	public int scanInfront(){
		int leftValue, rightValue, frontValue;
		frontValue = retrieveRealSensor(SENSOR_FRONT);
		leftValue = retrieveRealSensor(SENSOR_FRONTLEFT);
		rightValue = retrieveRealSensor(SENSOR_FRONTRIGHT);
		
		frontValue -= FRONT_SENSOR_YOFFSET;
		
		return Math.min(Math.min(leftValue, rightValue), frontValue);
	}
	
	public boolean isMoveableInFront(){
		/*if(scanInfront() <= 15)
			return false;
		return true;*/
		int[] curGrid = Exploration.coordinatesToGrid(xPos, yPos);
		int[][] curMap = robotMap.getMap();
		int[] offset = getSensorOffsets(this.direction, SENSOR_FRONT);
		curGrid[0] += offset[0]*2;
		curGrid[1] += offset[1]*2;
		if(Exploration.isOutofArenaByGrid(curGrid[0],curGrid[1]))
			return false;
		if(curMap[curGrid[0]][curGrid[1]] == 1 || curMap[curGrid[0]][curGrid[1]] == 2)
			return false;
		
		int tempGridX = curGrid[0] + offset[1];
		int tempGridY = curGrid[1] + offset[0];
		if(Exploration.isOutofArenaByGrid(tempGridX, tempGridY))
			return false;
		if(curMap[tempGridX][tempGridY] == 1 || curMap[tempGridX][tempGridY] == 2)
			return false;
		tempGridX = curGrid[0] + offset[1]*-1;
		tempGridY = curGrid[1] + offset[0]*-1;
		if(Exploration.isOutofArenaByGrid(tempGridX, tempGridY))
			return false;
		if(curMap[tempGridX][tempGridY] == 1 || curMap[tempGridX][tempGridY] == 2)
			return false;
		return true;
	}
	
	public int scanLeft(){
		return retrieveRealSensor(SENSOR_LEFT);
	}
	
	public boolean isMoveableOnLeft(){
		int[] curGrid = Exploration.coordinatesToGrid(xPos, yPos);
		int[][] curMap = robotMap.getMap();
		int[] offset = getSensorOffsets(this.direction, SENSOR_LEFT);
		curGrid[0] += offset[0]*2;
		curGrid[1] += offset[1]*2;
		if(Exploration.isOutofArenaByGrid(curGrid[0],curGrid[1]))
			return false;
		if(curMap[curGrid[0]][curGrid[1]] == 1 || curMap[curGrid[0]][curGrid[1]] == 2)
			return false;
		
		int tempGridX = curGrid[0] + offset[1];
		int tempGridY = curGrid[1] + offset[0];
		if(Exploration.isOutofArenaByGrid(tempGridX, tempGridY))
			return false;
		if(curMap[tempGridX][tempGridY] == 1 || curMap[tempGridX][tempGridY] == 2)
			return false;
		tempGridX = curGrid[0] + offset[1]*-1;
		tempGridY = curGrid[1] + offset[0]*-1;
		if(Exploration.isOutofArenaByGrid(tempGridX, tempGridY))
			return false;
		if(curMap[tempGridX][tempGridY] == 1 || curMap[tempGridX][tempGridY] == 2)
			return false;
		return true;
	}
	
	/*public boolean isUnExploredOnLeft(){
		int[] curGrid = Exploration.coordinatesToGrid(xPos, yPos);
		int[][] curMap = robotMap.getMap();
		int[] offset = getSensorOffsets(this.direction, SENSOR_LEFT);
		curGrid[0] += offset[0]*2;
		curGrid[1] += offset[1]*2;
		if(Exploration.isOutofArenaByGrid(curGrid[0],curGrid[1]))
			return false;
		if(curMap[curGrid[0]][curGrid[1]] == 2)
			return true;
		
		int tempGridX = curGrid[0] + offset[1];
		int tempGridY = curGrid[1] + offset[0];
		if(Exploration.isOutofArenaByGrid(tempGridX, tempGridY))
			return false;
		if(curMap[tempGridX][tempGridY] == 2)
			return true;
		tempGridX = curGrid[0] + offset[1]*-1;
		tempGridY = curGrid[1] + offset[0]*-1;
		if(Exploration.isOutofArenaByGrid(tempGridX, tempGridY))
			return false;
		if(curMap[tempGridX][tempGridY] == 2)
			return true;
		return false;
	}*/
	
	
	//--------------- ADDED EFFICIENT CODE --------------
	public boolean isUnExploredOnLeft(){
		int[] curGrid = Exploration.coordinatesToGrid(xPos, yPos);
		int[][] curMap = robotMap.getMap();
		int[] offset = getSensorOffsets(this.direction, SENSOR_LEFT);
		curGrid[0] += offset[0]*2;
		curGrid[1] += offset[1]*2;
		if(Exploration.isOutofArenaByGrid(curGrid[0],curGrid[1]))
			return false;
		if(curMap[curGrid[0]][curGrid[1]] == 2)
			return true;
		
		int tempGrid1X = curGrid[0] + offset[1];
		int tempGrid1Y = curGrid[1] + offset[0];
		int tempGrid2X = curGrid[0] + offset[1]*-1;
		int tempGrid2Y = curGrid[1] + offset[0]*-1;
		if(Exploration.isOutofArenaByGrid(tempGrid1X, tempGrid1Y))
			return false;
		
		if(Exploration.isOutofArenaByGrid(tempGrid2X, tempGrid2Y))
			return false;
		
		if(curMap[tempGrid1X][tempGrid1Y] == 2 && curMap[curGrid[0]][curGrid[1]] != 1 &&
				curMap[tempGrid2X][tempGrid2Y] != 1)
			return true;
		
		
		if(curMap[tempGrid2X][tempGrid2Y] == 2 && curMap[curGrid[0]][curGrid[1]] != 1 &&
				curMap[tempGrid1X][tempGrid1Y] != 1)
			return true;
		return false;
	}
	
	public int scanRight(){
		return retrieveRealSensor(SENSOR_RIGHT);
	}
	
	public boolean isGoal(){
		int[] curGrid = Exploration.coordinatesToGrid(this.xPos, this.yPos);
//		if(curGrid[0] >= 16 && curGrid[1] >= 12){
//			return true;
//		}
		if(this.xPos >= 125 && this.yPos >=175)
			return true;
		return false;
	}
	
	public boolean isStartingPt(){
		int[] curGrid = Exploration.coordinatesToGrid(this.xPos, this.yPos);
		if(curGrid[0] <= 2 && curGrid[1] <= 1){
			return true;
		}
		return false;
	}
	
	public boolean hastrulyReachGoal(){
		if(this.xPos == 135 && this.yPos == 185)
			return true;
		return false;
	}
	
	public boolean hastrulyReachStart(){
		if(this.xPos == 15 && this.yPos == 15)
			return true;
		return false;
	}
	

	// --------------------- GETING AND ACCESSING -----------------------
	public int[][] getRobotMap(){
		return robotMap.getMap();
	}
	
	public int getXPos(){
		return xPos;
	}
	
	public int getYPos(){
		return yPos;
	}
	
	public Directions getDirection(){
		return direction;
	}
	
	
	// --------------------- VALIDATION AND ACCESSING -----------------------
	
	public int[] getSensorPosition(String sensor){
		int x = this.xPos;
		int y = this.yPos;
		int[] ary = new int[2];
		if(this.direction == Directions.N){
			if(sensor == SENSOR_FRONTLEFT){
				x += FRONTLEFT_SENSOR_XOFFSET; 
				y += FRONTLEFT_SENSOR_YOFFSET;
			}else if(sensor == SENSOR_FRONTRIGHT){
				x += FRONTRIGHT_SENSOR_XOFFSET;
				y += FRONTRIGHT_SENSOR_YOFFSET;
			}else if(sensor == SENSOR_FRONT){
				y += FRONT_SENSOR_YOFFSET;
			}else if(sensor == SENSOR_LEFT){
				x += LEFT_SENSOR_XOFFSET;
			}else{
				x += RIGHT_SENSOR_XOFFSET;
			}
		}else if(this.direction == Directions.S){
			if(sensor == SENSOR_FRONTLEFT){
				x += FRONTLEFT_SENSOR_XOFFSET * -1;
				y += FRONTLEFT_SENSOR_YOFFSET * -1;
			}else if(sensor == SENSOR_FRONTRIGHT){
				x += FRONTRIGHT_SENSOR_XOFFSET * -1;
				y += FRONTRIGHT_SENSOR_YOFFSET * -1;
			}else if(sensor == SENSOR_FRONT){
				y += FRONT_SENSOR_YOFFSET * -1;
			}else if(sensor == SENSOR_LEFT){
				x += LEFT_SENSOR_XOFFSET * -1;
			}else{
				x += RIGHT_SENSOR_XOFFSET * -1;
			}
		}else if(this.direction == Directions.E){
			if(sensor == SENSOR_FRONTLEFT){
				y += FRONTLEFT_SENSOR_XOFFSET * -1;
				x += FRONTLEFT_SENSOR_YOFFSET;
			}else if(sensor == SENSOR_FRONTRIGHT){
				y += FRONTRIGHT_SENSOR_XOFFSET * -1;
				x += FRONTRIGHT_SENSOR_YOFFSET;
			}else if(sensor == SENSOR_FRONT){
				x += FRONT_SENSOR_YOFFSET;
			}else if(sensor == SENSOR_LEFT){
				y += LEFT_SENSOR_XOFFSET * -1;
			}else{
				y += RIGHT_SENSOR_XOFFSET * -1;
			}
		}else{
			if(sensor == SENSOR_FRONTLEFT){
				y += FRONTLEFT_SENSOR_XOFFSET; 
				x += FRONTLEFT_SENSOR_YOFFSET * -1;
			}else if(sensor == SENSOR_FRONTRIGHT){
				y += FRONTRIGHT_SENSOR_XOFFSET;
				x += FRONTRIGHT_SENSOR_XOFFSET * -1;
			}else if(sensor == SENSOR_FRONT){
				x += FRONT_SENSOR_YOFFSET * -1;
			}else if(sensor == SENSOR_LEFT){
				y += LEFT_SENSOR_XOFFSET;
			}else{
				y += RIGHT_SENSOR_XOFFSET;
			}
		}
		ary[0] = x;
		ary[1] = y;
		return ary;
	}
	
	
	public int retrieveRealSensor(String sensorName){
		
		
		int frontV = -1;
		int frontLeftV = -1;
		int frontRightV = -1;
		int leftV = -1;
		int rightV = -1;
		if(msgFromRpi == null)
			return -1;
//		if(msgFromRpi.charAt(0) == 'p'){
//			msgFromRpi = msgFromRpi.substring(1, msgFromRpi.length()-1);
			String str = "";
			char c = msgFromRpi.charAt(0);
			int i = 0;
			while(c != ','){
				str += c ;
				i++;
				c = msgFromRpi.charAt(i);
			}
			leftV = Integer.parseInt(str);
			i++;
			c = msgFromRpi.charAt(i);
			str = "";
			while(c != ','){
				str += c ;
				i++;
				c = msgFromRpi.charAt(i);
			}
			frontLeftV = Integer.parseInt(str);
			i++;
			c = msgFromRpi.charAt(i);
			str = "";
			while(c != ','){
				str += c ;
				i++;
				c = msgFromRpi.charAt(i);
			}
			frontV = Integer.parseInt(str);
			i++;
			c = msgFromRpi.charAt(i);
			str = "";
			while(c != ','){
				str += c ;
				i++;
				c = msgFromRpi.charAt(i);
			}
			frontRightV = Integer.parseInt(str);
			i++;
			c = msgFromRpi.charAt(i);
			str = "";
			while(c != 'q'){
				str += c ;
				i++;
				c = msgFromRpi.charAt(i);
			}
			rightV = Integer.parseInt(str);
//		}
			
		if(sensorName == SENSOR_FRONT){
			return frontV;
		}else if(sensorName == SENSOR_FRONTLEFT){
			return frontLeftV;
		}else if(sensorName == SENSOR_FRONTRIGHT){
			return frontRightV;
		}else if(sensorName == SENSOR_LEFT){
			return leftV;
		}else{
			return rightV;
		}
			
	}
	
	/**
	 * This function will return the detection value of the respective sensor.
	 * If there is no detection, it will retrun -1
	 * This function is ONLY FOR SIMULATION RUN
	 * @param robot
	 * @param sensor
	 * @return Sensor Detection Value
	 */
	public int retrieveSensor(String sensor){
		
		int sensorRange = 40;
		int nearestObstacle = 0, offsetX, offsetY;
		int x, y;
		boolean isObstacleFound = false;
		
		x = this.xPos;
		y = this.yPos;
		int[] tempArray = getSensorOffsets(this.direction, sensor);
		offsetX = tempArray[0];
		offsetY = tempArray[1];
		
		int[] ary = getSensorPosition(sensor);
		x = ary[0];
		y = ary[1];
		
		for(int i = 0; i < sensorRange; i+= Exploration.OBSTACLE_SIZE){
			
			if(x <= 0 || y <= 0 || x >= Exploration.TOTAL_HORIZONTAL_TILES*Exploration.OBSTACLE_SIZE 
					|| y >= Exploration.TOTAL_VERTICAL_TILES * Exploration.OBSTACLE_SIZE){//
				//isObstacleFound = true;
				return nearestObstacle;
			}
			
			x += offsetX*Exploration.OBSTACLE_SIZE;
			y += offsetY*Exploration.OBSTACLE_SIZE;
			nearestObstacle += Exploration.OBSTACLE_SIZE;
			if(map.isObstacleThere(x, y)){
				isObstacleFound = true;
				break;
			}
		}
		if(!isObstacleFound)
			nearestObstacle = 999;
		return nearestObstacle;
		
		
	}
	
	static int[] getSensorOffsets(Directions direction, String sensor){
		int x = 0, y = 0;
		int[] array = new int[2];
		if(sensor == "front" || sensor == "frontleft" || sensor == "frontright"){
			if(direction == Directions.E){
				x = 1;
			}else if(direction == Directions.N){
				y = 1;
			}else if(direction == Directions.S){
				y = -1;
			}else{
				x = -1;
			}
		}else if(sensor == "left"){
			if(direction == Directions.E){
				y = 1;
			}else if(direction == Directions.N){
				x = -1;
			}else if(direction == Directions.S){
				x = 1;
			}else{
				y = -1;
			}
		}else{
			if(direction == Directions.E){
				y = -1;
			}else if(direction == Directions.N){
				x = 1;
			}else if(direction == Directions.S){
				x = -1;
			}else{
				y = 1;
			}
		}
		
		array[0] = x;
		array[1] = y;
		return array;
	}
	
	int validateXCoordinates(int x){
		if(x > (Exploration.TOTAL_HORIZONTAL_TILES - 1) * Exploration.OBSTACLE_SIZE - Exploration.OBSTACLE_SIZE/2){
			return (Exploration.TOTAL_HORIZONTAL_TILES - 1) * Exploration.OBSTACLE_SIZE - Exploration.OBSTACLE_SIZE/2;
		}
		if(x < 15){
			return 15;
		} 
		return x;
	}
	
	int validateYCoordinates(int y){
		if(y > (Exploration.TOTAL_VERTICAL_TILES - 1) * Exploration.OBSTACLE_SIZE + Exploration.OBSTACLE_SIZE){
			return (Exploration.TOTAL_VERTICAL_TILES - 1) * Exploration.OBSTACLE_SIZE + Exploration.OBSTACLE_SIZE;
		}
		if(y < 15){
			return 15;
		}
		return y;
	}
	
	
	
	//----------------------------- ALIGNMENT METHODS ------------------------------
	
	public boolean isWallOnLeft(){
		int[] curGrid = Exploration.coordinatesToGrid(xPos, yPos);
		int[] offset = getSensorOffsets(this.direction, SENSOR_LEFT);
		curGrid[0] += offset[0]*2;
		curGrid[1] += offset[1]*2;
		if(Exploration.isitWall(curGrid[0], curGrid[1]))
			return true;
		return false;
	}
	
	public boolean isWallOnRight(){
		int[] curGrid = Exploration.coordinatesToGrid(xPos, yPos);
		int[] offset = getSensorOffsets(this.direction, SENSOR_RIGHT);
		curGrid[0] += offset[0]*2;
		curGrid[1] += offset[1]*2;
		if(Exploration.isitWall(curGrid[0], curGrid[1]))
			return true;
		return false;
	}
	
	public boolean isWallInFront(){
		int[] curGrid = Exploration.coordinatesToGrid(xPos, yPos);
		int[] offset = getSensorOffsets(this.direction, SENSOR_FRONT);
		curGrid[0] += offset[0]*2;
		curGrid[1] += offset[1]*2;
		if(Exploration.isitWall(curGrid[0], curGrid[1]))
			return true;
		return false;
	}
	
	public boolean isExploredOnLeftForAlign(){
		int[] curGrid = Exploration.coordinatesToGrid(xPos, yPos);
		int[][] curMap = robotMap.getMap();
		int[] offset = getSensorOffsets(this.direction, SENSOR_LEFT);
		curGrid[0] += offset[0]*2;
		curGrid[1] += offset[1]*2;
		if(Exploration.isOutofArenaByGrid(curGrid[0],curGrid[1]))
			return true;
		if(curMap[curGrid[0]][curGrid[1]] == 2)
			return false;
		
		int tempGridX = curGrid[0] + offset[1];
		int tempGridY = curGrid[1] + offset[0];
		if(Exploration.isOutofArenaByGrid(tempGridX, tempGridY))
			return true;
		if(curMap[tempGridX][tempGridY] == 2)
			return false;
		tempGridX = curGrid[0] + offset[1]*-1;
		tempGridY = curGrid[1] + offset[0]*-1;
		if(Exploration.isOutofArenaByGrid(tempGridX, tempGridY))
			return true;
		if(curMap[tempGridX][tempGridY] == 2)
			return false;
		return true;
	}
	
	public boolean isThereOneObstacleOnLeft(){
		int[] curGrid = Exploration.coordinatesToGrid(xPos, yPos);
		int[][] curMap = robotMap.getMap();
		int[] offset = getSensorOffsets(this.direction, SENSOR_LEFT);
		curGrid[0] += offset[0]*2;
		curGrid[1] += offset[1]*2;
		if(Exploration.isOutofArenaByGrid(curGrid[0],curGrid[1]))
			return false;
		
		int tempGridX = curGrid[0] + offset[1];
		int tempGridY = curGrid[1] + offset[0];
		if(Exploration.isOutofArenaByGrid(tempGridX, tempGridY))
			return false;
		if(curMap[tempGridX][tempGridY] == 1)
			return true;
		tempGridX = curGrid[0] + offset[1]*-1;
		tempGridY = curGrid[1] + offset[0]*-1;
		if(Exploration.isOutofArenaByGrid(tempGridX, tempGridY))
			return false;
		if(curMap[tempGridX][tempGridY] == 1)
			return true;
		return true;
	}
	
	public boolean isConsecObstacleOnLeft(){
		int[] curGrid = Exploration.coordinatesToGrid(xPos, yPos);
		int[][] curMap = robotMap.getMap();
		int[] offset = getSensorOffsets(this.direction, SENSOR_LEFT);
		curGrid[0] += offset[0]*2;
		curGrid[1] += offset[1]*2;
		if(Exploration.isOutofArenaByGrid(curGrid[0],curGrid[1]))
			return false;
		
		int tempGrid1X = curGrid[0] + offset[1];
		int tempGrid1Y = curGrid[1] + offset[0];
		int tempGrid2X = curGrid[0] + offset[1]*-1;
		int tempGrid2Y = curGrid[1] + offset[0]*-1;
		if(Exploration.isOutofArenaByGrid(tempGrid1X, tempGrid1Y))
			return false;
		
		if(Exploration.isOutofArenaByGrid(tempGrid2X, tempGrid2Y))
			return false;
		
		if(curMap[tempGrid1X][tempGrid1Y] == 1 && curMap[tempGrid2X][tempGrid2Y] == 1)
			return true;
		
		return false;
	}
	
	public boolean isConsecObstacleInFront(){
		int[] curGrid = Exploration.coordinatesToGrid(xPos, yPos);
		int[][] curMap = robotMap.getMap();
		int[] offset = getSensorOffsets(this.direction, SENSOR_FRONT);
		curGrid[0] += offset[0]*2;
		curGrid[1] += offset[1]*2;
		if(Exploration.isOutofArenaByGrid(curGrid[0],curGrid[1]))
			return false;
		
		int tempGrid1X = curGrid[0] + offset[1];
		int tempGrid1Y = curGrid[1] + offset[0];
		int tempGrid2X = curGrid[0] + offset[1]*-1;
		int tempGrid2Y = curGrid[1] + offset[0]*-1;
		if(Exploration.isOutofArenaByGrid(tempGrid1X, tempGrid1Y))
			return false;
		
		if(Exploration.isOutofArenaByGrid(tempGrid2X, tempGrid2Y))
			return false;
		
		if(curMap[tempGrid1X][tempGrid1Y] == 1 && curMap[tempGrid2X][tempGrid2Y] == 1)
			return true;
		
		return false;
	}
}
