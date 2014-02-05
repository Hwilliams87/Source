package me.stuntguy3000.infortwars.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import me.confuser.barapi.BarAPI;
import me.stuntguy3000.infortwars.INFortWars;
import me.stuntguy3000.infortwars.enums.GameBlock;
import me.stuntguy3000.infortwars.enums.Gamestate;
import me.stuntguy3000.infortwars.enums.Team;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameHandler {
	
	private INFortWars plugin;
	
	public int taskID;
	public int count = 0;
	
	private ItemStack YELLOW_ARMOR_HELMET;
	private ItemStack YELLOW_ARMOR_CHESTPLATE;
	private ItemStack YELLOW_ARMOR_LEGGINGS;
	private ItemStack YELLOW_ARMOR_BOOTS;
	private ItemStack GREEN_ARMOR_HELMET;
	private ItemStack GREEN_ARMOR_CHESTPLATE;
	private ItemStack GREEN_ARMOR_LEGGINGS;
	private ItemStack GREEN_ARMOR_BOOTS;
	
	private ItemStack SWORD;
	private ItemStack BOW;
	private ItemStack ARROW;
	private ItemStack HEALTHPOTION;
	private ItemStack SHEARS;
	
	public HashMap<Location, GameBlock> gb = new HashMap<Location, GameBlock>();
	
	public GameHandler(INFortWars instance) {
		this.plugin = instance;
	}
	
	public void check() {
		int yellow = plugin.ScoreboardHandler.yellowBlocks.getScore();
		int green = plugin.ScoreboardHandler.greenBlocks.getScore();
		
		if (yellow == 0 || green == 0)
			endGame();
	}
	
	public void checkPlayerCount() {
		if (plugin.gs.getGameState() == Gamestate.WAITING) {
			if (Bukkit.getOnlinePlayers().length >= plugin.gs.getNeeded()) {
				countdownTimer();
				plugin.gs.setGameState(Gamestate.WARMUP);
			} else {
				if (plugin.gs.getNeeded() - Bukkit.getOnlinePlayers().length == 1) {
					for (Player p : Bukkit.getOnlinePlayers())
						plugin.INCore.Util.sendMessage(p, "&6&l" + (plugin.gs.getNeeded() - Bukkit.getOnlinePlayers().length) + " more player required to start the game!", plugin.messagePrefix);
				} else {
					for (Player p : Bukkit.getOnlinePlayers())
						plugin.INCore.Util.sendMessage(p, "&6&l" + (plugin.gs.getNeeded() - Bukkit.getOnlinePlayers().length) + " more players required to start the game!", plugin.messagePrefix);
				}
			}
		}
		
		if (plugin.gs.getGameState() == Gamestate.BUILDING || plugin.gs.getGameState() == Gamestate.BATTLE) {
			int yellow = 0;
			int green = 0;
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (plugin.gs.getTeam(p.getName()).equals(Team.YELLOW)) {
					yellow = yellow + 1;
				} else if (plugin.gs.getTeam(p.getName()).equals(Team.GREEN)) {
					green = green + 1;
				}
			}
			
			if (yellow == 0 || green == 0)
				endGame();
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
					YELLOW_ARMOR_HELMET = plugin.INCore.Util.createItem(Material.LEATHER_HELMET, 1, 255, 255, 0, "§eHelmet", "§7Armor");
					YELLOW_ARMOR_CHESTPLATE = plugin.INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 255, 255, 0, "§eChestplate", "§7Armor");
					YELLOW_ARMOR_LEGGINGS = plugin.INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 255, 255, 0, "§eLeggings", "§7Armor");
					YELLOW_ARMOR_BOOTS = plugin.INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 255, 255, 0, "§eBoots", "§7Armor");
					GREEN_ARMOR_HELMET = plugin.INCore.Util.createItem(Material.LEATHER_HELMET, 1, 0, 255, 0, "§aHelmet", "§7Armor");
					GREEN_ARMOR_CHESTPLATE = plugin.INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 0, 255, 0, "§aChestplate", "§7Armor");
					GREEN_ARMOR_LEGGINGS = plugin.INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 0, 255, 0, "§aLeggings", "§7Armor");
					GREEN_ARMOR_BOOTS = plugin.INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 0, 255, 0, "§aBoots", "§7Armor");
					SWORD = new ItemStack(Material.STONE_SWORD, 1);
					BOW = new ItemStack(Material.BOW, 1);
					ARROW = new ItemStack(Material.ARROW, 1);
					HEALTHPOTION = new ItemStack(Material.POTION, 1);
					HEALTHPOTION.setDurability((short) 16453);
					SHEARS = new ItemStack(Material.SHEARS, 1);
					
					SHEARS.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
					SHEARS.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
					BOW.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
					BOW.addEnchantment(Enchantment.ARROW_INFINITE, 1);
				}
				
				if (plugin.gs.getTimeLeft() == 15) {
					plugin.getServer().createWorld(new WorldCreator(plugin.gs.getMap().name().toLowerCase()));
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
						plugin.gs.setTimeLeft((int) (60 * 2));
						startGame();
					} else {
						for (Player p : Bukkit.getOnlinePlayers())
							plugin.INCore.Util.sendMessage(p, "&cNot enough players! &l" + (plugin.gs.getNeeded() - Bukkit.getOnlinePlayers().length) + " players are required to start the game!", plugin.messagePrefix);
						
						plugin.gs.setGameState(Gamestate.WAITING);
						plugin.gs.setTimeLeft(30);
					}
				}
				
				plugin.ScoreboardHandler.updateScoreboard();
				plugin.gs.setTimeLeft(plugin.gs.getTimeLeft() - 1);
			}
		}, 20L, 20L);

		taskID = TaskID;
	}
	
	public void buildingTimer() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			plugin.INCore.Util.sendMessage(p, "§7The game has started! You have §b" + plugin.INCore.Util.getTime(plugin.gs.getTimeLeft()) + " §7to build a base to defend. Once this time is up, you have to destroy the other team's base and win the match!", plugin.messagePrefix);
			p.setLevel(0);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * plugin.gs.getTimeLeft(), 0));
			
			if (p.hasPermission("game.fortwars.fastershears.buildspeed.3")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * plugin.gs.getTimeLeft(), 2));
			} else if (p.hasPermission("game.fortwars.fastershears.buildspeed.2")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * plugin.gs.getTimeLeft(), 1));
			}
		}
		
		count = 0;
		
		int TaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				int yellow = 0;
				int green = 0;
				
				for (GameBlock b : gb.values()) if (b.getTeam() == Team.YELLOW) yellow = yellow + 1; else green = green + 1;
								
				plugin.ScoreboardHandler.greenBlocks.setScore(green);
				plugin.ScoreboardHandler.yellowBlocks.setScore(yellow);
				
				plugin.GameHandler.checkPlayerCount();
				
				if (plugin.gs.getTimeLeft() == 0) {
					stop();
					plugin.gs.setTimeLeft(60 * 10);
					pvpTimer();	
				} else {
					plugin.ScoreboardHandler.updateScoreboard();
					plugin.gs.setTimeLeft(plugin.gs.getTimeLeft() - 1);
				}
			}
		}, 0L, 20L);
		
		taskID = TaskID;
	}
	
	public void pvpTimer() {
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
		plugin.gs.setGameState(Gamestate.BATTLE);
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			plugin.INCore.Util.sendMessage(p, "§7Building has ended! You have §b" + plugin.INCore.Util.getTime(plugin.gs.getTimeLeft()) + " §7to destroy the other team's defences!", plugin.messagePrefix);
			plugin.INCore.Util.sendMessage(p, "§aUse your bows and shears to destroy the wool!", plugin.messagePrefix);
			p.setLevel(0);
			
			p.getInventory().clear();
			giveItems(p);
			p.teleport(plugin.INCore.Location.getLocation("FortWars_" + plugin.gs.getMap().name().toLowerCase() + "_" + plugin.gs.getTeam(p.getName()) + "_Spawn"));
		}
		
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Location loc1 = plugin.INCore.Location.getLocation("FORTWARS_FORTWARS_WALL_1");
				Location loc2 = plugin.INCore.Location.getLocation("FORTWARS_FORTWARS_WALL_2");
				int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
				int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
				int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
				int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
				int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
				int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
				 
				for(int x = minX; x <= maxX; x++){
					for(int y = minY; y <= maxY; y++){
						for(int z = minZ; z <= maxZ; z++){
							Block block = loc1.getWorld().getBlockAt(x, y, z);
							if (block.getType() == Material.LOG || 
								block.getType() == Material.WOOD_STAIRS ||
								block.getType() == Material.WOOD_STEP ||
								block.getType() == Material.COBBLESTONE_STAIRS ||
								block.getType() == Material.COBBLE_WALL ||
								block.getType() == Material.WOOD ||
								block.getType() == Material.getMaterial(126) ||
								block.getType() == Material.SPRUCE_WOOD_STAIRS ||
								block.getType() == Material.FENCE) {
								block.setType(Material.AIR);
							}
						}
					}
				}
			}
		});
		
		int TaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				int yellow = 0;
				int green = 0;
		
				for (GameBlock b : gb.values()) if (b.getTeam() == Team.YELLOW) yellow = yellow + 1; else green = green + 1;
				
				plugin.ScoreboardHandler.greenBlocks.setScore(green);
				plugin.ScoreboardHandler.yellowBlocks.setScore(yellow);
				
				plugin.GameHandler.checkPlayerCount();
				
				if (plugin.gs.getTimeLeft() == 0) {
					stop();
					endGame();
				} else {
					plugin.ScoreboardHandler.updateScoreboard();
					plugin.gs.setTimeLeft(plugin.gs.getTimeLeft() - 1);
				}
				
				check();
			}
		}, 0L, 20L);
		
		taskID = TaskID;
	}

	@SuppressWarnings("deprecation")
	public void startGame() {
		plugin.gs.setGameState(Gamestate.BUILDING);
		
		boolean Red = true;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!plugin.gs.getTeams().containsKey(p.getName())) {
				Red = !Red;
				p.playSound(p.getLocation(), "note.pling", 2, 2);
				if (Red) {
					plugin.INCore.Util.sendMessage(p, "§7You are on the §eYellow§7 team.", plugin.messagePrefix);
					BarAPI.setMessage(p, "§7You are a §eYellow§7 team", 10);
					plugin.gs.addToTeam(p, Team.YELLOW);
					plugin.ScoreboardHandler.sb.getTeam("Yellow").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§e" + p.getName());
				} else {
					plugin.INCore.Util.sendMessage(p, "§7You are on the §aGreen§7 team.", plugin.messagePrefix);
					BarAPI.setMessage(p, "§7You are a §aGreen§7 team", 10);
					plugin.gs.addToTeam(p, Team.GREEN);
					plugin.ScoreboardHandler.sb.getTeam("Green").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§a" + p.getName());
				}
			}
			
			p.playSound(p.getLocation(), "note.pling", 2, 2);
			
			giveItems(p);
		}
		
		int y = 0;
		int g = 0;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (plugin.gs.getTeams().containsKey(p.getName())) {
				if (plugin.gs.getTeam(p.getName()).equals(Team.YELLOW)) {
					y++;
				} else {
					g++;
				}
			}
		}
		
		if (y == 0 || g == 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				plugin.INCore.Util.sendMessage(p, "§b§lImbalanced Teams! Scrambling...", plugin.messagePrefix);
			
				plugin.gs.removeFromTeam(p.getName());
				plugin.ScoreboardHandler.sb.getTeam("Green").removePlayer(Bukkit.getOfflinePlayer(p.getName()));
				plugin.ScoreboardHandler.sb.getTeam("Yellow").removePlayer(Bukkit.getOfflinePlayer(p.getName()));
				
				Red = !Red;
				if (Red) {
					plugin.INCore.Util.sendMessage(p, "§7You are a §eYellow§7 team.", plugin.messagePrefix);
					plugin.gs.addToTeam(p, Team.YELLOW);
					plugin.ScoreboardHandler.sb.getTeam("Yellow").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§e" + p.getName());
				} else {
					plugin.INCore.Util.sendMessage(p, "§7You are a §aGreen§7 team.", plugin.messagePrefix);
					plugin.gs.addToTeam(p, Team.GREEN);
					plugin.ScoreboardHandler.sb.getTeam("Green").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
					p.setDisplayName("§a" + p.getName());
				}
			}		
		}
		
		buildingTimer();
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (plugin.gs.getTeam(p.getName()).equals(Team.YELLOW)) {
				p.teleport(plugin.INCore.Location.getLocation("FORTWARS_FORTWARS_YELLOW_SPAWN"));
			} else {
				p.teleport(plugin.INCore.Location.getLocation("FORTWARS_FORTWARS_GREEN_SPAWN"));
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void giveItems(final Player p) {
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				p.getInventory().clear();
				
				if (plugin.gs.getGameState() == Gamestate.BUILDING) {
					ItemStack usershears = SHEARS;
					usershears.removeEnchantment(Enchantment.DIG_SPEED);
					
					if (p.hasPermission("game.fortwars.fastershears.shears.5")) {
						usershears.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
					} else if (p.hasPermission("game.fortwars.fastershears.shears.3")) usershears.addEnchantment(Enchantment.DIG_SPEED, 3);
					
					if (plugin.gs.getTeam(p.getName()).equals(Team.YELLOW)) {
						ItemStack wool = plugin.INCore.Util.createItem(Material.WOOL, 64, (byte) 4, "§eYellow Wool", "§7Build your defences");
					
						Integer i = 0;
						while (i < 36) {
							p.getInventory().addItem(wool);
							i = i + 1;
						}
					} else {
						ItemStack wool = plugin.INCore.Util.createItem(Material.WOOL, 64, (byte) 5, "§aGreen Wool", "§7Build your defences");
					
						Integer i = 0;
						while (i < 36) {
							p.getInventory().addItem(wool);
							i = i + 1;
						}
					}
					
					p.getInventory().setItem(8, SHEARS);
					
					if (plugin.gs.getTeam(p.getName()).equals(Team.YELLOW)) {
						p.getInventory().setHelmet(YELLOW_ARMOR_HELMET);
						p.getInventory().setChestplate(YELLOW_ARMOR_CHESTPLATE);
						p.getInventory().setLeggings(YELLOW_ARMOR_LEGGINGS);
						p.getInventory().setBoots(YELLOW_ARMOR_BOOTS);
					} else {
						p.getInventory().setHelmet(GREEN_ARMOR_HELMET);
						p.getInventory().setChestplate(GREEN_ARMOR_CHESTPLATE);
						p.getInventory().setLeggings(GREEN_ARMOR_LEGGINGS);
						p.getInventory().setBoots(GREEN_ARMOR_BOOTS);
					}
				} else if (plugin.gs.getGameState() == Gamestate.BATTLE){
					ItemStack usershears = SHEARS;
					usershears.removeEnchantment(Enchantment.DIG_SPEED);
					
					if (p.hasPermission("game.fortwars.fastershears.shears.5")) {
						usershears.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
					} else if (p.hasPermission("game.fortwars.fastershears.shears.3")) usershears.addEnchantment(Enchantment.DIG_SPEED, 3);
					
					p.getInventory().addItem(SWORD);
					p.getInventory().addItem(BOW);
					p.getInventory().addItem(ARROW);
					p.getInventory().addItem(HEALTHPOTION);
					p.getInventory().addItem(usershears);
					
					if (plugin.gs.getTeam(p.getName()).equals(Team.YELLOW)) {
						p.getInventory().setHelmet(YELLOW_ARMOR_HELMET);
						p.getInventory().setChestplate(YELLOW_ARMOR_CHESTPLATE);
						p.getInventory().setLeggings(YELLOW_ARMOR_LEGGINGS);
						p.getInventory().setBoots(YELLOW_ARMOR_BOOTS);
					} else {
						p.getInventory().setHelmet(GREEN_ARMOR_HELMET);
						p.getInventory().setChestplate(GREEN_ARMOR_CHESTPLATE);
						p.getInventory().setLeggings(GREEN_ARMOR_LEGGINGS);
						p.getInventory().setBoots(GREEN_ARMOR_BOOTS);
					}
				} else {
					ItemStack book = plugin.INCore.Util.createItem(Material.WRITTEN_BOOK, 1, "§6Information Book", "§3This book will teach you how to play!");
					BookMeta im = (BookMeta) book.getItemMeta();
					
					im.addPage("§3§lGame Information\n§9§oFortWars\n§0§m-------------------\n\n\n§2§lContents\n §41) §0How to Play\n");
					im.addPage("§3§lHow to Play\n§0§m-------------------\n"
							+ "§r§0Two teams (§aGreen§0 and §eYellow§0) have 2 minutes and 30 seconds to place as many blocks (to build a Fort) as possible in their allocated area. \nThe aim is to completely destroy the other team's Fort!");
					book.setItemMeta(im);
					
					p.getInventory().setItem(0, book);
					//p.getInventory().setItem(1, plugin.INCore.Util.createItem(Material.ENCHANTED_BOOK, 1, "&6&lTeam Selector", "&3Click this item to choose a team!"));
					p.getInventory().setItem(8, plugin.INCore.Util.createItem(Material.getMaterial(351), 1, (byte) 1, "&c&lBack to Hub &r&7(Right click to use)", "&3Click this item to go to the hub"));
				}
			}
			
		}, 1l);
	}

	private void stop() {
		plugin.getServer().getScheduler().cancelTask(taskID);
	}
	
	public void endGame() {
		stop();
		Bukkit.getScheduler().cancelTasks(plugin);
		
		int yellow = plugin.ScoreboardHandler.yellowBlocks.getScore();
		int green = plugin.ScoreboardHandler.greenBlocks.getScore();
		Team winners = null;
		
		if (yellow == 0) {
			winners = Team.GREEN;
		}
		
		if (green == 0) {
			winners = Team.YELLOW;
		}
		
		if (winners == null) {
			yellow = plugin.ScoreboardHandler.yellowKills.getScore();
			green = plugin.ScoreboardHandler.greenKills.getScore();
			
			if (yellow > green) {
				winners = Team.YELLOW;
			} else if (yellow < green) {
				winners = Team.GREEN;
			}
		}
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (winners == null) {
				plugin.INCore.Coin.addCoins(p.getName(), 10);
				plugin.INCore.Util.sendMessage(p, "§6The match was a draw!", plugin.messagePrefix);
				BarAPI.setMessage(p, "§6The match was a draw!");
			} else {
				if (winners == Team.YELLOW) {
					if (plugin.gs.getTeam(p.getName()).equals(winners)) {
						plugin.INCore.Coin.addCoins(p.getName(), 100);
					}
					plugin.INCore.Util.sendMessage(p, "§7The §eYellow §7team won!", plugin.messagePrefix);
					BarAPI.setMessage(p, "§7The §eYellow §7team won!");
				} else {
					if (plugin.gs.getTeam(p.getName()).equals(winners)) {
						plugin.INCore.Coin.addCoins(p.getName(), 100);
					}
					plugin.INCore.Util.sendMessage(p, "§7The §aGreen §7team won!", plugin.messagePrefix);
					BarAPI.setMessage(p, "§7The §aGreen §7team won!");
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