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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class SensorsValue{
	public int front, left, right, frontLeft, frontRight;
	//  , back, backLeft, backRight;
}


class EdgeList{
	int N, E, S, W;
	
}




enum RobotStatus {
	AUTO, MANUAL, STOPPED;
	public static RobotStatus next(RobotStatus val) {
		if (val == AUTO) return MANUAL;
		if (val == MANUAL) return STOPPED;
		return AUTO;
	}
}

enum Directions {
	W(0),N(1),E(2),S(3);
	
    private final int value;

    private Directions(int value) {
        this.value = value;
    }
}

public class Exploration {
	
	
	//UI variables
	static JFrame frame;
	static JTextArea timetext = new JTextArea();
	static int timelimit;
	static int spacelimit;
	static JLabel lbTime = new JLabel( "Time limit     : ");
	static JLabel lbSpace = new JLabel("Space limit % : ");
	static JButton jbExplore = new JButton("Explore");
	static JTextField txtTime = new JTextField("", 5);
	static JTextField txtSpace = new JTextField("", 5);
	static JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	static int time = 0;
	
	
	public static final int[] DIRECTION_NORTH = {0, 1};
	public static final int[] DIRECTION_SOUTH = {0, -1};
	public static final int[] DIRECTION_EAST = {1, 0};
	public static final int[] DIRECTION_WEST = {-1, 0};
	public static final int TOTAL_HORIZONTAL_TILES = 15;
	public static final int TOTAL_VERTICAL_TILES = 20;
	public static final int OBSTACLE_SIZE = 10;
	
	
	
	//static int vertex[][] = new int[TOTAL_HORIZONTAL_TILES - 1][TOTAL_VERTICAL_TILES - 1];
	//static CrunchifyLinkedList edges[][] = new CrunchifyLinkedList[TOTAL_HORIZONTAL_TILES - 1][TOTAL_VERTICAL_TILES - 1];
	
//	static int[][] map = new int[TOTAL_HORIZONTAL_TILES][TOTAL_VERTICAL_TILES];
//	static int[][] robotMap = new int[TOTAL_HORIZONTAL_TILES][TOTAL_VERTICAL_TILES];
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*
		 * int map[][] = { {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
		 * {0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1}, };
		 */
		
		
		//------------- Variable Assigning ----------------
		
		/*
		Robot robot = new Robot();
		initializeMap();
		initializeRobot();
		while(true){
			if(scanInfront(robot) < 20){
				
			}
		}
		*/
		
		
	}
	
	//---------------------------- INITIALIZATION ------------------------------
	
	static void initializeMap(){
		
	}
	
	static void initializeRobot(){
		/*
		//------------Initialize vertex--------------
		// 0 - yet to discover, 1 - discovered
		for(int i = 0; i < (TOTAL_HORIZONTAL_TILES-1) * (TOTAL_VERTICAL_TILES-1); i++){
			vertex[i] = 0;
		}
		
		//------------Initialize edges--------------
		// 0 - no path, 1 - there is a path
		for(int i = 0; i < (TOTAL_HORIZONTAL_TILES-1) * (TOTAL_VERTICAL_TILES-1); i++){
			int[] temp = singleToDoubleAry(i);
			if(temp[0] - 1 < 0){
				edges[i].S = 0;
			}else{
				edges[i].S = 1;
			}
			
			if(temp[1] - 1 < 0){
				edges[i].W = 0;
			}else{
				edges[i].W = 1;
			}
			
			if(temp[0] + 1 >= TOTAL_HORIZONTAL_TILES){
				edges[i].N = 0;
			}else{
				edges[i].N = 1;
			}
			
			if(temp[1] + 1 >= TOTAL_VERTICAL_TILES){
				edges[i].E = 0;
			}else{
				edges[i].E = 1;
			}
		}
		*/
	}
	
	
	
	
	
	
	
	

	
	
	//---------------------- CONVERTING ------------------------------
	public static int doubleAryToSingle(int horizontal, int vertical){
		return vertical*TOTAL_HORIZONTAL_TILES + horizontal;
	}
	
	public static int[] singleToDoubleAry(int number){
		int[] array = new int[2];
		int horizontal = number % TOTAL_HORIZONTAL_TILES;
		int vertical = number / TOTAL_HORIZONTAL_TILES;
		array[0] = horizontal; array[1] = vertical;
		return array;
	}
	
	public static int getNearbyVertex(int vertex, char direction){
		int temp[] = singleToDoubleAry(vertex);
		
		if(direction == 'N'){
			temp[0]++;
			if(temp[0] >= TOTAL_HORIZONTAL_TILES)
				temp[0] = TOTAL_HORIZONTAL_TILES;
		}else if(direction == 'E'){
			temp[1]++;
			if(temp[1] >= TOTAL_VERTICAL_TILES)
				temp[1] = TOTAL_VERTICAL_TILES;
		}else if(direction == 'S'){
			temp[0]--;
			if(temp[0] < 0)
				temp[0] = 0;
		}else if(direction == 'W'){
			temp[1]--;
			if(temp[1] < 0)
				temp[1] = 0;
		}
		
		return doubleAryToSingle(temp[0], temp[1]);
	}
	
	public static int[] gridToCoordinates(int gridX, int gridY){
		int corX, corY;
		int[] coordinates = new int[2];
		
		corX = gridX * OBSTACLE_SIZE;
		corY = gridY * OBSTACLE_SIZE;
		
		coordinates[0] = corX;
		coordinates[1] = corY;
		
		return coordinates;
	}
	
