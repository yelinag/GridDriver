package Exploration;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import Exploration.SimulationMain.Node;

public class RobotMap {
	protected ArrayList<Grid> vertex;
	// static CrunchifyLinkedList edges[] = new
	// CrunchifyLinkedList[(TOTAL_HORIZONTAL_TILES-1) *
	// (TOTAL_VERTICAL_TILES-1)];
	private EdgeList edges[];
	private int map[][];
	private float mapValue[][];
	private int mapCount[][];
	protected int numExploredGrids = 0;
	protected int obstacleCount = 0;
	static final int dx[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	static final int dy[] = { 1, 1, 0, -1, -1, -1, 0, 1 };
	private int virtualMap[][];

	public RobotMap() {
		// vertex = new int[(Exploration.TOTAL_HORIZONTAL_TILES-1) *
		// (Exploration.TOTAL_VERTICAL_TILES-1)];
		// vertex = new Grid[Exploration.TOTAL_HORIZONTAL_TILES]
		// [Exploration.TOTAL_VERTICAL_TILES];

		virtualMap = map = new int[Exploration.TOTAL_HORIZONTAL_TILES][Exploration.TOTAL_VERTICAL_TILES];
		mapValue = new float[Exploration.TOTAL_HORIZONTAL_TILES][Exploration.TOTAL_VERTICAL_TILES];
		mapCount = new int[Exploration.TOTAL_HORIZONTAL_TILES][Exploration.TOTAL_VERTICAL_TILES];
		for(int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i ++){
			for(int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++){
				mapCount[i][j] = 0;
			}
		}

		vertex = new ArrayList<Grid>();
		for (int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++) {
			for (int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++) {
				vertex.add(new Grid(i, j));
			}
		}

		edges = new EdgeList[(Exploration.TOTAL_HORIZONTAL_TILES - 1)
				* (Exploration.TOTAL_VERTICAL_TILES - 1)];
		map = new int[Exploration.TOTAL_HORIZONTAL_TILES][Exploration.TOTAL_VERTICAL_TILES];
		for (int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++) {
			for (int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++) {
				map[i][j] = 2;
				virtualMap[i][j] = 2;

			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				map[i][j] = 3;
				virtualMap[i][j] = 3;
				numExploredGrids++;
			}
		}

		/*
		 * for(int i = Exploration.TOTAL_HORIZONTAL_TILES - 3; i <
		 * Exploration.TOTAL_HORIZONTAL_TILES; i++){ for(int j =
		 * Exploration.TOTAL_VERTICAL_TILES - 3; j <
		 * Exploration.TOTAL_VERTICAL_TILES; j++){ map[i][j] = 3;
		 * virtualMap[i][j] = 3; } }
		 */

		for (int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++) {
			for (int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++) {
				if (isGoalPt(i, j) || isStartingPt(i, j))
					continue;
				if (i == 0 || j == 0
						|| i == Exploration.TOTAL_HORIZONTAL_TILES - 1
						|| j == Exploration.TOTAL_VERTICAL_TILES - 1)
					virtualMap[i][j] = 1;

			}
		}

		// printMap();
		// System.out.println();
		// printVirtualMap();
		
		

	}
	
	private void CREATE_VIRTUAL_MAP(){
		for(int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++){
			for(int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++){
				map[i][j] = 0;
			}
		}
		map[7][2] =1;
		  map[11][4] =1;
		  map[12][4] =1;
		  map[13][4] =1;
		  map[1][5] =1;
		  map[5][8] =1;
		  map[6][8] =1;
		  map[7][8] =1;
		  map[10][10] =1;
		  map[0][13] =1;
		  map[1][13] =1;
		  map[2][13] =1;
		  map[3][13] =1;
		  map[4][13] =1;
		  map[5][13] =1;
		  map[11][14] =1;
		  map[12][14] =1;
		  map[13][14] =1;
		  map[7][17] =1;
	}

	public Grid nearestUnexplored(Grid inputGrid) {
		Grid outputGrid = new Grid(2, 2);
		/*
		 * for(int i = 1; i < Exploration.TOTAL_HORIZONTAL_TILES; i++){ int xInc
		 * = inputGrid.x + i; int xDec = inputGrid.x - i; int yInc = inputGrid.y
		 * + i; int yDec = inputGrid.y - i; if(xInc >=
		 * Exploration.TOTAL_HORIZONTAL_TILES -3) xInc =
		 * Exploration.TOTAL_HORIZONTAL_TILES - 3; if(xDec < 0) xDec = 0;
		 * if(yInc >= Exploration.TOTAL_VERTICAL_TILES -3) yInc =
		 * Exploration.TOTAL_VERTICAL_TILES - 3; if(yDec < 0) yDec = 0;
		 * if(map[xInc][yInc] == 2){ outputGrid = new Grid(xInc, yInc); break;
		 * }else if(map[xDec][yInc] == 2){ outputGrid = new Grid(xDec, yInc);
		 * break; }else if(map[xInc][yDec] == 2){ outputGrid = new Grid(xInc,
		 * yDec); break; }else if(map[xDec][yDec] == 2){ outputGrid = new
		 * Grid(xDec, yDec); break; }else if(map[inputGrid.x][yInc] == 2){
		 * outputGrid = new Grid(inputGrid.x, yInc); break; }else
		 * if(map[xInc][inputGrid.y] == 2){ outputGrid = new Grid(xInc,
		 * inputGrid.y); break; }else if(map[inputGrid.x][yDec] == 2){
		 * outputGrid = new Grid(inputGrid.x, yDec); break; }else
		 * if(map[xDec][inputGrid.y] == 2){ outputGrid = new Grid(xDec,
		 * inputGrid.y); break; }
		 * 
		 * }
		 */
		for (int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++) {
			for (int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++) {
				if (map[i][j] == 2) {
					Grid outGrid = new Grid(i, j);
					return outGrid;
				}
			}
		}
		// outputGrid = vertex.get(0);

		return outputGrid;
	}

	public boolean isObstacleThere(int corX, int corY) {
		if (corX < 0
				|| corY < 0
				|| corX >= Exploration.TOTAL_HORIZONTAL_TILES
						* Exploration.OBSTACLE_SIZE
				|| corY > Exploration.TOTAL_VERTICAL_TILES
						* Exploration.OBSTACLE_SIZE)
			return false;
		int[] xy = Exploration.coordinatesToGrid(corX, corY);

		if (map[xy[0]][xy[1]] == 1)
			return true;
		return false;
	}

	public boolean isOkaytoMove(int xGrid, int yGrid) {
		if (Exploration.isOutofBoundByGrid(xGrid, yGrid))
			return false;
		if (virtualMap[xGrid][yGrid] == 0 || virtualMap[xGrid][yGrid] == 3)
			return true;
		return false;
	}

	/*
	 * public boolean isOkaytoAdd(int xGrid, int yGrid){
	 * if(Exploration.isOutofArena(xGrid, yGrid)) return false;
	 * if(map[xGrid][yGrid] == 0 || map[xGrid][yGrid] == 3) return true; return
	 * false; }
	 */

	public void updateRobotMap(int horizontal, int vertical, int value) {
		if (horizontal < 0
				|| horizontal > Exploration.TOTAL_HORIZONTAL_TILES - 1
				|| vertical < 0
				|| vertical > Exploration.TOTAL_VERTICAL_TILES - 1)
			return;
		if (isStartingPt(horizontal, vertical))
			return;

		if (map[horizontal][vertical] == 2) {
			numExploredGrids++;
			mapValue[horizontal][vertical] = value;
			mapCount[horizontal][vertical]++;
			if (value == 1)
				obstacleCount++;
		}else{
			mapValue[horizontal][vertical] = (mapValue[horizontal][vertical] * mapCount[horizontal][vertical] + value)/
					(mapCount[horizontal][vertical] + 1);
			mapCount[horizontal][vertical]++;
		}

		

		if(mapValue[horizontal][vertical] < 0.5)
			map[horizontal][vertical] = 0;
		else
			map[horizontal][vertical] = 1;
		
		if (horizontal >= Exploration.TOTAL_HORIZONTAL_TILES - 3
				&& vertical >= Exploration.TOTAL_VERTICAL_TILES - 3)
			map[horizontal][vertical] = 3;
		
		updateVirtualMap();

		
		for (int i = 0; i < vertex.size(); i++) {
			Grid tempGrid = vertex.get(i);

			if (tempGrid.x == horizontal && tempGrid.y == vertical) {
				vertex.remove(tempGrid);
			}
		}

	}

	private void updateVirtualMap() {
		for (int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++) {
			for (int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++) {
				if (virtualMap[i][j] != 1 && virtualMap[i][j] != 3)
					virtualMap[i][j] = map[i][j];
				if (map[i][j] == 1) {
					for (int d = 0; d < 8; d++) {
						int xTemp = i + dx[d];
						int yTemp = j + dy[d];
						if (xTemp < 1
								|| yTemp < 1
								|| xTemp >= Exploration.TOTAL_HORIZONTAL_TILES - 1
								|| yTemp >= Exploration.TOTAL_VERTICAL_TILES - 1)
							continue;
						if (isGoalPt(xTemp, yTemp)
								|| isStartingPt(xTemp, yTemp))
							continue;
						virtualMap[xTemp][yTemp] = 1;
					}
				}
			}

		}
	}

	public int[][] getMap() {
		/*
		 * int[][] tempMap = new
		 * int[Exploration.TOTAL_HORIZONTAL_TILES][Exploration
		 * .TOTAL_VERTICAL_TILES]; for(int i = 0; i <
		 * Exploration.TOTAL_HORIZONTAL_TILES; i++){ for(int j = 0; j
		 * <Exploration.TOTAL_VERTICAL_TILES; j++){ tempMap[i][j] = map[i][j]; }
		 * } return tempMap;
		 */
		return map;
	}

	public int[][] getVirtualMap() {
		/*
		 * int[][] temp2Map = new
		 * int[Exploration.TOTAL_HORIZONTAL_TILES][Exploration
		 * .TOTAL_VERTICAL_TILES]; for(int i = 0; i <
		 * Exploration.TOTAL_HORIZONTAL_TILES; i++){ for(int j = 0; j
		 * <Exploration.TOTAL_VERTICAL_TILES; j++){ temp2Map[i][j] =
		 * virtualMap[i][j]; } } return temp2Map;
		 */
		return virtualMap;
	}

	public boolean isGoalPt(int hor, int ver) {
		if (hor >= Exploration.TOTAL_HORIZONTAL_TILES - 3
				&& ver >= Exploration.TOTAL_VERTICAL_TILES - 3) {
			return true;
		}
		return false;
	}

	public boolean isStartingPt(int hor, int ver) {
		if (hor < 3 && ver < 3) {
			return true;
		}
		return false;
	}

	public void printMap() {
		System.out.println("Actual Map");
		for (int i = 0; i < Exploration.TOTAL_VERTICAL_TILES; i++) {
			for (int j = 0; j < Exploration.TOTAL_HORIZONTAL_TILES; j++) {
				System.out.print(map[j][Exploration.TOTAL_VERTICAL_TILES - i
						- 1]
						+ ", ");
			}
			System.out.println();
		}
	}

	public void printVirtualMap() {
		System.out.println("Virtual Map");
		for (int i = 0; i < Exploration.TOTAL_VERTICAL_TILES; i++) {
			for (int j = 0; j < Exploration.TOTAL_HORIZONTAL_TILES; j++) {
				System.out.print(virtualMap[j][Exploration.TOTAL_VERTICAL_TILES
						- i - 1]
						+ ", ");
			}
			System.out.println();
		}
	}

	public String convertMapToBinary() {
		String bin1 = "11";
		String bin2 = "";
		String hex1 = "";
		String hex2 = "";
		for (int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++) {
			for (int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++) {
				if (map[i][j] == 2) {
					bin1 += "0";
				} else {
					bin1 += "1";
					if (map[i][j] == 1)
						bin2 += "1";
					else
						bin2 += "0";
				}

			}
		}

		bin1 += "11";

		hex1 = Map.binToHex(bin1);
		hex2 = Map.binToHex(bin2);

		return hex1 + "\n" + hex2;
		/*
		 * String bin1 = ""; String hex1 = ""; for(int i = 0; i<
		 * Exploration.TOTAL_HORIZONTAL_TILES; i++){ for(int j=0; j<
		 * Exploration.TOTAL_VERTICAL_TILES ; j++){ if(map[i][j] == 1){ bin1 +=
		 * "1"; } else{ bin1 += "0"; }
		 * 
		 * } }
		 * 
		 * hex1 = Map.encodeMap(bin1); return hex1;
		 */
	}

}
