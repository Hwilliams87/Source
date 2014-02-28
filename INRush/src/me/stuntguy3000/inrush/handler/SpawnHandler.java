package me.stuntguy3000.inrush.handler;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.stuntguy3000.inrush.INRush;
import me.stuntguy3000.inrush.enums.Team;

public class SpawnHandler {
	private INRush plugin;
	
	public SpawnHandler(INRush instance) {
		this.plugin = instance;
	}
	
	public int getSpawns(Team t) {
		int count = 0;
		
		boolean found = false;
		
		int i = 0;
		while (!found) {
			i = i + 1;
			if (plugin.INCore.Location.getLocation("SIEGE_" + plugin.gs.getMap().name() + "_" + t.name().toUpperCase() + "_Spawn_" + i).getWorld() != null){
				count = count + 1;
			} else {
				found = true;
			}
		}
		
		return count;
	}

	public void spawn(Player p) {
		if (plugin.gs.getTeam(p.getName()) == Team.BANDIT) {
			int num = 0;
			Random r = new Random();
			
			num = r.nextInt(plugin.gs.getBanditSpawnCount()) + 1;
			
			p.teleport(plugin.INCore.Location.getLocation("SIEGE_" + plugin.gs.getMap().name() + "_BANDIT_Spawn_" + num));
		} else {
			int num = 0;
			Random r = new Random();
			
			num = r.nextInt(plugin.gs.getRoyalSpawnCount()) + 1;
			
			p.teleport(plugin.INCore.Location.getLocation("SIEGE_" + plugin.gs.getMap().name() + "_ROYAL_Spawn_" + num));
		}
	}
	
	public Location spawnGetLocation(Player p) {
		if (plugin.gs.getTeam(p.getName()) == Team.BANDIT) {
			int num = 0;
			Random r = new Random();
			
			num = r.nextInt(plugin.gs.getBanditSpawnCount()) + 1;
			
			return plugin.INCore.Location.getLocation("SIEGE_" + plugin.gs.getMap().name() + "_BANDIT_Spawn_" + num);
		} else {
			int num = 0;
			Random r = new Random();
			
			num = r.nextInt(plugin.gs.getRoyalSpawnCount()) + 1;
			
			return plugin.INCore.Location.getLocation("SIEGE_" + plugin.gs.getMap().name() + "_ROYAL_Spawn_" + num);
		}
	}
}
