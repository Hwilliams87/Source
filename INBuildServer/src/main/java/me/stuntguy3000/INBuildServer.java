package me.stuntguy3000;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class INBuildServer extends JavaPlugin implements Listener {
	
	public List<String> allowed = new ArrayList<String>();
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		allowed.add("stuntguy3000");
		allowed.add("PixelSwiftYT");
		allowed.add("SubZeroExtabyte");
		allowed.add("DavisA20");
		allowed.add("unknown2nd");
		allowed.add("wazbat");
		allowed.add("BaconBlaster");
		allowed.add("R3DY246");
		allowed.add("kluberge");
		allowed.add("MadJack69");
	}
	
	@EventHandler
	public void onJoin(final AsyncPlayerPreLoginEvent event) {
		System.out.println("***** Attempting to join: " + event.getName() + " *****");
		if (!canJoin(event.getName())) {
			System.out.println("***** DENIED ACCESS: " + event.getName() + " *****");
		} else {
			System.out.println("***** Granted Access for " + event.getName() + " *****");
		}
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				p.sendMessage("§6§kAAAAA §aWelcome to the ImpulseBuild Server §6§kAAAAA");
				p.sendMessage("§cWarning: §n§lEverything§r§c is logged. Abuse your powers and you will be banned.");
			}
		}, 10L);
	}
	
	public boolean canJoin(String username) {
		for (String name : allowed) {
			if (name.equalsIgnoreCase(username)) return true;
		}
		return false;
	}
}
