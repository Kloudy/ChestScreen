package kloudy.mc.chestscreen.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kloudy.mc.chestscreen.Display;
import kloudy.mc.chestscreen.enums.*;
import kloudy.mc.chestscreen.util.DisplayManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlckBreakEvent implements Listener {
	
	private DisplayManager displayManager;
	private HashMap<Integer, ArrayList<Display>> displays;
	
	public BlckBreakEvent(HashMap<Integer, ArrayList<Display>> displays){
		this.displayManager = DisplayManager.getInstance();
		this.displays = displays;
	}

	@EventHandler
	public int blockBreakListener(BlockBreakEvent event){
		
		if(event.isCancelled())
		{
			return 0;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		World world = block.getWorld();

		int bx = block.getLocation().getBlockX();
		int by = block.getLocation().getBlockY();
		int bz = block.getLocation().getBlockZ();
		
		//broken block is sign
		if(block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){					
			Sign sign = (Sign)block.getState();				
			if(displayManager.getSignDisplayType(sign) != DisplayTypes.None){				
				Pattern p = Pattern.compile("\\d+");//pull chestID number from sign
				Matcher m = p.matcher(sign.getLine(3));
								
				if(m.find()){	
					int id = Integer.parseInt(m.group());
					boolean isPending = false;
					
					//check if sign is in pending displays list
					for(int i = 0; i < displays.get(-2).size(); i++){
						if(displays.get(-2).get(i).getChestID() == id){
							isPending = true;
							i = displays.get(-2).size();
						}
					}

					if(displays.containsKey(id) || isPending){	
						//used to compare sign and chest coordinates to make sure the sign or chest broken is
						//actually the one associated with the display
						//Needed as a check for if the display data gets wiped and there are old signs
						//in the world.
						
						Display display = null;
						
						if(isPending){
							
							for(int i = 0; i < displays.get(-2).size(); i++){
								if(displays.get(-2).get(i).getChestID() == id){
									display = displays.get(-2).get(i);
									i = displays.size();
								}
							}
						}
						
						else if(displays.containsKey(id)){
							display = displays.get(id).get(0);
						}
						
						//Compare Coords
						if(block.getX() == display.getSign().getX() && block.getY() == display.getSign().getY() && block.getZ() == display.getSign().getZ()){
							displayManager.removeDisplay(player.getName(), id, block.getWorld());
						}
						else{
							player.sendMessage(ChatColor.RED + "Broke inactive sign");
						}						
					}
				}
			}
		}
		
		//broken block is chest
		else if(block.getType() == Material.CHEST){
			Sign sign  = null;
			
			if(world.getBlockAt(bx, by+1, bz).getType() == Material.SIGN_POST || world.getBlockAt(bx, by+1, bz).getType() == Material.WALL_SIGN){
				sign = (Sign)world.getBlockAt(bx, by+1, bz).getState();//get sign above chest		
				Block b = world.getBlockAt(bx, by+1, bz);
				
				if(sign.getLine(0).equals("[ChestDisplay]") || sign.getLine(0).equals("[ChestDisplayN]")){
					Pattern p = Pattern.compile("\\d+");//pull chestID number from sign
					Matcher m = p.matcher(sign.getLine(3));					
					
					if(m.find()){
						int id = Integer.parseInt(m.group());
						boolean isPending = false;
						
						//check if sign is in pending displays list
						for(int i = 0; i < displays.get(-2).size(); i++){
							if(displays.get(-2).get(i).getChestID() == id){
								isPending = true;
								i = displays.get(-2).size();
							}
						}
						
						if(displays.containsKey(id) || isPending){	
							
							//used to compare sign and chest coordinates to make sure the sign or chest broken is
							//actually the one associated with the display
							//Needed as a check for if the display data gets wiped and there are old signs
							//in the world.
							
							Display display = null;
							
							if(isPending){
								
								for(int i = 0; i < displays.get(-2).size(); i++){
									if(displays.get(-2).get(i).getChestID() == id){
										display = displays.get(-2).get(i);
										i = displays.size();
									}
								}
							}
							
							else if(displays.containsKey(id)){
								display = displays.get(id).get(0);
							}
							
							//Compare Coords
							if(block.getX() == display.getChest().getX() && block.getY() == display.getChest().getY() && block.getZ() == display.getChest().getZ()){
								displayManager.removeDisplay(player.getName(), id, block.getWorld());
								b.breakNaturally();//break display sign
							}
							else{
								player.sendMessage(ChatColor.RED + "Broke inactive sign");
							}
						}
					}
				}
			}
		}
		
		//block is part of a display
		//cancel break event and set block to air
		if(displayManager.isDisplayBlock(block)){
			event.setCancelled(true);
			block.setTypeId(0);
		}
		
		return 0;
	}
}
