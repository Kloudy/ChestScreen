package kloudy.mc.chestscreen.events;
import kloudy.mc.chestscreen.BlockChangeThread;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneEvent implements Listener{

	@EventHandler
	public void RedstoneListener(BlockRedstoneEvent event){		
		@SuppressWarnings("unused")
		BlockChangeThread thread = new BlockChangeThread(event);
	}
}
