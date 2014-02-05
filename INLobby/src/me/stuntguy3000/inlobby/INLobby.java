package me.stuntguy3000.inlobby;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.confuser.barapi.BarAPI;
import me.stuntguy3000.incore.INCore;
import me.stuntguy3000.inlobby.command.FireFunCommand;
import me.stuntguy3000.inlobby.command.MakeShopCommand;
import me.stuntguy3000.inlobby.command.SecretCommand;
import me.stuntguy3000.inlobby.command.SetSignCommand;
import me.stuntguy3000.inlobby.command.SpawnCommand;
import me.stuntguy3000.inlobby.event.PlayerEvents;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

public class INLobby extends JavaPlugin implements PluginMessageListener {
	
	public INCore INCore;
	
	public int bossbar = 0;
	public List<String> bossBarMessages = new ArrayList<String>();
	public List<String> noSee = new ArrayList<String>();
	
	public HashMap<String, StatusSign> signs = new HashMap<String, StatusSign>();
	public HashMap<String, Integer> cooldownScroll = new HashMap<String, Integer>();
	public HashMap<String, Integer> cooldownSkull = new HashMap<String, Integer>();
	public HashMap<String, Integer> cooldownBackToLobby = new HashMap<String, Integer>();
	public HashMap<String, Integer> cooldownFirework = new HashMap<String, Integer>();
	public HashMap<String, Integer> cooldownCheckpoint = new HashMap<String, Integer>();
	public HashMap<String, Integer> cooldownSpeed = new HashMap<String, Integer>();
	
	public HashMap<String, String> setSign = new HashMap<String, String>();
	public HashMap<String, Location> checkpoints = new HashMap<String, Location>();
	
	public Inventory armorSelector = null;
	public List<Material> materials = new ArrayList<Material>();
	public int signTimerID;
	
	public ScoreboardHandler ScoreboardHandler;
	public CoinShopHandler CoinShopHandler;
	
