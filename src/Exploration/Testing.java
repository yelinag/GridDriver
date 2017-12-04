package Exploration;

import java.util.List;

public class Testing {
	public static final int inexplored = 2;
	public static final int obstacle = 1;
	public static final int empty = 0;
	
	//public static void runTestForShortestPath(){
	public static void main(String[] args) {
	
		long time1 = System.nanoTime();
		int[][] pMap = {{0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0},
						{0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0},
						{0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0},
						{0,2,2,2,0, 0,0,0,0,0, 0,0,0,0,0, 1,1,0,0,0},
						{0,2,2,2,0, 0,0,0,0,0, 0,0,0,0,1, 1,1,0,0,0},
						
						{0,2,2,2,0, 1,1,0,0,0, 0,0,0,0,0, 1,1,0,0,0},
						{0,1,1,1,0, 1,1,0,0,0, 1,1,1,1,0, 0,1,0,0,0},
						{0,1,1,1,0, 0,0,0,0,0, 1,1,1,1,1, 0,0,0,0,0},
						{0,0,0,0,0, 0,0,0,0,0, 1,1,1,1,1, 1,0,0,0,0},
						{0,0,0,0,0, 0,0,0,0,0, 1,1,1,1,1, 1,1,0,0,0},
						
						{0,0,0,0,0, 0,0,0,1,1, 1,1,1,1,1, 1,1,0,0,0},
						{0,0,0,0,0, 1,1,1,1,1, 1,1,1,1,1, 1,1,0,0,0},
						{0,0,0,0,0, 1,1,1,1,1, 1,1,0,0,0, 1,1,0,0,0},
						{0,0,0,0,0, 1,1,1,1,1, 1,1,0,0,0, 1,1,0,0,0},
						{0,0,0,1,1, 1,1,1,1,1, 1,1,0,0,0, 1,1,0,0,0},};
		
		//the below x-y is for the central point of the robot
		int[] pStart = {1,1}; //for physical map {row, col}, according to data structure
		int[] pGoal = {13,18};  //for physical map {row, col}, according to data structure
		
		int[][] vpath;
		VirtualMap vp = new VirtualMap(pMap, 3);
		vp.printVirtualMap();
		ShortestPath sp = new ShortestPath(vp.transPointToVirtualFromPhysical(pStart), vp.transPointToVirtualFromPhysical(pGoal), vp.vMap);
		vpath=sp.dijkstra1();
		int[][] ppath = vp.transPathToPhysicalFromVirtual(vpath);
		String instructions = vp.transPhysicalPathToInstruction(ppath); //translate the path to instructions for the robot
		long time2 = System.nanoTime();
		long timeTaken = time2 - time1;  
		System.out.println("Time taken " + timeTaken + " ns");  
	}
	
}
