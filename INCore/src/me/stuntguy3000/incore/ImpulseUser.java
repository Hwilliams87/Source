package me.stuntguy3000.incore;

import java.util.HashMap;

import me.stuntguy3000.incore.enums.Achievement;
import me.stuntguy3000.incore.enums.Statistic;

import org.bukkit.ChatColor;

public class ImpulseUser {
	private String username;
	
	private String prefix;
	private String suffix;
	private String colour;
	private String cprefix;
	private HashMap<Achievement, Long> achievements = new HashMap<Achievement, Long>();
	private HashMap<Statistic, Double> stats = new HashMap<Statistic, Double>();
	
	public ImpulseUser(String username, String prefix, String suffix, String colour, HashMap<Achievement, Long> achievements, HashMap<Statistic, Double> stats) {
		this.username = c(username);
		this.prefix = c(prefix);
		this.suffix = c(suffix);
		this.colour = c(colour);
		
		if (prefix.equalsIgnoreCase("&7Member")) {
			this.cprefix = "";
		} else {
			this.cprefix = prefix + "§f»§7";
		}
		
		this.achievements = achievements;
		this.stats = stats;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}
	
	private String c(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	public String getChatPrefix() {
		return ChatColor.translateAlternateColorCodes('&', cprefix);
	}

	public HashMap<Achievement, Long> getAchievements() {
		return achievements;
	}

	public void setAchievements(HashMap<Achievement, Long> achievements) {
		this.achievements = achievements;
	}

	public HashMap<Statistic, Double> getStats() {
		return stats;
	}

	public void setStats(HashMap<Statistic, Double> stats) {
		this.stats = stats;
	}
}
