package me.stuntguy3000.infortwars.command;

import me.stuntguy3000.infortwars.INFortWars;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StatusCommand implements CommandExecutor {

	private INFortWars plugin;
	
	public StatusCommand(INFortWars instance) {
		this.plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		plugin.INCore.Util.sendMessage(sender, 
				"§7Game Status: §f" + plugin.INCore.Util.friendlyify(plugin.gs.getGameState().name()), true);
		plugin.INCore.Util.sendMessage(sender, 
				"§eYellow Blocks Left: " + plugin.ScoreboardHandler.yellowBlocks.getScore(), true);
		plugin.INCore.Util.sendMessage(sender, 
				"§eYellow Kills: " + plugin.ScoreboardHandler.yellowKills.getScore(), true);
		plugin.INCore.Util.sendMessage(sender, 
				"§aGreen Blocks Left: " + plugin.ScoreboardHandler.greenBlocks.getScore(), true);
		plugin.INCore.Util.sendMessage(sender, 
				"§aGreen Kills: " + plugin.ScoreboardHandler.greenKills.getScore(), true);
		plugin.INCore.Util.sendMessage(sender, 
				"§7Time Left: §f" + plugin.INCore.Util.getTime(plugin.gs.getTimeLeft()), true);
		return false;
	}

}
