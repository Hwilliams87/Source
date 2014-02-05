package me.stuntguy3000.incore.command;

import me.stuntguy3000.incore.INCore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ListCommand implements CommandExecutor {
	public INCore plugin;
	
	public ListCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (Bukkit.getOnlinePlayers().length == 0) {
			plugin.Util.sendMessage(sender, "§7There are no players online.", true);
		} else {
			StringBuilder sb = new StringBuilder();
			
			int count = 0;
			for (Player p : Bukkit.getOnlinePlayers()) {
				count = count + 1;
				
				if (count == Bukkit.getOnlinePlayers().length) {
					sb.append("§f" + p.getDisplayName() + "§8.");
				} else {
					sb.append("§f" + p.getDisplayName() + "§8, ");
				}
			}
			
			plugin.Util.sendMessage(sender, "§7Online Players §8(§7" + Bukkit.getOnlinePlayers().length + "§8)§7: " + sb.toString(), true);
		}
		
		return false;
	}
}
