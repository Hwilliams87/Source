package me.stuntguy3000.inrush.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import me.confuser.barapi.BarAPI;
import me.stuntguy3000.inrush.INRush;
import me.stuntguy3000.inrush.enums.CaptureZone;
import me.stuntguy3000.inrush.enums.Gamestate;
import me.stuntguy3000.inrush.enums.Team;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameHandler {
	
	private INRush plugin;
	public int taskID;
	
	private ItemStack LEATHER_HELMET_ROYAL;
	private ItemStack LEATHER_CHESTPLATE_ROYAL;
	private ItemStack LEATHER_LEGGINGS_ROYAL;
	private ItemStack LEATHER_BOOTS_ROYAL;
	private ItemStack LEATHER_HELMET_BANDIT;
	private ItemStack LEATHER_CHESTPLATE_BANDIT;
	private ItemStack LEATHER_LEGGINGS_BANDIT;
	private ItemStack LEATHER_BOOTS_BANDIT;
	public ItemStack COMPASS;
	
	public Boolean gameEnding = false;
	
	public GameHandler(INRush instance) {
		this.plugin = instance;
		
		LEATHER_HELMET_ROYAL = instance.INCore.Util.createItem(Material.LEATHER_HELMET, 1, 255, 165, 0, "§6Royal Armor", "§7Genuine Limited Edition");
		LEATHER_CHESTPLATE_ROYAL = instance.INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 255, 165, 0, "§6Royal Armor", "§7Genuine Limited Edition");
		LEATHER_LEGGINGS_ROYAL = instance.INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 255, 165, 0, "§6Royal Armor", "§7Genuine Limited Edition");
		LEATHER_BOOTS_ROYAL = instance.INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 255, 165, 0, "§6Royal Armor", "§7Genuine Limited Edition");
		LEATHER_HELMET_BANDIT = instance.INCore.Util.createItem(Material.LEATHER_HELMET, 1, 255, 0, 0, "§cBandit Armor", "§7Genuine Limited Edition");
		LEATHER_CHESTPLATE_BANDIT = instance.INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 255, 0, 0, "§cBandit Armor", "§7Genuine Limited Edition");
		LEATHER_LEGGINGS_BANDIT = instance.INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 255, 0, 0, "§cBandit Armor", "§7Genuine Limited Edition");
		LEATHER_BOOTS_BANDIT = instance.INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 255, 0, 0, "§cBandit Armor", "§7Genuine Limited Edition");
		COMPASS = instance.INCore.Util.createItem(Material.COMPASS, 1, "§6Zone Locator - &aRight Click to Use", "§7Use this to find all the capture zones.");
	}
	
	public void checkPlayerCount() {
		if (plugin.gs.getGameState() == Gamestate.WAITING) {
			if (Bukkit.getOnlinePlayers().length > 1) {
				countdownTimer();
				plugin.gs.setGameState(Gamestate.WARMUP);
			} else {
				for (Player p : Bukkit.getOnlinePlayers())
					plugin.INCore.Util.sendMessage(p, "&6&l" + (2 - Bukkit.getOnlinePlayers().length) + " more players required to start the game!", plugin.messagePrefix);
			}
		}
		
		if (plugin.gs.getGameState() == Gamestate.INGAME) {
			int royal = 0;
			int bandit = 0;
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (plugin.gs.getTeam(p.getName()) == Team.BANDIT) bandit = bandit + 1;
				if (plugin.gs.getTeam(p.getName()) == Team.ROYAL) royal = royal + 1;
			}
			
			if (royal == 0 || bandit == 0) {
				if (royal == 0) endGame(Team.BANDIT, "§7All the §cBandit's§7 disconnected!");
				if (bandit == 0) endGame(Team.ROYAL, "§7All the §6Royal's disconnected!");
			}
		}
	}

	public void countdownTimer() {
		for (Player p : Bukkit.getOnlinePlayers())
			plugin.INCore.Util.sendMessage(p, "Game starting in &e" + plugin.gs.getTimeLeft(), plugin.messagePrefix);
		
		int TaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (plugin.gs.getTimeLeft() == 20) {
					for (Player p : Bukkit.getOnlinePlayers()) plugin.INCore.Util.sendMessage(p, "Game starting in &e" + plugin.gs.getTimeLeft(), plugin.messagePrefix);
					plugin.gs.setBanditSpawnCount(plugin.SpawnHandler.getSpawns(Team.BANDIT));
				}
				
				if (plugin.gs.getTimeLeft() == 15) {
					for (Player p : Bukkit.getOnlinePlayers()) plugin.INCore.Util.sendMessage(p, "Game starting in &e" + plugin.gs.getTimeLeft(), plugin.messagePrefix);
					plugin.getServer().createWorld(new WorldCreator(plugin.gs.getMap().name().toLowerCase()));
				}
				
				if (plugin.gs.getTimeLeft() == 10) {
					for (Player p : Bukkit.getOnlinePlayers()) plugin.INCore.Util.sendMessage(p, "Game starting in &e" + plugin.gs.getTimeLeft(), plugin.messagePrefix);
					plugin.gs.setRoyalSpawnCount(plugin.SpawnHandler.getSpawns(Team.ROYAL));
				}
				
				if (plugin.gs.getTimeLeft() == 5)
					for (Player p : Bukkit.getOnlinePlayers()) plugin.INCore.Util.sendMessage(p, "Game starting in &e" + plugin.gs.getTimeLeft(), plugin.messagePrefix);
				
				if (plugin.gs.getTimeLeft() < 4 && plugin.gs.getTimeLeft() > 0) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						plugin.INCore.Util.sendMessage(p, "Game starting in &e" + plugin.gs.getTimeLeft(), plugin.messagePrefix);
						p.playSound(p.getLocation(), "note.pling", 2, 1);
					}
				}
					
				plugin.ScoreboardHandler.updateScoreboard();
				
				if (plugin.gs.getTimeLeft() == 0) {
					stop();
					
					if (Bukkit.getOnlinePlayers().length > 1) {
						plugin.gs.setTimeLeft(60 * 10);
						startGame();
					} else {
						for (Player p : Bukkit.getOnlinePlayers())
							plugin.INCore.Util.sendMessage(p, "&cNot enough players! &l" + (10 - Bukkit.getOnlinePlayers().length) + " players are required to start the game!", plugin.messagePrefix);
						plugin.gs.setGameState(Gamestate.WAITING);
						stop();
						plugin.gs.setTimeLeft(90);
					}
					
					plugin.ScoreboardHandler.updateScoreboard();
				} else {
					plugin.gs.setTimeLeft(plugin.gs.getTimeLeft() - 1);
				}
			}
		}, 20L, 20L);
		
		taskID = TaskID;
	}
	
	public void ingameTimer() {
		for (Player p : Bukkit.getOnlinePlayers())
			plugin.INCore.Util.sendMessage(p, "The game has started! The §6Royals §7have &b" 
		+ plugin.INCore.Util.getTime(plugin.gs.getTimeLeft()) + " §7to defend and prevent the §cBandits §7from capturing all zones!", plugin.messagePrefix);
		
		int TaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (plugin.gs.getTimeLeft() == 0)
					endGame(Team.ROYAL, "§6The §cBandit's §6failed to capture all the control zones!");
				
				plugin.ScoreboardHandler.updateScoreboard();
				plugin.gs.setTimeLeft(plugin.gs.getTimeLeft() - 1);
			}
		}, 20L, 20L);
		
		taskID = TaskID;
	}

	@SuppressWarnings("deprecation")
	public void startGame() {
		int gametime = 10;
		int count = 0;
		
		for (@SuppressWarnings("unused") Player p : Bukkit.getOnlinePlayers()) {
			count = count + 1;
			if (count == 10) {
				gametime = gametime + 5;
				count = 0;
			}
		}
		
		plugin.gs.setTimeLeft(gametime * 60);
		
		plugin.gs.setGameState(Gamestate.INGAME);
		
		Random ran = new Random();
		
		boolean Royal = true;
		
		if (ran.nextInt(100) > 50) Royal = false; else Royal = true;
		if (ran.nextInt(100) > 50) Royal = false; else Royal = true;
		if (ran.nextInt(100) > 50) Royal = false; else Royal = true;
		if (ran.nextInt(100) > 50) Royal = false; else Royal = true;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!plugin.gs.getTeams().containsKey(p.getName())) {
				Royal = !Royal;
				p.playSound(p.getLocation(), "note.pling", 2, 2);
				if (Royal) {
					plugin.INCore.Util.sendMessage(p, "§7You are a §6Royal§7.", plugin.messagePrefix);
					BarAPI.setMessage(p, "§7You are a §6Royal§7", 10);
					plugin.gs.addToTeam(p, Team.ROYAL);
					plugin.ScoreboardHandler.sb.getTeam("Royal").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§6" + p.getName());
				} else {
					plugin.INCore.Util.sendMessage(p, "§7You are a §cBandit§7.", plugin.messagePrefix);
					BarAPI.setMessage(p, "§7You are a §cBandit§7", 10);
					plugin.gs.addToTeam(p, Team.BANDIT);
					plugin.ScoreboardHandler.sb.getTeam("Bandit").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§c" + p.getName());
				}
			}
			
			giveItems(p);
			plugin.SpawnHandler.spawn(p);
		}
		
		int r = 0;
		int b = 0;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (plugin.gs.getTeam(p.getName()).equals(Team.ROYAL)) {
				r++;
			} else {
				b++;
			}
		}
		
		if (r == 0 || b == 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				plugin.INCore.Util.sendMessage(p, "§b§lImbalanced Teams! Scrambling...", plugin.messagePrefix);
			
				plugin.gs.removeFromTeam(p.getName());
				plugin.ScoreboardHandler.sb.getTeam("Royal").removePlayer(Bukkit.getOfflinePlayer(p.getName()));
				plugin.ScoreboardHandler.sb.getTeam("Bandit").removePlayer(Bukkit.getOfflinePlayer(p.getName()));
				
				Royal = !Royal;
				if (Royal) {
					plugin.INCore.Util.sendMessage(p, "§7You are a §6Royal§7.", plugin.messagePrefix);
					plugin.gs.addToTeam(p, Team.ROYAL);
					plugin.ScoreboardHandler.sb.getTeam("Royal").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§6" + p.getName());
				} else {
					plugin.INCore.Util.sendMessage(p, "§7You are a §cBandit§7.", plugin.messagePrefix);
					plugin.gs.addToTeam(p, Team.BANDIT);
					plugin.ScoreboardHandler.sb.getTeam("Bandit").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§c" + p.getName());
				}
			}		
		}

		ingameTimer();
	}

	private void stop() {
		plugin.getServer().getScheduler().cancelTask(taskID);
	}
	
	public void endGame(Team winners, String reason) {
		if (gameEnding) return;
		
		stop();
		Bukkit.getScheduler().cancelTasks(plugin);
		gameEnding = true;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (plugin.gs.getTeam(p.getName()).equals(winners)) {
				if (winners == Team.BANDIT) plugin.INCore.Coin.addCoins(p.getName(), 50);
				if (winners == Team.ROYAL) plugin.INCore.Coin.addCoins(p.getName(), 25);
			}
			
			plugin.INCore.Util.sendMessage(p, 
					(winners == Team.ROYAL ? "§7The §6Royals §7defeated the §cBandits§7!" : "§7The §cBandits §7defeated the §6Royals§7!"), plugin.messagePrefix);
			BarAPI.setMessage(p, (winners == Team.ROYAL ? "§7The §6Royals §7defeated the §cBandits§7!" : "§7The §cBandits §7defeated the §6Royals§7!"));
			plugin.INCore.Util.sendMessage(p, "§cRestarting in 10 seconds...", plugin.messagePrefix);
		}
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					String hub = "HUB_01";
					
					ByteArrayOutputStream b = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(b);
					
					try {
						out.writeUTF("Connect");
						out.writeUTF(hub);
					} catch (IOException ex) {
						
					}
					
					p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
				}
			}
		}, 20 * 10);
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				plugin.getServer().shutdown();
			}
		}, 20 * 11);
	}

	@SuppressWarnings("deprecation")
	public void giveItems(final Player p) {
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				if (!(plugin.gs.getGameState() == Gamestate.INGAME)) {
					
					ItemStack book = plugin.INCore.Util.createItem(Material.WRITTEN_BOOK, 1, "§6Information Book", "§3This book will teach you how to play!");
					BookMeta im = (BookMeta) book.getItemMeta();
					
					im.addPage("§3§lGame Information\n§9§oRush\n§0§m-------------------\n\n\n§2§lContents\n §41) §0How to Play\n");
					im.addPage("§3§lHow to Play\n§0§m-------------------\n"
							+ "§r§0There are two teams (§6Royal§0 and §cBandit§0). There are multiple capture zones, which the §cBandits §0have to capture. The §6Royals §0have to stop the capturing of these zones. The zones are marked via beacons.");
					book.setItemMeta(im);
					
					p.getInventory().setItem(0, book);
					p.getInventory().setItem(1, plugin.INCore.Util.createItem(Material.EMERALD, 1, "§b&lKit Selector &r&7(Right click to use)", "§3Right click this item to select a kit!"));
					p.getInventory().setItem(8, plugin.INCore.Util.createItem(Material.getMaterial(351), 1, (byte) 1, "&c&lBack to Hub &r&7(Right click to use)", "&3Click this item to go to the hub"));
				} else {
					if (plugin.gs.getTeam(p.getName()) == Team.ROYAL) {
						p.getInventory().setHelmet(LEATHER_HELMET_ROYAL);
						p.getInventory().setChestplate(LEATHER_CHESTPLATE_ROYAL);
						p.getInventory().setLeggings(LEATHER_LEGGINGS_ROYAL);
						p.getInventory().setBoots(LEATHER_BOOTS_ROYAL);
						
						if (plugin.CaptureZoneHandler.lockedZones) {
							p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
							p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
						}
						
						if (plugin.CaptureZoneHandler.finalZone) {
							p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0));
						}
					} else {
						p.getInventory().setHelmet(LEATHER_HELMET_BANDIT);
						p.getInventory().setChestplate(LEATHER_CHESTPLATE_BANDIT);
						p.getInventory().setLeggings(LEATHER_LEGGINGS_BANDIT);
						p.getInventory().setBoots(LEATHER_BOOTS_BANDIT);
					}
					
					plugin.KitHandler.giveItems(p);
					p.getInventory().setItem(8, COMPASS);
					
					Random generator = new Random();
					Object[] values = plugin.zones.values().toArray();
					Object randomValue = values[generator.nextInt(values.length)];
					
					CaptureZone cz = (CaptureZone) randomValue;
					
					p.setCompassTarget(cz.getLocation());
				}
			}
			
		}, 1l);
		
		p.getInventory().clear();	
	}
}