package me.stuntguy3000.inctf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.stuntguy3000.incore.INCore;
import me.stuntguy3000.inctf.command.MapCommand;
import me.stuntguy3000.inctf.command.StatusCommand;
import me.stuntguy3000.inctf.enums.Gamestate;
import me.stuntguy3000.inctf.enums.Map;
import me.stuntguy3000.inctf.enums.Team;
import me.stuntguy3000.inctf.handler.EventsHandler;
import me.stuntguy3000.inctf.handler.GameHandler;
import me.stuntguy3000.inctf.handler.ScoreboardHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class INCTF extends JavaPlugin {
	
	public INCore INCore;
	
	public String messagePrefix = "&8[&eCTF&8] &7";
	public GameSettings gs;
	
	public ScoreboardHandler ScoreboardHandler;
	public GameHandler GameHandler;
	
	public Location redFlag;
	public Location blueFlag;
	
	public HashMap<String, Team> hasFlag = new HashMap<String, Team>();
	
	public String serverID = new File("").getAbsolutePath().split("/")[5];
	
	public void onEnable() {
		INCore = (INCore) Bukkit.getPluginManager().getPlugin("INCore");
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "IPNKBroadcast");
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "IPNKGame");
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		registerEvents();
		registerCommands();
		registerHandlers();
		initGameSettings();
		
		ScoreboardHandler.init();
		  
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				String status;
				if (gs.getGameState() == Gamestate.WAITING || gs.getGameState() == Gamestate.WARMUP)
					status = serverID + "," + Bukkit.getOnlinePlayers().length + "," + gs.getMaxPlayers() + ",LOBBY";
				else status = serverID + "," + Bukkit.getOnlinePlayers().length + "," + gs.getMaxPlayers() + "," + gs.getGameState();
				INCore.updateSign(serverID, status);
			}
		}, 20, 20 * 2);
		
		for (Map m : Map.values())
			Bukkit.createWorld(new WorldCreator(m.name().toLowerCase()));
	}

	private void registerEvents() {
		this.getServer().getPluginManager().registerEvents(new EventsHandler(this), this);
	}

	private void registerCommands() {
		this.getCommand("map").setExecutor(new MapCommand(this));
		this.getCommand("status").setExecutor(new StatusCommand(this));
	}

	private void registerHandlers() {
		ScoreboardHandler = new ScoreboardHandler(this);
		GameHandler = new GameHandler(this);
	}

	private void initGameSettings() {
		ArrayList<Map> maps = new ArrayList<Map>();
		
		for (Map m : Map.values())
			maps.add(m);
		
		gs = new GameSettings(Gamestate.WAITING, 50, 90, maps.get(new Random().nextInt(maps.size())), 5, 2);
	}

	public String friendlyify(String name) {
		String friendlyName = name.replaceAll("_", " ");
		friendlyName = friendlyName.toLowerCase();
		friendlyName = Character.toUpperCase(friendlyName.charAt(0)) + friendlyName.substring(1);
		return friendlyName;
	}

	@SuppressWarnings("deprecation")
	public void takeFlag(Player p, Team team) {
		Block b;
		if (team.equals(Team.RED)) {
			b = redFlag.getBlock();
		} else {
			b = blueFlag.getBlock();
		}
		
		b.setType(Material.BEDROCK);
		
		b.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 5);
		b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.ENDER_STONE.getId());
		b.getWorld().playEffect(b.getLocation(), Effect.SMOKE, 10);
		b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, 1);
		b.getWorld().playEffect(b.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 15);
		b.getWorld().createExplosion(b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ(), 4f, false);
		
		if (team.equals(Team.RED)) {
			for (Player target : Bukkit.getOnlinePlayers()) {
				if (this.gs.getTeam(target.getName()) == team)
					INCore.Util.sendMessage(target, "&c&lYOUR FLAG WAS TAKEN BY &9" + p.getName(), messagePrefix);
			}
		} else {
			for (Player target : Bukkit.getOnlinePlayers()) {
				if (this.gs.getTeam(target.getName()) == team)
					INCore.Util.sendMessage(target, "&c&lYOUR FLAG WAS TAKEN BY &9" + p.getName(), messagePrefix);
			}
		}
		
		hasFlag.remove(p.getName());
		hasFlag.put(p.getName(), team);
		
		INCore.Coin.addCoins(p.getName(), 5);
		
		if (team == Team.BLUE) {
			INCore.Util.spawnFirework(blueFlag, Color.BLUE);
			p.getInventory().clear();
			ItemStack wool = new ItemStack(Material.WOOL, 64, (byte) 11);
			ItemMeta wm = wool.getItemMeta();
			wm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9&lYou have the Blue team's flag!"));
			wool.setItemMeta(wm);
			
			Integer i = 0;
			while (i < 36) {
				p.getInventory().addItem(wool);
				i = i + 1;
			}
			
			i = 0;
		} else {
			INCore.Util.spawnFirework(redFlag, Color.RED);
			p.getInventory().clear();
			ItemStack wool = new ItemStack(Material.WOOL, 64, (byte) 14);
			ItemMeta wm = wool.getItemMeta();
			wm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lYou have the Red team's flag!"));
			wool.setItemMeta(wm);
			
			Integer i = 0;
			while (i < 36) {
				p.getInventory().addItem(wool);
				i = i + 1;
			}
			
			i = 0;
		}
	}
	
	public void addCapture(Team t) {
		int red = ScoreboardHandler.red.getScore();
		int blue = ScoreboardHandler.blue.getScore();
		
		if (t == Team.RED) {
			red = red + 1;
			INCore.Util.spawnFirework(redFlag, Color.RED);
		} else {
			blue = blue + 1;
			INCore.Util.spawnFirework(blueFlag, Color.BLUE);
		}
		
		ScoreboardHandler.red.setScore(red);
		ScoreboardHandler.blue.setScore(blue);
		
		if (red == gs.getMaxScore() || blue == gs.getMaxScore()) {
			GameHandler.endGame();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void resetFlag(Team t) {
		if (t.equals(Team.RED)) {
			redFlag.getBlock().setType(Material.WOOL);
			redFlag.getBlock().setData((byte) 14);
			
			Block b = redFlag.getBlock();
			b.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 5);
			b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.ENDER_STONE.getId());
			b.getWorld().playEffect(b.getLocation(), Effect.SMOKE, 10);
			b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, 1);
			b.getWorld().playEffect(b.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 15);
		} else {
			blueFlag.getBlock().setType(Material.WOOL);
			blueFlag.getBlock().setData((byte) 11);
			
			Block b = blueFlag.getBlock();
			b.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 5);
			b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.ENDER_STONE.getId());
			b.getWorld().playEffect(b.getLocation(), Effect.SMOKE, 10);
			b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, 1);
			b.getWorld().playEffect(b.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 15);
		}
	}

	public void openTeamSelection(Player p) {
		Inventory selection = Bukkit.createInventory(null, 9, "§0Team Selector");
		selection.setItem(3, INCore.Util.createItem(Material.WOOL, 1, (byte) 11, "§7Click here to join the §9Blue §7team", "§3Reserve your slot in the §9Blue§3 team!"));
		selection.setItem(5, INCore.Util.createItem(Material.WOOL, 1, (byte) 14, "§7Click here to join the §cRed §7team", "§3Reserve your slot in the §cRed§3 team"));
	
		p.openInventory(selection);
	}
}
