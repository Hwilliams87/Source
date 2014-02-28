package me.stuntguy3000.inrush;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.stuntguy3000.incore.INCore;
import me.stuntguy3000.inrush.command.ClassCommand;
import me.stuntguy3000.inrush.command.MapCommand;
import me.stuntguy3000.inrush.command.StatusCommand;
import me.stuntguy3000.inrush.enums.CaptureZone;
import me.stuntguy3000.inrush.enums.Gamestate;
import me.stuntguy3000.inrush.enums.Map;
import me.stuntguy3000.inrush.handler.CaptureZoneHandler;
import me.stuntguy3000.inrush.handler.EventsHandler;
import me.stuntguy3000.inrush.handler.GameHandler;
import me.stuntguy3000.inrush.handler.KitHandler;
import me.stuntguy3000.inrush.handler.PhysicsEvents;
import me.stuntguy3000.inrush.handler.ScoreboardHandler;
import me.stuntguy3000.inrush.handler.SpawnHandler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class INRush extends JavaPlugin {
	
	public INCore INCore;
	
	public String messagePrefix = "§8[§eRush§8] §7";
	public GameSettings gs;
	public HashMap<String, CaptureZone> zones = new HashMap<String, CaptureZone>();
	
	public ScoreboardHandler ScoreboardHandler;
	public GameHandler GameHandler;
	public CaptureZoneHandler CaptureZoneHandler;
	public SpawnHandler SpawnHandler;
	public KitHandler KitHandler;
	
	public String serverID = new File("").getAbsolutePath().split("/")[5];
	
	public void onEnable() {
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "IPNKBroadcast");
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "IPNKGame");
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		INCore = (INCore) Bukkit.getPluginManager().getPlugin("INCore");
		registerEvents();
		registerCommands();
		registerHandlers();
		initGameSettings();
		
		getServer().createWorld(new WorldCreator(gs.getMap().name().toLowerCase()));
		
		CaptureZoneHandler.init();
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
	}

	private void registerEvents() {
		this.getServer().getPluginManager().registerEvents(new EventsHandler(this), this);
		this.getServer().getPluginManager().registerEvents(new PhysicsEvents(this), this);
	}

	private void registerCommands() {
		this.getCommand("map").setExecutor(new MapCommand(this));
		this.getCommand("class").setExecutor(new ClassCommand(this));
		this.getCommand("status").setExecutor(new StatusCommand(this));
	}

	private void registerHandlers() {
		ScoreboardHandler = new ScoreboardHandler(this);
		GameHandler = new GameHandler(this);
		CaptureZoneHandler = new CaptureZoneHandler(this);
		KitHandler = new KitHandler(this);
		SpawnHandler = new SpawnHandler(this);
	}

	private void initGameSettings() {
		ArrayList<Map> maps = new ArrayList<Map>();
		
		for (Map m : Map.values())
			maps.add(m);
		
		gs = new GameSettings(Gamestate.WAITING, 30, 90, maps.get(new Random().nextInt(maps.size())), 0, 0, 50);
	}

	public String friendlyify(String name) {
		String friendlyName = name.replaceAll("_", " ");
		friendlyName = friendlyName.toLowerCase();
		friendlyName = Character.toUpperCase(friendlyName.charAt(0)) + friendlyName.substring(1);
		return friendlyName;
	}

	public void openTeamSelection(Player p) {
		Inventory selection = Bukkit.createInventory(null, 9, "§0Team Selector");
		selection.setItem(3, INCore.Util.createItem(Material.WOOL, 1, (byte) 1, "§7Click here to become a §6Royal", "§3Reserve your slot in the §6Royal§3 team!"));
		selection.setItem(5, INCore.Util.createItem(Material.WOOL, 1, (byte) 14, "§7Click here to become a §cBandit", "§3Reserve your slot in the §cBandit§3 team"));
	
		p.openInventory(selection);
	}
}
