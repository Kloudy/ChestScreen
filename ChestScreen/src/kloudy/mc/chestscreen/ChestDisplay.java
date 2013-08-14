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
import java.util.logging.Logger;

import kloudy.mc.chestscreen.events.BlckBreakEvent;
import kloudy.mc.chestscreen.events.OnCommandEvent;
import kloudy.mc.chestscreen.events.OnInteractEvent;
import kloudy.mc.chestscreen.events.RedstoneEvent;
import kloudy.mc.chestscreen.events.SignEvent;
import kloudy.mc.chestscreen.util.DisplayManager;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

//TODO thread block changes
//TODO sign on side of chest

/******************************************************************************************************
 * File Storage HashMap layout:
 * 
 * Key(int chestID):		Value:
 * -3 (Display object counter) Number of Display object instances there are
 * -2 (pending displayManager.displays)	ArrayList of displayManager.displays that are pending creation
 * -1 (unused displayManager.displays)		ArrayList of unregistered displayManager.displays with IDs that are now open for reuse
 * 0...n					ArrayList containing one Display object
 ******************************************************************************************************/

public class ChestDisplay extends JavaPlugin{
	DisplayManager displayManager;
	public Logger logger;
	
	/**
	 * Called when the plug-in is enabled on the server
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable(){	
		logger = getLogger();
		logger.info("Chest Screen Plugin Author: Tim Kerr (Kloudy)");
		logger.info("Reading display data from file");

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
	         displayManager = DisplayManager.getInstance();
	         displayManager.displays.put(-1, new ArrayList<Display>());
 
	         //don't open input stream if file is empty
	         if(file.length() > 0){
	        	 in = new ObjectInputStream(fileIn);  
	        	 displayManager.displays = (HashMap<Integer, ArrayList<Display>>) in.readObject();
	        	 getLogger().info(displayManager.displays.toString());
	        	 Display.newID = displayManager.displays.get(-3).get(0).getChestID();//initialize display object counter
		         in.close();
	         }
	         
	         //no data in the file (on first start)
	         else{
	        	 Display.newID = 0;//initialize display object counter	
	         }
	         	         
	         displayManager.displays.put(-2, new ArrayList<Display>());//initialize pending displayManager.displays list
	         displayManager.displays.put(-3, new ArrayList<Display>());
	         displayManager.displays.get(-3).add(new Display(null, null, Display.newID ,null, null, null));//keeps track of number of displayManager.displays created
	         fileIn.close();
         
	 		 getServer().getPluginManager().registerEvents(new BlckBreakEvent(displayManager.displays), this);
	 		 getServer().getPluginManager().registerEvents(new OnInteractEvent(displayManager.displays, getWorldGuard()), this);
	 		 getServer().getPluginManager().registerEvents(new RedstoneEvent(), this);
	 		 getServer().getPluginManager().registerEvents(new SignEvent(displayManager.displays), this);

	 		getCommand("cd").setExecutor(new OnCommandEvent());
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
		getLogger().info("Writing displayManager.displays data to file");
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
			 
			displayManager.displays.remove(-2);
			
	        FileOutputStream fileOut = new FileOutputStream("plugins/DisplayData/displayData.ser");
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);	         
        	out.writeObject(displayManager.displays);
        	out.close();
	        fileOut.close();
	      }
		 catch(IOException i){
	          i.printStackTrace();
	     }
	}
	
	public WorldGuardPlugin getWorldGuard() {
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
}