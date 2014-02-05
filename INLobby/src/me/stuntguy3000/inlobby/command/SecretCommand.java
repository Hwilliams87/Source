package me.stuntguy3000.inlobby.command;

import java.util.HashMap;
import java.util.Random;

import me.stuntguy3000.inlobby.INLobby;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Jukebox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;

public class SecretCommand implements CommandExecutor {
	private INLobby plugin;
	private HashMap<Integer, Location> locs = new HashMap<Integer, Location>();

	public SecretCommand(INLobby instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			
			int i = 1;
			while (i < 25) {
				locs.put(i, plugin.INCore.Location.getLocation("HUB_FIREWORK_" + i));
				i ++;
			}
			
			if (p.hasPermission("staff.administration.secretcommand") && p.getName().equals("stuntguy3000")) {
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() { for (Player pp : Bukkit.getOnlinePlayers()) {pp.sendMessage("");pp.sendMessage("");pp.sendMessage("                    §4§l10...");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage(""); } }
				}, 20 * 0L);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() { for (Player pp : Bukkit.getOnlinePlayers()) {pp.sendMessage("");pp.sendMessage("");pp.sendMessage("                    §4§l9...");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage(""); } }
				}, 20 * 1L);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() { for (Player pp : Bukkit.getOnlinePlayers()) {pp.sendMessage("");pp.sendMessage("");pp.sendMessage("                    §4§l8...");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage(""); } }
				}, 20 * 2L);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() { for (Player pp : Bukkit.getOnlinePlayers()) {pp.sendMessage("");pp.sendMessage("");pp.sendMessage("                    §4§l7...");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage(""); } }
				}, 20 * 3L);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() { for (Player pp : Bukkit.getOnlinePlayers()) {pp.sendMessage("");pp.sendMessage("");pp.sendMessage("                    §4§l6...");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage(""); } }
				}, 20 * 4L);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() { for (Player pp : Bukkit.getOnlinePlayers()) {pp.sendMessage("");pp.sendMessage("");pp.sendMessage("                    §4§l5...");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage(""); } }
				}, 20 * 5L);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() { for (Player pp : Bukkit.getOnlinePlayers()) {pp.sendMessage("");pp.sendMessage("");pp.sendMessage("                    §4§l4...");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage(""); } }
				}, 20 * 6L);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() { for (Player pp : Bukkit.getOnlinePlayers()) {pp.sendMessage("");pp.sendMessage("");pp.sendMessage("                    §4§l3...");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage(""); } }
				}, 20 * 7L);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() { for (Player pp : Bukkit.getOnlinePlayers()) {pp.sendMessage("");pp.sendMessage("");pp.sendMessage("                    §4§l2...");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage(""); } }
				}, 20 * 8L);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() { for (Player pp : Bukkit.getOnlinePlayers()) {pp.sendMessage("");pp.sendMessage("");pp.sendMessage("                    §4§l1...");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage(""); } }
				}, 20 * 9L);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@SuppressWarnings("deprecation")
					@Override
					public void run() { 
						for (Player pp : Bukkit.getOnlinePlayers()) {
							pp.sendMessage("");pp.sendMessage("");pp.sendMessage("       §b§k|||||||||| §a§lWELCOME TO THE IMPULSE NETWORK! §b§k||||||||||");pp.sendMessage("      §4§k|||||||||| §6§lNow enjoy a suprise Firework show! §4§k||||||||||");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");
						} 
						
						Jukebox b = (Jukebox) Bukkit.getWorld("world").getBlockAt(0, 61, 0).getState();
						b.setPlaying(Material.getMaterial(2261));
						boom();
					}
				}, 20 * 10L);
			} else {
				plugin.INCore.Util.noPerm(p);
			}
			
			return true;
		} else {
			return false;
		}
	}

	private void boom() {
		int i = 1;
		while (i < 25) {
			launch(locs.get(i), 0);
			i ++;
		}
		
		launch(locs.get(1), 4 * 20, Color.RED);
		launch(locs.get(2), 4 * 20, Color.YELLOW);
		launch(locs.get(3), 4 * 20, Color.RED);
		launch(locs.get(4), 4 * 20, Color.YELLOW);
		
		launch(locs.get(5), 7.7 * 20, Color.GREEN);
		launch(locs.get(6), 7.7 * 20, Color.PURPLE);
		launch(locs.get(7), 7.7 * 20, Color.GREEN);
		launch(locs.get(8), 7.7 * 20, Color.PURPLE);
		
		launch(locs.get(1), 11.5 * 20, Color.WHITE);
		launch(locs.get(2), 11.5 * 20, Color.TEAL);
		launch(locs.get(3), 11.5 * 20, Color.WHITE);
		launch(locs.get(4), 11.5 * 20, Color.TEAL);
		
		launch(locs.get(5), 15.5 * 20, Color.OLIVE);
		launch(locs.get(6), 15.5 * 20, Color.TEAL);
		launch(locs.get(7), 15.5 * 20, Color.OLIVE);
		launch(locs.get(8), 15.5 * 20, Color.TEAL);
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers())
					launch(p.getLocation(), 0);
				
				launch(locs.get(5), 0 * 20);
				launch(locs.get(6), 0 * 20);
				launch(locs.get(7), 0 * 20);
				launch(locs.get(8), 0 * 20);
			}
			
		}, ((long) 18.5 * 20));
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers())
					launch(p.getLocation(), 0);
				
				launch(locs.get(1), 0 * 20);
				launch(locs.get(2), 0 * 20);
				launch(locs.get(3), 0 * 20);
				launch(locs.get(4), 0 * 20);
			}
			
		}, ((long) 22.5 * 20));
		
		launch(locs.get(new Random().nextInt(locs.size()) + 1), 31.5 * 20);
		launch(locs.get(new Random().nextInt(locs.size()) + 1), 31.5 * 20);
		launch(locs.get(new Random().nextInt(locs.size()) + 1), 31.5 * 20);
		launch(locs.get(new Random().nextInt(locs.size()) + 1), 31.5 * 20);
		
		launch(locs.get(1), 33.5 * 20);
		launch(locs.get(2), 33.5 * 20);
		launch(locs.get(3), 33.5 * 20);
		launch(locs.get(4), 33.5 * 20);
		
		launch(locs.get(5), 37.5 * 20);
		launch(locs.get(6), 37.5 * 20);
		launch(locs.get(7), 37.5 * 20);
		launch(locs.get(8), 37.5 * 20);
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers())
					launch(p.getLocation(), 0);
			}
			
		}, ((long) 41.5 * 20));
		
		launch(locs.get(1), 45.5 * 20);
		launch(locs.get(3), 45.5 * 20);
		launch(locs.get(5), 45.5 * 20);
		launch(locs.get(7), 45.5 * 20);
		
		launch(locs.get(2), 48.5 * 20);
		launch(locs.get(4), 48.5 * 20);
		launch(locs.get(6), 48.5 * 20);
		launch(locs.get(8), 48.5 * 20);
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers())
					launch(p.getLocation(), 0);
			}
			
		}, ((long) 52 * 20));
		
		launch(locs.get(1), 52.8 * 20);
		launch(locs.get(2), 52.8 * 20);
		launch(locs.get(3), 52.8 * 20);
		launch(locs.get(4), 52.8 * 20);
		
		launch(locs.get(1), 59.5 * 20, Color.RED);
		launch(locs.get(2), 59.5 * 20, Color.RED);
		launch(locs.get(3), 59.5 * 20, Color.RED);
		launch(locs.get(4), 59.5 * 20, Color.RED);
		launch(locs.get(5), 59.5 * 20, Color.RED);
		launch(locs.get(6), 59.5 * 20, Color.RED);
		launch(locs.get(7), 59.5 * 20, Color.RED);
		launch(locs.get(8), 59.5 * 20, Color.RED);
		
		launch(locs.get(1), 63.5 * 20, Color.LIME);
		launch(locs.get(2), 63.5 * 20, Color.LIME);
		launch(locs.get(3), 63.5 * 20, Color.LIME);
		launch(locs.get(4), 63.5 * 20, Color.LIME);
		launch(locs.get(5), 63.5 * 20, Color.LIME);
		launch(locs.get(6), 63.5 * 20, Color.LIME);
		launch(locs.get(7), 63.5 * 20, Color.LIME);
		launch(locs.get(8), 63.5 * 20, Color.LIME);

		launch(locs.get(1), 67.5 * 20, Color.AQUA);
		launch(locs.get(2), 67.5 * 20, Color.AQUA);
		launch(locs.get(3), 67.5 * 20, Color.AQUA);
		launch(locs.get(4), 67.5 * 20, Color.AQUA);
		launch(locs.get(5), 67.5 * 20, Color.AQUA);
		launch(locs.get(6), 67.5 * 20, Color.AQUA);
		launch(locs.get(7), 67.5 * 20, Color.AQUA);
		launch(locs.get(8), 67.5 * 20, Color.AQUA);
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					Firework f = plugin.INCore.Util.spawnFirework(p.getLocation(), 2);
					f.setPassenger(p);
				}
			}
			
		}, ((long) 70.5 * 20));
		
		launch(locs.get(1), 75.2 * 20);
		launch(locs.get(2), 74.3 * 20);
		launch(locs.get(3), 74.4 * 20);
		launch(locs.get(4), 74.5 * 20);
		launch(locs.get(5), 74.6 * 20);
		launch(locs.get(6), 74.7 * 20);
		launch(locs.get(7), 74.8 * 20);
		launch(locs.get(8), 74.9 * 20);
		
		launch(locs.get(1), 78 * 20, Color.PURPLE);
		launch(locs.get(2), 78 * 20, Color.PURPLE);
		launch(locs.get(3), 78 * 20, Color.PURPLE);
		launch(locs.get(4), 78 * 20, Color.PURPLE);
		
		launch(locs.get(5), 81.5 * 20, Color.YELLOW);
		launch(locs.get(6), 81.5 * 20, Color.YELLOW);
		launch(locs.get(7), 81.5 * 20, Color.YELLOW);
		launch(locs.get(8), 81.5 * 20, Color.YELLOW);
		
		launch(locs.get(5), 89 * 20, Color.GREEN);
		launch(locs.get(6), 89 * 20, Color.GREEN);
		launch(locs.get(7), 89 * 20, Color.GREEN);
		launch(locs.get(8), 89 * 20, Color.GREEN);
		
		launch(locs.get(1), 93 * 20);
		launch(locs.get(2), 93 * 20);
		launch(locs.get(3), 93 * 20);
		launch(locs.get(4), 93 * 20);
		launch(locs.get(5), 93 * 20);
		launch(locs.get(6), 93 * 20);
		launch(locs.get(7), 93 * 20);
		launch(locs.get(8), 93 * 20);
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player pp : Bukkit.getOnlinePlayers()) {
					pp.sendMessage("");pp.sendMessage("");pp.sendMessage("    §b§k|||||||||| §a§lThe ImpulseNetwork is OFFICIALLY OPEN! Hope you enjoyed the show! §b§k||||||||||");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");pp.sendMessage("");
					Firework f = plugin.INCore.Util.spawnFirework(pp.getLocation(), 2);
					f.setPassenger(pp);
				}
				
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
						
						i = 1;
						while (i < 25) {
							launch(locs.get(i), 0);
							i ++;
						}
					}
				});
			}
			
		}, ((long) 95 * 20));
	}
	
	private void launch(final Location l, double delay, final Color c) {
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				plugin.INCore.Util.spawnFirework(l, c);
			}
		}, (long) delay);
	}
	
	private void launch(final Location l, double delay) {
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				plugin.INCore.Util.spawnFirework(l);
			}
		}, (long) delay);
	}
}
