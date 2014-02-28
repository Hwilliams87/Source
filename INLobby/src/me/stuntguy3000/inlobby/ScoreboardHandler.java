package me.stuntguy3000.inlobby;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardHandler {
	
	public Scoreboard sb;
	public Objective obj;
	public INLobby plugin;
	
	public Score ctf;
	public Score RUSH;
	public Score fortwars;
	public Score hub;
	public Score total;
	
	public int ctfI;
	public int RUSHI;
	public int fortwarsI;
	public int hubI;
	public int totalI;
	
	public HashMap<String, Team> tags = new HashMap<String, Team>();
	
	public ScoreboardHandler(INLobby instance) {
		this.plugin = instance;
	}
	
	public void setScoreboard() {
		for (Player p : Bukkit.getOnlinePlayers())
			p.setScoreboard(sb);
	}
	
	public void init() {
		sb = plugin.getServer().getScoreboardManager().getNewScoreboard();
		obj = sb.registerNewObjective("tags", "dummy");
		
		// Init teams
		tags.put("default", sb.registerNewTeam("default"));
		tags.put("vip", sb.registerNewTeam("vip"));
		tags.put("vip+", sb.registerNewTeam("vip+"));
		tags.put("premium", sb.registerNewTeam("premium"));
		tags.put("mod", sb.registerNewTeam("mod"));
		tags.put("admin", sb.registerNewTeam("admin"));
		tags.put("developer", sb.registerNewTeam("developer"));
		tags.put("founder", sb.registerNewTeam("founder"));
		tags.put("youtube", sb.registerNewTeam("youtube"));
		tags.put("architect", sb.registerNewTeam("architect"));
		
		// Setup teams
		tags.get("default").setPrefix("§7");
		tags.get("default").setSuffix("§r");
		tags.get("vip").setPrefix("§a");
		tags.get("vip").setSuffix("§r");
		tags.get("vip+").setPrefix("§2");
		tags.get("vip+").setSuffix("§r");
		tags.get("premium").setPrefix("§5");
		tags.get("premium").setSuffix("§r");
		tags.get("mod").setPrefix("§bS§8»§b");
		tags.get("mod").setSuffix("§r");
		tags.get("admin").setPrefix("§bS§8»§4");
		tags.get("admin").setSuffix("§r");
		tags.get("founder").setPrefix("§bS§8»§c");
		tags.get("founder").setSuffix("§r");
		tags.get("developer").setPrefix("§bS§8»§3");
		tags.get("developer").setSuffix("§r");
		tags.get("architect").setPrefix("§bS§8»§6");
		tags.get("architect").setSuffix("§r");
		tags.get("youtube").setPrefix("§fY§cT§8»§e");
		tags.get("youtube").setSuffix("§r");
		

		ctf = obj.getScore(Bukkit.getOfflinePlayer("§6CTF"));
		RUSH = obj.getScore(Bukkit.getOfflinePlayer("§6Rush"));
		fortwars = obj.getScore(Bukkit.getOfflinePlayer("§6Fortwars"));
		hub = obj.getScore(Bukkit.getOfflinePlayer("§6Hub"));
		total = obj.getScore(Bukkit.getOfflinePlayer("§bGlobal Count"));
		
		obj.setDisplayName("§aWhere is everyone?");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		hub.setScore(0);
		ctf.setScore(0);
		RUSH.setScore(0);
		fortwars.setScore(0);
		total.setScore(0);
	}

	public void playerCount() {
		hubI = Bukkit.getOnlinePlayers().length;
		ctfI = 0;
		RUSHI = 0;
		fortwarsI = 0;
		totalI = 0;
		
		if (hubI > 0) {
			getServer("RUSH_01");
			getServer("RUSH_02");
			getServer("RUSH_03");
			getServer("RUSH_04");
			getServer("RUSH_05");
			getServer("RUSH_06");
			getServer("FORTWARS_01");
			getServer("FORTWARS_02");
			getServer("FORTWARS_03");
			getServer("FORTWARS_04");
			getServer("FORTWARS_05");
			getServer("FORTWARS_06");
			getServer("CTF_01");
			getServer("CTF_02");
			getServer("CTF_03");
			getServer("CTF_04");
			getServer("CTF_05");
			getServer("CTF_06");
			getServer("ALL");
			
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					hub.setScore(hubI);
					ctf.setScore(ctfI);
					RUSH.setScore(RUSHI);
					fortwars.setScore(fortwarsI);
					total.setScore(totalI);
				}
			}, 10L);
		}
	}
	
	public void getServer(String name) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		 
		try {
		    out.writeUTF("PlayerCount");
		    out.writeUTF(name);
		} catch (IOException e) {
			
		}
		
		Bukkit.getOnlinePlayers()[0].sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
	}
}
