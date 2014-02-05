package me.stuntguy3000.incore.command;

import me.stuntguy3000.incore.INCore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class FlyCommand implements CommandExecutor {
	public INCore plugin;
	
	public FlyCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			if (p.hasPermission("staff.administration.fly")) {
				if (plugin.flySpeed.contains(p.getName())) {
					plugin.flySpeed.remove(p.getName());
					p.setFlying(false);
					p.setFlySpeed(0.1f);
					p.setAllowFlight(false);
					plugin.Util.sendMessage(p, "§6Flymode disabled.", true);
				} else {
					if (args.length == 0) {
						plugin.Util.sendMessage(p, "§6Flymode enabled at default speed.", true);
						p.setAllowFlight(true);
						p.setFlying(true);
						p.setAllowFlight(true);
						plugin.flySpeed.add(p.getName());
					} else {
						String posnum = args[0];
						
						int speed = 1;
						
						try {
							speed = Integer.parseInt(posnum);
							
							if (speed < 1 || speed > 10) {
								plugin.Util.sendMessage(p, "§7Usage: /fly [speed (1-10)]", true);
							} else {
								if (speed == 1) p.setFlySpeed(0.1f);
								if (speed == 2) p.setFlySpeed(0.2f);
								if (speed == 3) p.setFlySpeed(0.3f);
								if (speed == 4) p.setFlySpeed(0.4f);
								if (speed == 5) p.setFlySpeed(0.5f);
								if (speed == 6) p.setFlySpeed(0.6f);
								if (speed == 7) p.setFlySpeed(0.7f);
								if (speed == 8) p.setFlySpeed(0.8f);
								if (speed == 9) p.setFlySpeed(0.9f);
								if (speed == 10) p.setFlySpeed(1.0f);
								
								plugin.Util.sendMessage(p, "§6Flymode enabled at speed " + speed + ".", true);
								p.setAllowFlight(true);
								p.setFlying(true);
								p.setAllowFlight(true);
								plugin.flySpeed.add(p.getName());
							}
						} catch (NumberFormatException ex) {
							plugin.Util.sendMessage(p, "§7Usage: /fly [speed (1-10)]", true);
							return true;
						}
					}
				}
			} else plugin.Util.noPerm(p);
		}
		
		return false;
	}

}
