package me.stuntguy3000.incore.command;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.stuntguy3000.incore.INCore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class CoinManagementCommand implements CommandExecutor {
	public INCore plugin;
	
	public CoinManagementCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender.hasPermission("staff.administration.coinmanagement") ) {
			if (args.length < 2 || args.length > 3) {
				plugin.Util.sendMessage(sender, "&6&lCoin Management help menu", true);
				plugin.Util.helpMenu(sender, "/cm get <player>", "View a player's amount of coins");
				plugin.Util.helpMenu(sender, "/cm set <player> <amount>", "Set a player's amount of coins");
				plugin.Util.helpMenu(sender, "/cm add <player> <amount>", "Give coins to a player");
				plugin.Util.helpMenu(sender, "/cm take <player> <amount>", "Take coins from a player");
			}
			
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("get")) {
					if (sender.hasPermission("INCore.coinmanagement.get")) {
						String player = args[1];
						
						checkExists(player);
						
						ResultSet rs = plugin.DB.query("SELECT * FROM `in_users` WHERE `username`='" + player + "'").getResultSet();
						
						try {
							if (!rs.isBeforeFirst()) {
								plugin.Util.sendMessage(sender, "&7Player not found.", true);
							} else {
								while (rs.next()) {
									plugin.Util.sendMessage(sender, "&e" + player + " &6has &e" + rs.getInt("coins") + " &6coins.", true);
								}
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						plugin.Util.noPerm((Player) sender); 
					}
					
					return true;
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("set")) {
					if (sender.hasPermission("INCore.coinmanagement.set")) {
						String player = args[1];
						checkExists(player);
						
						String amount = args[2];
						
						int am = 0;
						
						try {
							am = Integer.parseInt(amount);
						} catch (NumberFormatException ex) {
							plugin.Util.sendMessage(sender, "&7Invalid amount.", true);
							return true;
						}
						
						plugin.Coin.setAllCoins(player, am);
						plugin.Util.sendMessage(sender, "&e" + player + " &6now has &e" + am + " &6coins.", true);
					} else {
						plugin.Util.noPerm((Player) sender); 
					}
					
					return true;
				}
				
				if (args[0].equalsIgnoreCase("add")) {
					if (sender.hasPermission("INCore.coinmanagement.add")) {
						String player = args[1];
						checkExists(player);
						
						String amount = args[2];
						
						int am = 0;
						
						try {
							am = Integer.parseInt(amount);
						} catch (NumberFormatException ex) {
							plugin.Util.sendMessage(sender, "&7Invalid amount.", true);
							return true;
						}
						
						try {
							plugin.Coin.setAllCoins(player, (Integer.parseInt(plugin.Coin.getCoins(player)) + am));
							plugin.Util.sendMessage(sender, "&e" + player + " &6now has been given &e" + am + " &6coins.", true);
						} catch (NumberFormatException ex) {
							plugin.Util.sendMessage(sender, "&7Thats not a number.", true);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						plugin.Util.noPerm((Player) sender); 
					}
					
					return true;
				}
				
				if (args[0].equalsIgnoreCase("take")) {
					if (sender.hasPermission("INCore.coinmanagement.take")) {
						String player = args[1];
						String amount = args[2];
						
						int am = 0;
						
						try {
							am = Integer.parseInt(amount);
						} catch (NumberFormatException ex) {
							plugin.Util.sendMessage(sender, "&7Invalid amount.", true);
							return true;
						}
						
						try {
							plugin.Coin.setAllCoins(player, (Integer.parseInt(plugin.Coin.getCoins(player)) - am));
							plugin.Util.sendMessage(sender, "&e" + player + " &6now has lost &e" + am + " &6coins.", true);
						} catch (NumberFormatException ex) {
							plugin.Util.sendMessage(sender, "&7Player not found.", true);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						plugin.Util.noPerm((Player) sender); 
					}
					
					return true;
				}
			}
			
			plugin.Util.sendMessage(sender, "&7Type /cm for command help", true);
			return true;
		} else plugin.Util.noPerm((Player) sender);
		
		return true;
	}

	private void checkExists(String player) {
		try {
			if (!plugin.DB.query("SELECT * FROM `in_users` WHERE `username`='" + player + "'").getResultSet().isBeforeFirst()) {
				plugin.DB.query("INSERT INTO `in_users` "
						+ "(`username`, `firstJoin`, `lastJoin`, `firstIP`, `lastIP`, `coins`, `achievements`, `stats`) "
						+ "VALUES "
						+ "('" + player + "', "
						+ "'0', "
						+ "'0', "
						+ "'0', "
						+ "'0', "
						+ "'0', "
						+ "'', '');");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
