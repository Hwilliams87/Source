package me.stuntguy3000.incore.event;

import java.io.File;
import java.sql.SQLException;

import me.stuntguy3000.incore.INCore;
import me.stuntguy3000.incore.ImpulseUser;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PlayerEvents implements Listener {

	private INCore plugin;
	
	public PlayerEvents(INCore instance) {
		this.plugin = instance;
	}
	
	@EventHandler
	public void AsyncPlayerPreLoginEvent(final AsyncPlayerPreLoginEvent event) {
		try {
			if (!plugin.DB.query("SELECT * FROM `in_users` WHERE `username`='" + event.getName() + "'").getResultSet().isBeforeFirst()) {
				plugin.DB.query("INSERT INTO `in_users` "
						+ "(`username`, `firstJoin`, `lastJoin`, `firstIP`, `lastIP`, `coins`, `achievements`, `stats`) "
						+ "VALUES "
						+ "('" + event.getName() + "', "
						+ "'" + System.currentTimeMillis() + "', "
						+ "'" + System.currentTimeMillis() + "', "
						+ "'" + event.getAddress().getHostAddress() + "', "
						+ "'" + event.getAddress().getHostAddress() + "', "
						+ "'0', "
						+ "'', '');");
			} else {
				plugin.DB.query("UPDATE `in_users` SET "
						+ "`lastJoin` = '" + System.currentTimeMillis() + "', "
						+ "`lastIP` = '" + event.getAddress().getHostAddress() + "' "
						+ "WHERE `username` = '" + event.getName() + "' LIMIT 1");
			}
			
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					plugin.DB.query("UPDATE `in_users` SET `status` = '" + new File("").getAbsolutePath().split("/")[5] + 
							"' WHERE `username` = '" + event.getName() + "';");
				}
			}, 20L);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		
		p.getInventory().clear();
		
		for (PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
	
		p.setHealth(20D);
		p.setFoodLevel(20);
		
		event.setJoinMessage(null);
		
		plugin.Util.sendMessage(p, "&eWelcome to the ImpulseNetwork!", true);
		plugin.Util.sendMessage(p, "&bPurchase &aVIP&b, &aCoins &b& &aMore &bat", true);
		plugin.Util.sendMessage(p, "&chttp://www.ImpulseNetwork.org/", true);
		
		plugin.flySpeed.remove(p.getName());
		
		p.setFlying(false);
		p.setFlySpeed(0.1f);
		p.setAllowFlight(false);
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					plugin.Cache.setup(p.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				PermissionUser pp = PermissionsEx.getUser(p);
				plugin.impulseusers.put(p.getName(), new ImpulseUser(p.getName(),
					pp.getGroups()[0].getPrefix(),
					pp.getGroups()[0].getSuffix(),
					pp.getGroups()[0].getPrefix().substring(0, 2),
					plugin.Achievement.getAchievements(p.getName()),
					plugin.Statistics.getStats(p.getName())));
				
				if (p.hasPermission("staff.administration.chat")) {
					p.chat("!Joined the server.");
					p.performCommand("ncp notify off");
					plugin.Util.sendMessage(p, "§6NoCheatPlus notifications disabled.", true);
					plugin.Util.sendMessage(p, "§aType /ncp notify on to enable.", true);
				}
			}
		}, 10L);
	}
	
	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		plugin.Cache.updateDB(p.getName());
		
		p.getInventory().clear();
		
		for (PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
	
		p.setHealth(20D);
		p.setFoodLevel(20);
		p.setFlying(false);
		
		event.setQuitMessage(null);
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				if (plugin.deploy)
					if (Bukkit.getOnlinePlayers().length == 0) Bukkit.shutdown();
			}
			
		}, 1l);
	}
}
