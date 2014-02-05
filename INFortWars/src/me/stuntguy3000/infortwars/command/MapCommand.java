package me.stuntguy3000.infortwars.command;

import me.stuntguy3000.infortwars.INFortWars;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MapCommand implements CommandExecutor {

	private INFortWars plugin;
	
	public MapCommand(INFortWars instance) {
		this.plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			plugin.INCore.Util.sendMessage((Player) sender, "The current map is §f" + plugin.friendlyify(plugin.gs.getMap().name()) + "§7.", plugin.messagePrefix);
		} else {
			sender.sendMessage("The current map is §f" + plugin.friendlyify(plugin.gs.getMap().name()) + ".");
		}
		return false;
	}

}
