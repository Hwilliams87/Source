package me.stuntguy3000.incore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.stuntguy3000.incore.command.AnnouncerCommand;
import me.stuntguy3000.incore.command.CoinManagementCommand;
import me.stuntguy3000.incore.command.CoinsCommand;
import me.stuntguy3000.incore.command.DeployRestartCommand;
import me.stuntguy3000.incore.command.DevServerCommand;
import me.stuntguy3000.incore.command.FlyCommand;
import me.stuntguy3000.incore.command.HubCommand;
import me.stuntguy3000.incore.command.ListCommand;
import me.stuntguy3000.incore.command.PlayerInformationCommand;
import me.stuntguy3000.incore.command.RestartServerCommand;
import me.stuntguy3000.incore.command.SeenCommand;
import me.stuntguy3000.incore.command.SetLocationCommand;
import me.stuntguy3000.incore.command.TeleportCommand;
import me.stuntguy3000.incore.command.TeleportHereCommand;
import me.stuntguy3000.incore.command.TogglePhysicsCommand;
import me.stuntguy3000.incore.command.VanishCommand;
import me.stuntguy3000.incore.event.FilterEvents;
import me.stuntguy3000.incore.event.PhysicsEvents;
import me.stuntguy3000.incore.event.PlayerEvents;
import me.stuntguy3000.incore.handler.AchievementHandler;
import me.stuntguy3000.incore.handler.AnnouncementHandler;
import me.stuntguy3000.incore.handler.CacheHandler;
import me.stuntguy3000.incore.handler.CoinHandler;
import me.stuntguy3000.incore.handler.DatabaseHandler;
import me.stuntguy3000.incore.handler.LocationHandler;
import me.stuntguy3000.incore.handler.StatisticsHandler;
import me.stuntguy3000.incore.handler.UtilHandler;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class INCore extends JavaPlugin {
	
	public UtilHandler Util;
	public DatabaseHandler DB;
	public CoinHandler Coin;
	public AnnouncementHandler Announcement;
	public LocationHandler Location;
	public AchievementHandler Achievement;
	public CacheHandler Cache;
	public StatisticsHandler Statistics;
	public FireworkEffectPlayer FireworkEffectPlayer;
	
	public Logger log = Bukkit.getLogger();
	
	public ArrayList<String> flySpeed = new ArrayList<String>();
	public ArrayList<String> vanish = new ArrayList<String>();
	
	public HashMap<String, ImpulseUser> impulseusers = new HashMap<String, ImpulseUser>();
	
	public boolean deploy = false;
	public boolean physics = false;
	
	public void onEnable() {
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		saveResource("build.yml", true);
        
        this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		log = this.getLogger();
		
		Util = new UtilHandler(this);
		DB = new DatabaseHandler(this.getConfig().getString("MySQL.Host"),
				this.getConfig().getInt("MySQL.Port"),
				this.getConfig().getString("MySQL.Name"),
				this.getConfig().getString("MySQL.Username"),
				this.getConfig().getString("MySQL.Password"),
				this);
		
		Coin = new CoinHandler(this);
		Announcement = new AnnouncementHandler(this);
		Location = new LocationHandler(this);
		Achievement = new AchievementHandler(this);
		Cache = new CacheHandler(this);
		Statistics = new StatisticsHandler(this);
		FireworkEffectPlayer = new FireworkEffectPlayer();
		registerCommands();
		registerEvents();
		
		initDB();
		
		Announcement.download();
		Announcement.timer();
		Location.download();
		Cache.init();
		dbRefreshTimer();
		
		getServer().createWorld(new WorldCreator("lobby"));
		
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "IPNKBroadcast");
	}

	private void dbRefreshTimer() {
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				DB.open();
			}
		}, 20 * 600, 20 * 60 * 300);
	}

	private void initDB() {
		DB.query("CREATE TABLE IF NOT EXISTS `in_announcements` ( `id` int(11) NOT NULL AUTO_INCREMENT, `message` longtext NOT NULL, PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
		DB.query("CREATE TABLE IF NOT EXISTS `in_locations` ( `locID` varchar(32) NOT NULL, `x` double NOT NULL, `y` double NOT NULL, `z` double NOT NULL, `yaw` double NOT NULL, `pitch` double NOT NULL, `world` varchar(256) NOT NULL, UNIQUE KEY `locID` (`locID`) ) ENGINE=MyISAM DEFAULT CHARSET=utf8;");
		DB.query("CREATE TABLE IF NOT EXISTS `in_users` ( `username` varchar(16) NOT NULL, `firstJoin` varchar(125) NOT NULL, `lastJoin` varchar(125) NOT NULL, `firstIP` varchar(125) NOT NULL, `lastIP` varchar(125) NOT NULL, `tokens` int(125) NOT NULL, `achievements` longtext NOT NULL ) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
	}

	private void registerEvents() {
		this.getServer().getPluginManager().registerEvents(new FilterEvents(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
		this.getServer().getPluginManager().registerEvents(new PhysicsEvents(this), this);
	}

	private void registerCommands() {
		this.getCommand("announcer").setExecutor(new AnnouncerCommand(this));
		this.getCommand("tokens").setExecutor(new CoinsCommand(this));
		this.getCommand("setlocation").setExecutor(new SetLocationCommand(this));
		this.getCommand("restartserver").setExecutor(new RestartServerCommand(this));
		this.getCommand("teleport").setExecutor(new TeleportCommand(this));
		this.getCommand("teleporthere").setExecutor(new TeleportHereCommand(this));
		this.getCommand("coinmanagement").setExecutor(new CoinManagementCommand(this));
		this.getCommand("vanish").setExecutor(new VanishCommand(this));
		this.getCommand("fly").setExecutor(new FlyCommand(this));
		this.getCommand("togglephysics").setExecutor(new TogglePhysicsCommand(this));
		this.getCommand("playerinformation").setExecutor(new PlayerInformationCommand(this));
		this.getCommand("deployrestart").setExecutor(new DeployRestartCommand(this));
		this.getCommand("list").setExecutor(new ListCommand(this));
		this.getCommand("seen").setExecutor(new SeenCommand(this));
		this.getCommand("hub").setExecutor(new HubCommand(this));
		this.getCommand("devserver").setExecutor(new DevServerCommand(this));
	}

	public void log(String msg, Level level) {
		log.log(level, msg);	
	}

	public void updateSign(String id, String status) {
		DB.query("DELETE FROM `in_signs` WHERE `id` = '" + id + "'");
		DB.query("INSERT INTO `in_signs` (`id`, `timestamp`, `data`) VALUES "
				+ "('" + id + "', '" + new Date().getTime() + "', '" + status + "');");
	}
}
