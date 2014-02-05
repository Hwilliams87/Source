package me.stuntguy3000.inlobby.command;

import me.stuntguy3000.inlobby.INLobby;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

public class MakeShopCommand implements CommandExecutor {
	private INLobby plugin;
	
	public MakeShopCommand(INLobby instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if (p.hasPermission("staff.administration.makeshop")) {
				if (args.length == 0) {
					plugin.INCore.Util.sendMessage(p, "Usage: /makeshop <name>", true);
					return true;
				}
				
				Villager shop = p.getWorld().spawn(p.getLocation(), Villager.class);
				shop.setAdult();
				shop.setAgeLock(true);
				shop.setCanPickupItems(false);
				shop.setBreed(false);
				shop.setCustomName("§aCoin Shop §8» §6" + args[0]);
				shop.setCustomNameVisible(true);
				shop.setProfession(Profession.LIBRARIAN);
			} else {
				plugin.INCore.Util.noPerm(p);
			}
			
			return true;
		} else {
			return false;
		}
	}

}
