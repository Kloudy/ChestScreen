package kloudy.mc.chestscreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import kloudy.mc.enums.CharacterEnums;
//TODO turn this into the Display manager for all displays
public class DisplayManager {
	
	//Matrix used to set pixels that are displayed at any given point in time
	private boolean[] viewMatrix;
	private HashMap<Integer, ArrayList<Display>> displays;
	
	public DisplayManager(HashMap<Integer, ArrayList<Display>> displays){
		viewMatrix = new boolean[54];
		this.displays = displays;
	}
	
	/**
	 * Removes a display from use
	 */
	public void removeDisplay(String pname, int id, World world){	
		Player player = Bukkit.getServer().getPlayer(pname);
		
		//check if the display is still pending
		int counter = 0;
		boolean found = false;
		while(counter < displays.get(-2).size()){
			if(displays.get(-2).get(counter) != null){
				if(displays.get(-2).get(counter).getChestID() == id){
					displays.get(-1).add(displays.get(-2).get(counter));//add to unused list
					displays.get(-2).remove(counter);//remove display from pending
					found = true;
					counter = displays.get(-2).size();
					
					if(player != null){
						player.sendMessage(ChatColor.RED  + "Setup Cancelled for Display Chest ID#: " + id );
					}				
				}
			}
			counter++;
		}

		//not pending, remove display from HashTable
		if(!found){
			if(displays.get(id).get(0) != null){
				displays.get(-1).add(displays.get(id).get(0));//add to unused list
				
				String direction = displays.get(id).get(0).getDirection();
				Coords offset = displays.get(id).get(0).getOffset();
				int x = offset.getX();
				int y = offset.getY();
				int z = offset.getZ();

				//set all blocks at display offset to air
				
				//x--
				//y--
				if(direction.matches("[nN]|[nN]orth")){
					
					for(int i = 0; i < 9; i++){
						for(int j = 0; j < 6; j++){
							world.getBlockAt(new Location(world, x-i, y-j, z)).setTypeId(0);
						}
					}
				}
				
				//x++
				//y--
				else if(direction.matches("[sS]|[sS]outh")){

					for(int i = 0; i < 9; i++){
						for(int j = 0; j < 6; j++){
							world.getBlockAt(new Location(world, x+i, y-j, z)).setTypeId(0);
						}
					}
				}
				
				//z--
				//y--
				else if(direction.matches("[eE]|[eE]ast")){
					
					for(int i = 0; i < 9; i++){
						for(int j = 0; j < 6; j++){
							world.getBlockAt(new Location(world, x, y-j, z-i)).setTypeId(0);
						}
					}
				}
				
				//z++
				//y--
				else if(direction.matches("[wW]|[wW]est")){

					for(int i = 0; i < 9; i++){
						for(int j = 0; j < 6; j++){
							world.getBlockAt(new Location(world, x, y-j, z+i)).setTypeId(0);
						}
					}
				}
				
				//x++
				//z++
				else if(direction.matches("[uU]|[uU]p") || direction.matches("[dD]|[dD]own")){
					for(int i = 0; i < 9; i++){
						for(int j = 0; j < 6; j++){
							world.getBlockAt(new Location(world, x+i, y, z+j)).setTypeId(0);
						}
					}
				}
							
				displays.remove(id);
			}
			
			if(player != null){
				player.sendMessage(ChatColor.RED + "Unregistered Display Chest ID#: " + id);
			}
		}
	}
	
	/**
	 * Returns the chest ID number off of a sign
	 * @param sign
	 * @return int id
	 */
	public int getIdFromSign(Sign sign){
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(sign.getLine(3));
		int id = 0;
		
		if(m.find()){
			id = Integer.parseInt(m.group());
		}
		return id;
	}
	
