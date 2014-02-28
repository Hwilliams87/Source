package me.stuntguy3000.incore.handler;

import java.util.HashMap;
import java.util.List;

import me.stuntguy3000.incore.INCore;
import me.stuntguy3000.incore.enums.Achievement;

import org.bukkit.configuration.file.YamlConfiguration;

public class AchievementHandler {
	private INCore plugin;
	
	public AchievementHandler(INCore instance) {
		this.plugin = instance;
	}

	public HashMap<Achievement, Long> getAchievements(String name) {
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(plugin.Cache.getPlayerConfig(name));
		
		if (yml == null) return null;
		
		List<String> data = yml.getStringList("achievemnts");
		return parse(data);
	}

	public HashMap<Achievement, Long> parse(List<String> d) {
		HashMap<Achievement, Long> achievements = null;
		
		for (String data : d) {
			String[] a = data.split(":");
			try {
				Achievement ach = Achievement.valueOf(a[0]);
				Long l = Long.parseLong(a[1]);
				achievements.put(ach, l);
			} catch (Exception ex) {
				
			}
		}
		
		return achievements;
	}
}
