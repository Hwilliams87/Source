package me.stuntguy3000.inrush;

import java.util.HashMap;

import me.stuntguy3000.inrush.enums.Gamestate;
import me.stuntguy3000.inrush.enums.Map;
import me.stuntguy3000.inrush.enums.Team;

import org.bukkit.entity.Player;

public class GameSettings {
	private Gamestate gs;
	private Map map;
	private int timeLeft;
	private HashMap<String, Team> teams = new HashMap<String, Team>();
	private int royalSpawnCount;
	private int banditSpawnCount;
	private int scoreToWin;
	private int maxPlayers;
	
	public GameSettings(Gamestate gs, int max, int time, Map map, int rs, int bs, int score) {
		setGameState(gs);
		setMaxPlayers(max);
		setTimeLeft(time);
		setMap(map);
		setRoyalSpawnCount(rs);
		setBanditSpawnCount(bs);
		setScoreToWin(score);
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

	public int getRoyalSpawnCount() {
		return royalSpawnCount;
	}

	public void setRoyalSpawnCount(int royalSpawnCount) {
		this.royalSpawnCount = royalSpawnCount;
	}

	public int getBanditSpawnCount() {
		return banditSpawnCount;
	}

	public void setBanditSpawnCount(int banditSpawnCount) {
		this.banditSpawnCount = banditSpawnCount;
	}

	public int getScoreToWin() {
		return scoreToWin;
	}
	
	public void setScoreToWin(int scoreToWin) {
		this.scoreToWin = scoreToWin;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
}