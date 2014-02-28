package me.stuntguy3000.incore.command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.stuntguy3000.incore.INCore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class HubCommand implements CommandExecutor {
	public INCore plugin;
	
	public HubCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			String hub = "HUB_01";
			
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			
			try {
				out.writeUTF("Connect");
				out.writeUTF(hub);
			} catch (IOException ex) {
				
			}
			
			((Player) sender).sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
		}
		return true;
	}

}
