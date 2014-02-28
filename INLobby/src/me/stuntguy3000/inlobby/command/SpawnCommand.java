package me.stuntguy3000.inlobby.command;

import me.stuntguy3000.inlobby.INLobby;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
	private INLobby plugin;
	
	public SpawnCommand(INLobby instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			Location loc = plugin.INCore.Location.getLocation("Hub");
			
			if (loc.getWorld() == null) {
				if (p.hasPermission("staff.administration.bypass.nolocation"))
					plugin.INCore.Util.sendMessage(p, "&c&lError: No spawnpoint set!", true);
				else {
					p.kickPlayer(plugin.INCore.Util.c("&c&lError: Please try again later!"));
					return true;
				}
			} else p.teleport(loc);
			return true;
		} else {
			return false;
		}
	}

}
