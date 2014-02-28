package me.stuntguy3000.inlobby.command;

import me.stuntguy3000.inlobby.INLobby;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckpointCommand implements CommandExecutor {
	private INLobby plugin;
	
	public CheckpointCommand(INLobby instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			if (plugin.checkpoints.containsKey(p.getName())) {
				plugin.INCore.Util.sendMessage(p, "&7Taken to your most recent checkpoint.", true);
				
				p.teleport(plugin.checkpoints.get(p.getName()));
			} else {
				plugin.INCore.Util.sendMessage(p, "&cYou have not set a checkpoint!", true);
			}
			
			return true;
		} else {
			return false;
		}
	}

}
