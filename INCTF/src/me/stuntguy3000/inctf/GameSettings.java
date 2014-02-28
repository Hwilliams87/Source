package me.stuntguy3000.inctf;

import java.util.HashMap;

import me.stuntguy3000.inctf.enums.Gamestate;
import me.stuntguy3000.inctf.enums.Map;
import me.stuntguy3000.inctf.enums.Team;

import org.bukkit.entity.Player;

public class GameSettings {
	private Gamestate gs;
	private Map map;
	private int timeLeft;
	private HashMap<String, Team> teams = new HashMap<String, Team>();
	private int maxScore;
	private int needed;
	private int maxPlayers;
	
	public GameSettings(Gamestate gs, int max, int time, Map map, int maxScore, int needed) {
		setGameState(gs);
		setTimeLeft(time);
		setMap(map);
		setMaxScore(maxScore);
		setNeeded(needed);
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

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public int getNeeded() {
		return needed;
	}

	public void setNeeded(int needed) {
		this.needed = needed;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
}