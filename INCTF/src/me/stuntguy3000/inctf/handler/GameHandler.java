package me.stuntguy3000.inctf.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.confuser.barapi.BarAPI;
import me.stuntguy3000.inctf.INCTF;
import me.stuntguy3000.inctf.enums.Gamestate;
import me.stuntguy3000.inctf.enums.Team;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class GameHandler {
	
	private INCTF plugin;
	
	public int taskID;
	public int count;
	
	private ItemStack RED_ARMOR_HELMET;
	private ItemStack RED_ARMOR_CHESTPLATE;
	private ItemStack RED_ARMOR_LEGGINGS;
	private ItemStack RED_ARMOR_BOOTS;
	private ItemStack BLUE_ARMOR_HELMET;
	private ItemStack BLUE_ARMOR_CHESTPLATE;
	private ItemStack BLUE_ARMOR_LEGGINGS;
	private ItemStack BLUE_ARMOR_BOOTS;
	
	private ItemStack SWORD;
	private ItemStack BOW;
	private ItemStack ARROW;
	private ItemStack HEALTHPOTION;
	private ItemStack SPEEDPOTION;
	
	public GameHandler(INCTF instance) {
		this.plugin = instance;
	}
	
	public void checkPlayerCount() {
		if (plugin.gs.getGameState() == Gamestate.WAITING) {
			if (Bukkit.getOnlinePlayers().length >= plugin.gs.getNeeded()) {
				countdownTimer();
				plugin.gs.setGameState(Gamestate.WARMUP);
			} else {
				if (plugin.gs.getNeeded() - Bukkit.getOnlinePlayers().length == 1) {
					for (Player p : Bukkit.getOnlinePlayers())
						plugin.INCore.Util.sendMessage(p, "&c" + (plugin.gs.getNeeded() - Bukkit.getOnlinePlayers().length) + " more player required to start the game!", plugin.messagePrefix);
				} else {
					for (Player p : Bukkit.getOnlinePlayers())
						plugin.INCore.Util.sendMessage(p, "&c" + (plugin.gs.getNeeded() - Bukkit.getOnlinePlayers().length) + " more players required to start the game!", plugin.messagePrefix);
				}
			}
		}
		
		if (plugin.gs.getGameState() == Gamestate.INGAME) {
			int red = 0;
			int blue = 0;
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (plugin.gs.getTeam(p.getName()).equals(Team.RED)) {
					red = red + 1;
				} else if (plugin.gs.getTeam(p.getName()).equals(Team.BLUE)) {
					blue = blue + 1;
				}
			}
			
			if (red == 0 || blue == 0) {
				endGame();
				stop();
			}
		}
	}

	public void countdownTimer() {
		for (Player p : Bukkit.getOnlinePlayers())
			plugin.INCore.Util.sendMessage(p, "Game starting in &e" + plugin.gs.getTimeLeft(), plugin.messagePrefix);
		
		count = 0;
		
		int TaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (plugin.gs.getTimeLeft() == 20) {
					RED_ARMOR_HELMET = plugin.INCore.Util.createItem(Material.LEATHER_HELMET, 1, 255, 0, 0, "§cHelmet", "§7Armor");
					RED_ARMOR_CHESTPLATE = plugin.INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 255, 0, 0, "§cChestplate", "§7Armor");
					RED_ARMOR_LEGGINGS = plugin.INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 255, 0, 0, "§cLeggings", "§7Armor");
					RED_ARMOR_BOOTS = plugin.INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 255, 0, 0, "§cBoots", "§7Armor");
					BLUE_ARMOR_HELMET = plugin.INCore.Util.createItem(Material.LEATHER_HELMET, 1, 0, 0, 255, "§9Helmet", "§7Armor");
					BLUE_ARMOR_CHESTPLATE = plugin.INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 0, 0, 255, "§9Chestplate", "§7Armor");
					BLUE_ARMOR_LEGGINGS = plugin.INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 0, 0, 255, "§9Leggings", "§7Armor");
					BLUE_ARMOR_BOOTS = plugin.INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 0, 0, 255, "§9Boots", "§7Armor");
					SWORD = new ItemStack(Material.STONE_SWORD, 1);
					BOW = new ItemStack(Material.BOW, 1);
					ARROW = new ItemStack(Material.ARROW, 1);
					HEALTHPOTION = new ItemStack(Material.POTION, 1);
					HEALTHPOTION.setDurability((short) 16453);
					SPEEDPOTION = new ItemStack(Material.POTION, 1);
					SPEEDPOTION.setDurability((short) 16386);
					
					BOW.addEnchantment(Enchantment.ARROW_INFINITE, 1);
					
					plugin.redFlag = plugin.INCore.Location.getLocation("CAPTURETHEFLAG_" + plugin.gs.getMap().name() + "_RED_FLAG");
					plugin.blueFlag = plugin.INCore.Location.getLocation("CAPTURETHEFLAG_" + plugin.gs.getMap().name() + "_BLUE_FLAG");
					
					for (Player p : Bukkit.getOnlinePlayers())
						plugin.INCore.Util.sendMessage(p, "§aMap: §2" + plugin.INCore.Util.friendlyify(plugin.gs.getMap().name()), plugin.messagePrefix);
					
				}
				
				if (plugin.gs.getTimeLeft() < 6 && plugin.gs.getTimeLeft() > 0) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						plugin.INCore.Util.sendMessage(p, "Game starting in &e" + plugin.gs.getTimeLeft(), plugin.messagePrefix);
						p.playSound(p.getLocation(), "note.pling", 2, 1);
					}
				}
					
				if (plugin.gs.getTimeLeft() == 0) {
					stop();
					
					if (Bukkit.getOnlinePlayers().length >= plugin.gs.getNeeded()) {
						plugin.gs.setTimeLeft(60 * 10);
						startGame();
					} else {
						for (Player p : Bukkit.getOnlinePlayers())
							plugin.INCore.Util.sendMessage(p, "&cNot enough players! &l" + (plugin.gs.getNeeded() - Bukkit.getOnlinePlayers().length) + " players are required to start the game!", plugin.messagePrefix);
						
						plugin.gs.setGameState(Gamestate.WAITING);
						plugin.gs.setTimeLeft(90);
					}
				}
				
				plugin.ScoreboardHandler.updateScoreboard();
				plugin.gs.setTimeLeft(plugin.gs.getTimeLeft() - 1);
			}
		}, 20L, 20L);
		
		taskID = TaskID;
	}
	
	public void ingameTimer() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			plugin.INCore.Util.sendMessage(p, "§7The game has started! Your goal is to capture the other teams flag §b" + plugin.gs.getMaxScore() + " §7times.", plugin.messagePrefix);
			p.setLevel(0);
		}
		
		count = 0;
		
		int TaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				plugin.GameHandler.checkPlayerCount();
				
				count = count + 1;
				
				if (plugin.gs.getTimeLeft() == 0) {
					stop();
					endGame();
				} else {
					plugin.ScoreboardHandler.updateScoreboard();
					plugin.gs.setTimeLeft(plugin.gs.getTimeLeft() - 1);
				}
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
		
		boolean Red = true;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!plugin.gs.getTeams().containsKey(p.getName())) {
				Red = !Red;
				p.playSound(p.getLocation(), "note.pling", 2, 2);
				if (Red) {
					plugin.INCore.Util.sendMessage(p, "§7You are on the §cRed§7 team.", plugin.messagePrefix);
					BarAPI.setMessage(p, "§7You are a §cRed§7 team", 10);
					plugin.gs.addToTeam(p, Team.RED);
					plugin.ScoreboardHandler.sb.getTeam("Red").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§c" + p.getName());
				} else {
					plugin.INCore.Util.sendMessage(p, "§7You are on the §9Blue§7 team.", plugin.messagePrefix);
					BarAPI.setMessage(p, "§7You are a §9Blue§7 team", 10);
					plugin.gs.addToTeam(p, Team.BLUE);
					plugin.ScoreboardHandler.sb.getTeam("Blue").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§9" + p.getName());
				}
			}
			
			p.playSound(p.getLocation(), "note.pling", 2, 2);
			
			giveItems(p);
		}
		
		int r = 0;
		int b = 0;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (plugin.gs.getTeams().containsKey(p.getName())) {
				if (plugin.gs.getTeam(p.getName()).equals(Team.RED)) {
					r++;
				} else {
					b++;
				}
			}
		}
		
		if (r == 0 || b == 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				plugin.INCore.Util.sendMessage(p, "§b§lImbalanced Teams! Scrambling...", plugin.messagePrefix);
			
				plugin.gs.removeFromTeam(p.getName());
				plugin.ScoreboardHandler.sb.getTeam("Red").removePlayer(Bukkit.getOfflinePlayer(p.getName()));
				plugin.ScoreboardHandler.sb.getTeam("Blue").removePlayer(Bukkit.getOfflinePlayer(p.getName()));
				
				Red = !Red;
				if (Red) {
					plugin.INCore.Util.sendMessage(p, "§7You are a §cRed§7 team.", plugin.messagePrefix);
					plugin.gs.addToTeam(p, Team.RED);
					plugin.ScoreboardHandler.sb.getTeam("Red").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§c" + p.getName());
				} else {
					plugin.INCore.Util.sendMessage(p, "§7You are a §9Blue§7 team.", plugin.messagePrefix);
					plugin.gs.addToTeam(p, Team.BLUE);
					plugin.ScoreboardHandler.sb.getTeam("Blue").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§9" + p.getName());
				}
			}		
		}
		
		for (Player p : Bukkit.getOnlinePlayers())
			p.teleport(plugin.INCore.Location.getLocation("CaptureTheFlag_" + plugin.gs.getMap().name() + "_" + plugin.gs.getTeam(p.getName()) + "_Spawn"));
		
		ingameTimer();
	}

	@SuppressWarnings("deprecation")
	public void giveItems(final Player p) {
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				p.getInventory().clear();
				
				if (plugin.gs.getGameState() == Gamestate.INGAME) {
					ItemStack usword = SWORD;
					
					if (p.hasPermission("game.ctf.sharp1")) {
						usword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
					}
					
					if (p.hasPermission("game.ctf.knockback")) {
						usword.addEnchantment(Enchantment.KNOCKBACK, 1);
					}
					
					p.getInventory().addItem(usword);
					p.getInventory().addItem(BOW);
					p.getInventory().addItem(ARROW);
					p.getInventory().addItem(HEALTHPOTION);
					
					if (p.hasPermission("game.ctf.knockback")) {
						p.getInventory().addItem(SPEEDPOTION);
					}
					
					if (plugin.gs.getTeam(p.getName()).equals(Team.RED)) {
						p.getInventory().setHelmet(RED_ARMOR_HELMET);
						p.getInventory().setChestplate(RED_ARMOR_CHESTPLATE);
						p.getInventory().setLeggings(RED_ARMOR_LEGGINGS);
						p.getInventory().setBoots(RED_ARMOR_BOOTS);
					} else {
						p.getInventory().setHelmet(BLUE_ARMOR_HELMET);
						p.getInventory().setChestplate(BLUE_ARMOR_CHESTPLATE);
						p.getInventory().setLeggings(BLUE_ARMOR_LEGGINGS);
						p.getInventory().setBoots(BLUE_ARMOR_BOOTS);
					}
				} else {
					ItemStack book = plugin.INCore.Util.createItem(Material.WRITTEN_BOOK, 1, "§6Information Book", "§3This book will teach you how to play!");
					BookMeta im = (BookMeta) book.getItemMeta();
					
					im.addPage("§3§lGame Information\n§9§oCTF\n§0§m-------------------\n\n\n§2§lContents\n §41) §0How to Play");
					im.addPage("§3§lHow to Play\n§0§m-------------------\n"
							+ "§r§0Two teams (§cRed§0 and §9Blue§0) have to battle it out to capture the opposite team's Flags! \n\nThe Flags are next to the spawn points. \n\nTo capture, you have to break the other team's flag and bring it to your Flag.");
					book.setItemMeta(im);
					p.getInventory().addItem(book);
					p.getInventory().setItem(8, plugin.INCore.Util.createItem(Material.getMaterial(351), 1, (byte) 1, "&c&lBack to Hub &r&7(Right click to use)", "&3Click this item to go to the hub"));
				}
			}
			
		}, 1L);
	}

	private void stop() {
		plugin.getServer().getScheduler().cancelTask(taskID);
	}
	
	public void endGame() {
		stop();
		Bukkit.getScheduler().cancelTasks(plugin);
		
		int red = plugin.ScoreboardHandler.red.getScore();
		int blue = plugin.ScoreboardHandler.blue.getScore();
		
		Team winners = null;
		
		if (red == plugin.gs.getMaxScore()) {
			winners = Team.RED;
		} else if (blue == plugin.gs.getMaxScore()) {
			winners = Team.BLUE;
		}
		
		if (winners == null) {
			if (red > blue) {
				winners = Team.RED;
			} else if (red < blue) {
				winners = Team.BLUE;
			}
		}
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (winners == null) {
				plugin.INCore.Coin.addCoins(p.getName(), 25);
			} else {
				if (winners == Team.RED) {
					if (plugin.gs.getTeam(p.getName()).equals(winners)) {
						plugin.INCore.Coin.addCoins(p.getName(), 50);
						plugin.INCore.Util.sendMessage(p, "§7The §cRed §7team won!", plugin.messagePrefix);
						BarAPI.setMessage(p, "§7The §cRed §7team won!");
					}
				} else {
					if (plugin.gs.getTeam(p.getName()).equals(winners)) {
						plugin.INCore.Coin.addCoins(p.getName(), 50);
						plugin.INCore.Util.sendMessage(p, "§7The §9Blue §7team won!", plugin.messagePrefix);
						BarAPI.setMessage(p, "§7The §9Blue §7team won!");
					}
				}
			}
			
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
}