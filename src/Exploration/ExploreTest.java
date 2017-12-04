package Exploration;

import java.util.concurrent.TimeUnit;

public class ExploreTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		//----------------------------- TESTING isGridWithinCurRobotGrid ----------------------------
		/*Robot robot = new Robot(15, 15, Directions.E);
		for(int i = -2; i < 4; i++){
			for(int j = -2; j < 4; j++){
				System.out.print("Row: " + i + ", Col: " + j);
				System.out.println(", Result: " + robot.isGridWithinCurRobotGrid(i, j));
			}
		}*/
		
		//----------------------------- TESTING TIMER ----------------------------
		int startTime = currentTimeMillis();
		for(int i = 0; i < 10; i++)
			try {
				TimeUnit.MILLISECONDS.sleep(100);
				System.out.println("loop");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		int endTime = currentTimeMillis();
		int duration = endTime - startTime;
		System.out.println(startTime);
		System.out.println(endTime);
		System.out.println(duration);
		
	}
	
	public static int currentTimeMillis() {
	    return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
	}

}
