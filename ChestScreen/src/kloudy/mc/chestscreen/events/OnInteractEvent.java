package kloudy.mc.chestscreen.events;

import java.util.ArrayList;
import java.util.HashMap;

import kloudy.mc.chestscreen.Coords;
import kloudy.mc.chestscreen.Display;
import kloudy.mc.chestscreen.util.DisplayManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class OnInteractEvent implements Listener{

	private DisplayManager displayManager;
	private WorldGuardPlugin wg;
	
	public OnInteractEvent(HashMap<Integer, ArrayList<Display>> displays, WorldGuardPlugin wg){
		this.displayManager = DisplayManager.getInstance();
		this.wg = wg;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {	
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		int found = 0;//how many pending displays for player
		boolean canBuild = false;
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getItemInHand().getType() != Material.SIGN){		
			ArrayList<Display> pendingDisplays = displayManager.getPlayerPendingDisplays(player);
			
			for(Display display : pendingDisplays){
				//check if player has permissions to set offset block at chosen location
				canBuild = checkBuildRadiusPermissions(block.getLocation(), player, display.getDirection());						
				if(canBuild){
					display.setOffset(new Coords(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()));
					
					//only display message once if player is setting up multiple displays at once
					if(found == 0)
						player.sendMessage(ChatColor.GREEN + "Set offset block: X: " + block.getX() + " ,Y: " + block.getY() + " ,Z: " + block.getZ() +"\n" + ChatColor.AQUA + "type /cd finish to complete display setup.");
				}
				
				else{
					player.sendMessage(ChatColor.RED + "Must set offset farther away from protected region!");
				}
				found++;
			}
		}
	}
	
	/**
	 * Checks to see if a player has placed a block at least 9 blocks away from a World Guard region 
	 * that they do not belong to. Must be at least 9 blocks away b/c chest have 9 cols and I want
	 * to prevent the possibilty of a display having an offset that isn't protected, but the display 
	 * extends into a region that is protected.
	 * @return true if player can is allowed to build at location
	 */
	public boolean checkBuildRadiusPermissions(Location location, Player player, String direction){
		World world = location.getWorld();
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();

		if(wg != null){			
			//loop through cuboid area (19x19x6) to check if player is making a display that will extend into a region
			for(int i = 0; i < 6; i++){
				for(int j = -9; j < 10; j++){
					for(int k = -9; k < 10; k++){
						if(!wg.canBuild(player, new Location(world, x + j, y - i, z + k))){
							return false;
						}
					}
				}
			}
		}		
		return true;
	}
}
