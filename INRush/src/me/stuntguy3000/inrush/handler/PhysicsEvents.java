package me.stuntguy3000.inrush.handler;

import me.stuntguy3000.inrush.INRush;

import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class PhysicsEvents implements Listener {
	
	public PhysicsEvents(INRush instance) {
		
	}

	@EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
		
			event.setCancelled(true);
	}
	
	@EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
		
			event.setCancelled(true);
	}
	
	@EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
		
			event.setCancelled(true);
	}
	
	@EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
		
			event.setCancelled(true);
	}
	
	@EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
		
			event.setCancelled(true);
	}
	
	@EventHandler
    public void onBlockForm(BlockFormEvent event) {
		
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityBlockForm(EntityBlockFormEvent event) {
		
			event.setCancelled(true);
	}
	
	@EventHandler
	public void EntityChangeBlockEvent (EntityChangeBlockEvent event) {
		
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) { 
		
			event.setCancelled(true);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onExplode(EntityExplodeEvent event) { 
		for (Block block : event.blockList())
			block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
		
		event.setYield(0);
		event.setCancelled(true);
	}
}
