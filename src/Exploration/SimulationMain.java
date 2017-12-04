package Exploration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class SimulationMain {
	public static final String LEFT_INSTRUCTION = "A";
	public static final String RIGHT_INSTRUCTION = "D";
	public static final String FRONT_INSTRUCTION = "W";
	public static final String ALIGN_INSTRUCTION = "T";
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static final int TOTAL_HORIZONTAL_TILES = 15;
	public static final int TOTAL_VERTICAL_TILES = 20;
	public static final int alignFlag = 5; //alignment constant for shortest path alignment
	static SimulationMap simulationMap = new SimulationMap();
	static int time = 0;
	static int realTime = 0;
	static JFrame frame;
	static JTextArea timetext = new JTextArea();
	static int count = 0;
	static int timelimit = 360;
	static JLabel lbTime = new JLabel( "Time limit     : ");
	//static JPanel map = new JPanel(new FlowLayout(FlowLayout.LEADING));
	static JButton jbExplore = new JButton("Explore");
	static JButton jbManualExplore = new JButton("Speed Limit Explore");
	static JButton jbBackToStart = new JButton("Back to Start");
	static JButton jbShortestPath = new JButton("GO!!");
	static JTextField txtTime = new JTextField("", 5);
	static JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//	static int dx[] = {0,-1,0,1};
//	static int dy[] = {1,0,-1,0};
	static final int dx[] = {-1,0,1,0};
	static final int dy[] = {0,1,0,-1};
	static Directions dtd[] = {Directions.W,Directions.N,Directions.E,Directions.S};
	static int backToStart = 0;
	static int startExplore = 0;
	static int manualExplore = 0;
	static int doNextAction = 0;
	static int startingLastExplore = 0;
	static int startShortestPath = 0;
	static int foundroute = 0;
	static int wallf = 1;
	static final int requiredToExplore = (int)(Exploration.TOTAL_HORIZONTAL_TILES * Exploration.TOTAL_VERTICAL_TILES);
	static int startTime;
	static int currentTime;
	static int leftAlignCounter = 0;
	static int frontAlignCounter = 0;
	static String midInstruction = "";
	static String shorteststring = "U";
	
	private static Robot robot = new Robot(15, 15, Directions.N);
	
	
	public static Client client;
	
	/**
	 * Launch the application.
	 */
	
	static class Node { 
		public Node(int sx, int sy) {
			x = sx; y = sy; prev = -1;
		}
		public Node(int sx, int sy, int pv) {
			x = sx; y = sy; prev = pv;
		}
		int x,y,prev;		
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimulationMain window = new SimulationMain();
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		try {
			InetAddress host = InetAddress.getByName("192.168.20.20");
			int port = 5000;
			client = new Client(host, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(client != null)
			System.out.println("Connection Successful");
		else
			System.out.println("Connection Unsuccessful");
		
		
		
		
		
		String msg = SimulationMain.recMsgFromArduino();
		System.out.println("Msg =" + msg);
		if(msg.equals("Start")){
			System.out.println("Received start msg from android");
			startExplore = 1;
			
		}else{
			System.out.println("Didn't received start msg from android");
		}
		
		repaint(); // REQUIRED TO DE-COMMENT
		while(startExplore == 0)
			waitFor5ms();
		performAlignmentForStart(); // REQUIRED TO DE-COMMENT
		
		startTime = currentTimeMillis();
		auto_explore();
		repaint();// REQUIRED TO DE-COMMENT
		
		//System.out.println(robot.robotMap.convertMapToBinary());
		try{
			File file = new File("map_descriptor.txt");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(robot.robotMap.convertMapToBinary());
			bw.close();
		}catch(IOException ex){
			System.out.println("File error : "+ex.getMessage());
		}
	}
	
	public static void auto_explore() {
		while(robot.robotMap.numExploredGrids < requiredToExplore && !robot.hastrulyReachGoal() && backToStart != 1){
//			lvnAlgorithm();
			leftWallSticking();
			if(manualExplore == 1){
				while(doNextAction == 0)
					waitFor5ms();
			}
		}
		performAlignmentForEnd();
		while(!robot.hastrulyReachStart() && backToStart != 1){
//			lvnAlgorithm();
			leftWallSticking();
			if(manualExplore == 1){
				while(doNextAction == 0)
					waitFor5ms();
			}
		}
//		robot.rotateRobot(Directions.N);
//		repaint();
//		doAlignmentForStart();
		
		
		startingLastExplore = 1;
		int explored = 0;
		while(explored < Exploration.TOTAL_HORIZONTAL_TILES * Exploration.TOTAL_VERTICAL_TILES * 0.9 && backToStart != 1){
			lastPartExplore();
			repaint();
			explored = robot.robotMap.numExploredGrids;
			if(manualExplore == 1){
				while(doNextAction == 0)
					waitFor5ms();
			}
		}
		startingLastExplore = 0;
		
		if(realTime >= timelimit){
			System.out.println("Time limit exceeded");
			System.out.println("Going back to Start!");
			backToStart = 1;
		}
		
		if(backToStart == 1){
			route = bfs(Exploration.coordinatesToGrid(robot.getXPos()),
					Exploration.coordinatesToGrid(robot.getYPos()));
			
			while(robot.getXPos() != 15 || robot.getYPos() != 15){
				int d = route.charAt(route.length()-1) - '0';
				moveRobot(dtd[d]);
				repaint();
				route = route.substring(0,route.length()-1);
			}
		}
		
		System.out.println(robot.robotMap.convertMapToBinary());
		try{
			File file = new File("map_descriptor.txt");
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(robot.robotMap.convertMapToBinary());
			bw.close();
		}catch(IOException ex){
			System.out.println("File error : "+ex.getMessage());
		}
		System.out.println("Ready to go fastest path!");
		
		
		
		//exploration ends
		//String instructions = prepareFastestPath();
		System.out.println("Ready to go fastest path!");
		
		robot.robotMap.printMap();
		
		String msg = SimulationMain.recMsgFromArduino();
		System.out.println("Msg =" + msg);
		if(msg.equals("Shortest")){
			System.out.println("Received shortest msg from android");
			startShortestPath = 1;
			
		}else{
			System.out.println("Didn't received shortest msg from android");
		}
		
		while(startShortestPath == 0)
			waitFor5ms();
		fastestPathRun();
		//System.out.println("Shortest path instruction: " + instructions);
		//sendMsgToArduino(instructions);
		//fastestPathRun();
			
	}
	
	public SimulationMain(){
		initialize();
	}
	
	

	/**
	 * Initialize the contents of the frame.
	 */
	public static void initialize() {
		
		frame = new JFrame();
		frame.getContentPane().setEnabled(false);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(0, 0, 820, 680);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		timetext.setEditable(false);
		timetext.setFocusable(false);
		timetext.setText("Time: " + time);
		frame.getContentPane().add(timetext, BorderLayout.NORTH);
		
		textPanel.add(lbTime);
		textPanel.add(txtTime);
		
		jbExplore.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				startExplore = 1;
//				try{
////					timelimit = Integer.parseInt(txtTime.getText());
//				}catch(Exception e2){
//					System.out.println("ERROR : "+e2.toString());
//					timelimit = 1000;
//				}
			}
		});
		
		textPanel.add(jbExplore);
		textPanel.setPreferredSize(new Dimension(200,200));
		
		jbManualExplore.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				manualExplore = 1;
				doNextAction = 1;
			}
		});
		
		textPanel.add(jbManualExplore);
		textPanel.setPreferredSize(new Dimension(200,200));
		
		
		jbBackToStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				backToStart = 1;
			}
		});
		
		textPanel.add(jbBackToStart);
		textPanel.setPreferredSize(new Dimension(200,200));
		
		jbShortestPath.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				startShortestPath = 1;
			}
		});
		textPanel.add(jbShortestPath);
		textPanel.setPreferredSize(new Dimension(200,200));
		frame.getContentPane().add(textPanel, BorderLayout.EAST);		
		frame.getContentPane().add(simulationMap, BorderLayout.CENTER);
		frame.requestFocus();
	}
	
	
	
	
	
	//----------------------------- ALGORITHMS AND APPROACHES -------------------------
	
	static String route = "";
	static Node tempGoal;
	
	public static void lastPartExplore(){
		
		if(true){
			if (foundroute == 1) {
				
				if(route.length() < 1)
				{
					//checkAround();
					foundroute = 0;
					return;
				}
				int d = route.charAt(route.length()-1) - '0';
				
//				time = time +robot.moveRobot(dtd[d]);
//				System.out.println(route);
				moveRobot(dtd[d]);
				repaint();
				if (route.length() == 1) {
					foundroute = 0;
					return ;
				}
				route = route.substring(0,route.length()-1);
				int[][] tempMap = robot.robotMap.getMap();
				/*if(route == null && 
						tempMap[tempGoal.x][tempGoal.y] == 2 )
				{
					robot.turnLeft();
					robot.updateRobotSensors();
					robot.updateRobotMap();
					robot.turnLeft();
					robot.updateRobotSensors();
					robot.updateRobotMap();
					robot.turnLeft();
					repaint();
				}*/
				return ;
			}
			route = bfs(Exploration.coordinatesToGrid(robot.getXPos()),
					Exploration.coordinatesToGrid(robot.getYPos()));
//			System.out.println(route);
			if (route == null) {
				System.out.println("Stop");
				timelimit=0;
				count = 0;
				return ;
			}
			foundroute = 1;
			repaint();
		}
	}
	
