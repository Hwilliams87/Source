package me.stuntguy3000.incore.command;

import me.stuntguy3000.incore.INCore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TogglePhysicsCommand implements CommandExecutor {
	private INCore plugin;
	
	public TogglePhysicsCommand(INCore instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			if (p.hasPermission("staff.administration.togglephysics")) {
				if (plugin.physics) {
					plugin.Util.sendMessage(p, "Physics has been disabled.", true);
				} else {
					plugin.Util.sendMessage(p, "Physics has been enabled.", true);
				}
				
				plugin.physics = !plugin.physics;
			} else {
				plugin.Util.noPerm(p);
			}
			
			return true;
		} else {
			if (plugin.physics) {
				plugin.Util.sendMessage(sender, "Physics has been disabled.", true);
			} else {
				plugin.Util.sendMessage(sender, "Physics has been enabled.", true);
			}
			
			plugin.physics = !plugin.physics;
			return false;
		}
	}
}
