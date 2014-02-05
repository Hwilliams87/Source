package me.stuntguy3000.incore.command;

import me.stuntguy3000.incore.INCore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SetLocationCommand implements CommandExecutor {
	public INCore plugin;
	
	public SetLocationCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			if (p.hasPermission("staff.administration.setlocation") ) {
				if (args.length == 0) {
					plugin.Util.sendMessage(p, "&7Usage: /setlocation <name>", true);
				} else {
					plugin.Util.sendMessage(p, "&7Location set.", true);
					plugin.Location.setLocation(args[0], p.getLocation());
				}
			} else plugin.Util.noPerm(p);
			
			return true;
		} else {
			return false;
		}
	}

}