	public static int coordinatesToGrid(int coord){
		return (int)(coord/OBSTACLE_SIZE);
	}

	public static int[] coordinatesToGrid(int corX, int corY){
		int gridX, gridY;
		int[] grids = new int[2];
		
		gridX = (int)corX/OBSTACLE_SIZE;
		gridY = (int)corY/OBSTACLE_SIZE;
		if(corX < 0)
			gridX = -1;
		if(corY < 0)
			gridY = -1;
		grids[0] = gridX;
		grids[1] = gridY;
		
		return grids;
		
	}
	
	
	public static boolean isOutofBoundByGrid(int row, int col){
		if(row < 1 || col < 1 || row > Exploration.TOTAL_HORIZONTAL_TILES - 2 ||
				col > Exploration.TOTAL_VERTICAL_TILES - 2)
			return true;
		return false;
	}
	
	public static boolean isOutofArenaByGrid(int row, int col){
		if(row < 0 || col < 0 || row>Exploration.TOTAL_HORIZONTAL_TILES -1 ||
				col > Exploration.TOTAL_VERTICAL_TILES - 1)
			return true;
		return false;
	}
	
	public static boolean isOutofBoundByCoord(int x, int y){
		if(x < 15 || y < 15 || x > (TOTAL_HORIZONTAL_TILES - 2) * OBSTACLE_SIZE + OBSTACLE_SIZE/2 ||
				y > (TOTAL_VERTICAL_TILES - 2) * OBSTACLE_SIZE + OBSTACLE_SIZE/2)
			return true;
		return false;
	}
	
	public static boolean isOutofArenaByCoord(int x, int y){
		if(x < 0 || y < 0 || x > TOTAL_HORIZONTAL_TILES * OBSTACLE_SIZE ||
				y > TOTAL_VERTICAL_TILES * OBSTACLE_SIZE)
			return true;
		return false;
	}
	
	
	public static boolean isitWall(int row, int col){
		if(col >= 0 && col < TOTAL_VERTICAL_TILES && (row == -1 || row == TOTAL_HORIZONTAL_TILES))
			return true;
		if(row >= 0 && row < TOTAL_HORIZONTAL_TILES && (col == -1 || col == TOTAL_VERTICAL_TILES))
			return true;
		return false;
	}
	
	
	
	
	
	
	
	/**
	 * Create the application.
	 */
	public Exploration() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	public  void initialize() {
		frame = new JFrame();
		frame.getContentPane().setEnabled(false);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 820, 495);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		timetext.setEditable(false);
		timetext.setFocusable(false);
		timetext.setText("Time: " + time);
		frame.getContentPane().add(timetext, BorderLayout.NORTH);
		
		textPanel.add(lbTime);
		textPanel.add(txtTime);
		textPanel.add(lbSpace);
		textPanel.add(txtSpace);
		/*
		jbExplore.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
            	try{
        			timelimit = Integer.parseInt(txtTime.getText());
        			spacelimit = Integer.parseInt(txtSpace.getText());
        			
        			spacelimit = 3 * spacelimit;//(300/100 = 3)
        		}catch(Exception ex){
        			System.out.println("ERROR : "+ex.getMessage());
        			timelimit = 1000;
        			spacelimit = 300;
        		}
            	
            	map.robotNextStatus();
				if (map.getstatus() == RobotStatus.AUTO) 
					wallfollowing();
				System.out.println(map.getstatus().toString());
				return ;
            }
        });
		
		textPanel.add(jbExplore);
		textPanel.setPreferredSize(new Dimension(200,200));
		
		frame.getContentPane().add(textPanel, BorderLayout.EAST);
		
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (((KeyEvent) e).getKeyCode() == KeyEvent.VK_ENTER){
					map.robotNextStatus();
					if (map.getstatus() == RobotStatus.AUTO) 
						wallfollowing();
					System.out.println(map.getstatus().toString());
					return ;
				}
				check(map.getrobotx()/30-1,map.getroboty()/30-1);
				
				if (map.getstatus() == RobotStatus.MANUAL){
					if (((KeyEvent) e).getKeyCode() == KeyEvent.VK_RIGHT) 
						time = time + map.moveRobot(Directions.E);
					if (((KeyEvent) e).getKeyCode() == KeyEvent.VK_LEFT) 
						time = time + map.moveRobot(Directions.W);
					if (((KeyEvent) e).getKeyCode() == KeyEvent.VK_UP) 
						time = time + map.moveRobot(Directions.N);
					if (((KeyEvent) e).getKeyCode() == KeyEvent.VK_DOWN) 
						time = time + map.moveRobot(Directions.S);
				}

				timetext.setText("Time: " + time);
				frame.getContentPane().add(timetext, BorderLayout.NORTH);
				check(map.getrobotx()/30-1,map.getroboty()/30-1);

			}
		});
		*/

		//check(map.getrobotx()/30-1,map.getroboty()/30-1);
		//frame.getContentPane().add(map, BorderLayout.CENTER);				
		frame.requestFocus();
	}
	
	public static int[][] transposeMap(int[][] inputMap){
		int col = inputMap.length;
		int row = inputMap[0].length;
		int[][] outputMap = new int[row][col];
		
		for(int i = 0; i < row; i ++){
			for(int j = 0; j < col; j++){
				outputMap[i][j] = inputMap[j][i];
			}
		}
		
		return outputMap;
	}
	
	
	

}
