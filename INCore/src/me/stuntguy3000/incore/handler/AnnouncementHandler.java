package me.stuntguy3000.incore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.stuntguy3000.incore.INCore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AnnouncementHandler {
	private INCore plugin;
	
	public List<String> announcements = new ArrayList<String>();
	public String announceMessagePrefix = " &9> &b";
	public int seconds = 60 * 2;
	
	public AnnouncementHandler(INCore instance)
	{
		this.plugin = instance;
	}
	
	public void download() {
		announcements.clear();
		ResultSet rs = plugin.DB.query("SELECT * FROM `in_announcements`").getResultSet();
		
		try {
			if (rs.isBeforeFirst())
			{
				while (rs.next()) {
					announcements.add(rs.getString("message"));
				}
				
				saveAnnouncements();
			} else {
				announcements.add("&cError downloading announcements. Please contact staff!");
				saveAnnouncements();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			announcements.add("&cError downloading announcements. Please contact staff!");
			saveAnnouncements();
		}
	}
	
	public void saveAnnouncements() {
		plugin.getConfig().set("Announcements", announcements);
		plugin.saveConfig();
	}
	
	public void getAnnouncements() {
		announcements = plugin.getConfig().getStringList("Announcements");
	}

	public void timer() {
		new BukkitRunnable() {
    		public void run() {
    			announceMessage();
    		}	 
    	}.runTaskTimer(plugin, 20 * seconds, 20 * seconds);
	}
	
	public void announceMessage() {
		int annNo = new Random().nextInt(announcements.size());
		announceMessage(annNo + 1);
	}
	
	public void announceMessage(int val) {
		int annNo = val - 1;
		
		for (Player p : Bukkit.getOnlinePlayers())
			plugin.Util.sendMessage(p, announcements.get(annNo), announceMessagePrefix);
	}
	
	public boolean exists(int val) {
		Boolean exists = true;
		
		try {
			announcements.get(val - 1);
		} catch (IndexOutOfBoundsException e) {
			exists = false;
		}	
		
		return exists;
	}

	public void remove(int val) {
		plugin.DB.query("DELETE FROM `in_announcements` WHERE `message` = '" + announcements.get(val - 1) + "';");

		new BukkitRunnable() {
    		public void run() {
    			download();
    		}	 
    	}.runTaskLater(plugin, 20);}
	
	public void add(String msg) {
		plugin.DB.query("INSERT INTO `in_announcements` (`message`) VALUES ('" + msg + "');");
		
		new BukkitRunnable() {
    		public void run() {
    			download();
    		}	 
    	}.runTaskLater(plugin, 20);
	}
}
