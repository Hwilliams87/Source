package me.stuntguy3000.inrush.command;

import me.stuntguy3000.inrush.INRush;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClassCommand implements CommandExecutor {

	private INRush plugin;
	
	public ClassCommand(INRush instance) {
		this.plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			plugin.KitHandler.openKit((Player) sender);
			plugin.INCore.Util.sendMessage((Player) sender, "§3If you change class, it will be applied on respawn.", plugin.messagePrefix);
		} else {
			sender.sendMessage("You are not a player, dumbo.");
		}
		return false;
	}

}
