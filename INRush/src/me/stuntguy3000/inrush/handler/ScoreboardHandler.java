package me.stuntguy3000.inrush.handler;

import java.util.HashMap;

import me.stuntguy3000.inrush.INRush;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardHandler {
	
	private INRush plugin;
	
	public Scoreboard sb;
	public Objective main;
	
	public Team bandit;
	public Team royal;
	
	public HashMap<String, Score> zones = new HashMap<String, Score>();
	
	public ScoreboardHandler(INRush instance) {
		this.plugin = instance;
	}
	
	public void updateScoreboard() {
		
		main.setDisplayName("§2" + plugin.friendlyify(plugin.gs.getGameState().name())+ " §8- §b" + plugin.INCore.Util.getTime(plugin.gs.getTimeLeft()));
		
		for (Player p : Bukkit.getOnlinePlayers())
			p.setScoreboard(sb);
	}

	public void init() {
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		
		if (sb.getObjective("mainObjective") == null)
			sb.registerNewObjective("mainObjective", "dummy");
		
		main = sb.getObjective("mainObjective");
		main.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		if (sb.getTeam("Royal") != null) sb.getTeam("Royal").unregister();
		if (sb.getTeam("Bandit") != null) sb.getTeam("Bandit").unregister();
		
		sb.registerNewTeam("Royal");
		sb.registerNewTeam("Bandit");
		
		bandit = sb.getTeam("Bandit");
		bandit.setPrefix("§c");
		bandit.setSuffix("§r");
		bandit.setCanSeeFriendlyInvisibles(true);
		bandit.setAllowFriendlyFire(false);
		
		royal = sb.getTeam("Royal");
		royal.setPrefix("§6");
		royal.setSuffix("§r");
		royal.setCanSeeFriendlyInvisibles(true);
		royal.setAllowFriendlyFire(false);
		
		for (String zone : plugin.zones.keySet()) {
			zones.put(zone, main.getScore(Bukkit.getOfflinePlayer("§b" + zone)));
			sb.registerNewTeam(zone);
			sb.getTeam(zone).addPlayer(Bukkit.getOfflinePlayer("§b" + zone));
			sb.getTeam(zone).setSuffix(plugin.CaptureZoneHandler.getSuffix(zone));
		}
		
		for (String s : zones.keySet())
			zones.get(s).setScore(plugin.zones.get(s).getPower());
		
		main.getScore(Bukkit.getOfflinePlayer("§dScore to Win")).setScore(plugin.gs.getScoreToWin() * 2);
		sb.registerNewTeam("towin");
		sb.getTeam("towin").addPlayer(Bukkit.getOfflinePlayer("§dScore to Win"));
		sb.getTeam("towin").setSuffix(" §4» §6" + plugin.gs.getScoreToWin());
	}
}
