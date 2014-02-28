package me.stuntguy3000.incore.command;

import java.util.logging.Level;

import me.stuntguy3000.incore.INCore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class VanishCommand implements CommandExecutor {
	public INCore plugin;
	
	public VanishCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if (p.hasPermission("incore.vanish")) {
				if (plugin.vanish.contains(p.getName())) {
					plugin.vanish.remove(p.getName());
					
					for (Player t : Bukkit.getOnlinePlayers())
						t.showPlayer(p);
					
					plugin.Util.sendMessage(p, "You have unvanished.", true);
					
					plugin.vanish.remove(p.getName());
				} else {
					plugin.Util.sendMessage(p, "You have vanished.", true);
					plugin.vanish.add(p.getName());
					
					for (Player t : Bukkit.getOnlinePlayers())
						if (!t.hasPermission("staff.administration.bypass.vanish")) t.hidePlayer(p);
				}
			} else plugin.Util.noPerm(p);
		} else {
			plugin.log("Console can't vanish.", Level.WARNING);
		}
		return false;
	}

}
