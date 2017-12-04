package Exploration;

public class Map {
	
	private int[][] map;
	
	//--------------------- ACCESSING TO MAP DIRECTLY ----------------------
	
	/**
	* This function returns whether obstacle is there or not
	* This function is ONLY FOR SIMULATION RUN
	* @param corX
	* @param corY
	* @return
	*/
	
	public Map(){
		map = new int[Exploration.TOTAL_HORIZONTAL_TILES][Exploration.TOTAL_VERTICAL_TILES];
		
		
		
//		map[4][1] = 1;
		map[10][10] = 1;
		map[4][5] = 1;
		map[0][4] = 1;
		map[4][6] = 1;
		map[5][7] = 1;
		map[8][10] = 1;
		map[4][0] = 1;
		map[5][0] = 1;
		map[5][1] = 1;
		map[8][4] = 1;
		map[4][0] = 1;
		map[5][0] = 1;
		map[14][0] = 1;
//		map[12][0] = 1;
		map[5][2] = 1;
//		map[8][3] = 1;
		map[8][4] = 1;
		map[14][2] = 1;
//		map[12][5] = 1;
//		map[0][16] = 1;
//		map[1][16] = 1;
//		map[2][16] = 1;
//		map[3][16] = 1;
//		map[4][16] = 1;
		
	}
	
	public Map(String hex){
		int curPos = 0;
		map = new int[Exploration.TOTAL_HORIZONTAL_TILES][Exploration.TOTAL_VERTICAL_TILES];
		String bin = decodeMap(hex);
//		if(bin.length() < Exploration.TOTAL_HORIZONTAL_TILES * Exploration.TOTAL_VERTICAL_TILES)
//		{
//			System.out.println("The map generated is invalid!");
//			new Map();
//		}
		for(int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++){
			for(int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++){
				if(bin.charAt(curPos) == '1'){
					map[i][j] = 1;
				}
				curPos++;
			}
		}
	}
	
	public boolean isObstacleThere(int corX, int corY){
		if(corX < 0 || corY < 0 || corX >= Exploration.TOTAL_HORIZONTAL_TILES * Exploration.OBSTACLE_SIZE
				|| corY > Exploration.TOTAL_VERTICAL_TILES * Exploration.OBSTACLE_SIZE)
			return false;
		int[] xy = Exploration.coordinatesToGrid(corX, corY);
		
		if(map[xy[0]][xy[1]] == 1)
			return true;
		return false;
	}
	
	public void printMap(){
		for(int i = 0; i < Exploration.TOTAL_HORIZONTAL_TILES; i++){
			for(int j = 0; j < Exploration.TOTAL_VERTICAL_TILES; j++){
				System.out.print(map[Exploration.TOTAL_HORIZONTAL_TILES - i - 1][j] + ", ");
			}
			System.out.println();
		}
	}
	
	public static String decodeMap(String hex){
		String output = hexToBin(hex);
		output = changeToMapFormat(output);
		return output;
	}
	
	public static String encodeMap(String binary){
		String output = changeToHexFormat(binary);
		output = binToHex(output);
		return output;
	}
	
	public static String hexToBin(String hex){
        String bin = "";
        String binFragment = "";
        int iHex;
        hex = hex.trim();
        hex = hex.replaceFirst("0x", "");

        for(int i = 0; i < hex.length(); i++){
            iHex = Integer.parseInt(""+hex.charAt(i),16);
            binFragment = Integer.toBinaryString(iHex);

            while(binFragment.length() < 4){
                binFragment = "0" + binFragment;
            }
            bin += binFragment;
        }
        return bin;
    }
	
	public static String binToHex(String bin){
    	String hex = "";
    	String hexFragment = "";
    	String binFragment = "";
        int iBin = 0;
        bin = bin.trim();
        bin = bin.replaceFirst("0b", "");

       for(int i = 0; i < bin.length(); i++){
    	   	binFragment +=bin.charAt(i);
    	   	if(binFragment.length()==4){
    	   		iBin = Integer.parseInt(binFragment,2);
    	   		hexFragment = Integer.toHexString(iBin);
    	   		binFragment = "";
    	   		hex += hexFragment;
    	   	}
        }
       
       if(!binFragment.equals("")){
    	   iBin = Integer.parseInt(binFragment,2);
    	   hexFragment = Integer.toHexString(iBin);
    	   hex += hexFragment;
       }
       hex = "0x" + hex.toUpperCase();
       return hex;
    }
	
	public static String changeToMapFormat(String binary){
		String newBin;
		newBin = binary.substring(2, binary.length());
		newBin = newBin.substring(0, newBin.length()-2);
		return newBin;
	}
	
	public static String changeToHexFormat(String binary){
		String newBin;
		newBin = "11" + binary + "11";
		return newBin;
	}
	
	
	
	
}
