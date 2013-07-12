package kloudy.mc.chestscreen;

@SuppressWarnings("serial")
/**
 * Represents a display that "scrolls" the input retrieved from a book onto the screen.
 * Allows for a wider width than is permitted with other displays. (Up to 15 blocks)
 */
public class ShiftDisplay extends Display{
	
	/*
	 * Height of Shift Displays are 6 blocks high and cannot be changed
	 */
	
	private int width;
	private final int height = 6;
	
	public ShiftDisplay(Coords chest, Coords sign, int chestID, Coords offset, String pname, String dir, int width) {
		super(chest, sign, chestID, offset, pname, dir);
		this.width = width;
	}	
	
	public void setWidth(int width){
		this.width = width;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return this.height;
	}
}
