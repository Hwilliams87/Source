package me.stuntguy3000.infortwars;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import me.stuntguy3000.incore.INCore;
import me.stuntguy3000.infortwars.command.MapCommand;
import me.stuntguy3000.infortwars.command.StatusCommand;
import me.stuntguy3000.infortwars.enums.Gamestate;
import me.stuntguy3000.infortwars.enums.Map;
import me.stuntguy3000.infortwars.handler.EventsHandler;
import me.stuntguy3000.infortwars.handler.GameHandler;
import me.stuntguy3000.infortwars.handler.ScoreboardHandler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class INFortWars extends JavaPlugin {
	
	public INCore INCore;
	
	public String messagePrefix = "§8[§eFortWars§8] §7";
	public GameSettings gs;
	
	public ScoreboardHandler ScoreboardHandler;
	public GameHandler GameHandler;
	
	public String serverID = new File("").getAbsolutePath().split("/")[5];
	
	public void onEnable() {
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "IPNKSign");
		
		INCore = (INCore) Bukkit.getPluginManager().getPlugin("INCore");

		registerEvents();
		registerCommands();
		registerHandlers();
		initGameSettings();
		
		ScoreboardHandler.init();
		  
		getServer().createWorld(new WorldCreator("fortwars"));
		getServer().createWorld(new WorldCreator("lobby"));
		
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
		
		gs = new GameSettings(Gamestate.WAITING, 30, 90, maps.get(new Random().nextInt(maps.size())), 2, 0, 0);
	}

	public String friendlyify(String name) {
		String friendlyName = name.replaceAll("_", " ");
		friendlyName = friendlyName.toLowerCase();
		friendlyName = Character.toUpperCase(friendlyName.charAt(0)) + friendlyName.substring(1);
		return friendlyName;
	}
	
	public void openTeamSelection(Player p) {
		Inventory selection = Bukkit.createInventory(null, 9, "§0Team Selector");
		selection.setItem(3, INCore.Util.createItem(Material.WOOL, 1, (byte) 4, "§7Click here to join the §eYellow §7team", "§3Reserve your slot in the §eYellow§3 team!"));
		selection.setItem(5, INCore.Util.createItem(Material.WOOL, 1, (byte) 5, "§7Click here to join the §aGreen §7team", "§3Reserve your slot in the §aGreen§3 team"));
	
		p.openInventory(selection);
	}
}
