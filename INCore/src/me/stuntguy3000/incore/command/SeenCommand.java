package me.stuntguy3000.incore.command;

import java.sql.ResultSet;
import java.sql.Timestamp;

import me.stuntguy3000.incore.INCore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SeenCommand implements CommandExecutor {
	public INCore plugin;
	
	public SeenCommand(INCore instance) {
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			if (p.hasPermission("staff.administration.seen") ) {
				if (args.length == 0) {
					plugin.Util.sendMessage(p, "&7Usage: /seen <name>", true);
				} else {
					plugin.Util.sendMessage(p, "&7Fetching information...", true);
					
					ResultSet userData = plugin.DB.query("SELECT * FROM `in_users` WHERE `username`='" + args[0] + "'").getResultSet();
					
					try {
						if (userData.isBeforeFirst()) {
							while (userData.next()) {
								plugin.Util.sendMessage(p, "&6Server Status: §e" + userData.getString("status"), true);
								plugin.Util.sendMessage(p, "&6Last IP: §e" + userData.getString("lastIP"), true);
								
								Timestamp last = new Timestamp(Long.parseLong(userData.getString("lastJoin")));
								Timestamp now = new Timestamp(System.currentTimeMillis());
								
								plugin.Util.sendMessage(p, "&6Last Seen: §e" + last.toString(), true);
								plugin.Util.sendMessage(p, "&7Now: §f" + now.toString(), true);
							}
						} else plugin.Util.sendMessage(p, "&cUser not found.", true);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} else plugin.Util.noPerm(p);
			
			return true;
		} else {
			return false;
		}
	}
}
