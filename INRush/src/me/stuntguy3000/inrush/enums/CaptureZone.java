package me.stuntguy3000.inrush.enums;

import me.stuntguy3000.inrush.CaptureZoneStatus;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class CaptureZone {
	
	private int x;
	private int y;
	private int z;
	private String worldName;
	
	private String zoneName;
	private CaptureZoneStatus status;
	private int power;
	
	public CaptureZone(Location l, String zoneName, CaptureZoneStatus status, int power) {
		this.x = (int) l.getX();
		this.y = (int) l.getY();
		this.z = (int) l.getZ();
		this.worldName = l.getWorld().getName();
		
		this.zoneName = zoneName;
		this.status = status;
		this.power = power;
	}
	
	public int getX() {
		return this.x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getZ() {
		return this.z;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
	
	public String getWorld() {
		return this.worldName;
	}
	
	public void setWorld(String world) {
		this.worldName = world;
	}
	
	public Location getLocation() {
		return new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ());
	}
	
	public String getZoneName() {
		return this.zoneName;
	}
	
	public void setZoneName(String name) {
		this.zoneName = name;
	}
	
	public CaptureZoneStatus getStatus() {
		return status;
	}
	
	public void setStatus(CaptureZoneStatus status) {
		this.status = status;
	}
	
	public int getPower() {
		return this.power;
	}
	
	public void setPower(int power) {
		this.power = power;
	}
}
 