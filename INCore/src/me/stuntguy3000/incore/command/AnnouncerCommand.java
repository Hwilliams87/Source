package me.stuntguy3000.incore.command;

import me.stuntguy3000.incore.INCore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class AnnouncerCommand implements CommandExecutor {
	public INCore plugin;
	
	public AnnouncerCommand(INCore instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if (args.length == 1) {
				if (args[0].equals("help")) {
					if (p.hasPermission("staff.administration.announcer")) {
						plugin.Util.sendMessage(p, "&6&lAnnouncer help menu", true);
						plugin.Util.helpMenu(p, "/announcer list", "View all announcements.");
						plugin.Util.helpMenu(p, "/announcer add <String>", "View all announcements.");
						plugin.Util.helpMenu(p, "/announcer remove <ID>", "View all announcements.");
						plugin.Util.helpMenu(p, "/announcer sync", "Sync all announcements.");
						plugin.Util.helpMenu(p, "/announcer now <id>", "Broadcast an announcement.");
					} else {
						plugin.Util.noPerm(p);
					}
					return true;
				}
				
				if (args[0].equals("list")) {
					if (p.hasPermission("staff.administration.announcer")) {
						if (plugin.Announcement.announcements.size() == 0) {
							plugin.Util.sendMessage(p, "&cThere are no announcements.", true);
							return true;
						}
						
						plugin.Util.sendMessage(p, "&6&lAnnouncements:", true);
						int count = 0;
						
						for (String announcement : plugin.Announcement.announcements) {
							count ++;
							plugin.Util.sendMessage(p, " &8(&7" + count + "&8) &r&7" + announcement, false);
						}
					} else {
						plugin.Util.noPerm(p);
					}
					return true;
				}
				
				if (args[0].equals("sync")) {
					if (p.hasPermission("staff.administration.announcer")) {
						plugin.Util.sendMessage(p, "&aAnnouncements have been downloaded.", true);
						plugin.Announcement.download();
					} else {
						plugin.Util.noPerm(p);
					}
					return true;
				}
			}
			
			if (args.length == 2) {
				if (args[0].equals("remove")) {
					if (p.hasPermission("staff.administration.announcer")) {
						try {
							int val = Integer.parseInt(args[1]);
							if (plugin.Announcement.exists(val)) {
								plugin.Util.sendMessage(p, "Announcement removed.", true);
								plugin.Announcement.remove(val);
							} else {
								plugin.Util.sendMessage(p, "&cInvalid announcement.", true);
							}
						} catch (NumberFormatException e) {
							plugin.Util.sendMessage(p, "&cEnter a valid number.", true);
						}
					} else {
						plugin.Util.noPerm(p);
					}
					return true;
				}
			}

			if (args.length > 1) {
				if (args[0].equals("add")) {
					if (p.hasPermission("staff.administration.announcer")) {
						StringBuilder sb = new StringBuilder();
						for (int i = 1; i < args.length; i++){
							sb.append(args[i]).append(" ");
						}
						 
						String allArgs = sb.toString().trim();
						plugin.Announcement.add(allArgs);
						plugin.Util.sendMessage(p, "Announcement added.", true);
					} else {
						plugin.Util.noPerm(p);
					}
					return true;
				}
				
				if (args[0].equals("now")) {
					if (p.hasPermission("staff.administration.announcer")) {
						try {
							int val = Integer.parseInt(args[1]);
							if (plugin.Announcement.exists(val)) {
								plugin.Announcement.announceMessage(val);
							} else {
								plugin.Util.sendMessage(p, "&cInvalid announcement.", true);
							}
						} catch (NumberFormatException e) {
							plugin.Util.sendMessage(p, "&cEnter a valid number.", true);
						}
					} else {
						plugin.Util.noPerm(p);
					}
					return true;
				}
			}
			plugin.Util.sendMessage(p, "&cInvalid command.", true);
			plugin.Util.sendMessage(p, "For help type /announcer help", true);
		} else {
			
		}
		return false;
	}

}
