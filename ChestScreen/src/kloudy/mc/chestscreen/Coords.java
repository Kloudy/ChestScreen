package kloudy.mc.chestscreen;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Coords implements Serializable{

	private int x;
	private int y;
	private int z;
	
	public Coords(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean equals(Coords obj){
		if(this.x == obj.x && this.y == obj.y && this.z == obj.z)
			return true;
		
		return false;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setZ(int z){
		this.z = z;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getZ(){
		return z;
	}
}
