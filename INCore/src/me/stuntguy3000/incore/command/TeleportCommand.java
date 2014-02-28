package me.stuntguy3000.incore.command;

import me.stuntguy3000.incore.INCore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class TeleportCommand implements CommandExecutor {
	public INCore plugin;
	
	public TeleportCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			if (p.hasPermission("staff.administration.teleport") ) {
				if (args.length == 0) {
					plugin.Util.sendMessage(p, "&7Usage: /tp [player|x] <y> <y>", true);
					return true;
				}
				
				if (args.length == 1) {
					Player t = Bukkit.getPlayer(args[0]);
					
					if (t == null) {
						plugin.Util.sendMessage(p, "&cPlayer not found.", true);
					} else {
						p.teleport(t);
						plugin.Util.sendMessage(p, "&6Teleporting to " + t.getName(), true);
					}
					
					return true;
				}
				
				if (args.length == 3) {
					String xS = args[0];
					String yS = args[1];
					String zS = args[2];
					
					try {
						double x = Double.parseDouble(xS);
						double y = Double.parseDouble(yS);
						double z = Double.parseDouble(zS);
						
						p.teleport(new Location(p.getWorld(), x, y, z));
					} catch (NumberFormatException ex) {
						plugin.Util.sendMessage(p, "&cInvalid coordinates.", true);
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
