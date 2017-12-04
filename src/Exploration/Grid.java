package Exploration;

public class Grid {
	protected int x;
	protected int y;
	
	public Grid(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Grid newGrid){
		if(newGrid.x == this.x && newGrid.y == this.y){
			return true;
		}
		return false;
	}
}