//	private static String findTrace(){
//		Node node = (Node)queue.remove();
////		if (has_unknown(node.x, node.y)) { 
//			
//			System.out.println("Found Unknown: " + node.x + ", " + node.y);
//			
//			tempGoal = node;
//			return trace(node,prev);
////		}
//	}
	
	static Queue<Node> queue;
	static int[][] prev;
	private static String bfs(int sx, int sy) {
//		int[][] prev;
		prev = new int[15][20];
		for (int i = 0; i < 15; i ++ )
			for (int j = 0; j < 20; j ++) prev[i][j] = -2;
//		Queue<Node> queue = new LinkedList<Node>();
		queue = new LinkedList<Node>();
		queue.add(new Node(sx,sy));
		prev[sx][sy] = -1;
		while(!queue.isEmpty()) {
			Node node = (Node)queue.remove();
//			System.out.println(node.x + " " + node.y + " ");
				
			if(backToStart != 1){
				if (has_unknown(node.x, node.y)) { 
					System.out.println("Found Unknown: " + node.x + ", " + node.y);
					tempGoal = node;
					return trace(node,prev);
				}
			}else{
				if(node.x == 1 && node.y == 1)
//				node.x = 1; node.y = 1;
					return trace(node,prev);
			}
				
			for (int d = 0; d < 4; d ++) {				
				int tx = node.x + dx[d];
				int ty = node.y + dy[d];
				
				/*if(!isknown(tx, ty) && prev[tx][ty] == -2){
					queue.add(new Node(tx,ty));
					prev[tx][ty] = d;				
//					return findTrace();
					break;
				}*/
				if (!robot.robotMap.isOkaytoMove(tx,ty)) continue;
//				if(!robot.robotMap.isOkaytoAdd(tx, ty)) continue;
				if (prev[tx][ty] != -2) continue;
				queue.add(new Node(tx,ty));
				prev[tx][ty] = d;
//				System.out.println(loopcount);
			
			}
			
			
		}
		repaint();
		return null;
	}
	
	//--------------------- PRINTING ----------------
	/*if(tx == 7 && ty == 5){
		System.out.println("Map");
		robot.robotMap.printMap();
		System.out.println("Virtual Map");
		robot.robotMap.printVirtualMap();
		
		//Print Prev
		System.out.println("This is the route map");
		for(int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++){
			for(int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++){
				//System.out.print(prev[i][Exploration.TOTAL_VERTICAL_TILES - j - 1] + ", ");
				System.out.format("%3d", prev[i][j]);
			}
			System.out.println();
		}
	}*/
	
	
	public static boolean isknown(int x, int y){
		if(Exploration.isOutofArenaByGrid(x, y))
			return true;
		int[][] tMap = robot.robotMap.getMap();
		if(tMap[x] [y] == 2)
			return false;
		return true;
	}
	
	public static boolean has_unknown(int x, int y) {
		
		for (int d = 0; d < 4; d ++) {
			int tx = x + dx[d];
			int ty = y + dy[d];
//			if (map.getCell(tx, ty) == Map.UNKNOWN) return true;
			int[][] tempMap = robot.robotMap.getMap();
			if(tx >= 0 && ty >= 0 && tx < Exploration.TOTAL_HORIZONTAL_TILES &&
					ty < Exploration.TOTAL_VERTICAL_TILES){
				if(tempMap[tx][ty]==2) 
					return true;
			}
				
		}
		return false;
	}
	
