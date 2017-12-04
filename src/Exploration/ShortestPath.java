package Exploration;
import java.io.*;
import java.util.*;

public class ShortestPath {
	
	public final int infinity = 50000;
	private int totalCell = 0;
	private int start = -1;
	private int goal = -1;
	private AlgoMap algoMap = null;
	private int[][] matrix = null;
	int[] distance = null;
	int[] visited = null;
	int[] preD = null;
	int min;
	int nextNode = 0;
	public List<Integer> finalPath = new ArrayList<Integer>();
	public String finalPathString="";
    public String finalSteps="";
	int pos[]=new int[2];
    private int countSteps=0;
	
	private ArrayList shortestPath = new ArrayList();
	
	public ShortestPath(int[] startP, int[] endP, int [][] vMap)
	{    
		algoMap  = new AlgoMap(vMap[0].length, vMap.length); 
		algoMap.transAMap(startP, endP, vMap);
		matrix = algoMap.aMap;
		start = algoMap.transPointToAlgoFromVirtual(startP);
		goal = algoMap.transPointToAlgoFromVirtual(endP);
		System.out.println("start point: "+ start);
		System.out.println("goal point: "+ goal);
		totalCell = algoMap.totalCell;
		distance = new int[totalCell];
		visited = new int[totalCell];
		preD = new int[totalCell];
		for (int i=0; i<totalCell; i++){
			visited[i] = 0;
			preD[i] = start;
			distance[i] = infinity;
		}
	}
	
	public int[][] dijkstra1(){
		//starting condition
		distance = matrix[start];
		//distance = algoMap.aMap[start];
		distance[start] = 0;
		visited[start] = 1;
	
		for(int i=0; i<totalCell; i++){
			min = infinity;  
			for(int j=0; j<totalCell; j++){
				if(min>distance[j] && visited[j]!=1){
					min = distance[j];
					nextNode = j;
				}
			}
			System.out.println("nextNode: " + nextNode);
			visited[nextNode] = 1;
		//start...
		//distance
			if(nextNode == goal){
				break;
			}
			for(int k=0; k<totalCell; k++){
				if(visited[k]!=1){
					if(min+matrix[nextNode][k]<distance[k]){
						distance[k]=min+matrix[nextNode][k];
						preD[k]=nextNode;
					}
				}
			}
			if(nextNode == goal){
				break;
			}
		}
		System.out.println("totalCell: " + totalCell);
		System.out.println("goal distance: " + distance[goal]);
		
		//final path runs from the destination point to the starting point
		finalPath.add(goal); 
		finalPathString = finalPathString + Integer.toString(goal) + " ";
		System.out.println(finalPathString);
		int temp=goal;
		do{
			temp=preD[temp];
			finalPath.add(temp);
			//finalPathString = finalPathString + Integer.toString(temp) + " ";
			//System.out.println(Integer.toString(temp));
			System.out.print("distance for ");
			printVirtualPoint(algoMap.transPointToVirtualFromAlgo(temp));
			System.out.println(distance[temp]);
		}while(temp != start);
		System.out.println("path: "+ finalPathString);
		printPath(finalPath);
		int[][] vpath = transPathToVirtualFromAlgo(finalPath);
		return vpath;
	
	}
	
	//translation + convert the path from starting point to destination point
	public int[][] transPathToVirtualFromAlgo(List<Integer> apath){
		int[][] vpath = new int[apath.size()][2];
		for(int i=0; i<apath.size(); i++){
			vpath[i]=algoMap.transPointToVirtualFromAlgo(apath.get(apath.size()-i-1));   
		}
		System.out.print("vpath:");
		for (int i=0; i<apath.size(); i++){
			System.out.print(" ["+vpath[i][0]+"]["+vpath[i][1]+"]");
		}
		System.out.println();
		return vpath;
	}
	
	public void printPath(List<Integer> finalPath){
		System.out.print("path: in (x,y) ");
		int[] vpoint = new int[2];
		for(int i = 0; i< finalPath.size(); i++){
			vpoint=algoMap.transPointToVirtualFromAlgo(finalPath.get(i));
			printVirtualPoint(vpoint);
		}
		System.out.println();
	}
	
	public void printVirtualPoint(int[] vpoint){
		System.out.print("[" + vpoint[0] +"]["+ vpoint[1] + "] ");
		
	}
}
        
        
