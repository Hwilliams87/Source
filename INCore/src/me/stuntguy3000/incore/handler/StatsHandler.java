package me.stuntguy3000.incore.handler;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;

import me.stuntguy3000.incore.INCore;
import me.stuntguy3000.incore.enums.Statistic;

import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.parser.ParseException;

public class StatsHandler {
	private INCore plugin;
	
	public StatsHandler (INCore plugin) {
		this.plugin = plugin;
	}

	public String getStatsJSON(String username) throws SQLException {
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(plugin.CacheHandler.getPlayerConfig(username));
		return yml.getString("stats");
	}
	
	public void setStatsJSON(String username, String JSON) throws Exception {
		File f = plugin.CacheHandler.getPlayerConfig(username);
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
		yml.set("stats", JSON);
		yml.save(f);
	}
	
	public HashMap<Statistic, Integer> parseString(String input) throws ParseException {
		return null;
	}
	
	public void addStat(String username, Statistic stat, int value) throws ParseException, SQLException {
		
	}
}
