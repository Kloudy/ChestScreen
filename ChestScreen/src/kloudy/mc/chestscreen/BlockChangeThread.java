package kloudy.mc.chestscreen;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;

import kloudy.mc.chestscreen.enums.Directions;
import kloudy.mc.chestscreen.enums.DisplayTypes;
import kloudy.mc.chestscreen.util.DisplayManager;

public class BlockChangeThread implements Runnable{

	private DisplayManager displayManager;
	private Thread thread;
	private BlockRedstoneEvent event;
	
	public BlockChangeThread(BlockRedstoneEvent event)
	{
		this.event = event;
		displayManager = DisplayManager.getInstance();
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() 
	{
		Block poweredBlock = event.getBlock();
		Chest chest = null;
		ItemStack[] items = null;
		Coords offset = null;
		World world = poweredBlock.getWorld();
		
		boolean remove = false;
		String removeName = null;
		int removeId = 0;
		
		//check to see if block is one of the signs already registered		
		if(poweredBlock.getType() == Material.SIGN_POST || poweredBlock.getType() == Material.WALL_SIGN){					
			Sign sign = (Sign)poweredBlock.getState();
			int id = displayManager.getIdFromSign(sign);
			boolean isValid = false;
			int bx = poweredBlock.getLocation().getBlockX();
			int by = poweredBlock.getLocation().getBlockY();
			int bz = poweredBlock.getLocation().getBlockZ();
			
			if(displayManager.displays.get(id) != null){
				//check if powered sign location is valid (i.e. the sign coord of powered sign should match up with sign coords in Hashtable)
				if(poweredBlock.getX() == displayManager.displays.get(id).get(0).getSign().getX() && poweredBlock.getY() == displayManager.displays.get(id).get(0).getSign().getY() && poweredBlock.getZ() == displayManager.displays.get(id).get(0).getSign().getZ()){
					isValid = true;
				}
			}
			
			if(displayManager.getSignDisplayType(sign) != DisplayTypes.None && displayManager.displays.get(id) != null && sign.getBlock().isBlockPowered() && isValid){			
				
				chest = (Chest)world.getBlockAt(bx, by-1, bz).getState();//get chest below sign			
				items = chest.getInventory().getContents();
				offset = displayManager.displays.get(id).get(0).getOffset();
				
				//display items in chest at offset block location	
				Location currLoc = new Location(world, offset.getX(), offset.getY(), offset.getZ());
				
				int xRepeat = 1;
				int zRepeat = 1;
				
				if(displayManager.getSignDisplayType(sign) == DisplayTypes.ShiftDisplay)
				{
					ShiftDisplay d = (ShiftDisplay)displayManager.displays.get(id).get(0);
					xRepeat = d.getRepeatX();
					zRepeat = d.getRepeatZ();
				}
				
				//check the direction of the display
				Directions direction = displayManager.getDirectionFromSign(sign);			
				
				//repeat display in x direction
				for(int j = 0; j < xRepeat; j++){
					
					//repeat display in z direction
					for(int k = 0; k < zRepeat; k++){
						
						//iterate through chest contents
						for(int i = 1; i <= items.length; i++){
							Block currBlock = world.getBlockAt(currLoc);
							
							if((i%9) == 0){//new row
								if(direction == Directions.North){
									currLoc.setX(offset.getX() + 1);
								}
								
								else if(direction == Directions.South){
									currLoc.setX(offset.getX() - 1);
								}
								
								else if(direction == Directions.East){
									currLoc.setZ(offset.getZ()+1);
								}
								
								else if(direction == Directions.West){
									currLoc.setZ(offset.getZ()-1);
								}
								
								else if(direction == Directions.Horizontal){
									currLoc.setX(offset.getX()-1);
								}
								
								//y not incremented if direction is up or down
								if(direction != Directions.Horizontal){
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
									DisplayTypes type = displayManager.getSignDisplayType(s);
									
									if(type != DisplayTypes.None){
										int tempId = displayManager.getIdFromSign(s);
										
										//display is not destroying its own sign or chest
										if(tempId != id){
											displayManager.removeDisplay(s.getLine(2), tempId, sign.getWorld());
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
										if(displayManager.getSignDisplayType(s) != DisplayTypes.None){
											int tempId = displayManager.getIdFromSign(s);
											
											if(tempId != id){
												displayManager.removeDisplay(s.getLine(2), tempId, b.getWorld());
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
								}					
							}
							
							//next pixel to display
							
							if(displayManager.getSignDisplayType(sign) == DisplayTypes.ChestDisplayNull && items[i-1] == null){
								currBlock.setTypeId(0);//place air block for null space in chest	
							}
							
							if(displayManager.displays.get(id).get(0).getDirection().matches("[nN]|[nN]orth")){
								currLoc.setX(currLoc.getX() - 1);
							}
							
							else if(displayManager.displays.get(id).get(0).getDirection().matches("[sS]|[sS]outh")){
								currLoc.setX(currLoc.getX() + 1);
							}
							
							else if(displayManager.displays.get(id).get(0).getDirection().matches("[eE]|[eE]ast")){
								currLoc.setZ(currLoc.getZ()-1);
							}
							
							else if(displayManager.displays.get(id).get(0).getDirection().matches("[wW]|[wW]est")){
								currLoc.setZ(currLoc.getZ()+1);
							}
							
							else if(displayManager.displays.get(id).get(0).getDirection().matches("[dD]|[dD]own") || displayManager.displays.get(id).get(0).getDirection().matches("[uU]|[uU]p")){
								currLoc.setX(currLoc.getX()+1);
							}
						}	
						
						
					}
				}
				
				//display destroyed its own source sign or chest
				if(remove){
					displayManager.removeDisplay(removeName, removeId, currLoc.getWorld());
				}
			}
			
			//Shift Display
			/*else if(displayManager.getSignDisplayType(sign) == DisplayTypes.ShiftDisplay){
				chest = (Chest)world.getBlockAt(bx, by-1, bz).getState();//get chest below sign			
				items = chest.getInventory().getContents();
				offset = displayManager.displays.get(id).get(0).getOffset();
				ShiftDisplay d = (ShiftDisplay)displayManager.displays.get(id).get(0);
				
				for(int i = 0; i < d.getRepeatX(); i ++){
					for(int j = 0; j < d.getRepeatZ(); j++){
						for(int k = 0; k < items.length; k++){
							
						}
					}
				}
			
				//display items in chest at offset block location	
				Location currLoc = new Location(world, offset.getX(), offset.getY(), offset.getZ());
			}*/
		}
	}

}
