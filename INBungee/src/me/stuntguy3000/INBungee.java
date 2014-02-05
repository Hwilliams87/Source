package me.stuntguy3000;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class INBungee extends Plugin implements Listener {

	public boolean whitelist = false;
	public List<String> whitelisted = new ArrayList<String>();
	public int max = 1000;
	
	public String messagePrefix = "§8[§aImpulse§8] §7";
	public String noPermission = messagePrefix + "§cYou do not have permission to perform this action!";
	
	public HashMap<String, String> messagesToReturn = new HashMap<String, String>();
	public HashMap<String, ToggleOptions> toggleOptions = new HashMap<String, ToggleOptions>();
	
	@Override
    public void onEnable() {
		whitelisted.add("stuntguy3000");
		whitelisted.add("PixelSwiftYT");
		whitelisted.add("DavisA20");
		whitelisted.add("SubZeroExtabyte");
		whitelisted.add("dogryan100");
		
		this.getProxy().getPluginManager().registerCommand(this, new Command("privatemessage", "inbungee.command.privatemessage", "pm", "msg") {
	    	@Override
				public void execute(CommandSender sender, String[] args) {
	    			if (!toggleOptions.containsKey(sender.getName()))
		    			toggleOptions.put(sender.getName(), new ToggleOptions());
		    		
		    		if (args.length < 2) {
							sender.sendMessage(msg(messagePrefix + "Syntax: /pm <player> <message>"));
							return;
						}
						
						String reciepent = args[0];
						ProxiedPlayer p = getProxy().getPlayer(reciepent);
						
						if (p == null) {
							sender.sendMessage(msg(messagePrefix + "§cSpecified player is not online."));
							return;
						}
						
						if (p.getName().equals(sender.getName())) {
							sender.sendMessage(msg(messagePrefix + "§cYou cannot send yourself a message."));
							return;
						}
						
						StringBuilder msg = new StringBuilder();
						
						for (String arg : args)
							if (!arg.equals(reciepent)) msg.append(" " + arg);
						
						if (toggleOptions.get(p.getName()).isAllowPrivateMessages()) {
							sender.sendMessage(msg("§8[§7PM§8] §8(§7To§8) §f" + reciepent + "§8:§f" + ChatColor.stripColor(msg.toString())));
							p.sendMessage(msg("§8[§7PM§8] §8(§7From§8) §f" + sender.getName() + "§8:§f" + ChatColor.stripColor(msg.toString())));
						} else {
							sender.sendMessage(msg(messagePrefix + "§b" + p.getName() + " §chas disabled private messaging."));
						}
					
						messagesToReturn.put(sender.getName(), p.getName());
						messagesToReturn.put(p.getName(), sender.getName());
	    	}
	    });
	    
		this.getProxy().getPluginManager().registerCommand(this,
	        new Command("Respond", "inbungee.command.respond", "r") {
	        @Override
	    		public void execute(CommandSender sender, String[] args) {
	        	if (!toggleOptions.containsKey(sender.getName()))
	    			toggleOptions.put(sender.getName(), new ToggleOptions());
	        	
	        	if (args.length < 1) {
	    				sender.sendMessage(msg(messagePrefix + "Syntax: /r <message>"));
	    				return;
	    			}
	    				
	    			if (!messagesToReturn.containsKey(sender.getName())) {
	    				sender.sendMessage(msg(messagePrefix + "§cYou have no message to respond to."));
	    				return;
	    			}
	    			
	    			String reciepent = messagesToReturn.get(sender.getName());
	    			ProxiedPlayer p = getProxy().getPlayer(reciepent);
					
					if (p == null) {
						sender.sendMessage(msg(messagePrefix + "§cThe player you are responding to disconnected."));
						messagesToReturn.remove(sender.getName());
						return;
					}
					
					StringBuilder msg = new StringBuilder();
					
					for (String arg : args)
						msg.append(arg + " ");
					
					if (toggleOptions.get(p.getName()).isAllowPrivateMessages()) {
						sender.sendMessage(msg("§8[§7PM§8] §8(§7To§8) §f" + reciepent + "§8:§f " + ChatColor.stripColor(msg.toString())));
						p.sendMessage(msg("§8[§7PM§8] §8(§7From§8) §f" + sender.getName() + "§8:§f " + ChatColor.stripColor(msg.toString())));
					} else {
						sender.sendMessage(msg(messagePrefix + "§b" + p.getName() + " §chas disabled private messaging."));
					}
					
					messagesToReturn.put(sender.getName(), p.getName());
					messagesToReturn.put(p.getName(), sender.getName());
	        }
	    });
	    
	    this.getProxy().getPluginManager().registerCommand(this,
	    		new Command("FindPlayer", "inbungee.command.findplayer", "findp", "whereis", "f", "fp") {
	        @Override
	        public void execute(CommandSender sender, String[] args) {
	        	if (!toggleOptions.containsKey(sender.getName()))
	        			toggleOptions.put(sender.getName(), new ToggleOptions());
	        	
	        	if (args.length < 1) {
	    				sender.sendMessage(msg(messagePrefix + "§cSyntax: /findplayer <player>"));
	    				return;
	    			}
	        	
	        	String reciepent = args[0];
	    			ProxiedPlayer p = getProxy().getPlayer(reciepent);
	    				
	    				if (p == null) {
	    					sender.sendMessage(msg(messagePrefix + "§cThe player you specified is not online."));
	    					return;
	    				}
	    				
	    				if (toggleOptions.get(sender.getName()).isAllowServerLookup()) {
	    					sender.sendMessage(msg(messagePrefix + "§b" + p.getName() + " §fis on §6" + friendlyify(p.getServer().getInfo().getName())));
	    				p.sendMessage(msg(messagePrefix + "§b" + sender.getName() + " §fhas been told what server you are on."));
	    				} else {
	    					sender.sendMessage(msg(messagePrefix + "§b" + sender.getName() + " §chas disabled server lookups."));
	    				}
	        }
	    });
	    
	    this.getProxy().getPluginManager().registerCommand(this,
	    		new Command("ToggleOptions", "inbungee.command.toggleoptions", "toggleo", "options", "o") {
	        @Override
	        public void execute(CommandSender sender, String[] args) {
	        	if (!toggleOptions.containsKey(sender.getName()))
	        			toggleOptions.put(sender.getName(), new ToggleOptions());
	        	
	        	if (args.length < 1) {
	        		StringBuilder optionsList = new StringBuilder();
	        		
	        		if (toggleOptions.get(sender.getName()).isAllowPrivateMessages()) 
	        			optionsList.append("§aPrivateMessages§7, "); else optionsList.append("§cPrivateMessages§7, ");
	        		
	        		if (toggleOptions.get(sender.getName()).isAllowServerLookup()) 
	        			optionsList.append("§aServerLookup§7."); else optionsList.append("§cServerLookup§7.");
	        		
	        		sender.sendMessage(msg(messagePrefix + "§bOptions: " + optionsList.toString()));
	        		sender.sendMessage(msg(messagePrefix + "§7Toggle these options with /o <name>"));
	        		return;
	    			}
	        	
	        	String input = args[0];
	        	if (input.equalsIgnoreCase("PrivateMessages")) {
	        		toggleOptions.get(sender.getName()).setAllowPrivateMessages(!toggleOptions.get(sender.getName()).isAllowPrivateMessages());
	        	} else if (input.equalsIgnoreCase("ServerLookup")) {
	        		toggleOptions.get(sender.getName()).setAllowServerLookup(!toggleOptions.get(sender.getName()).isAllowServerLookup());
	        	} else {
	        		sender.sendMessage(msg(messagePrefix + "§cInvalid option specified."));
	        		return;
	        	}
	        	
	        	StringBuilder optionsList = new StringBuilder();
	        		
	        		if (toggleOptions.get(sender.getName()).isAllowPrivateMessages()) 
	        			optionsList.append("§aPrivateMessages§7, "); else optionsList.append("§cPrivateMessages§7, ");
	        		
	        		if (toggleOptions.get(sender.getName()).isAllowServerLookup()) 
	        			optionsList.append("§aServerLookup§7."); else optionsList.append("§cServerLookup§7.");
	        		
	        		sender.sendMessage(msg(messagePrefix + "§bNew Options: " + optionsList.toString()));
	        		sender.sendMessage(msg(messagePrefix + "§7Toggle these options with /o <name>"));
	        }
	    });
		
		this.getProxy().getPluginManager().registerListener(this, this);
		BungeeCord.getInstance().registerChannel("IPNKBroadcast");
		
		this.getProxy().getPluginManager().registerCommand(this, new Command("rebootkick", "inbungee.command.rebootkick", "rbkick") {
				@Override
				public void execute(CommandSender arg0, String[] arg1) {
					String prefix = "§3ImpulseNetwork §8» §6";
					for (ProxiedPlayer pp : getProxy().getPlayers()) {
						pp.disconnect(msg(prefix + "Network Restart! \n\n§eThe ImpulseNetwork will be online in around §b2§e minutes.\n\n§aCheck our website at http://www.ImpulseNetwork.org"));
        			}
				}
		});
		
		this.getProxy().getPluginManager().registerCommand(this, new Command("globalwhitelist", "inbungee.command.globalwhitelist", "wlist") {
	        @Override
	        public void execute(CommandSender sender, String[] args) {
	        	String prefix = "§8[§eGlobal Whitelist§8] §7";
	        	if (args.length == 0) {
	        		sender.sendMessage(msg(prefix + "§fWhitelist Status: " + (whitelist ? "§aEnabled" : "§cDisabled")));
		        	sender.sendMessage(msg(prefix + "§7/wlist <on/enable> §8- §7Enable the Whitelist"));
	        		sender.sendMessage(msg(prefix + "§7/wlist <off/disable> §8- §7Disable the Whitelist"));
	        		sender.sendMessage(msg(prefix + "§7/wlist kick §8- §7Kick all non-whitelisted players"));
	        		return;
	        	}
	        	
	        	if (args.length == 1) {
	        		String third = args[0];
	        		
	        		if (third.equalsIgnoreCase("on") || third.equalsIgnoreCase("enable")) {
	        			whitelist = true;
	        			sender.sendMessage(msg(prefix + "§7Whitelist §aEnabled."));
	    	        	sender.sendMessage(msg(prefix + "§fWhitelist Status: " + (whitelist ? "§aEnabled" : "§cDisabled")));
	        			return;
	        		} else if (third.equalsIgnoreCase("off") || third.equalsIgnoreCase("disable")) {
	        			whitelist = false;
	        			sender.sendMessage(msg(prefix + "§7Whitelist §cDisabled."));
	    	        	sender.sendMessage(msg(prefix + "§fWhitelist Status: " + (whitelist ? "§aEnabled" : "§cDisabled")));
	        			return;
	        		} else if (third.equalsIgnoreCase("kick")){
	        			for (ProxiedPlayer pp : getProxy().getPlayers()) {
	        				if (whitelisted.contains(pp.getName())) {
	        					pp.sendMessage(msg(prefix + "§6You have not been kicked due to the Whitelist"));
	        				} else {
	        					pp.disconnect(msg("§cNetwork under Whitelist! Check back later..."));
	        				}
	        			}
	        			return;
	        		}
	        	}
	        	
	        	sender.sendMessage(msg(prefix + "§cInvalid command!"));
	        	sender.sendMessage(msg(prefix + "§fWhitelist Status: " + (whitelist ? "§aEnabled" : "§cDisabled")));
	        }
	    });
	}
	
	public TextComponent msg(String input) {
		return new TextComponent(input);
	}
	
    @EventHandler
	public void onPing(ProxyPingEvent event) {
		ServerPing sp = event.getResponse();
		Players p = sp.getPlayers();
		
		p.setMax(max);
		sp.setDescription("       §6>> " + getRandom() + "ImpulseNetwork §7- " + getRandom() + "ImpulseNetwork.org §6<<\n"
				+ "§eUse the code §bLAUNCH §efor 25% off in our shop!");
		sp.setPlayers(p);
		
		event.setResponse(sp);
	}
	
	private String getRandom() {
		List<String> colours = new ArrayList<String>();
		
		colours.add("§f");
		colours.add("§6");
		colours.add("§e");
		colours.add("§a");
		colours.add("§2");
		colours.add("§5");
		colours.add("§c");
		colours.add("§4");
		colours.add("§b");
		colours.add("§9");
		
		return colours.get(new Random().nextInt(colours.size()));
	}

	@EventHandler
	public void onJoin(LoginEvent event) {
		if (whitelist && !whitelisted.contains(event.getConnection().getName())) {
			event.setCancelled(true);
			event.setCancelReason("§cNetwork under Whitelist! Check back later...");
		}
	}
	
	@EventHandler
	public void onServerJoin(ServerConnectEvent event) {
		if (!toggleOptions.containsKey(event.getPlayer().getName())) toggleOptions.put(event.getPlayer().getName(), new ToggleOptions());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onKick(ServerKickEvent e) {
		ProxiedPlayer p = e.getPlayer();
		ServerInfo kickedFrom = null;
		
        if (e.getPlayer().getServer() != null) {
            kickedFrom = e.getPlayer().getServer().getInfo();
        } else if (this.getProxy().getReconnectHandler() != null) {
            kickedFrom = this.getProxy().getReconnectHandler().getServer(e.getPlayer());
        } else {
            kickedFrom = AbstractReconnectHandler.getForcedHost(e.getPlayer().getPendingConnection());
            if (kickedFrom == null) kickedFrom = ProxyServer.getInstance().getServerInfo(e.getPlayer().getPendingConnection().getListener().getDefaultServer());
        }
		
        if (!kickedFrom.getName().startsWith("HUB")) {
			String hub = "HUB_01";
			
			e.setCancelled(true);
            e.setCancelServer(ProxyServer.getInstance().getServerInfo(hub));
            
            p.sendMessage("§cDisconnected: §7" + e.getKickReason());
		}
	}
	
	@EventHandler
    public void onMessageRecieve(PluginMessageEvent e) {
		if (e.getTag().equals("IPNKBroadcast")){
        	DataInputStream di = new DataInputStream(new ByteArrayInputStream(e.getData()));
        	
        	try {
            	String somedata = di.readUTF();
            	
            	for (ServerInfo s : BungeeCord.getInstance().getServers().values())
            		sendMessageToServer(somedata, s.getName());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
		}
	}
	
	public void sendMessageToServer(String msg, String server){
        ServerInfo si = ProxyServer.getInstance().getServerInfo(server);
        
        TextComponent t = new TextComponent(msg);
        
        for (ProxiedPlayer p : si.getPlayers())
            p.sendMessage(t);
	}
	
	private String friendlyify(String name) {
		String server = name;
		
		server = server.replaceAll("_", "");
		server = server.substring(0,1).toUpperCase() + server.substring(1).toLowerCase();
		
		return server;
	}
}
