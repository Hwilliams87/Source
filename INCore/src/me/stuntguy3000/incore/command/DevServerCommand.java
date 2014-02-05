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

public class DevServerCommand implements CommandExecutor {
	public INCore plugin;
	
	public DevServerCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			if (p.hasPermission("staff.administration.devserver")) {
				
				String hub = "ORB_01";
				
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);
				
				try {
					out.writeUTF("Connect");
					out.writeUTF(hub);
				} catch (IOException ex) {
					
				}
				
				p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
				
			} else plugin.Util.noPerm(p);
		}
		
		return true;
	}

}
