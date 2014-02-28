package me.stuntguy3000.incore.handler;

import java.io.File;
import java.sql.ResultSet;

import me.stuntguy3000.incore.INCore;

import org.bukkit.configuration.file.YamlConfiguration;

public class CacheHandler {
	private INCore plugin;
	private File folder;
	
	public CacheHandler (INCore plugin) {
		this.plugin = plugin;
	}

	public void init() {
		folder = new File("plugins/INCore/cache");
		if (!folder.exists()) folder.mkdirs();
	}
	
	public void setup(String p) throws Exception {
		File u = new File("plugins/INCore/cache/" + p + ".yml");
		if (!u.exists()) u.createNewFile();
		
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(u);
		
		ResultSet data = plugin.DB.query("SELECT * FROM `in_users` WHERE `username`='" + p + "'").getResultSet();
		
		while (data.next()) {
			yml.set("username", p);
			yml.set("achievements", data.getString("achievements"));
			yml.set("stats", data.getString("stats"));
		}
		
		yml.save(u);
	}
	
	public File getPlayerConfig(String p) {
		return new File("plugins/INCore/cache/" + p + ".yml");
	}
	
	public void updateDB(String p) {
		plugin.DB.query(
				"UPDATE `in_users` SET "
				+ "`achievements` = 'null', "
				+ "`stats` = 'null'"
				+ " WHERE `in_users`.`username` = '" + p + "';");
	}
}
