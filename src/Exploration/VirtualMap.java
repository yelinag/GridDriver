package Exploration;

import java.util.ArrayList;
import java.util.List;

public class VirtualMap {
	public static final int inexplored = 2;
	public static final int obstacle = 1;
	public static final int empty = 0;
	public static final String LEFT_INSTRUCTION = "D";
	public static final String RIGHT_INSTRUCTION = "A";
	public static final String FRONT_INSTRUCTION = "W";
	int length = 18; //for row
	int width = 13;  //for col
	int start;
	int end;
	int[][] vMap = null;
	int virtualSize = 2;
	
	public VirtualMap(int[][] passIn, int virtualSizeIn){
		int signObstacle = 0;
		int signInexplored = 0;
		vMap = passIn;
		virtualSize=virtualSizeIn;
		length = passIn.length-(virtualSize-1);
		width = passIn[0].length-(virtualSize-1);
		System.out.println("passIn.length, passIn[0].length: "+passIn.length+" "+passIn[0].length);
		System.out.println("length, width: "+length+" "+width);
		vMap = new int[length][width];
		for(int i=0; i<length; i++){
			for(int j=0; j<width; j++){
				signObstacle=0;
				signInexplored=0;
				for(int k=i; k<i+virtualSize; k++){
					for(int l=j; l<j+virtualSize; l++){
//						System.out.println("i, j: "+i+" "+j);
//						System.out.println("k, l: "+k+" "+l);
						if (passIn[k][l]== obstacle)
							signObstacle += 1;
						else if (passIn[k][l]== inexplored)
							signInexplored +=1;
					}
				}
				if(signObstacle==0 && signInexplored==0)
					vMap[i][j]=empty;
				else if (signObstacle>0)
					vMap[i][j]=obstacle;
				else vMap[i][j]=inexplored;
			}
		}
	}
	
	
//	public int[] transPointToVirtualFromPhysics(int[] ppoint, int virtualSize){
//		int[] vpoint = new int[2];
//		//vpoint
//		return vpoint;
//	}
	
	//robot is presented by 3*3, centered at a 10cm*10cm box
	public int[][] transPathToPhysicalFromVirtual(int[][] vpath){
		int[][] ppath = new int[vpath.length][2];
		for(int i=0; i<vpath.length; i++){
			ppath[i][0] = vpath[i][0]+1;
			ppath[i][1] = vpath[i][1]+1;
		}
		System.out.print("ppath:");
		for (int i=0; i<vpath.length; i++){
			System.out.print(" ["+ppath[i][0]+"]["+ppath[i][1]+"]");
		}
		System.out.println();
		return ppath; 
	}
	
	//robot is presented by 3*3, centered at a 10cm*10cm box
	public int[] transPointToPhysicalFromVirtual(int[] vpoint){
		int[] ppoint = new int[2];
		ppoint[0] = vpoint[0]+1;
		ppoint[1] = vpoint[1]+1;
		return ppoint;
	}
	
	public int[] transPointToVirtualFromPhysical(int[] ppoint){
		int[] vpoint = new int[2];
		vpoint[0] = ppoint[0]-1;
		vpoint[1] = ppoint[1]-1;
		return vpoint;
	}
	
	public void printVirtualMap(){
        for(int i=0;i<vMap.length;i++){
           System.out.print("{");
           for(int j=0;j<vMap[0].length;j++){
               System.out.print(vMap[i][j]+",");
           }
            System.out.println("},");
       }
   }
	
	public void printPhysicalPoint(int[] ppoint){
		System.out.print("[" + ppoint[0] +"]["+ ppoint[1] + "] ");
		
	}
	
	public String transPhysicalPathToInstruction(int[][] ppath){
		int[] robotDir = {0, 0};
		int[] robotDirToBe = new int[2];
		String dir = "";
		String dirToBe = "";
		String robotTurn = "";
		String instructions= "";
		String iniDir = "";
		robotDirToBe[0] = ppath[2][0]-ppath[1][0];
		robotDirToBe[1] = ppath[1][1]-ppath[1][1];
		dir = pathDirection(robotDirToBe, robotDir);
		iniDir = dir;
		if(iniDir == "East")
			instructions += RIGHT_INSTRUCTION;
		else 
			instructions= "";
		instructions += "U";
		for(int i=0; i<ppath.length-3; i++){
			if((ppath[i][0]==ppath[i+1][0]) && (ppath[i][1]==ppath[i+1][1])){
				robotDir[0] = robotDirToBe[0];
				robotDir[1] = robotDirToBe[1];
				robotDirToBe[0] = ppath[i+2][0]-ppath[i][0];
				robotDirToBe[1] = ppath[i+2][1]-ppath[i][1];
				dirToBe = pathDirection(robotDirToBe, robotDir);
				if((dirToBe=="North"&&dir=="West")||(dirToBe=="West"&&dir=="South")||(dirToBe=="South"&&dir=="East")||(dirToBe=="East"&&dir=="North"))
					instructions+= RIGHT_INSTRUCTION; 
				else instructions+= LEFT_INSTRUCTION; 
			}else 
				instructions += FRONT_INSTRUCTION;
			System.out.println(instructions);
		}
		instructions += "WZ";
		System.out.println(instructions);
		return instructions;
	}
	
	public String pathDirection(int[] ppath_new, int[] ppath_old){
		String direction = "";
		String dir = "";
		dir += Integer.toString(ppath_new[0]-ppath_old[0]);
		dir += Integer.toString(ppath_new[1]-ppath_old[1]);
		switch(dir){
		case "10": direction = "East"; break;
		case "-10": direction = "West"; break;
		case "01": direction = "North"; break;
		case "0-1": direction = "South"; break;
		default: direction = "Error"; break;
		}
		return direction;
	}
	
//	public enum
	
//	public String PhysicalPathToSteps(int[][] ppath){
//	String steps="";
//	for(int i=0; i<ppath.length; i++){
//		
//		if(ppath)
//	}
//	return steps;
//}

}
