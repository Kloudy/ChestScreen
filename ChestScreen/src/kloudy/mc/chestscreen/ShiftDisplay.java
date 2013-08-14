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
	
	private int repeatX;
	private int repeatZ;
	
	public ShiftDisplay(Coords chest, Coords sign, int chestID, Coords offset, String pname, String dir, int repeatX, int repeatZ) {
		super(chest, sign, chestID, offset, pname, dir);
		this.repeatX = repeatX;
		this.repeatZ = repeatZ;
	}	
	
	public int getRepeatX(){
		return repeatX;
	}
	
	public int getRepeatZ(){
		return repeatZ;
	}
	
	public void setRepeatX(int repeatX){
		this.repeatX = repeatX;
	}
	
	public void setRepeatZ(int repeatZ){
		this.repeatZ = repeatZ;
	}
}
