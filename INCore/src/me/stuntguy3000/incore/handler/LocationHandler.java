package me.stuntguy3000.incore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.stuntguy3000.incore.INCore;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationHandler {
	private INCore plugin;
	
	public LocationHandler(INCore instance)
	{
		this.plugin = instance;
	}
	
	public void download() {
		plugin.getConfig().set("Locations", null);
		plugin.saveConfig();
		
		ResultSet locations = plugin.DB.query("SELECT * FROM in_locations").getResultSet();
		
		try {
			while (locations.next()) {
				String id = locations.getString("locID").toUpperCase();
				plugin.getConfig().set("Locations." + id + ".x", locations.getDouble("x"));
				plugin.getConfig().set("Locations." + id + ".y", locations.getDouble("y"));
				plugin.getConfig().set("Locations." + id + ".z", locations.getDouble("z"));
				plugin.getConfig().set("Locations." + id + ".yaw", locations.getDouble("yaw"));
				plugin.getConfig().set("Locations." + id + ".pitch", locations.getDouble("pitch"));
				plugin.getConfig().set("Locations." + id + ".world", locations.getString("world"));
				plugin.saveConfig();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		plugin.saveConfig();
	}
	
	public Location getLocation(String id) {
		Location loc = new Location(null, 0, 0, 0);
		
		if (plugin.getConfig().getString("Locations." + id.toUpperCase()) != null) {
			loc.setX(plugin.getConfig().getDouble("Locations." + id.toUpperCase() + ".x"));
			loc.setY(plugin.getConfig().getDouble("Locations." + id.toUpperCase() + ".y"));
			loc.setZ(plugin.getConfig().getDouble("Locations." + id.toUpperCase() + ".z"));
			loc.setYaw((float) plugin.getConfig().getDouble("Locations." + id.toUpperCase() + ".yaw"));
			loc.setPitch((float) plugin.getConfig().getDouble("Locations." + id.toUpperCase() + ".pitch"));
			loc.setWorld(Bukkit.getWorld(plugin.getConfig().getString("Locations." + id.toUpperCase() + ".world")));
		}
		
		return loc;
	}
	
	public void setLocation(String id, Location loc) {
		plugin.DB.query("DELETE FROM `in_locations` WHERE `locID` = '" + id + "';");
		plugin.DB.query("INSERT INTO `in_locations` (`locID`, `x`, `y`, `z`, `yaw`, `pitch`, `world`) "
				+ "VALUES ('" + id.toUpperCase() + "', '" + (double) loc.getX() + "', '" + (double) loc.getY() + "', '" + (double) loc.getZ() + "', "
						+ "'" + (double) loc.getYaw() + "', '" + (double) loc.getPitch() + "', '" + loc.getWorld().getName() + "');");
		download();
	}
	
	public boolean exists(String id) {
		return plugin.getConfig().getString("Locations." + id.toUpperCase()) != null;
	}
}
