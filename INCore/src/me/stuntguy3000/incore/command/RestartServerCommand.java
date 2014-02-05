package me.stuntguy3000.incore.command;

import me.stuntguy3000.incore.INCore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class RestartServerCommand implements CommandExecutor {
	public INCore plugin;
	
	public RestartServerCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender.hasPermission("staff.administration.playerinformation") ) {
			for (Player t : Bukkit.getOnlinePlayers())
				plugin.Util.sendMessage(t, "&4&l>> SERVER RESTARTING IN 10 SECONDS <<", false);
			
			plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					for (Player t : Bukkit.getOnlinePlayers())
						t.kickPlayer("§6Restarting Server....");
				}
			}, 20 * 9L);
			
			plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					plugin.getServer().shutdown();
				}
			}, 20 * 10L);
		} else plugin.Util.noPerm((Player) sender);
		
		return false;
	}
}