	public int rotor = 1;
	public void onEnable() {
		saveDefaultConfig();
		signTimer();
		
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        
		INCore = (INCore) Bukkit.getPluginManager().getPlugin("INCore");
		
		registerCommands();
		registerEvents();
		
		armorSelector = Bukkit.createInventory(Bukkit.getPlayer("workaround"), 9 * 6, "§0Choose your outfit!");
		ScoreboardHandler = new ScoreboardHandler(this);
		CoinShopHandler = new CoinShopHandler(this);
		ScoreboardHandler.init();
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				HashMap<String, Integer> cooldown = new HashMap<String, Integer>();
				
				for (String name : cooldownScroll.keySet())
					if (cooldownScroll.get(name) != 1) cooldown.put(name, cooldownScroll.get(name) - 1);
				
				cooldownScroll = cooldown;
				
				
				cooldown = new HashMap<String, Integer>();
				
				for (String name : cooldownSkull.keySet())
					if (cooldownSkull.get(name) != 1) cooldown.put(name, cooldownSkull.get(name) - 1);
				
				cooldownSkull = cooldown;
				
				
				cooldown = new HashMap<String, Integer>();
				
				for (String name : cooldownBackToLobby.keySet())
					if (cooldownBackToLobby.get(name) != 1) cooldown.put(name, cooldownBackToLobby.get(name) - 1);
				
				cooldownBackToLobby = cooldown;
				
				
				cooldown = new HashMap<String, Integer>();
				
				for (String name : cooldownFirework.keySet())
					if (cooldownFirework.get(name) != 1) cooldown.put(name, cooldownFirework.get(name) - 1);
				
				cooldownFirework = cooldown;
				
				
				cooldown = new HashMap<String, Integer>();
				
				for (String name : cooldownCheckpoint.keySet())
					if (cooldownCheckpoint.get(name) != 1) cooldown.put(name, cooldownCheckpoint.get(name) - 1);
				
				cooldownCheckpoint = cooldown;
				
				
				cooldown = new HashMap<String, Integer>();
				
				for (String name : cooldownSpeed.keySet())
					if (cooldownSpeed.get(name) != 1) cooldown.put(name, cooldownSpeed.get(name) - 1);
				
				cooldownSpeed = cooldown;
				
				newArmorInventoryItem();
			}
		}, 20L, 20L);
		
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				ScoreboardHandler.playerCount();
			}
		}, 20 * 5, 20 * 5);
		
		bossBarMessages.add("§6Welcome to the ImpulseNetwork §c{NAME}§6!");
		bossBarMessages.add("§aPurchase VIP at ImpulseNetwork.org/donate");
		bossBarMessages.add("§bJoin our Website §8- §bImpulseNetwork.org");
		bossBarMessages.add("§eBuy Coins at ImpulseNetwork.org/donate");
		bossBarMessages.add("§3Be sure to read the information book!");
		bossBarMessages.add("§5Found a bug? Goto ImpulseNetwork.org/forum");
		bossBarMessages.add("§dFollow @ImpulseNK");
		bossBarMessages.add("§cHide Players with the Magic Scroll!");
		
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers())
					BarAPI.setMessage(p, bossBarMessages.get(new Random().nextInt(bossBarMessages.size() - 1)).replace("{NAME}", p.getName()));
			}
		}, 15 * 20L, 15 * 20L);
		
		refreshSigns();
		
		for (Material mat : Material.values())
			materials.add(mat);
		
		/*final Location l1 = new Location(Bukkit.getWorld("world"), 7, 76, 10);
		final Location l2 = new Location(Bukkit.getWorld("world"), -40, 76, -39);
		final Schematic s1;
		final Schematic s2;
		final Schematic s3;
		final Schematic s4;
		final Schematic s5;
		final Schematic s6;
		final Schematic s7;
		final Schematic s8;
		final Schematic s9;
		
		try {
			s1 = INCore.Util.loadSchematic(new File("plugins/INLobby/rotor1.schematic"));
			s2 = INCore.Util.loadSchematic(new File("plugins/INLobby/rotor2.schematic"));
			s3 = INCore.Util.loadSchematic(new File("plugins/INLobby/rotor3.schematic"));
			s4 = INCore.Util.loadSchematic(new File("plugins/INLobby/rotor4.schematic"));
			s5 = INCore.Util.loadSchematic(new File("plugins/INLobby/rotor5.schematic"));
			s6 = INCore.Util.loadSchematic(new File("plugins/INLobby/rotor6.schematic"));
			s7 = INCore.Util.loadSchematic(new File("plugins/INLobby/rotor7.schematic"));
			s8 = INCore.Util.loadSchematic(new File("plugins/INLobby/rotor8.schematic"));
			s9 = INCore.Util.loadSchematic(new File("plugins/INLobby/rotor9.schematic"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				if (rotor == 1) {
					INCore.Util.pasteSchematic(l1.getWorld(), l1, s1); 
					INCore.Util.pasteSchematic(l2.getWorld(), l2, s1); 
				}
				if (rotor == 2) {
					INCore.Util.pasteSchematic(l1.getWorld(), l1, s2); 
					INCore.Util.pasteSchematic(l2.getWorld(), l2, s2); 
				}
				if (rotor == 3) {
					INCore.Util.pasteSchematic(l1.getWorld(), l1, s3); 
					INCore.Util.pasteSchematic(l2.getWorld(), l2, s3); 
				}
				if (rotor == 4) {
					INCore.Util.pasteSchematic(l1.getWorld(), l1, s4); 
					INCore.Util.pasteSchematic(l2.getWorld(), l2, s4); 
				}
				if (rotor == 5) {
					INCore.Util.pasteSchematic(l1.getWorld(), l1, s5); 
					INCore.Util.pasteSchematic(l2.getWorld(), l2, s5); 
				}
				if (rotor == 6) {
					INCore.Util.pasteSchematic(l1.getWorld(), l1, s6); 
					INCore.Util.pasteSchematic(l2.getWorld(), l2, s6); 
				}
				if (rotor == 7) {
					INCore.Util.pasteSchematic(l1.getWorld(), l1, s7); 
					INCore.Util.pasteSchematic(l2.getWorld(), l2, s7); 
				}
				if (rotor == 8) {
					INCore.Util.pasteSchematic(l1.getWorld(), l1, s8); 
					INCore.Util.pasteSchematic(l2.getWorld(), l2, s8); 
				}
				if (rotor == 9) {
					INCore.Util.pasteSchematic(l1.getWorld(), l1, s9); 
					INCore.Util.pasteSchematic(l2.getWorld(), l2, s9); 
				}
				
				rotor = rotor + 1;
				if (rotor == 10) rotor = 1;
			} 
		}, 5l, 5l);*/
	}

	private void newArmorInventoryItem() {
		if (armorSelector == null)
			return;
		
		for (HumanEntity p : armorSelector.getViewers())
			openArmorInventory((Player) p);
	}

	public void onDisable() {
		this.getServer().getScheduler().cancelTasks(this);
	}
	
	private void registerEvents() {
		this.getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
		this.getServer().getPluginManager().registerEvents(new CoinShopHandler(this), this);
	}

	private void registerCommands() {
		this.getCommand("SetSign").setExecutor(new SetSignCommand(this));
		this.getCommand("Spawn").setExecutor(new SpawnCommand(this));
		this.getCommand("MakeShop").setExecutor(new MakeShopCommand(this));
		this.getCommand("FireFun").setExecutor(new FireFunCommand(this));
		this.getCommand("Secret").setExecutor(new SecretCommand(this));
	}

	public void giveItems(Player p) {
		ItemStack book = INCore.Util.createItem(Material.WRITTEN_BOOK, 1, "§6Information Book", "§3This book will give you info about the ImpulseNetwork!");
		BookMeta im = (BookMeta) book.getItemMeta();
		
		im.addPage("§8§m-------------------\n§1Hey §6" + p.getName() + "\n\n§2Welcome to the §3ImpulseNetwork§2!"
				+ "\n\n§4Turn to the next page to learn about the ImpulseNetwork!\n\n§9§lVisit out website!\n§r§9ImpulseNetwork.org");
		im.addPage("§8§m-------------------\n§4Index\n§8§m-------------------\n\n§91) §2About Us!\n"
				+ "§92) §2Server Rules\n"
				+ "§93) §2Donation Perks\n"
				+ "§94) §2Useful Commands\n"
				+ "§95) §2FAQ Information\n"
				+ "§96) §2Social Media Links\n");
		im.addPage("§8§m-------------------\n§4About Us!\n§8§m-------------------\n"
				+ "§9Welcome to the ImpulseNetwork!\n\n§9"
				+ "This is a newly created Minecraft Minigame Network founded by SubZeroExtabyte, DavisA20, PixelSwiftYT and TheFamousFilms.");
		im.addPage("§8§m-------------------\n§4About Us!\n§8§m-------------------\n"
				+ "§9We run custom coded plugins and unique gamemodes, coded by our unique lead developer: \n\n§3stuntguy3000§9.\n\n§9We have §33 §9games open and more in the works!");
		im.addPage("§8§m-------------------\n§4Server Rules\n§8§m-------------------\n"
				+ "§31. §9No advertising servers and/or social media links\n§32. §9Keep profane language to a bare minimum\n§33. §9No Spamming\n§34. §9No excessive use of caps lock");
		im.addPage("§8§m-------------------\n§4Server Rules\n§8§m-------------------\n"
				+ "§35. §9No Exploiting or use of Hacked Clients\n"
				+ "§36. §9Be respectful to fellow players and staff members\n"
				+ "§37. §9No offensive skins and player names\n\n§9These rules are subject to change.");		
		im.addPage("§8§m-------------------\n§4Donation Perks\n§8§m-------------------\n"
				+ "§9You can support us by donating at \n§3ImpulseNetwork.org/doonate\n\n§9Donating gives you many perks, such as Double Coins, Reserved Slots and more features!");	
		im.addPage("§8§m-------------------\n§4Useful Commands\n§8§m-------------------\n"
				+ "§9/pm or /r - Private message any player on the Network"
				+ "\n§9/fp - Find a player on the Network"
				+ "\n§9/hub - Go back to the Hub"
				+ "\n§9/coins - See how many Coins you have");	
		im.addPage("§8§m-------------------\n§4Social Media Links\n§8§m-------------------\n"
				+ "§9Website:\n"
				+ "§3ImpulseNetwork.org\n\n§9Official Twitter:\n§3@ImpulseNK"
				+ "\n\n§9Founders Twitters:\n§3@SubZeroExtabyte\n@DavisA20\n@PixelSwiftYT");	
		im.addPage("§8§m-------------------\n§4Social Media Links\n§8§m-------------------\n"
				+ "§9Founders Twitters:\n§3@TheFamousFilms\n\n§3For direct help/bug reports tweet §6@stuntguy3000");
		im.addPage("§8§m-------------------\n§4Social Media Links\n§8§m-------------------\n"
				+ "§9Youtube: \n§6SubZeroExtabyte\n§3/MayaTOOTs"
				+ "\n§6PixelSwiftYT\n§3/PixelSwiftDraws"
				+ "\n§6TheFamousFilms\n§3/TheFamousFilms\n\n§3Our Teamspeak:\n§9TS.ImpulseNetwork.org");
		im.addPage("§2The ImpulseNetwork is Hosted by the amazing\n§4ProMinecraftHost\n\n§1Get a server at\n§3ProMinecraftHost.com/Impulse\n\n§9Thank you for reading this book! We hope you enjoy the ImpulseNetwork!");
		book.setItemMeta(im);
		
		p.getInventory().addItem(INCore.Util.createItem(Material.COMPASS, 1, "&6&lGame Selector &r&7(Right click to use)", "&3Why not play some games!"));
		p.getInventory().addItem(INCore.Util.createItem(Material.ENCHANTED_BOOK, 1, "&b&lMagic Scroll &r&7(Right click to use)", "&3Banish those players from your vision!"));
		p.getInventory().addItem(book);
		if (p.hasPermission("donor.lobby.speedboost")) p.getInventory().setItem(6, INCore.Util.createItem(Material.GLASS_BOTTLE, 1, "&c&lSpeed Boost &r&7(Right click to use)", "&3Give yourself Speed 2."));
		if (p.hasPermission("donor.lobby.armorselector")) p.getInventory().setItem(7, INCore.Util.createItem(Material.SKULL_ITEM, 1, (byte) 3, "&a&lWardrobe &r&7(Right click to use)", "&3Make yourself look fabulous!"));
		if (p.hasPermission("donor.lobby.fireworklauncher")) p.getInventory().setItem(8, INCore.Util.createItem(Material.FIREWORK, 1, "&d&lRocket Launcher &r&7(Right click to use)", "&3Fly high in the sky!"));
	}	
	
	public void openArmorInventory(Player p) {
		armorSelector.setItem((9 * 6) - 1, INCore.Util.createItem(Material.WOOL, 1, (byte) 14,"&cClear your armor!", "&fClick here to remove all armor"));
		armorSelector.setItem((9 * 6) - 9, INCore.Util.createItem(getRandomMaterial(), 1,"&aRandomize", "&fClick here to choose random armor"));
		armorSelector.setItem(0, INCore.Util.createItem(Material.WOOL, 1, (byte) 14,"&cClear your armor!", "&fClick here to remove all armor"));
		armorSelector.setItem(8, INCore.Util.createItem(getRandomMaterial(), 1,"&aRandomize", "&fClick here to choose random armor"));
		
		// Red Leather
		armorSelector.setItem(10, INCore.Util.createItem(Material.LEATHER_HELMET, 1, 255, 0, 0,"&4Red Helmet", "&fClick here to apply this item"));
		armorSelector.setItem(19, INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 255, 0, 0,"&4Red Chestplate", "&fClick here to apply this item"));
		armorSelector.setItem(28, INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 255, 0, 0,"&4Red Leggings", "&fClick here to apply this item"));
		armorSelector.setItem(37, INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 255, 0, 0,"&4Red Boots", "&fClick here to apply this item"));
		
		// Green Leather
		armorSelector.setItem(11, INCore.Util.createItem(Material.LEATHER_HELMET, 1, 0, 255, 0,"&2Green Helmet", "&fClick here to apply this item"));
		armorSelector.setItem(20, INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 0, 255, 0,"&2Green Chestplate", "&fClick here to apply this item"));
		armorSelector.setItem(29, INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 0, 255, 0,"&2Green Leggings", "&fClick here to apply this item"));
		armorSelector.setItem(38, INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 0, 255, 0,"&2Green Boots", "&fClick here to apply this item"));

		// Blue Leather
		armorSelector.setItem(12, INCore.Util.createItem(Material.LEATHER_HELMET, 1, 0, 0, 255,"&1Blue Helmet", "&fClick here to apply this item"));
		armorSelector.setItem(21, INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 0, 0, 255,"&1Blue Chestplate", "&fClick here to apply this item"));
		armorSelector.setItem(30, INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 0, 0, 255,"&1Blue Leggings", "&fClick here to apply this item"));
		armorSelector.setItem(39, INCore.Util.createItem(Material.LEATHER_BOOTS, 1,  0, 0, 255,"&1Blue Boots", "&fClick here to apply this item"));

		// Yellow Leather
		armorSelector.setItem(13, INCore.Util.createItem(Material.LEATHER_HELMET, 1, 255, 255, 0,"&eYellow Helmet", "&fClick here to apply this item"));
		armorSelector.setItem(22, INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 255, 255, 0,"&eYellow Chestplate", "&fClick here to apply this item"));
		armorSelector.setItem(31, INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 255, 255, 0,"&eYellow Leggings", "&fClick here to apply this item"));
		armorSelector.setItem(40, INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 255, 255, 0,"&eYellow Boots", "&fClick here to apply this item"));
		
		// Iron Armor
		armorSelector.setItem(14, INCore.Util.createItem(Material.IRON_HELMET, 1, "&7Iron Helmet", "&fClick here to apply this item"));
		armorSelector.setItem(23, INCore.Util.createItem(Material.IRON_CHESTPLATE, 1, "&7Iron Chestplate", "&fClick here to apply this item"));
		armorSelector.setItem(32, INCore.Util.createItem(Material.IRON_LEGGINGS, 1, "&7Iron Leggings", "&fClick here to apply this item"));
		armorSelector.setItem(41, INCore.Util.createItem(Material.IRON_BOOTS, 1, "&7Iron Boots", "&fClick here to apply this item"));

		// Gold Armor
		armorSelector.setItem(15, INCore.Util.createItem(Material.GOLD_HELMET, 1, "&6Gold Helmet", "&fClick here to apply this item"));
		armorSelector.setItem(24, INCore.Util.createItem(Material.GOLD_CHESTPLATE, 1, "&6Gold Chestplate", "&fClick here to apply this item"));
		armorSelector.setItem(33, INCore.Util.createItem(Material.GOLD_LEGGINGS, 1, "&6Gold Leggings", "&fClick here to apply this item"));
		armorSelector.setItem(42, INCore.Util.createItem(Material.GOLD_BOOTS, 1, "&6Gold Boots", "&fClick here to apply this item"));
		
		// Diamond Armor
		armorSelector.setItem(16, INCore.Util.createItem(Material.DIAMOND_HELMET, 1, "&bDiamond Helmet", "&fClick here to apply this item"));
		armorSelector.setItem(25, INCore.Util.createItem(Material.DIAMOND_CHESTPLATE, 1, "&bDiamond Chestplate", "&fClick here to apply this item"));
		armorSelector.setItem(34, INCore.Util.createItem(Material.DIAMOND_LEGGINGS, 1, "&bDiamond Leggings", "&fClick here to apply this item"));
		armorSelector.setItem(43, INCore.Util.createItem(Material.DIAMOND_BOOTS, 1, "&bDiamond Boots", "&fClick here to apply this item"));
	}

	private Material getRandomMaterial() {
		Random r = new Random();
		
		return materials.get(r.nextInt(materials.size()));
	}

	public void addSign(Location l, String id) {
		this.getConfig().set("StatusSigns." + id + ".x", l.getBlockX());
		this.getConfig().set("StatusSigns." + id + ".y", l.getBlockY());
		this.getConfig().set("StatusSigns." + id + ".z", l.getBlockZ());
		this.getConfig().set("StatusSigns." + id + ".world", l.getWorld().getName());
		this.saveConfig();
		refreshSigns();
	}
	
	private void refreshSigns() {
		signs.clear();
		ConfigurationSection cs = getConfig().getConfigurationSection("StatusSigns");
		
		if (cs == null)
			return;
		
		for (String id : cs.getKeys(false))
			signs.put(id, new StatusSign(id, 
					this.getConfig().getInt("StatusSigns." + id + ".x"), 
					this.getConfig().getInt("StatusSigns." + id + ".y"),
					this.getConfig().getInt("StatusSigns." + id + ".z"),
					this.getConfig().getString("StatusSigns." + id + ".world")));
	}

	public void signTimer() {
		BukkitTask t = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				try {
					ResultSet rs = INCore.DB.query("SELECT * FROM `in_signs`").getResultSet();
					
					if (!rs.isBeforeFirst()) {
						return;
					}
					
					while (rs.next()) {
						String id = rs.getString("id");
						
						StatusSign ss = signs.get(id);
						if (ss != null) {
							Block ps = ss.getLocation().getBlock();
							if (ps.getType() == Material.SIGN_POST || ps.getType() == Material.WALL_SIGN) {
								Sign s = (Sign) ps.getState();
								
								long lastUpdate = Long.parseLong(rs.getString("timestamp"));
								long now = new Date().getTime();
								long difference = Math.abs(now - lastUpdate);
								
								String motd = rs.getString("data");
								
								if (difference < 5000) {
									String[] sections = motd.split(",");
									try {
										String sid = sections[0];
										int players = Integer.parseInt(sections[1]);
										int max = Integer.parseInt(sections[2]);
										String gs = sections[3];
										 
										if (gs.equalsIgnoreCase("LOBBY")) {
											if (players == max) {
												s.setLine(0, "§b§l[Full]");
											} else s.setLine(0, "§a§l[Join]");
										} else {
											s.setLine(0, "§8[Cannot Join]");
										}
										 
										s.setLine(1, "§1> " + friendlyify(gs) + " <");
										s.setLine(2, "§f" + players + "§0/§f" + max);
										s.setLine(3, "§8" + sid);
									 } catch (IndexOutOfBoundsException | NumberFormatException ex) {
										s.setLine(0, "§6§m----------------");
										s.setLine(1, "§4Invalid");
										s.setLine(2, "§4Server");
										s.setLine(3, "§6§m----------------");
									 }
								} else {
									s.setLine(0, "§6§m----------------");
									s.setLine(1, "§4Server");
									s.setLine(2, "§4Restarting...");
									s.setLine(3, "§6§m----------------");
								}
								
								s.update(true);
							}
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}, 20 * 1, 20 * 1);
		
		signTimerID = t.getTaskId();
	}
	
	public String friendlyify(String name) {
		String server = name;
		
		server = server.replaceAll("_", "");
		server = server.substring(0,1).toUpperCase() + server.substring(1).toLowerCase();
		
		return server;
	}
	
	@SuppressWarnings("resource")
	public String getMotd(String ip, String port) {
		try {
			Socket sock = new Socket(ip, Integer.valueOf(port));
			 
			DataOutputStream out = new DataOutputStream(sock.getOutputStream());
			DataInputStream in = new DataInputStream(sock.getInputStream());
			 
			out.write(0xFE);
			 
			int b;
			StringBuffer str = new StringBuffer();
			while ((b = in.read()) != -1)
				if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) str.append((char) b);
			 
			String[] data = str.toString().split("§");
			String serverMotd = data[0];
			
			System.out.print(str);
			
			return serverMotd;
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("BungeeCord")) {
        	DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subchannel;
    		try {
    			subchannel = in.readUTF();
    		} catch (IOException e) {
    			e.printStackTrace();
    			return;
    		}
           
    		if (subchannel.equals("PlayerCount")) {
    			try {
					String server = in.readUTF();
					int playercount = in.readInt();
					
					if (server.startsWith("RUSH")) ScoreboardHandler.RUSHI = ScoreboardHandler.RUSHI + playercount;
					if (server.startsWith("FORTWARS")) ScoreboardHandler.fortwarsI = ScoreboardHandler.fortwarsI + playercount;
					if (server.startsWith("CTF")) ScoreboardHandler.ctfI = ScoreboardHandler.ctfI + playercount;
					if (server.startsWith("ALL")) ScoreboardHandler.totalI = ScoreboardHandler.totalI + playercount;
					if (server.startsWith("HUB")) ScoreboardHandler.hubI = ScoreboardHandler.hubI + playercount;
					
				} catch (IOException e) {
					e.printStackTrace();
				}
    			
    		}
        }
	}
}
