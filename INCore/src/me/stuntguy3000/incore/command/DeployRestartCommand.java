package me.stuntguy3000.incore.command;

import me.stuntguy3000.incore.INCore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class DeployRestartCommand implements CommandExecutor {
	public INCore plugin;
	
	public DeployRestartCommand(INCore instance) {
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender.hasPermission("staff.administration.deployrrestart")) {
			plugin.deploy = true;
			
			sender.sendMessage("§4§l>> SERVER WILL RESTART WHEN ALL PLAYERS LEAVE <<");
			if (Bukkit.getOnlinePlayers().length == 0) {
				Bukkit.shutdown();
			}
		} else plugin.Util.noPerm((Player) sender);
		
		return false;
	}

}
