package me.stuntguy3000.incore.command;

import me.stuntguy3000.incore.INCore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class TeleportHereCommand implements CommandExecutor {
	public INCore plugin;
	
	public TeleportHereCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			if (p.hasPermission("staff.administration.teleporthere") ) {
				if (args.length == 0) {
					plugin.Util.sendMessage(p, "&7Usage: /tphere [player]", true);
					return true;
				}
				
				if (args.length == 1) {
					Player t = Bukkit.getPlayer(args[0]);
					
					if (t == null) {
						plugin.Util.sendMessage(p, "&cPlayer not found.", true);
					} else {
						t.teleport(p);
						plugin.Util.sendMessage(p, "&6Summoning " + t.getName(), true);
					}
					
					return true;
				}
				
				plugin.Util.sendMessage(p, "&7Usage: /tp [player|x] <y> <y>", true);
			} else plugin.Util.noPerm(p);
			
			return true;
		} else {
			return false;
		}
	}

}
