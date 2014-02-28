package me.stuntguy3000.incore.command;

import me.stuntguy3000.incore.INCore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class PlayerInformationCommand implements CommandExecutor {
	public INCore plugin;
	
	public PlayerInformationCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender.hasPermission("staff.administration.playerinformation") ) {
			if (args.length < 0) {
				plugin.Util.sendMessage(sender, "§7Usage: /p <name>", true);
				return false;
			}
			
			if (sender instanceof Player) {
				((Player) sender).performCommand("bminfo " + args[0]);
			} else plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "bminfo " + args[0]);
		} else plugin.Util.noPerm((Player) sender);
		
		return false;
	}

}
