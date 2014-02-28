package me.stuntguy3000.incore.command;

import java.util.logging.Level;

import me.stuntguy3000.incore.INCore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class TokensCommand implements CommandExecutor {
	public INCore plugin;
	
	public TokensCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			int coins = Integer.parseInt(plugin.Coin.getCoins(p.getName()));
			if (coins > 1) {
				plugin.Util.sendMessage(p, "§aYou have §b" + coins + " Coins§a.", "§8[§bCoin§8] §a");
			} else plugin.Util.sendMessage(p, "§aYou have §b" + coins + " Coins§a.", "§8[§bCoin§8] §a");
			
			return true;
		} else {
			plugin.log("Console has infinite coins! (Just kidding...)", Level.WARNING);
			return false;
		}
	}

}
