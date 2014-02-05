package me.stuntguy3000.inrush.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.confuser.barapi.BarAPI;
import me.stuntguy3000.inrush.CaptureZoneStatus;
import me.stuntguy3000.inrush.INRush;
import me.stuntguy3000.inrush.enums.CaptureZone;
import me.stuntguy3000.inrush.enums.Gamestate;
import me.stuntguy3000.inrush.enums.Team;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CaptureZoneHandler {
	
	private INRush plugin;
	public boolean lockedZones = true;
	public boolean finalZone = false;
	
	public CaptureZoneHandler(INRush instance) {
		this.plugin = instance;
	}
	
	public void init() {
		try {
			String mapName = plugin.friendlyify(plugin.gs.getMap().name()).toLowerCase();
			File zonesFile = new File(plugin.getDataFolder() + File.separator + "zones.yml");
			YamlConfiguration yml = new YamlConfiguration();
			yml.load(zonesFile);
			
			ConfigurationSection zonesConfig = yml.getConfigurationSection(mapName);
			
			if (zonesConfig == null) System.out.println("filenull");
			
			for (String zoneName : zonesConfig.getKeys(false)) {
				CaptureZoneStatus s = CaptureZoneStatus.OPEN;
				
				if (yml.getBoolean(mapName + "." + zoneName + ".locked")) s = CaptureZoneStatus.LOCKED;
				
				plugin.zones.put(zoneName, new CaptureZone(plugin.INCore.Location.getLocation("SIEGE_" + mapName.toUpperCase() + "_" + zoneName.toUpperCase()), zoneName, s, 0));
			}
			
			Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
				@Override
				public void run() {
					zoneCheck();
				}
			}, 20L, 20L);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void zoneCheck() {
		if (!(plugin.gs.getGameState() == Gamestate.INGAME))
			return;
	
		HashMap<String, CaptureZone> zones = plugin.zones;
		
		for (String zName : zones.keySet()) {
			CaptureZone z = zones.get(zName);
			
			if (z.getStatus() == CaptureZoneStatus.OPEN || z.getStatus() == CaptureZoneStatus.CAPTURING) {
				z.setStatus(CaptureZoneStatus.OPEN);
				
				int bandits = 0;
				int royals = 0;
				
				for (Entity e : plugin.INCore.Util.getNearbyEntities(z.getLocation(), 5)) {
					if (e instanceof Player) {
						Player p = (Player) e;
						Team t = plugin.gs.getTeam(p.getName());
						
						if (t == Team.BANDIT) ++bandits;
						if (t == Team.ROYAL) ++royals;
					}
				}
				
				if (royals == 0 && bandits > 0) {
					z.setPower(z.getPower() + 1);
					z.setStatus(CaptureZoneStatus.CAPTURING);
				}
				
				int power = z.getPower();
				
				if (power >= plugin.gs.getScoreToWin()) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						plugin.INCore.Util.sendMessage(p, "§b§kAAAAA §a" + z.getZoneName() + " was captured! §b§kAAAAA", plugin.messagePrefix);
						BarAPI.setMessage(p, "§a" + z.getZoneName() + " was captured!", 10);
					}
					
					plugin.INCore.Util.spawnFirework(z.getLocation());
					plugin.INCore.Util.spawnFirework(z.getLocation());
					plugin.INCore.Util.spawnFirework(z.getLocation());
					z.setStatus(CaptureZoneStatus.CAPTURED);
					
					for (Player p : Bukkit.getOnlinePlayers()) {
						Team t = plugin.gs.getTeam(p.getName());
						
						if (t == Team.ROYAL)
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 0));
					}
				} 
				
				updateZone(z);
				plugin.zones.put(zName, z);
			}
		}
		
		zones = plugin.zones;
		int captured = 0;
		int capturing = 0;
		int open = 0;
		int locked = 0;
		int total = zones.size();
		
		for (String zName : zones.keySet()) {
			if (zones.get(zName).getStatus() == CaptureZoneStatus.CAPTURED) ++captured;
			if (zones.get(zName).getStatus() == CaptureZoneStatus.CAPTURING) ++capturing;
			if (zones.get(zName).getStatus() == CaptureZoneStatus.OPEN) ++open;
			if (zones.get(zName).getStatus() == CaptureZoneStatus.LOCKED) ++locked;
		}
		
		if (captured == total)
			plugin.GameHandler.endGame(Team.BANDIT, "The Bandit's captured all the control zones!");
		
		if (capturing == 0 && captured > 0 && open == 0 && locked > 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				plugin.INCore.Util.sendMessage(p, "§b§kAAAAA §eALL CONTROL POINTS ARE UNLOCKED! §b§kAAAAA", plugin.messagePrefix);
				plugin.INCore.Util.sendMessage(p, "§6Royals §ano longer recieve potion effects on respawn!", plugin.messagePrefix);
			}
			
			lockedZones = false;
			
			for (String zName : zones.keySet()) {
				CaptureZone z = zones.get(zName);
				
				if (z.getStatus() == CaptureZoneStatus.LOCKED)
					z.setStatus(CaptureZoneStatus.OPEN);
				
				plugin.zones.put(zName, z);
			}
			
			for (CaptureZone z : plugin.zones.values())
				updateZone(z);
		}
		
		if (capturing == 0 && captured > 0 && open == 1 && locked == 0) {
			if (!finalZone) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					plugin.INCore.Util.sendMessage(p, "§b§kAAAAA §aONE CONTROL POINT REMAINING! §b§kAAAAA", plugin.messagePrefix);
					plugin.INCore.Util.sendMessage(p, "§6Royals §arecieve Weakness on respawn!", plugin.messagePrefix);
				}
				finalZone = true;
			}
		}
		
		Bukkit.getScheduler().runTask(plugin, new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				HashMap<String, CaptureZone> zones = plugin.zones;
				zones = plugin.zones;
				
				for (String zName : zones.keySet()) {
					List<Block> beacon = new ArrayList<Block>();
					
					for (Location bl : plugin.INCore.Util.circle(plugin.zones.get(zName).getLocation(), 4, 4, false, true, 0)) {
						if (bl.getBlock() != null && bl.getBlock().getType() == Material.BEACON || bl.getBlock().getType() == Material.BEDROCK) {
							beacon.add(bl.getBlock());
						}
					}
					
					if (plugin.zones.get(zName).getStatus() == CaptureZoneStatus.CAPTURED || plugin.zones.get(zName).getStatus() == CaptureZoneStatus.LOCKED) {
			
						for (Block b : beacon) {
							if (b.getType() != Material.BEDROCK) {
								b.setType(Material.BEDROCK);
								b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType().getId());
							}
						}
					} else {
						for (Block b : beacon) {
							if (b.getType() != Material.BEACON) {
								b.setType(Material.BEACON);
								b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType().getId());
							}
						}
					}
				}
			}
			
		});
	}

	private void updateZone(CaptureZone z) {
		plugin.ScoreboardHandler.sb.getTeam(z.getZoneName()).setSuffix(getSuffix(z.getZoneName()));
		plugin.ScoreboardHandler.main.getScore(Bukkit.getOfflinePlayer("§b" + z.getZoneName())).setScore(z.getPower());
	}

	public String getSuffix(String zone) {
		CaptureZone z = plugin.zones.get(zone);
		
		if (z.getStatus() == CaptureZoneStatus.CAPTURED) {
			return " §4» §aCaptured";
		} else if (z.getStatus() == CaptureZoneStatus.CAPTURING) {
			return " §4» §6Capturing";
		} else if (z.getStatus() == CaptureZoneStatus.LOCKED) {
			return " §4» §8Locked";
		} else {
			return " §4» §fOpen";
		}
	}
}
