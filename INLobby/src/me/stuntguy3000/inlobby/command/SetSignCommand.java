package me.stuntguy3000.inlobby.command;

import me.stuntguy3000.inlobby.INLobby;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSignCommand implements CommandExecutor {
	private INLobby plugin;
	
	public SetSignCommand(INLobby instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			if (p.hasPermission("staff.administration.setsign")) {
				if (args.length == 0) {
					plugin.INCore.Util.sendMessage(p, "Usage: /setsign <id>", true);
					return true;
				}
				
				plugin.setSign.put(p.getName(), args[0]);
				plugin.INCore.Util.sendMessage(p, "Please break the designated sign.", true);
			} else {
				plugin.INCore.Util.noPerm(p);
			}
			
			return true;
		} else {
			return false;
		}
	}
}
