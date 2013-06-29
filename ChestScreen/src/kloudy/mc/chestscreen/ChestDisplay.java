/**
 * Chest Screen - Plugin for Bukkit Minecraft Servers
 * @author Tim Kerr (Kloudy)
 */
package kloudy.mc.chestscreen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;


import org.bukkit.plugin.Plugin;

/******************************************************************************************************
 * File Storage HashMap layout:
 * 
 * Key(int chestID):		Value:
 * -3 (Display object counter) Number of Display object instances there are
 * -2 (pending displays)	ArrayList of displays that are pending creation
 * -1 (unused displays)		ArrayList of unregistered displays with IDs that are now open for reuse
 * 0...n					ArrayList containing one Display object
 ******************************************************************************************************/

public class ChestDisplay extends JavaPlugin implements Listener{
	HashMap<Integer, ArrayList<Display>> displays = new HashMap<Integer, ArrayList<Display>>();
	WorldGuardPlugin wg;
	
	/**
	 * Called when the plug-in is enabled on the server
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Chest Screen Plugin Author: Tim Kerr (Kloudy)");
		getLogger().info("Reading display data from file");
		wg = getWorldGuard();
		
		displays.put(-1, new ArrayList<Display>());//initialize removed displays list
		
		//read display data from file
		 try{
			 File folder = new File("plugins/DisplayData");			 
			 if(!folder.exists()){
				 folder.mkdir();
			 }
			 
			 File file = new File("plugins/DisplayData/displayData.ser");
			 
			 if(!file.exists())
				 file.createNewFile();
			 
	         FileInputStream fileIn = new FileInputStream(file);
	         ObjectInputStream in = null;         
	         
	         //don't open input stream if file is empty
	         if(file.length() > 0){
	        	 in = new ObjectInputStream(fileIn);  
	        	 displays = (HashMap<Integer, ArrayList<Display>>) in.readObject();
	        	 Display.newID = displays.get(-3).get(0).getChestID();//initialize display object counter
		         in.close();
	         }
	         
	         //no data in the file (on first start)
	         else{
	        	 Display.newID = 0;//initialize display object counter	
	         }
	         	         
	         displays.put(-2, new ArrayList<Display>());//initialize pending displays list
	         displays.put(-3, new ArrayList<Display>());
	         displays.get(-3).add(new Display(null, null, Display.newID ,null, null, null));//keeps track of number of displays created
	         fileIn.close();
	      }
		 catch(IOException i){
	         i.printStackTrace();
	      }
		 catch(ClassNotFoundException c){
	         c.printStackTrace();
	      }
	}
	
	/**
	 * Called when the plug-in is disabled on the server
	 */
	@Override
	public void onDisable(){
		getLogger().info("Writing displays data to file");
		//TODO Format /cd list toString better
		
		//write display data to file
		 try{
			File folder = new File("plugins/DisplayData");			 
			if(!folder.exists()){
				folder.mkdir();
			}
			 
			File file = new File("plugins/DisplayData/displayData.ser");		 
			if(!file.exists())
				file.createNewFile();
			 
			displays.remove(-2);
			
	        FileOutputStream fileOut = new FileOutputStream("plugins/DisplayData/displayData.ser");
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);	         
        	out.writeObject(displays);
        	out.close();
	        fileOut.close();
	      }
		 catch(IOException i){
	          i.printStackTrace();
	     }
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		Player player;
		if(sender instanceof Player){
			player = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("cd")){			
				if(args.length == 0){
					player.sendMessage(ChatColor.RED + "Too few arguments.\n");
				}
			
				//command "/cd finish" completes display setup
				if(args.length == 1 && args[0].equals("finish")){
					
					//find Display(s) by player that have yet to be completed
					int counter = 0;
					int num = 0;
					while(counter < displays.get(-2).size()){
						if(displays.get(-2).get(counter) != null){
							if(displays.get(-2).get(counter).getPName().equals(player.getName())){
								
								
								if(displays.get(-2).get(counter).getOffset() == null){
									player.sendMessage(ChatColor.RED + "Must select an offset block before display can be finished.");
									counter = displays.get(-2).size();
								}
								
								else{
									//add display to HashTable
									ArrayList<Display> a = new ArrayList<Display>();
									a.add(displays.get(-2).get(counter));
									int id = a.get(0).getChestID();//displays.get(-2).get(counter).getChestID();
									displays.put(id, a);
									displays.get(-2).remove(counter);//remove completed display from pending displays list							
									counter--;
									num++;
								}
							}
						}
						counter++;
					}
					if(num == 1){
						player.sendMessage(ChatColor.GREEN + "Successfully created display");
					}
					
					else if(num > 1){
						player.sendMessage(ChatColor.GREEN + "Successfully created " + num + " displays");
					}
				}
				
				//list all displays and their coordinates (mainly for debugging purposes)
				else if(args.length == 1 && args[0].matches("[Ll]ist")){		
					
					Set entrySet = displays.entrySet();
					Object[] obj = entrySet.toArray();
					
					for(int i = 0; i < obj.length; i++){
						if(obj[i] != null)
							player.sendMessage(ChatColor.GREEN + obj[i].toString());
					}
					
					//player.sendMessage(ChatColor.GREEN + displays.toString());
				}
				
				//remove display at index specified
				/*else if(args.length == 2 && args[0].matches("[rR]emove") && args[1].matches("\\d+")){
					
					int id = Integer.parseInt(args[1]);
					
					if(displays.get(id) != null){
						removeDisplay(player.getName(), id, world);
						player.sendMessage(ChatColor.GREEN + "Removed Display #" + args[1]);
					}
					
					else{
						player.sendMessage(ChatColor.RED + "Display does not exist");
					}
				}*/

				else{
					player.sendMessage(ChatColor.RED + "Invalid arguements for chest display\n" + 
					"Commands List:\n/cd finish\n/cd list\n/cd remove [ChestID#]");
				}
			}
		}
		
		else{
			sender.sendMessage("Must be a player to do that!");
			return false;
		}
		
		return false;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {	
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		int found = 0;//how many pending displays for player
		boolean canBuild = false;
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getItemInHand().getType() != Material.SIGN){
			//get display player is currently setting up
			int counter = 0;
			while(counter < displays.get(-2).size()){
				if(displays.get(-2).get(counter) != null){
					if(displays.get(-2).get(counter).getPName().equals(player.getName())){
						
						//check if player has permissions to set offset block at chosen location
						canBuild = checkBuildRadiusPermissions(block.getLocation(), player, displays.get(-2).get(counter).getDirection());						
						if(canBuild){
							displays.get(-2).get(counter).setOffset(new Coords(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()));
							player.sendMessage(ChatColor.GREEN + "Set offset block: X: " + block.getX() + " ,Y: " + block.getY() + " ,Z: " + block.getZ() +"\n" + ChatColor.AQUA + "type /cd finish to complete display setup.");
						}
						
						else{
							player.sendMessage(ChatColor.RED + "Must set offset farther away from protected region!");
						}
						//displays.get(-2).get(counter).setOffset(new Coords(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()));
						found++;
					}
				}
				counter++;
			}
			
			if(found > 0){

			}
		}
	}
	
	@EventHandler
	public void blockBreakListener(BlockBreakEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();
		World world = block.getWorld();

		int bx = block.getLocation().getBlockX();
		int by = block.getLocation().getBlockY();
		int bz = block.getLocation().getBlockZ();
		
		//broken block is sign
		if(block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){					
			Sign sign = (Sign)block.getState();				
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
						if(block.getX() == display.getSign().getX() && block.getY() == display.getSign().getY() && block.getZ() == display.getSign().getZ()){
							removeDisplay(player.getName(), id, block.getWorld());
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
								removeDisplay(player.getName(), id, block.getWorld());
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
		
		//TODO when display is removed, set all blocks at its offset to air
		
		//Check to see if broken block is part of the display.
		//If it is, cancel the block break event and prevent and drops from falling
		boolean noBreak = isDisplayBlock(block);
		
		//block is part of a display
		//cancel break event and set block to air
		if(noBreak){
			event.setCancelled(true);
			block.setTypeId(0);
		}
	}
	
	@EventHandler
	public void signListener(SignChangeEvent event){		
		Player player = event.getPlayer();
		Block block = event.getBlock();	
		Location location = block.getLocation();	
		location.setY(location.getY()-1);
		World world = block.getWorld();
		
		String line0 = event.getLine(0);
		String line1 = event.getLine(1);
		
		//sign placed on top of chest
		if((line0.equals("[CD]") || line0.equals("[CDN]")) && line1.matches("[nNsSeEwWdDuU]|[nN]orth|[sS]outh|[eE]ast|[wW]est|[uU]p|[dD]own") && world.getBlockAt(location).getType() == Material.CHEST){					
			Chest chest = (Chest) world.getBlockAt(location).getState();//chest directly below sign			
			Coords chestCoords = new Coords(chest.getLocation().getBlockX(), chest.getLocation().getBlockY(), chest.getLocation().getBlockZ());
			Coords signCoords = new Coords(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			
			int id = calcID();
			displays.get(-2).add(new Display(chestCoords, signCoords, id, null, player.getName(), line1));//add display to array of incomplete displays

			if(line0.equals("[CD]"))
				event.setLine(0, "[ChestDisplay]");
			else if(line0.equals("[CDN]"))
				event.setLine(0, "[ChestDisplayN]");
			
			event.setLine(2, player.getName());
			event.setLine(3, "ChestID#: " + id);
			
			//parse sign
			if(line1.matches("[nN]|[nN]orth")){
				event.setLine(1, "Direction: North");
			}
			else if(line1.matches("[sS]|[sS]outh")){
				event.setLine(1, "Direction: South");
			}
			else if(line1.matches("[eE]|[eE]ast")){
				event.setLine(1, "Direction: East");
			}
			else if(line1.matches("[wW]|[wW]est")){
				event.setLine(1, "Direction: West");
			}
			else if(line1.matches("[uU]|[uU]p")){
				event.setLine(1, "Direction: Up");
			}
			else if(line1.matches("[dD]|[dD]own")){
				event.setLine(1, "Direction: Down");
			}
			
			player.sendMessage(ChatColor.GREEN + "Display chest successfully created.");
			player.sendMessage(ChatColor.GREEN + "Right click origin block (Top left pixel of display)");
		}
		
		else if((line0.equals("[CD]") || line0.equals("[CDN]")) && !line1.matches("[nNsSeEwWdDuU]|[nN]orth|[sS]outh|[eE]ast|[wW]est|[uU]p|[dD]own") && world.getBlockAt(location).getType() == Material.CHEST){
			player.sendMessage(ChatColor.RED + "Invalid direction");
			block.breakNaturally();
		}
		//sign not placed on a chest
		else if((line0.equals("[CD]") || line0.equals("[CDN]")) && world.getBlockAt(location).getType() != Material.CHEST){
			player.sendMessage(ChatColor.RED + "Unsuccessful. Chest display sign must be placed one block above a chest.");
			block.breakNaturally();
		}
	}
	
	@EventHandler
	public void RedstoneListener(BlockRedstoneEvent event){		
		Block poweredBlock = event.getBlock();
		Chest chest = null;
		ItemStack[] items = null;
		Coords offset = null;
		World world = poweredBlock.getWorld();
		
		boolean remove = false;
		String removeName = null;
		int removeId = 0;
		
		int bx = poweredBlock.getLocation().getBlockX();
		int by = poweredBlock.getLocation().getBlockY();
		int bz = poweredBlock.getLocation().getBlockZ();

		//check to see if block is one of the signs already registered		
		if(poweredBlock.getType() == Material.SIGN_POST || poweredBlock.getType() == Material.WALL_SIGN){					
			Sign sign = (Sign)poweredBlock.getState();
			int id = getIdFromSign(sign);
			boolean isValid = false;
			
			if(displays.get(id) != null){
				//check if powered sign location is valid (i.e. the sign coord of powered sign should match up with sign coords in Hashtable)
				if(poweredBlock.getX() == displays.get(id).get(0).getSign().getX() && poweredBlock.getY() == displays.get(id).get(0).getSign().getY() && poweredBlock.getZ() == displays.get(id).get(0).getSign().getZ()){
					isValid = true;
				}
			}
			
			if((sign.getLine(0).equals("[ChestDisplay]") || sign.getLine(0).equals("[ChestDisplayN]")) && displays.get(id) != null && sign.getBlock().isBlockPowered() == true && isValid){			
				chest = (Chest)world.getBlockAt(bx, by-1, bz).getState();//get chest below sign			
				items = chest.getInventory().getContents();
				offset = displays.get(id).get(0).getOffset();
				
				//display items in chest at offset block location	
				Location currLoc = new Location(world, offset.getX(), offset.getY(), offset.getZ());
				for(int i = 1; i <= items.length; i++){
					Block currBlock = world.getBlockAt(currLoc);
					
					if((i%9) == 0){//new row
						if(displays.get(id).get(0).getDirection().matches("[nN]|[nN]orth")){
							currLoc.setX(offset.getX() + 1);
						}
						
						else if(displays.get(id).get(0).getDirection().matches("[sS]|[sS]outh")){
							currLoc.setX(offset.getX() - 1);
						}
						
						else if(displays.get(id).get(0).getDirection().matches("[eE]|[eE]ast")){
							currLoc.setZ(offset.getZ()+1);
						}
						
						else if(displays.get(id).get(0).getDirection().matches("[wW]|[wW]est")){
							currLoc.setZ(offset.getZ()-1);
						}
						
						else if(displays.get(id).get(0).getDirection().matches("[dD]|[dD]own") || displays.get(id).get(0).getDirection().matches("[uU]|[uU]p")){
							currLoc.setX(offset.getX()-1);
						}
						
						//y not incremented if direction is up or down
						if(!displays.get(id).get(0).getDirection().matches("[dD]|[dD]own") && !displays.get(id).get(0).getDirection().matches("[uU]|[uU]p")){
							currLoc.setY(currLoc.getY()-1);	
						}
						else{
							currLoc.setZ(currLoc.getZ()+1);
						}
					}
					
					//place block at currLoc corresponding to chest item
					if(items[i-1] != null){	
						//check to make sure its not destroying the sign or chest
						//if it does, unregister the display
						if(currBlock.getType() == Material.SIGN_POST || currBlock.getType() == Material.WALL_SIGN){
							Sign s = (Sign)currBlock.getState();
							
							if(s.getLine(0).equals("[ChestDisplay]") || s.getLine(0).equals("[ChestDisplayN]")){
								int tempId = getIdFromSign(s);
								
								//display is not destroying its own sign or chest
								if(tempId != id){
									removeDisplay(s.getLine(2), tempId, sign.getWorld());
								}
								//display is destroying its own source chest and sign
								else{
									remove = true;
									removeName = s.getLine(2);
									removeId = tempId;
								}
							}
						}
						
						else if(currBlock.getType() == Material.CHEST){
							Block b = world.getBlockAt(currBlock.getX(), currBlock.getY()+1, currBlock.getZ());
							if(b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN){
								Sign s = (Sign)b.getState();
								if(s.getLine(0).equals("[ChestDisplay]") || s.getLine(0).equals("[ChestDisplayN]")){
									int tempId = getIdFromSign(s);
									
									//display is not destroying its own sign or chest
									if(tempId != id){
										removeDisplay(s.getLine(2), tempId, b.getWorld());
									}
									//display is destroying its own source chest and sign
									else{
										remove = true;
										removeName = s.getLine(2);
										removeId = tempId;
									}
								}
							}
						}
						
						//place block
						if(items[i-1].getTypeId() < 256){
							currBlock.setTypeIdAndData(items[i-1].getTypeId(), items[i-1].getData().getData(), false);
							currBlock.setMetadata(sign.getLine(3), new FixedMetadataValue(this, "hi"));
						}					
					}
					
					if(sign.getLine(0).equals("[ChestDisplayN]") && items[i-1] == null){
						currBlock.setTypeId(0);//place air block for null space in chest	
						currBlock.setMetadata(sign.getLine(3), new FixedMetadataValue(this, "hi"));
					}
					
					if(displays.get(id).get(0).getDirection().matches("[nN]|[nN]orth")){
						currLoc.setX(currLoc.getX() - 1);
					}
					
					else if(displays.get(id).get(0).getDirection().matches("[sS]|[sS]outh")){
						currLoc.setX(currLoc.getX() + 1);
					}
					
					else if(displays.get(id).get(0).getDirection().matches("[eE]|[eE]ast")){
						currLoc.setZ(currLoc.getZ()-1);
					}
					
					else if(displays.get(id).get(0).getDirection().matches("[wW]|[wW]est")){
						currLoc.setZ(currLoc.getZ()+1);
					}
					
					else if(displays.get(id).get(0).getDirection().matches("[dD]|[dD]own") || displays.get(id).get(0).getDirection().matches("[uU]|[uU]p")){
						currLoc.setX(currLoc.getX()+1);
					}
				}	
				if(remove){
					removeDisplay(removeName, removeId, currLoc.getWorld());
				}
			}
		}
	}
	
	/**
	 * Calculates a new chestID
	 */
	private int calcID(){
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
	
	/**
	 * Removes a display from use
	 */
	private void removeDisplay(String pname, int id, World world){	
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
		
		if(player != null){
			getLogger().info( player.getName() + " Unregistered Chest Display: " + Integer.toString(id));
		}
	}
	
	/**
	 * Returns the chest ID number off of a sign
	 * @param sign
	 * @return int id
	 */
	private int getIdFromSign(Sign sign){
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(sign.getLine(3));
		int id = 0;
		
		if(m.find()){
			id = Integer.parseInt(m.group());
		}
		return id;
	}
	
	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = null;
		
		try{
			plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		}
		catch(Exception e){
			getLogger().info(e.getMessage());
		}
	 
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null;
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
	
	/**
	 * Checks to see if a player has placed a block at least 9 blocks away from a World Guard region 
	 * that they do not belong to. Must be at least 9 blocks away b/c chest have 9 cols and I want
	 * to prevent the possibilty of a display having an offset that isn't protected, but the display 
	 * extends into a region that is protected.
	 * @return true if player can is allowed to build at location
	 */
	private boolean checkBuildRadiusPermissions(Location location, Player player, String direction){
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
	
	/**
	 * checks if block has been generated by a display
	 * @return true if the block is part of a display
	 */
	private boolean isDisplayBlock(Block block){
		
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
}