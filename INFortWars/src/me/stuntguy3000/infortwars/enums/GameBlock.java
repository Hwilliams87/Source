package me.stuntguy3000.infortwars.enums;

import org.bukkit.Location;

public class GameBlock {
	private Location location;
	private String player;
	private Team team;
	
	public GameBlock(Location l, String p, Team t) {
		setLocation(l);
		setPlayer(p);
		setTeam(t);
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
}
