package me.stuntguy3000.inlobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class StatusSign {

	private String id;
	private int x;
	private int y;
	private int z;
	private String world;
	
	public StatusSign(String id, int x, int y, int z, String world) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ());
	}
}
