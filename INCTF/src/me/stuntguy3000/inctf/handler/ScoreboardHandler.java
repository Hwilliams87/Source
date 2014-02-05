package me.stuntguy3000.inctf.handler;

import me.stuntguy3000.inctf.INCTF;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardHandler {
	
	private INCTF plugin;
	
	public Scoreboard sb;
	public Objective main;
	
	public Team redTeam;
	public Team blueTeam;
	
	public Score red;
	public Score blue;
	public Score goal;
	
	public ScoreboardHandler(INCTF instance) {
		this.plugin = instance;
	}
	
	public void updateScoreboard() {
		main.setDisplayName("§6" + plugin.friendlyify(plugin.gs.getGameState().name()) + " §8- §b" + plugin.INCore.Util.getTime(plugin.gs.getTimeLeft()));
		
		for (Player p : Bukkit.getOnlinePlayers())
			p.setScoreboard(sb);
	}

	public void init() {
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		
		if (sb.getObjective("mainObjective") == null)
			sb.registerNewObjective("mainObjective", "dummy");
		
		main = sb.getObjective("mainObjective");
		main.setDisplaySlot(DisplaySlot.SIDEBAR);
		main.setDisplayName("§4CTF §8- §6" + plugin.friendlyify(plugin.gs.getMap().name()));
		
		if (sb.getTeam("Red") != null) sb.getTeam("Red").unregister();
		if (sb.getTeam("Blue") != null) sb.getTeam("Blue").unregister();
		
		sb.registerNewTeam("Red");
		sb.registerNewTeam("Blue");
		
		redTeam = sb.getTeam("Red");
		redTeam.setPrefix("§c");
		redTeam.setSuffix("§r");
		redTeam.setCanSeeFriendlyInvisibles(true);
		redTeam.setAllowFriendlyFire(false);
		
		blueTeam = sb.getTeam("Blue");
		blueTeam.setPrefix("§9");
		blueTeam.setSuffix("§r");
		blueTeam.setCanSeeFriendlyInvisibles(true);
		blueTeam.setAllowFriendlyFire(false);
		
		red = main.getScore(Bukkit.getOfflinePlayer("§cRed Team"));
		blue = main.getScore(Bukkit.getOfflinePlayer("§9Blue Team"));
		goal = main.getScore(Bukkit.getOfflinePlayer("§6Score to win"));
		
		red.setScore(0);
		blue.setScore(0);
		goal.setScore(plugin.gs.getMaxScore());
	}
}