	/**
	 * checks if block has been generated by a display
	 * @return true if the block is part of a display
	 */
	public boolean isDisplayBlock(Block block){
		
		Set<Integer> keySet = displays.keySet();
		Iterator<Integer> it = keySet.iterator();
		
		//loops through all finished display objects
		while(it.hasNext()){
			int id = (Integer)it.next();
			
			//special locations in HashTable, skip these
			if(!(id == -3 || id == -2 || id == -1)){
				if(displays.get(id) != null){
					
					Coords pos1 = displays.get(id).get(0).getOffset();
					Coords pos2 = null;
					String direction = displays.get(id).get(0).getDirection();
					
					int x = block.getX();
					int y = block.getY();
					int z = block.getZ();
					
					/* Chest View
					 * pos1 (top left), pos2 (bottom right)
					 * X O O O O O O O O
					 * O O O O O O O O O
					 * O O O O O O O O O
					 * O O O O O O O O O
					 * O O O O O O O O O
					 * O O O O O O O O X
					 */
					if(direction.matches("[nN]|[nN]orth")){
						pos2 = new Coords(pos1.getX() - 8, pos1.getY() - 5, pos1.getZ());
						
						if((x <= pos1.getX() && x >= pos2.getX()) && (y <= pos1.getY() && y >= pos2.getY()) && z == pos1.getZ()){
							return true;
						}
					}
					
					else if(direction.matches("[sS]|[sS]outh")){
						pos2 = new Coords(pos1.getX() + 8, pos1.getY() - 5, pos1.getZ());
						
						if((x >= pos1.getX() && x <= pos2.getX()) && (y <= pos1.getY() && y >= pos2.getY()) && z == pos1.getZ()){
							return true;
						}
					}
					
					else if(direction.matches("[eE]|[eE]ast")){
						pos2 = new Coords(pos1.getX(), pos1.getY() - 5, pos1.getZ() - 8);
						
						if(x == pos1.getX() && (y <= pos1.getY() && y >= pos2.getY()) && (z <= pos1.getZ() && z >= pos2.getZ())){
							return true;
						}
					}
					
					else if(direction.matches("[wW]|[wW]est")){
						pos2 = new Coords(pos1.getX(), pos1.getY() - 5, pos1.getZ() + 8);
						
						if(x == pos1.getX() && (y <= pos1.getY() && y >= pos2.getY()) && (z >= pos1.getZ() && z <= pos2.getZ())){
							return true;
						}
					}
					
					else if(direction.matches("[uU]|[uU]p") || direction.matches("[dD]|[dD]own")){
						pos2 = new Coords(pos1.getX() + 8, pos1.getY(), pos1.getZ() + 5);
						
						if((x >= pos1.getX() && x <= pos2.getX()) && y == pos1.getY() && (z >= pos1.getZ() && z <= pos2.getZ())){
							return true;
						}
					}					
				}
			}
		}
		return false;
	}
	
	/**
	 * Takes input  and converts it into boolean array that represents the character
	 * 
	 * Example:
	 * 
	 * A
	 * 
	 * boolean[] array = {   | { 0, 1, 0
	 * false, true, false,   |   1, 0, 1
	 * true, false, true,    |   1, 1, 1
	 * true, false, true,    |   1, 0, 1
	 * true, true, true,     |   1, 0, 1 }
	 * true, false, true,    |
	 * }
	 * 
	 * @return boolean array of representing a character
	 */
	private boolean[] generateCharacterBitMatrix(char c){		
		String value = CharacterEnums.valueOf("" + c).tosString();
		boolean[] bitMatrix = new boolean[value.length()];
		
		for(int i = 1; i <= value.length(); i++){
			
			//pixel off
			if(value.charAt(i) == ' '){
				bitMatrix[i] = false;
			}
			
			//pixel on
			else{
				bitMatrix[i] = true;
			}
		}	
		return bitMatrix;
	}
	
	/**
	 * Converts String of text into boolean array
	 * @return boolean array
	 */
	private boolean[][] generateStringBitMatrix(String str){
		boolean[][] stringBitMatrix = new boolean[str.length()][];
		
		for(int i = 0; i < stringBitMatrix.length; i++){				
			stringBitMatrix[i] = generateCharacterBitMatrix(str.charAt(i));
		}
		return stringBitMatrix;
	}
	
	/*
	 * Shifts display view to the left 1 block
	 */
	public void shift(){
		
	}
}