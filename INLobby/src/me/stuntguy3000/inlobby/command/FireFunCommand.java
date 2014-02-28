package me.stuntguy3000.inlobby.command;

import me.stuntguy3000.inlobby.INLobby;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FireFunCommand implements CommandExecutor {
	public enum FireworkLocation {
		SIEGE,
		PARTY,
		CTF,
		RUSH
	}
	
	private INLobby plugin;
	
	public FireFunCommand(INLobby instance) {
		this.plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			if (p.hasPermission("staff.administration.firefun")) {
				if (args.length == 0) {
					plugin.INCore.Util.sendMessage(p, "Usage: /firefun <siege/party/ctf/rush>", true);
					return true;
				}
				
				String input = args[0];
				if (input.equalsIgnoreCase("siege")) {
					launch(FireworkLocation.SIEGE);
				} else if (input.equalsIgnoreCase("party")) {
					launch(FireworkLocation.PARTY);
				} else if (input.equalsIgnoreCase("ctf")) {
					launch(FireworkLocation.CTF);
				} else if (input.equalsIgnoreCase("rush")) {
					launch(FireworkLocation.RUSH);
				} else plugin.INCore.Util.sendMessage(p, "Usage: /firefun <siege/party/ctf/rush>", true);
			} else {
				plugin.INCore.Util.noPerm(p);
			}
			
			return true;
		} else {
			return false;
		}
	}

	private void launch(FireworkLocation type) {
		if (type == FireworkLocation.PARTY) {
			Bukkit.getScheduler().runTask(plugin, new Runnable() {
				@Override
				public void run() {
					int i = 0;
					int delay = 5;
					
					int x = -15;
					int y = 68;
					int z1 = 6;
					int z2 = -5;
					final World w = Bukkit.getWorld("world");
					
					Type fwType = Type.BURST;
					
					Color c = Color.ORANGE;
					Color f = Color.PURPLE;
					
					final FireworkEffect effect = FireworkEffect.builder().withFade(f).withColor(c).with(fwType).withTrail().build();
					
					while (i < 14) {
						i ++;
						final Location loc1 = new Location(w, x, y, z1);
						final Location loc2 = new Location(w, x, y, z2);
						x = x - 2;
						delay = delay + 5;
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

							@Override
							public void run() {
								try {
									plugin.INCore.FireworkEffectPlayer.playFirework(w, loc1, effect);
									plugin.INCore.FireworkEffectPlayer.playFirework(w, loc2, effect);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
						}, delay);
						
					}
				}
			});
		}
	}

}