//	public static boolean has_unknown(int x, int y) {
//		
//		int mini = -2;
//		int maxi = 2;
//		for(int i = mini; i <= maxi; i++){
//			for(int j = mini; j <= maxi; j++){
//				int tx = x + i;
//				int ty = y + j;
//				if((i == -2 && j == -2) || (i == -2 && j == 2) || 
//						(i == 2 && j == -2) || (i == 2 && j == 2))
//					continue;
//				int[][] tempMap = robot.robotMap.getMap();
//				if(tx >= 0 && ty >= 0 && tx < Exploration.TOTAL_HORIZONTAL_TILES &&
//						ty < Exploration.TOTAL_VERTICAL_TILES){
//					if(tempMap[tx][ty]==2) 
//						return true;
//				}
//			}
//		}
//		return false;
//	}
	private static String trace(Node node,int[][] prev) {
		int x = node.x; int y = node.y;
		String s = "";
		while (prev[x][y] != -1) {
			int p = prev[x][y]; 
			char c = (char) ('0' + p); 
			s = s + c;
			if(p < 0) continue;
			x = x - dx[p];
			y = y - dy[p];
		}
		return s;
	}
	
	public static void lvnAlgorithm(){

		if(robot.robotMap.numExploredGrids < requiredToExplore){
			int[][] tempMap = robot.robotMap.getMap();
			Grid tempGoalGrid = new Grid(1,1);
			for(int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++){
				for(int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++){
					if(tempMap[i][j] ==2)
						tempGoalGrid = new Grid(i, j);
				}
			}
			int turnLeftCount = 0;
			int turnRightCount = 0;
			while(Exploration.coordinatesToGrid(robot.getXPos()) != tempGoalGrid.x ||
					Exploration.coordinatesToGrid(robot.getYPos()) != tempGoalGrid.y)
			{
			if(robot.robotMap.numExploredGrids > requiredToExplore)
				break;
			tempMap = robot.robotMap.getMap();
			Grid curGrid = new Grid(Exploration.coordinatesToGrid(robot.getXPos()),
					Exploration.coordinatesToGrid(robot.getYPos()));
			
			int[] frontOffset = Robot.getSensorOffsets(robot.getDirection(), Robot.SENSOR_FRONT);
			int[] leftOffset = Robot.getSensorOffsets(robot.getDirection(), Robot.SENSOR_LEFT);
			int[] rightOffset = Robot.getSensorOffsets(robot.getDirection(), Robot.SENSOR_RIGHT);
			Grid frontGrid = new Grid(curGrid.x + frontOffset[0]*2, curGrid.y + frontOffset[1]*2);
			Grid leftGrid = new Grid(curGrid.x + leftOffset[0]*2, curGrid.y + leftOffset[1]*2);
			Grid rightGrid = new Grid(curGrid.x + rightOffset[0]*2, curGrid.y + rightOffset[1]*2);
			int frontVisited, leftVisited, rightVisited;
			if(Exploration.isOutofBoundByGrid(frontGrid.x, frontGrid.y)){
				frontVisited = 9999;
			}else{
				frontVisited = robot.visitedCount[frontGrid.x][frontGrid.y];
			}
			if(Exploration.isOutofBoundByGrid(leftGrid.x, leftGrid.y)){
				leftVisited = 9999;
			}else{
				leftVisited = robot.visitedCount[leftGrid.x][leftGrid.y];
			}
			if(Exploration.isOutofBoundByGrid(rightGrid.x, rightGrid.y)){
				rightVisited = 9999;
			}else{
				rightVisited = robot.visitedCount[rightGrid.x][rightGrid.y];
			} 
			
			int minVisited = 9999;
			String minDirection = "backward";
//			if
//			frontVisited = robot.visitedCount[frontGrid.x][frontGrid.y];
//			leftVisited = robot.visitedCount[leftGrid.x][leftGrid.y];
//			rightVisited = robot.visitedCount[rightGrid.x][rightGrid.y];
			if(!Exploration.isOutofBoundByGrid(frontGrid.x, frontGrid.y) && 
					frontVisited < 999 && tempMap[frontGrid.x][frontGrid.y] != 1 && 
					tempMap[frontGrid.x+leftOffset[0]][frontGrid.y+leftOffset[1]] != 1 &&
					tempMap[frontGrid.x+rightOffset[0]][frontGrid.y+rightOffset[1]] != 1 && 
					frontVisited < minVisited){
				minVisited = frontVisited;
				 minDirection = Robot.SENSOR_FRONT;
				 
				 
			}
//			if(leftVisited == rightVisited){
//				leftVisited--;
//			}
			
			if(!Exploration.isOutofBoundByGrid(leftGrid.x, leftGrid.y) && 
					!Exploration.isOutofBoundByGrid(leftGrid.x+leftOffset[0], leftGrid.y+leftOffset[1]) &&
					!Exploration.isOutofBoundByGrid(leftGrid.x+rightOffset[0], leftGrid.y+rightOffset[1]) &&
					leftVisited < 999 && tempMap[leftGrid.x][leftGrid.y] != 1 &&
					tempMap[leftGrid.x+leftOffset[0]][leftGrid.y+leftOffset[1]] != 1 &&
					tempMap[leftGrid.x+rightOffset[0]][leftGrid.y+rightOffset[1]] != 1 && 
					leftVisited < minVisited && turnLeftCount < 1){
				minVisited = leftVisited;
				minDirection = Robot.SENSOR_LEFT;
				
			}
			
			if(!Exploration.isOutofBoundByGrid(rightGrid.x, rightGrid.y) &&
					!Exploration.isOutofBoundByGrid(rightGrid.x+leftOffset[0], rightGrid.y+leftOffset[1]) &&
					!Exploration.isOutofBoundByGrid(rightGrid.x+rightOffset[0], rightGrid.y+rightOffset[1]) &&
					rightVisited < 999 && tempMap[rightGrid.x][rightGrid.y] != 1 && 
					tempMap[rightGrid.x+leftOffset[0]][rightGrid.y+leftOffset[1]] != 1 &&
					tempMap[rightGrid.x+rightOffset[0]][rightGrid.y+rightOffset[1]] != 1 && 
					rightVisited < minVisited && turnRightCount < 1){
				minVisited = rightVisited;
				minDirection = Robot.SENSOR_RIGHT;
				
			}
			
//			System.out.println("Front : " + frontVisited);
//			System.out.println("Left : " + leftVisited);
//			System.out.println("Right : " + rightVisited);
//			System.out.println("Minimum : " + minVisited);
			
			if(minDirection == Robot.SENSOR_FRONT){
				time += robot.moveForward();
//				deadLock = 0;
				turnLeftCount = 0;
				turnRightCount = 0;
//				System.out.println("Moving Forward");
			}else if(minDirection == Robot.SENSOR_LEFT){
				time += robot.turnLeft();
				turnLeftCount++;
//				deadLock++;
			}else if(minDirection == Robot.SENSOR_RIGHT){
				time += robot.turnRight();
				turnRightCount++;
//				deadLock++;
			}else{
				time += robot.turnBackward();
//				deadLock++;
			}
			repaint();
			}
		}
	}
	
	public static void shortestPathExploration(){
		
		while(robot.robotMap.numExploredGrids < Exploration.TOTAL_HORIZONTAL_TILES * Exploration.TOTAL_VERTICAL_TILES){
			Grid curGrid = new Grid(Exploration.coordinatesToGrid(robot.getXPos()), 
					Exploration.coordinatesToGrid(robot.getYPos()));
			Grid tempGoal = robot.robotMap.nearestUnexplored(curGrid);
	
			int[] startP = {Exploration.coordinatesToGrid(robot.getXPos()),
					Exploration.coordinatesToGrid(robot.getYPos())}; //for virtual map {row, col}
			int[] goalP = {tempGoal.x, tempGoal.y};  //for virtual map {row, col}
			int[] startP2 = {startP[1], startP[0]};
			int[] goalP2 = {goalP[1], goalP[0]};
			System.out.println("Start Passed in value: " + startP[0] + "," + startP[1]);
			System.out.println("Goal Passed in value: " + goalP[0] + "," + goalP[1]);
			int[][] vpath;
			VirtualMap vp = new VirtualMap(robot.robotMap.getMap(), 3);
			vp.printVirtualMap();
			ShortestPath sp = new ShortestPath(startP, goalP, vp.vMap);
			vpath=sp.dijkstra1();
			int[][] ppath = vp.transPathToPhysicalFromVirtual(vpath);
			int i =0;
			while(robot.getXPos() != tempGoal.x && robot.getYPos() != tempGoal.y && i < ppath.length){
				repaint();
				
				simplyMoveToThisPosition(ppath[i][1], ppath[i][0]); 
				i++;
			}
				                                       
		}
	}
	
	//--------------------------- SHORTEST PATH ----------------------------
	
	//modified - new function
		static String prepareFastestPath(){
			int[] startP = {1,1}; //for virtual map {row, col}
			int[] goalP = {18,13};  //for virtual map {row, col}
			int[][] vpath;
			int[][] pMap = robot.getRobotMap();
			int[][] p2Map = Exploration.transposeMap(pMap);
			VirtualMap vp = new VirtualMap(p2Map, 3);
			vp.printVirtualMap();
			ShortestPath sp = new ShortestPath(vp.transPointToVirtualFromPhysical(startP),vp.transPointToVirtualFromPhysical(goalP), vp.vMap);
			vpath=sp.dijkstra1();
			int[][] ppath = vp.transPathToPhysicalFromVirtual(vpath);
			//this instructions dont have alignment instructions
			//String instructions = vp.transPhysicalPathToInstruction(ppath); //translate the path to instructions for the robot
			//this instructions have alignment instructions
			String instructions = transPhysicalPathToInstructionWithAlignment(ppath); //translate the path to instructions for the robot
			String newInstructions = "";
			if (instructions.startsWith("D")) {
				robot.rotateRobot(Directions.E);
			}
			else{ 
				robot.rotateRobot(Directions.N);
			}
			newInstructions = instructions.substring(1);
			System.out.println("newInstructions: "+newInstructions);
			return newInstructions;
		}
	
	
	static void fastestPathRun(){
		int[] startP = {1,1}; //for virtual map {row, col}
		int[] goalP = {18,13};  //for virtual map {row, col}
		int[][] vpath;
		int[][] pMap = robot.getRobotMap();
		int[][] p2Map = Exploration.transposeMap(pMap);
		VirtualMap vp = new VirtualMap(p2Map, 3);
		vp.printVirtualMap();
		ShortestPath sp = new ShortestPath(vp.transPointToVirtualFromPhysical(startP),vp.transPointToVirtualFromPhysical(goalP), vp.vMap);
		vpath=sp.dijkstra1();
		int[][] ppath = vp.transPathToPhysicalFromVirtual(vpath);
		shortestpathmove(ppath);
	}
	
	
	public static void shortestpathmove(int p[][]){
		  for (int i = 0; i<p.length-2;i++){
			  
			  
			  
		   if(p[i][0]<p[i+1][0]){
		    //p[i][X] more than p[i+1][X] increase in X coordinates which is North direction
		    System.out.println("Increase in X coordinates");
		    
		    if(robot.getDirection()== Directions.E){
		     shorteststring += "W";
		     robot.moveForwardsimu();
		     
		    }
		     
		    if(robot.getDirection()==Directions.S){
		     shorteststring += "A";
		     robot.turnLeftsimu();
		     shorteststring += "W";
		     robot.moveForwardsimu();
		    }
		    if(robot.getDirection()==Directions.N){
		     shorteststring += "D";
		     robot.turnRightsimu();
		     shorteststring += "W";
		     robot.moveForwardsimu();
		    }   
		    
		   }else if ( p[i][0]>p[i+1][0]){
		    // Decrease in X coordinates
		    System.out.println("Decrease in X coordinates");
		    
		    if(robot.getDirection()==Directions.W){
		    	 shorteststring += "W";
		    	 robot.moveForwardsimu();
		    }  

		    if(robot.getDirection()==Directions.S){
			     shorteststring += "D";
			     robot.turnRightsimu();
			     shorteststring += "W";
			     robot.moveForwardsimu();
		    }
		    if(robot.getDirection()==Directions.N){
			     shorteststring += "A";
			     robot.turnLeftsimu();
			     shorteststring += "W";
			     robot.moveForwardsimu();
		    }

		    
		   }else if (p[i][1] < p[i+1][1]){
		    //Increase in Y coordinates
		    System.out.println("Increase in Y coordinates");
		    
		    if(robot.getDirection()==Directions.N){
			     shorteststring += "W";
			     robot.moveForwardsimu();
		    }
		    if(robot.getDirection()==Directions.E){
			     shorteststring += "A";
			     robot.turnLeftsimu();
			     shorteststring += "W";
			     robot.moveForwardsimu();
		    }
		    if(robot.getDirection()==Directions.W){
			     shorteststring += "D";
			     robot.turnRightsimu();
			     shorteststring += "W";
			     robot.moveForwardsimu();
		    }
		    
		   }else if (p[i][1] > p[i+1][1]){
		    //Decrease in Y coordinates
		    System.out.println("Decrease in Y coordinates");
		    
		    if (robot.getDirection()==Directions.S){
			     shorteststring += "W";
			     robot.moveForwardsimu();
		    }
		    if(robot.getDirection()==Directions.E){
			     shorteststring += "D";
			     robot.turnRightsimu();
			     shorteststring += "W";
			     robot.moveForwardsimu();
			     
		    }
		    if(robot.getDirection()==Directions.W){
			     shorteststring += "A";
			     robot.turnLeftsimu();
			     shorteststring += "W";
			     robot.moveForwardsimu();
			     
		    }
		    
		   }
		   
		  }
		  shorteststring += "WZ";
		  System.out.println("Goal reached");
		  System.out.println(shorteststring);
		  sendMsgToArduino(shorteststring);
		  
		 }
	
	
	//---------------------------MOVING AND ACCESSING----------------------------
	
	public static void simplyMoveToThisPosition(int xGrid, int yGrid){
		if(robot.getDirection() != getDirectionForThePath(robot.getXPos()/Exploration.OBSTACLE_SIZE,
				robot.getYPos()/Exploration.OBSTACLE_SIZE,
				xGrid, yGrid)){
			robot.turnLeft();
		}else if(robot.getXPos() != xGrid || robot.getYPos() != yGrid){
			robot.moveForward();
		}
	}
	
	public static Directions getDirectionForThePath(int startX, int startY, int endX, int endY){
		int xDiffer, yDiffer;
		
		xDiffer = endX - startX;
		yDiffer = endY - startY;
		
		if(xDiffer == 0){
			if(yDiffer < 0)
				return Directions.S;
			else if(yDiffer > 0)
				return Directions.N;
		}else if(yDiffer == 0){
			if(xDiffer < 0 )
				return Directions.W;
			else if(xDiffer > 0 )
				return Directions.E;
		}
		if(xDiffer < 0){
			return Directions.W;
		}else if(xDiffer > 0){
			return Directions.E;
		}else if(yDiffer < 0){
			return Directions.S;
		}else if(yDiffer > 0){
			return Directions.N;
		}
		return Directions.E;
	}
	
	public static void moveRobot(Directions d){
		if(robot.getDirection() != d)
		{
			time += robot.rotateRobot(d);
			repaint();
		}
		
		time += robot.moveForward();
		repaint();
		
	}
	
	public static void waitFor5ms(){
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		
		
	/*public static void checkAround(){
		robot.turnLeft();
		robot.updateRobotSensors();
		robot.updateRobotMap();
		robot.turnLeft();
		robot.updateRobotSensors();
		robot.updateRobotMap();
		robot.turnLeft();
		robot.updateRobotSensors();
		robot.updateRobotMap();
		robot.turnLeft();
		
	}*/
		
		private static int loopingCounter = 0;
				
		public static void leftWallSticking(){
//			if(loopingCounter > 5){
//				if(robot.isMoveableInFront())
//					robot.moveForward();
//				
//			}
			
			if(loopingCounter > 12){
				while(robot.isMoveableInFront())
				{
					robot.moveForward();
					repaint();
					incAlignmentCounters();
					checkAlignment();
				}
			}
			if(robot.isUnExploredOnLeft()){
				time += robot.turnLeft();
				repaint();
//				incAlignmentCounters();
//				checkAlignment();
				if(robot.isMoveableInFront()){
					time += robot.moveForward();
					repaint();
					incAlignmentCounters();
					checkAlignment();
				}else{
					time += robot.turnRight();
//					incAlignmentCounters();
					repaint();
				}
				loopingCounter = 0;
				return;
			}
			
			if(robot.isMoveableOnLeft() && loopingCounter < 5){
				time += robot.turnLeft();
				repaint();
				
//				incAlignmentCounters();
//				checkAlignment();
				if(robot.isMoveableInFront()){
					time += robot.moveForward();
					repaint();
					incAlignmentCounters();
					checkAlignment();
					loopingCounter++; // to prevent looping
				}
				return;
			}
			
			if(robot.isMoveableInFront()){
				time += robot.moveForward();
				repaint();
				incAlignmentCounters();
				checkAlignment();
				loopingCounter = 0; // to prevent looping
				return;
			}
			
			time += robot.turnRight();
			repaint();
			loopingCounter = 0; // to prevent looping
//			incAlignmentCounters();
//			checkAlignment();
			
		}
		
		private static void checkAlignment(){
			int alignDoneForFront = 0;
			int alignDoneForLeft = 0;
			if(frontAlignCounter > 3){
				
				if(robot.isWallInFront()){
					//do alignment
					doAlignment();
					alignDoneForFront = 1;
				}
				if(alignDoneForFront == 0){
					if(robot.isConsecObstacleInFront()){
						//do alignment
						doAlignment();
						alignDoneForFront = 1;
					}
				}
			}
			if(leftAlignCounter > 3){
				if(robot.isWallOnLeft()){
					robot.turnLeft();
					repaint();
					//do alignment
					doAlignment();
					robot.turnRight();
					repaint();
					alignDoneForLeft = 1;
				}
				else if(robot.isWallOnRight()){
					robot.turnRight();
					repaint();
					//do alignment
					doAlignment();
					robot.turnLeft();
					repaint();
					alignDoneForLeft = 1;
				}
				if(alignDoneForLeft == 0){
					if(robot.isExploredOnLeftForAlign() && robot.isConsecObstacleOnLeft()){
						robot.turnLeft();
						repaint();
						//do alignment
						doAlignment();
						robot.turnRight();
						repaint();
						alignDoneForLeft = 1;
					}
					else if(!robot.isExploredOnLeftForAlign() && robot.isThereOneObstacleOnLeft()){
						robot.turnLeft();
						repaint();
						if(robot.isConsecObstacleInFront()){
							//do alignment
							doAlignment();
							alignDoneForLeft = 1;
						}
						robot.turnRight();
						repaint();
					}
				}
			}
			
			if(alignDoneForFront == 1){
				frontAlignCounter = 0;
			}
			if(alignDoneForLeft == 1){
				leftAlignCounter = 0;
			}
		}
		
		private static void performAlignmentForStart(){
			robot.turnLeft();
			repaint();
			//do alignment
			doAlignment();
			robot.turnLeft();
			repaint();
			//do alignment
			doAlignment();
			robot.turnRight();
			repaint();
			robot.turnRight();
			repaint();
		}
		
		private static void performAlignmentForEnd(){
			//I don't put it first cos i have to use rotate robot method which has not tested
			int endDirection = robot.getDirection().ordinal();
			robot.rotateRobot(Directions.N);
			repaint();
			//do alignment
			doAlignment();
			robot.turnRight();
			repaint();
			//do alignment
			doAlignment();
			robot.rotateRobot(dtd[endDirection]);
			repaint();
		}
		
		private static void incAlignmentCounters(){
			frontAlignCounter++;
			leftAlignCounter++;
		}
		
		private static void doAlignment(){
			sendMsgToArduino("T");
		}
		
		
		//--------------------- CONNECTION  ----------------
		public static void sendMsgToArduino(String msg){
			try {
				client.send(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public static String recMsgFromArduino(){
			try {
				String msg = client.recv();
//				System.out.println(msg);
				return msg;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		
		public static void repaint(){
			try {
				if(startingLastExplore == 1)
					TimeUnit.MILLISECONDS.sleep(25);
				else
					TimeUnit.MILLISECONDS.sleep(25);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				client.send("X");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Waiting for feedback");
			String msgRec = SimulationMain.recMsgFromArduino();
			System.out.println("Msg is = > " + msgRec);
			/*while(msgRec == null){
				msgRec = SimulationMain.recMsgFromArduino();
				System.out.println("Msg is = > " + msgRec);
			}*/
			
			robot.updateRobotSensors(msgRec);
			robot.updateRobotMap();
			
//			robot.robotMap.printMap();
//			System.out.println();
//			robot.robotMap.printVirtualMap();
			
			currentTime = currentTimeMillis();
			int duration = currentTime - startTime;
			realTime = duration;
//			if((duration/1000) > timelimit)
//				backToStart = 1;
			
			simulationMap.repaintMap(robot.getRobotMap(), robot);
			timetext.setText("Time: " + duration/1000 + "s, XPos: " + robot.getXPos() + ", YPos: " + robot.getYPos() + 
			", Obstacles Found: " + robot.robotMap.obstacleCount + "\n" + "Explored Grids:" + robot.robotMap.numExploredGrids + "/"
			+ Exploration.TOTAL_HORIZONTAL_TILES*Exploration.TOTAL_VERTICAL_TILES + ", Explored Percentage: " +
			(int)(robot.robotMap.numExploredGrids * 100/(Exploration.TOTAL_HORIZONTAL_TILES*Exploration.TOTAL_VERTICAL_TILES)) +
			"%");
			
			frame.getContentPane().remove(simulationMap);
			frame.getContentPane().add(simulationMap, BorderLayout.CENTER);		
			frame.getContentPane().revalidate();
			frame.getContentPane().repaint();
		}
		
		public static void repaint2(){
			try {
				TimeUnit.MILLISECONDS.sleep(100);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			simulationMap.repaintMap(robot.getRobotMap(), robot);
			frame.getContentPane().remove(simulationMap);
			frame.getContentPane().add(simulationMap, BorderLayout.CENTER);		
			frame.getContentPane().revalidate();
			frame.getContentPane().repaint();
		}
		
		public static int currentTimeMillis() {
		    return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
		}
		

		//================================================================================================
			//===========================Alignment For Shortest Path======================================
			//============================================================================================
			public static String transPhysicalPathToInstructionWithAlignment(int[][] ppath){
				int[] robotDir = {0, 0};
				int[] robotDirToBe = new int[2];
				int dir = -1;
				int dirToBe = -1;
				String robotTurn = "";
				String instructions= "";
				midInstruction = "";
				int iniDir = -1;
				int alignCount = 0;
				robotDirToBe[0] = ppath[2][0]-ppath[1][0];
				robotDirToBe[1] = ppath[1][1]-ppath[1][1];
				dir = pathDirection(robotDirToBe, robotDir);
				iniDir = dir;
				if(iniDir == 1)
					instructions += RIGHT_INSTRUCTION;
				else 
					instructions= "";
				instructions += "U";
				for(int i=0; i<ppath.length-3; i++){
					if(ppath[i][0]==ppath[i+1][0] && ppath[i][1]==ppath[i+1][1]){
						robotDir[0] = robotDirToBe[0];
						robotDir[1] = robotDirToBe[1];
						robotDirToBe[0] = ppath[i+2][0]-ppath[i][0];
						robotDirToBe[1] = ppath[i+2][1]-ppath[i][1];
						dirToBe = pathDirection(robotDirToBe, robotDir);
						if((dirToBe==NORTH&&dir==WEST)||(dirToBe==WEST&&dir==SOUTH)||(dirToBe==SOUTH&&dir==EAST)||(dirToBe==EAST&&dir==NORTH)){
							midInstruction+= RIGHT_INSTRUCTION; 
							alignCount += 1;
						}
						else{
							midInstruction+= LEFT_INSTRUCTION; 
							alignCount += 1;
						}
					}else{ 
						midInstruction += FRONT_INSTRUCTION;
						alignCount += 1;
					}
					if (alignCount >= alignFlag){
						int dirDifference = 0;
						if(isitWall(ppath[i][0]+1, ppath[i][1]) 
						&& isitWall(ppath[i][0]+1, ppath[i][1]-1)
						&& isitWall(ppath[i][0]+1, ppath[i][1]+1)){ //wall in north
							dirDifference = NORTH - dir;
							prepareAndDoAlignment(dirDifference);
							alignCount = 0;
						}
						else if(isitWall(ppath[i][0]+1, ppath[i][1]-1) 
						&& isitWall(ppath[i][0], ppath[i][1]-1)
						&& isitWall(ppath[i][0]-1, ppath[i][1]-1)){ //wall in west
							dirDifference = WEST - dir;
							prepareAndDoAlignment(dirDifference);
							alignCount = 0;
						}
						else if(isitWall(ppath[i][0]-1, ppath[i][1]+1) 
						&& isitWall(ppath[i][0]+1, ppath[i][1]+1)
						&& isitWall(ppath[i][0], ppath[i][1]+1)){ //wall in east
							dirDifference = EAST - dir;
							prepareAndDoAlignment(dirDifference);
							alignCount = 0;
						}
						else if(isitWall(ppath[i][0]-1, ppath[i][1]+1) 
						&& isitWall(ppath[i][0]-1, ppath[i][1])
						&& isitWall(ppath[i][0]-1, ppath[i][1]-1)){ //wall in south
							dirDifference = SOUTH - dir;
							prepareAndDoAlignment(dirDifference);
							alignCount = 0;
						}
					}
				}
				instructions += midInstruction;
				instructions += "WZ";
				System.out.println(instructions);
				return instructions;
			}
			
			public static void prepareAndDoAlignment(int dirDifference){
				switch(dirDifference){
				case 1:
				case -3: {
					midInstruction = midInstruction + RIGHT_INSTRUCTION + ALIGN_INSTRUCTION + LEFT_INSTRUCTION;
					break;
				}
				case -2:
				case 2:{
					midInstruction = midInstruction + RIGHT_INSTRUCTION + RIGHT_INSTRUCTION + ALIGN_INSTRUCTION + LEFT_INSTRUCTION + LEFT_INSTRUCTION;
					break;
				}
				case -1:
				case 3:{
					midInstruction = midInstruction + LEFT_INSTRUCTION + ALIGN_INSTRUCTION + RIGHT_INSTRUCTION;
					break;
				}
				case 0: {
					midInstruction += ALIGN_INSTRUCTION;
					break;
				}
				default: break;
				}
			}
			
			public static int pathDirection(int[] ppath_new, int[] ppath_old){
				int direction = -1;
				String dir = "";
				dir += Integer.toString(ppath_new[0]-ppath_old[0]);
				dir += Integer.toString(ppath_new[1]-ppath_old[1]);
				switch(dir){
				case "01": direction = EAST; break;
				case "-10": direction = SOUTH; break;
				case "10": direction = NORTH; break;
				case "0-1": direction = WEST; break;
				default: direction = -1; break;
				}
				return direction;
			}
			
			public static boolean isitWall(int row, int col){
				if(col >= 0 && col < TOTAL_VERTICAL_TILES && (row == -1 || row == TOTAL_HORIZONTAL_TILES))
					return true;
				if(row >= 0 && row < TOTAL_HORIZONTAL_TILES && (col == -1 || col == TOTAL_VERTICAL_TILES))
					return true;
				return false;
			}
	}

	
