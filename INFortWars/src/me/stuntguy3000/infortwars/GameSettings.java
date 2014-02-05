package me.stuntguy3000.infortwars;

import java.util.HashMap;

import me.stuntguy3000.infortwars.enums.Gamestate;
import me.stuntguy3000.infortwars.enums.Map;
import me.stuntguy3000.infortwars.enums.Team;

import org.bukkit.entity.Player;

public class GameSettings {
	private Gamestate gs;
	private Map map;
	private int timeLeft;
	private HashMap<String, Team> teams = new HashMap<String, Team>();
	private int greenSpawnCount;
	private int yellowSpawnCount;
	private int needed;
	private int maxPlayers;
	
	public GameSettings(Gamestate gs, int max, int time, Map map, int needed, int yellow, int green) {
		setGameState(gs);
		setTimeLeft(time);
		setMap(map);
		setNeeded(needed);
		setGreenSpawnCount(green);
		setYellowSpawnCount(yellow);
		setMaxPlayers(max);
	}
	
	public Gamestate getGameState() {
		return gs;
	}
	public void setGameState(Gamestate gs) {
		this.gs = gs;
	}
	
	public int getTimeLeft() {
		return timeLeft;
	}
	
	public void setTimeLeft(int timeLeft) {
		this.timeLeft = timeLeft;
	}
	
	public void clearTeams() {
		teams.clear();
	}
	
	public HashMap<String, Team> getTeams() {
		return teams;
	}
	
	public void setTeam(String player, Team t) {
		teams.put(player, t);
	}
	
	public Team getTeam(String player) {
		return teams.get(player);
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}
	
	public void removeFromTeam(String player) {
		teams.remove(player);
	}

	public void addToTeam(Player p, Team team) {
		teams.put(p.getName(), team);
	}

	public int getNeeded() {
		return needed;
	}

	public void setNeeded(int needed) {
		this.needed = needed;
	}

	public int getGreenSpawnCount() {
		return greenSpawnCount;
	}

	public void setGreenSpawnCount(int greenSpawnCount) {
		this.greenSpawnCount = greenSpawnCount;
	}

	public int getYellowSpawnCount() {
		return yellowSpawnCount;
	}

	public void setYellowSpawnCount(int yellowSpawnCount) {
		this.yellowSpawnCount = yellowSpawnCount;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
}