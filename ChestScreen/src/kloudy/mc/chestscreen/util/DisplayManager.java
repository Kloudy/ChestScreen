package kloudy.mc.chestscreen.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import kloudy.mc.chestscreen.Coords;
import kloudy.mc.chestscreen.Display;
import kloudy.mc.chestscreen.enums.*;

/**
 * Singleton utility class used to manage display objects
 */
public class DisplayManager{
	
	public HashMap<Integer, ArrayList<Display>> displays;
	private static DisplayManager instance;
	
	private DisplayManager()
	{
		displays = new HashMap<Integer, ArrayList<Display>>();
	}
	
	public static DisplayManager getInstance()
	{
		if(instance == null)
		{
			instance = new DisplayManager();
		}

		return instance;
	}
	
	/**
	 * Returns a list of pending displays associated with given player
	 */
	public ArrayList<Display> getPlayerPendingDisplays(Player player){
		if(player == null)
			return null;
		else{
			ArrayList<Display> pendingDisplays = new ArrayList<Display>();
			
			for(Display display : displays.get(-2)){
				if(display.getPName().equals(player.getName())){
					pendingDisplays.add(display);
				}
			}
			return pendingDisplays;
		}
	}
	
	public boolean isDisplaySign(Sign sign){
		return getSignDisplayType(sign) != DisplayTypes.None;
	}
	
	/**
	 * Determines the type of Display associated with a given sign block
	 */
	public DisplayTypes getSignDisplayType(Sign sign){
		if(sign.getLine(0).equals("[ChestDisplay]")){
			return DisplayTypes.ChestDisplay;
		}
		else if(sign.getLine(0).equals("[ChestDisplayN]")){
			return DisplayTypes.ChestDisplayNull;
		}
		else if(sign.getLine(0).equals("[ChestDisplayS]")){
			return DisplayTypes.ShiftDisplay;
		}
		else{
			return DisplayTypes.None;
		}
	}
	
	/**
	 * Removes a display from use and puts it in available ids list
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
				
				/*String direction = displays.get(id).get(0).getDirection();
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
				}*/
							
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
	
	public int getXRepeatFromSign(Sign sign){
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(sign.getLine(2));
		int x = 1;
		
		if(m.find()){
			x = Integer.parseInt(m.group(0));
		}
		return x;
	}
	
	public int getZRepeatFromSign(Sign sign){
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(sign.getLine(2));
		int z = 1;
		
		if(m.find()){
			z = Integer.parseInt(m.group(1));
		}
		return z;
	}
	
	public Directions getDirectionFromSign(Sign sign){
		
		Directions direction = Directions.None;
		int id = getIdFromSign(sign);
		
		if(displays.get(id).get(0).getDirection().matches("[nN]|[nN]orth")){
			direction = Directions.North;
		}
		else if(displays.get(id).get(0).getDirection().matches("[sS]|[sS]outh")){
			direction = Directions.South;
		}
		else if(displays.get(id).get(0).getDirection().matches("[eE]|[eE]ast")){
			direction = Directions.East;
		}
		else if(displays.get(id).get(0).getDirection().matches("[wW]|[wW]est")){
			direction = Directions.West;
		}
		else if(displays.get(id).get(0).getDirection().matches("[dD]|[dD]own") || displays.get(id).get(0).getDirection().matches("[uU]|[uU]p")){
			direction = Directions.Horizontal;
		}
		
		return direction;
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
	 * Calculates a new chestID
	 */
	public int calcID(){
		int id = 0;

		//no unused chest IDs
		if(displays.get(-1).size() == 0){
			id = Display.newID;
			Display.newID++;
			displays.get(-3).get(0).setChestID(Display.newID);
		}
       	 	       
        else{
       	 	id = displays.get(-1).get(0).getChestID();//pull chest ID from unused chest
       	 	displays.get(-1).remove(0);//removes display from unused list       	 	
        }
		
		return id;
	}	
}
