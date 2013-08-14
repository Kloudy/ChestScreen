package kloudy.mc.chestscreen.events;

import java.util.ArrayList;
import java.util.HashMap;

import kloudy.mc.chestscreen.Coords;
import kloudy.mc.chestscreen.Display;
import kloudy.mc.chestscreen.ShiftDisplay;
import kloudy.mc.chestscreen.util.DisplayManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignEvent implements Listener{
	
	private DisplayManager displayManager;
	HashMap<Integer, ArrayList<Display>> displays;
	
	public SignEvent(HashMap<Integer, ArrayList<Display>> displays){
		this.displayManager = DisplayManager.getInstance();
		this.displays = displays;
	}

	@EventHandler
	public void signListener(SignChangeEvent event){		
		Player player = event.getPlayer();
		Block block = event.getBlock();	
		Location location = block.getLocation();	
		location.setY(location.getY()-1);
		World world = block.getWorld();
		Sign sign = (Sign) event.getBlock().getState();
		
		String line0 = event.getLine(0);
		String line1 = event.getLine(1);
		
		//sign placed on top of chest
		if(player.hasPermission("cscreate") && (line0.equals("[CD]") || line0.equals("[CDN") || line0.equals("[CDS]")) && line1.matches("[nNsSeEwWdDuU]|[nN]orth|[sS]outh|[eE]ast|[wW]est|[uU]p|[dD]own") && world.getBlockAt(location).getType() == Material.CHEST){					
			Chest chest = (Chest) world.getBlockAt(location).getState();//chest directly below sign			
			Coords chestCoords = new Coords(chest.getLocation().getBlockX(), chest.getLocation().getBlockY(), chest.getLocation().getBlockZ());
			Coords signCoords = new Coords(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
		
			
			int id = displayManager.calcID();
			//create new display and store it in pending
			if(line0.equals("[CD]") || line0.equals("[CDN]")){
				displays.get(-2).add(new Display(chestCoords, signCoords, id, null, player.getName(), line1));//add display to array of incomplete displays
				
				//parse sign
				if(line1.matches("[nN]|[nN]orth")){
					event.setLine(1, "Direction: North");
				}
				else if(line1.matches("[sS]|[sS]outh")){
					event.setLine(1, "South");
				}
				else if(line1.matches("[eE]|[eE]ast")){
					event.setLine(1, "East");
				}
				else if(line1.matches("[wW]|[wW]est")){
					event.setLine(1, "West");
				}
				else if(line1.matches("[uU]|[uU]p")){
					event.setLine(1, "Up");
				}
				else if(line1.matches("[dD]|[dD]own")){
					event.setLine(1, "Down");
				}
			}
			//create new shift display and store it in pending
			else if(line0.equals("[CDS]")){
				displays.get(-2).add(new ShiftDisplay(chestCoords, signCoords, id, null, player.getName(), line1, displayManager.getXRepeatFromSign(sign), displayManager.getZRepeatFromSign(sign)));
				//parse sign
				if(line1.matches("[nN]|[nN]orth") || line1.matches("[nN]|[nN]orth[\\d*, \\d*]")){
					event.setLine(1, "Direction: North");
				}
				else if(line1.matches("[sS]|[sS]outh") || line1.matches("[sS]|[sS]outh[\\d*, \\d*]")){
					event.setLine(1, "South[" + displayManager.getXRepeatFromSign(sign) + ", " + displayManager.getZRepeatFromSign(sign));
				}
				else if(line1.matches("[eE]|[eE]ast") || line1.matches("[eE]|[eE]ast[\\d*, \\d*]")){
					event.setLine(1, "East");
				}
				else if(line1.matches("[wW]|[wW]est") || line1.matches("[wW]|[wW]est[\\d*, \\d*]")){
					event.setLine(1, "West");
				}
				else if(line1.matches("[uU]|[uU]p") || line1.matches("[uU]|[uU]p[\\d*, \\d*]")){
					event.setLine(1, "Up");
				}
				else if(line1.matches("[dD]|[dD]own") || line1.matches("[dD]|[dD]own[\\d*, \\d*]")){
					event.setLine(1, "Down");
				}
			}
			
			if(line0.equals("[CD]") || line0.equals("[CDN]")){
				if(line0.equals("[CD]"))
					event.setLine(0, "[ChestDisplay]");
				else if(line0.equals("[CDN]")){
					event.setLine(0, "[ChestDisplayN]");
				}
					
			}
	
			else if(line0.equals("[CDS]")){
				event.setLine(0, "[ChestDisplayS]");
			}
				
			
			event.setLine(2, player.getName());
			event.setLine(3, "ChestID#: " + id);
	
			player.sendMessage(ChatColor.GREEN + "Display chest successfully created.");
			player.sendMessage(ChatColor.GREEN + "Right click origin block (Top left pixel of display)");
		}
		
		else if(displayManager.isDisplaySign(sign) && !line1.matches("[nNsSeEwWdDuU]|[nN]orth|[sS]outh|[eE]ast|[wW]est|[uU]p|[dD]own") && world.getBlockAt(location).getType() == Material.CHEST){
			player.sendMessage(ChatColor.RED + "Invalid direction");
			block.breakNaturally();
		}
		//sign not placed on a chest
		else if((line0.equals("[CD]") || line0.equals("[CDN]")) && world.getBlockAt(location).getType() != Material.CHEST){
			player.sendMessage(ChatColor.RED + "Unsuccessful. Chest display sign must be placed one block above a chest.");
			block.breakNaturally();
		}
	}
}
