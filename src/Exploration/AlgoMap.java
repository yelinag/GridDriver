package Exploration;
import java.io.*;
import java.util.*;

public class AlgoMap {
	public static final int inexplored = 2;
	public static final int obstacle = 1;
	public static final int empty = 0;
	int length = 18; //for row
	int width = 13;  //for col
	int totalCell = length*width*2;
	int turnWeight = 3; 
	int rowWeight = 1; //weight along length
	int colWeight = 1; //weight along col
	int blkWeight = 50000;
	int start;
	int end;
	//divide each cell into two -> width*2
	int[] [] aMap = null; 
	
	public AlgoMap(int leng, int wid){
		length = leng; //for row
		width = wid;  //for col
		totalCell = length*width*2;
		aMap = new int[totalCell][totalCell];
		//translate from virtual map to algo map for dijkstra
		for (int i=0; i<totalCell; i++){
			for (int j=0; j<totalCell; j++){
				aMap[i][j]=blkWeight;
			}
		}
		//mark rowWeight 
		//System.out.println("rowWeight");
		for (int i=0; i<totalCell; i++){
			if ((i%(2*length)<length)&&((i+1)%length != 0)){
				//System.out.println("cell" + i + " " + (i+1));
				aMap[i][i+1] = rowWeight;
				aMap[i+1][i] = rowWeight;
			}
		}
		
		//mark colWeight 
		//System.out.println("colWeight");
		for (int i=0; i<totalCell; i++){
			if ((i%(2*length)>=length)&&(i<length*(2*width-1))){
				//System.out.println("cell" + i + " " + (i+2*length));
				aMap[i][i+2*length] = colWeight;
				aMap[i+2*length][i] = colWeight;
			}
		}
		
		//mark turnWeight
		//System.out.println("turnWeight");
		for (int i=0; i<totalCell; i++){
			if ((i%(2*length)<length)&&(i<length*(2*width-1))){
				//System.out.println("cell" + i + " " + (i+length));
				aMap[i][i+length] = turnWeight;
				aMap[i+length][i] = turnWeight;
			}
		}
		
	}
	
	public void transAMap(int[] startP, int[] endP, int[][] vMap){ 
		for(int i=0; i<vMap.length; i++){
			for(int j=0; j<vMap[0].length; j++){
				if (vMap[i][j] == obstacle || vMap[i][j] == inexplored){
					//System.out.println("vMap[i][j]: " + i + " " + j);
					int[] vpoint= new int[2];
					vpoint[0] = i;
					vpoint[1] = j;
					int cell1 = transPointToAlgoFromVirtual(vpoint);
					//System.out.println("cell1: " + cell1);
					for (int k=0; k<totalCell; k++){
						//System.out.println("obstacle " + cell1);
						aMap[k][cell1] = blkWeight;
						aMap[cell1][k] = blkWeight;
						aMap[k][cell1+length] = blkWeight;
						aMap[cell1+length][k] = blkWeight;
					}
				}
			}
		}
		//make sure start point and end point has no turn weight
		start = transPointToAlgoFromVirtual(startP);
		end = transPointToAlgoFromVirtual(endP);
		aMap[start][start+length] = 0;
		aMap[start+length][start] = 0;
		aMap[end][end+length] = 0;
		aMap[end+length][end] = 0;
		
	}
	
	public int transPointToAlgoFromVirtual(int[] vpoint){
		if (vpoint[0]>=width || vpoint[1]>=length){
			System.out.println("function: transPoint, invalid input");
			System.out.println("width: " + width);
			System.out.println("length " + length);
			System.out.println("vpoint [] []: " + vpoint[0] + " " + vpoint[1]);
		}
		return 2*length*(vpoint[0])+vpoint[1];
	}
	
	public int[] transPointToVirtualFromAlgo(int aPoint){
		int[] point = new int[2];
		point[0] = aPoint % length;
		point[1] = aPoint/(2*length);
		return point;
	}
	
    public void printAlgoMap(){
        for(int i=0;i<aMap.length;i++){
           System.out.print("{");
           for(int j=0;j<aMap[0].length;j++){
               System.out.print(aMap[i][j]+",");
           }
            System.out.println("},");
       }
   }

}
