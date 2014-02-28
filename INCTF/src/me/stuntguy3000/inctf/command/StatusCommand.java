package me.stuntguy3000.inctf.command;
import me.stuntguy3000.inctf.INCTF;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StatusCommand implements CommandExecutor {

	private INCTF plugin;
	
	public StatusCommand(INCTF instance) {
		this.plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		plugin.INCore.Util.sendMessage(sender, 
				"§6Game Status: " + plugin.INCore.Util.friendlyify(plugin.gs.getGameState().name()), true);
		plugin.INCore.Util.sendMessage(sender, 
				"§cRed Captures: " + plugin.ScoreboardHandler.red.getScore(), true);
		plugin.INCore.Util.sendMessage(sender, 
				"§9Blue Captures: " + plugin.ScoreboardHandler.blue.getScore(), true);
		plugin.INCore.Util.sendMessage(sender, 
				"§7Time Left: §f" + plugin.INCore.Util.getTime(plugin.gs.getTimeLeft()), true);
		return false;
	}

}
