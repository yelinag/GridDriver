package Exploration;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class SimulationMap extends JPanel{
	
	public static final int NUM_ROWS = 15;
	public static final int NUM_COLUMNS = 20;
	public static final int SCREEN_RATION = 3;
	public static final int SCREEN_BLOCK_SIZE = Exploration.OBSTACLE_SIZE * SCREEN_RATION;
	public static final ImageIcon imgN = new ImageIcon("pacmanN.png");
	public static final ImageIcon imgS = new ImageIcon("pacmanS.png");
	public static final ImageIcon imgE = new ImageIcon("pacmanE.png");
	public static final ImageIcon imgW = new ImageIcon("pacmanW.png");
	
	
	public int[][] grids;
	private Rectangle2D[][] rect = new Rectangle2D[NUM_ROWS][NUM_COLUMNS];
	private Image robotIcon;
	private Robot robot;
	
	public SimulationMap(){
		initialize();
		
	}
	
	public void initialize(){
		//grids = robot.getRobotMap();
	}
	
	public void repaintMap(int[][] map, Robot inputRobot){
		grids = map;
		robot = inputRobot;
	}
	
	@Override
    public void paintComponent(Graphics g) {
        // Important to call super class method
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Clear the board
        g.clearRect(0, 0, getWidth(), getHeight());
        // Draw the grid
        g.setColor(Color.BLACK);
        
        
        
        for (int i = 0; i <= NUM_ROWS; i ++)
        	g.drawLine(i * SCREEN_BLOCK_SIZE,0,i*SCREEN_BLOCK_SIZE,NUM_COLUMNS*SCREEN_BLOCK_SIZE);
        for (int i = 0; i <= NUM_COLUMNS; i ++)
        	g.drawLine(0,i * SCREEN_BLOCK_SIZE,NUM_ROWS*SCREEN_BLOCK_SIZE,i*SCREEN_BLOCK_SIZE);
        
        if(grids != null){
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                // Upper left corner of this terrain rect
                int x = i * SCREEN_BLOCK_SIZE;
                int y = (Exploration.TOTAL_VERTICAL_TILES - j - 1) * SCREEN_BLOCK_SIZE;
//                int x = i * SCREEN_BLOCK_SIZE;
//                int y = j * SCREEN_BLOCK_SIZE; 
                Color terrainColor;
                if(grids[i][j] == 0){
                	terrainColor = Color.WHITE;
                }else if(grids[i][j] == 1){
                	terrainColor = Color.BLACK;
                }else if(grids[i][j] == 3){
                	terrainColor = Color.GREEN;
                }else{
                	terrainColor = Color.GRAY;
                }
                
                rect[i][j] = new Rectangle(x+1, y+1, SCREEN_BLOCK_SIZE-1, SCREEN_BLOCK_SIZE-1);
                g2d.setColor(terrainColor);
                g2d.fill(rect[i][j]);
                  
            }
            
        }
        }
        
      //Draw the Robot
        if(robot!=null){
        if(robot.getDirection() == Directions.E)
        	robotIcon = imgE.getImage();
        else if(robot.getDirection() == Directions.S)
        	robotIcon = imgS.getImage();
        else if(robot.getDirection() == Directions.N)
        	robotIcon = imgN.getImage();
        else
        	robotIcon = imgW.getImage();
        
        
        g.setColor(Color.YELLOW);
        
        g.drawImage(robotIcon, (int)robot.getXPos()*SCREEN_RATION - SCREEN_BLOCK_SIZE + 1, 
        		((Exploration.TOTAL_VERTICAL_TILES)* Exploration.OBSTACLE_SIZE - 
        				(int)robot.getYPos())*SCREEN_RATION - SCREEN_BLOCK_SIZE + 1, 
        		SCREEN_BLOCK_SIZE * 2, SCREEN_BLOCK_SIZE * 2, null);
//        g.drawImage(robotIcon,robot.getXPos()*SCREEN_RATION - SCREEN_BLOCK_SIZE + 1, robot.getYPos()*SCREEN_RATION
//        		- SCREEN_BLOCK_SIZE + 1, 60, 60, null);
       
 //       g.fillOval(robot.posx - size + 1 , robot.posy - size + 1 , size*2-3, size*2-3);
        }
    }
	
	
}
