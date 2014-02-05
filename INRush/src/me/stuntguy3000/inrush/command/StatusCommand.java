package me.stuntguy3000.inrush.command;

import me.stuntguy3000.inrush.INRush;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StatusCommand implements CommandExecutor {

	private INRush plugin;
	
	public StatusCommand(INRush instance) {
		this.plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		plugin.INCore.Util.sendMessage(sender, 
				"§7Game Status: §f" + plugin.INCore.Util.friendlyify(plugin.gs.getGameState().name()), true);
		for (String z : plugin.ScoreboardHandler.zones.keySet()) {
			plugin.INCore.Util.sendMessage(sender, 
					"§7" + plugin.INCore.Util.friendlyify(z) +": §f" + plugin.INCore.Util.friendlyify(plugin.zones.get(z).getStatus().name()), true);
		}
		plugin.INCore.Util.sendMessage(sender, 
				"§7Time Left: §f" + plugin.INCore.Util.getTime(plugin.gs.getTimeLeft()), true);
		return false;
	}

}
