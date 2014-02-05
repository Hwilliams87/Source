package me.stuntguy3000.incore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.stuntguy3000.incore.INCore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CoinHandler implements Listener {
	private INCore plugin;
	
	public CoinHandler (INCore plugin) {
		this.plugin = plugin;
	}

	public String getCoins(String username) throws SQLException {
		ResultSet rs = plugin.DB.query("SELECT * FROM in_users WHERE `username`='" + username + "'").getResultSet();
		
		while (rs.next()) {
			return String.valueOf(rs.getInt("coins"));
		}
		
		return null;
	}
	
	public void setAllCoins(String username, int coins) {
		plugin.DB.query("UPDATE in_users SET `coins`='"+ coins +"' WHERE `username`='" + username + "'");
	}
	
	public int addCoins(String username, int value) {
		Player p = Bukkit.getPlayer(username);
		int coins = value;
		
		if (p == null) {
			plugin.DB.query("UPDATE in_users SET "
					+ "`coins`= `coins` + "+ coins +", `coinsEarned`=`coinsEarned` + "+ coins +" WHERE `username`='" + username + "'");
			return coins;
		}
		
		if (p.hasPermission("incore.doubleCoins"))
			coins = coins * 2;
		
		plugin.DB.query("UPDATE in_users SET "
					+ "`coins`= `coins` + "+ coins +", `coinsEarned`=`coinsEarned` + "+ coins +" WHERE `username`='" + username + "'");
		
		if (coins > 1) {
			plugin.Util.sendMessage(p, "§aYou have been given §b" + coins + " Coins§a.", "§8[§bCoins§8] §a");
		} else plugin.Util.sendMessage(p, "§aYou have been given §b1 Coin§a.", "§8[§bCoins§8] §a");
		
		return coins;
	}
	
	public int takeCoins(String username, int value) {
		Player p = Bukkit.getPlayer(username);
		int coins = value;
		
		if (p == null) {
			plugin.DB.query("UPDATE in_users SET `coins`= `coins` - " + coins + " WHERE `username`='" + username + "'");
			return coins;
		}
		
		if (p.hasPermission("incore.doubleCoins"))
			coins = coins * 2;
		
		plugin.DB.query("UPDATE in_users SET `coins`= `coins` - " + coins + " WHERE `username`='" + username + "'");
		
		if (coins > 1) {
			plugin.Util.sendMessage(p, "§aYou have lost §c" + coins + " Coins§a.", "§8[§bCoins§8] §a");
		} else plugin.Util.sendMessage(p, "§aYou have lost §c1 Coin§a.", "§8[§bCoins§8] §a");
		
		return coins;
	}
}
