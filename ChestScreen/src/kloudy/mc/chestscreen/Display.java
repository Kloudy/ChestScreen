package kloudy.mc.chestscreen;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Display implements Serializable{

	public static int newID;//static variable keeps track of how many displays have been created
	private Coords chest;
	private Coords sign;
	private int chestID;
	private Coords offset;
	private String pname;
	private String dir;
	
	public Display(Coords chest, Coords sign, int chestID, Coords offset, String pname, String dir){		
		this.chest = chest;
		this.sign = sign;
		this.chestID = chestID;
		this.offset = offset;
		this.pname = pname;
		this.dir = dir;
	}
	
	public Display copy(){
		return new Display(chest, sign, chestID, offset, pname, dir);
	}
	
	public Coords getChest(){
		return chest;
	}
	
	public Coords getSign(){
		return sign;
	}
	
	public int getChestID(){
		return chestID;
	}
	
	public Coords getOffset(){
		return offset;
	}
	
	public String getPName(){
		return pname;
	}
	
	public String getDirection(){
		return dir;
	}
	
	public void setDirection(String dir){
		this.dir = dir;
	}
	
	public void setChest(Coords chest){
		this.chest = chest;
	}
	
	public void setSign(Coords sign){
		this.sign = sign;
	}
	
	public void setChestID(int chestID){
		this.chestID = chestID;
	}
	
	public void setOffset(Coords offset){
		this.offset = offset;
	}
	
	public void setPName(String pname){
		this.pname = pname;
	}
	
	public String toString(){
		String str = new String();
		
		//format array list elements
		if(chestID == -2 || chestID == -1){
			
		}
		
		//single elements in Hashtable
		else{
			if(offset != null)
				str += pname + ", ID#: " + chestID + ", X: " + offset.getX() + " ,Y: " + offset.getY() + " ,Z: " + offset.getZ();
			
			else
				str += pname + ", ID#: " + chestID;
		}
		
		return str;
	}
}
