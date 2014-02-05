package me.stuntguy3000.incore.event;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import me.stuntguy3000.incore.INCore;
import me.stuntguy3000.incore.ImpulseUser;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.Plugin;

public class FilterEvents implements Listener {

	private INCore plugin;
	
	private HashMap<String, Integer> lastSent = new HashMap<String, Integer>();
	
	public FilterEvents(INCore instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void commandPreProcessEvent(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		
		if (event.getMessage().toLowerCase().contains("/pl") ||
			event.getMessage().toLowerCase().contains("/plugins") ||
			event.getMessage().toLowerCase().contains("/?")) {
			event.setCancelled(true);
			
			if (p.hasPermission("incore.plugins")) {
				for (Plugin pp : Bukkit.getPluginManager().getPlugins())
					plugin.Util.sendMessage(p, "§b" + pp.getName() + " §7v§b" + pp.getDescription().getVersion(), true);
				
				YamlConfiguration build = new YamlConfiguration();
				try {
					build.load(new File(plugin.getDataFolder() + "/build.yml"));
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}
				
				plugin.Util.sendMessage(p, "§6INCore build #" + build.getInt("version"), true);
			} else plugin.Util.noPerm(p);
		} else if (event.getMessage().toLowerCase().startsWith("/me ")) {
			event.setCancelled(true);
			plugin.Util.noPerm(p);
		} else if (event.getMessage().toLowerCase().equals("/me")) {
			event.setCancelled(true);
			plugin.Util.noPerm(p);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerChatEvent (AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		
		if (event.isCancelled())
			return;
		
		event.setMessage(event.getMessage().replace("%", "%%"));
		
		ImpulseUser u = plugin.impulseusers.get(p.getName());
		
		if (event.getMessage().startsWith("!") && p.hasPermission("staff.administration.chat")) {
			if (event.getMessage().equals("!")) {
				event.setMessage("I am a noob.");
			} else event.setMessage(event.getMessage().replaceFirst("!", ""));
			
			event.getRecipients().clear();
			
			
			for (Player t : Bukkit.getOnlinePlayers())
				if (t.hasPermission("staff.administration.chat")) event.getRecipients().add(t);
			
			event.setFormat("§8[§2Staff§8] " + u.getChatPrefix() + "§7" + u.getColour() + p.getDisplayName() + "§8: §7" + u.getSuffix() + event.getMessage());
			return;
		}
		
		if (event.getMessage().startsWith("**") && p.hasPermission("staff.administration.broadcast")) {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream datastream = new DataOutputStream(bytes);
            
            try {
            	if (event.getMessage().startsWith("** ")) {
                	event.setMessage(event.getMessage().substring(3));
            	} else event.setMessage(event.getMessage().substring(2));
            	
            	datastream.writeUTF("§8[§e§lBroadcast§r§8] §f" + event.getMessage());
                p.sendPluginMessage(plugin, "IPNKBroadcast", bytes.toByteArray());
            } catch (IOException e) {
            }
            
            event.setCancelled(true);
            return;
		}
		
		event.setFormat(u.getChatPrefix() + "§7" + u.getColour() + p.getDisplayName() + "§8: §7" + u.getSuffix() + event.getMessage());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLightningStrike(LightningStrikeEvent event) {
		event.setCancelled(true);
    }
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onThunderChange(ThunderChangeEvent event) {
		event.setCancelled(true);
    }
	 
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}
}
