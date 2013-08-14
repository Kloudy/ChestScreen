package kloudy.mc.chestscreen.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import kloudy.mc.chestscreen.Display;
import kloudy.mc.chestscreen.util.DisplayManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnCommandEvent implements CommandExecutor{
	
	private HashMap<Integer, ArrayList<Display>> displays;
	private DisplayManager displayManager;
	
	public OnCommandEvent(){
		displayManager = DisplayManager.getInstance();
		displays = displayManager.displays;
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
					
					//get Display(s) by player that have yet to be completed				
					ArrayList<Display> pendingDisplays = displayManager.getPlayerPendingDisplays(player);
							
					int num = 0;
					for(Display display : pendingDisplays){
						if(display.getOffset() == null){
							player.sendMessage(ChatColor.RED + "Must select an offset block before display can be finished.");
						}
						
						else{
							//add display to HashTable
							ArrayList<Display> a = new ArrayList<Display>();
							a.add(display);
							int id = a.get(0).getChestID();
							displays.put(id, a);
							displays.get(-2).remove(display);//remove completed display from pending displays list							
							num++;
						}
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
				}

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
}
