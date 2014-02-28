package me.stuntguy3000.infortwars.handler;

import me.stuntguy3000.infortwars.INFortWars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardHandler {
	
	private INFortWars plugin;
	
	public Scoreboard sb;
	public Objective main;
	
	public Team yellowTeam;
	public Team greenTeam;
	
	public Score yellowBlocks;
	public Score greenBlocks;
	public Score yellowKills;
	public Score greenKills;
	
	public ScoreboardHandler(INFortWars instance) {
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
		main.setDisplayName("§4FortWars §8- §6" + plugin.friendlyify(plugin.gs.getMap().name()));
		
		if (sb.getTeam("Yellow") != null) sb.getTeam("Yellow").unregister();
		if (sb.getTeam("Green") != null) sb.getTeam("Green").unregister();
		
		sb.registerNewTeam("Yellow");
		sb.registerNewTeam("Green");
		
		yellowTeam = sb.getTeam("Yellow");
		yellowTeam.setPrefix("§e");
		yellowTeam.setSuffix("§r");
		yellowTeam.setCanSeeFriendlyInvisibles(true);
		yellowTeam.setAllowFriendlyFire(false);
		
		greenTeam = sb.getTeam("Green");
		greenTeam.setPrefix("§a");
		greenTeam.setSuffix("§r");
		greenTeam.setCanSeeFriendlyInvisibles(true);
		greenTeam.setAllowFriendlyFire(false);
		
		yellowBlocks = main.getScore(Bukkit.getOfflinePlayer("§eYellow Blocks"));
		greenBlocks = main.getScore(Bukkit.getOfflinePlayer("§aGreen Blocks"));
		yellowKills = main.getScore(Bukkit.getOfflinePlayer("§eYellow Kills"));
		greenKills = main.getScore(Bukkit.getOfflinePlayer("§aGreen Kills"));
		
		yellowBlocks.setScore(0);
		greenBlocks.setScore(0);
		yellowKills.setScore(0);
		greenKills.setScore(0);
	}
}
