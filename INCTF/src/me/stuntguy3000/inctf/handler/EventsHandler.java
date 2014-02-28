package me.stuntguy3000.inctf.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.confuser.barapi.BarAPI;
import me.stuntguy3000.inctf.INCTF;
import me.stuntguy3000.inctf.enums.Gamestate;
import me.stuntguy3000.inctf.enums.Team;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class EventsHandler implements Listener {
	private INCTF plugin;
	
	public EventsHandler(INCTF instance) {
		this.plugin = instance;
	}
	
	public int taskID;

	@EventHandler
	public void onConnect(AsyncPlayerPreLoginEvent event) {
		if (plugin.gs.getGameState() == Gamestate.INGAME) {
			event.disallow(Result.KICK_OTHER, "§cYou cannot join ingame matches!");
			return;
		}
		
		if (Bukkit.getOnlinePlayers().length == plugin.gs.getMaxPlayers()) {
			PermissionUser pex = PermissionsEx.getUser(event.getName());
			if (pex.has("donor.misc.reservedslot")) {
				boolean looping = true;
				boolean foundKick = false;
				int count = 0;
				while (looping) {
					try {
						final Player c = Bukkit.getOnlinePlayers()[count];
						
						if (c != null) {
							if (!c.hasPermission("donor.misc.reservedslot")) {
								foundKick = true;
								looping = false;
								
								
								Bukkit.getScheduler().runTask(plugin, new Runnable() {
									  public void run() {
										  c.kickPlayer("§cYou have been kicked by a donor/staff member! "
													+ "§cDonate today to recieve a reserved slot.");
									  }
							    });
							}
						}
					} catch (IndexOutOfBoundsException ex) {
						foundKick = false;
						looping = false;
						ex.printStackTrace();
					}
					
					count = count + 1;
				}
				
				if (!foundKick) {
					event.disallow(Result.KICK_FULL, "§cAll reserved slots have been used! Try again soon.");
					return;
				}
			} else {
				event.disallow(Result.KICK_FULL, "§cThis server is full! §cDonate today to recieve a reserved slot.");
				return;
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		
		if (plugin.hasFlag.containsKey(p.getName())) {
			Team t = plugin.gs.getTeam(p.getName());
			Team oT = plugin.hasFlag.get(p.getName());
			
			if (t == Team.RED) {
				for (Player target : Bukkit.getOnlinePlayers()) {
					plugin.INCore.Util.sendMessage(target, "§a§kAAAAA §r§c" + p.getName() + " §r§6DIED WITH §r§9BLUE'S§6 FLAG! §r§a§kAAAAA", plugin.messagePrefix);
					BarAPI.setMessage(target, "§c" + p.getName() + " §r§6DIED WITH §r§9BLUE'S§6 FLAG!", 10);
				}
			} else {
				for (Player target : Bukkit.getOnlinePlayers()) {
					plugin.INCore.Util.sendMessage(target, "§a§kAAAAA §r§9" + p.getName() + " §r§6DIED WITH §r§cRED'S§6 FLAG! §r§a§kAAAAA", plugin.messagePrefix);
					BarAPI.setMessage(target, "§c" + p.getName() + " §r§6DIED WITH §r§cRED'S§6 FLAG!", 10);
				}
			}
			
			plugin.hasFlag.remove(p.getName());
			plugin.resetFlag(oT);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		plugin.gs.removeFromTeam(p.getName());
		
		if (!(plugin.gs.getGameState() == Gamestate.INGAME)) {
			Location lobby = plugin.INCore.Location.getLocation("GLOBAL_LOBBY");
			if (lobby.getWorld() == null) {
				if (!p.hasPermission("INCTF.admin.bypass.nolobby")) p.kickPlayer("§cError! Try again later.");
			} else {
				p.teleport(lobby);
				plugin.INCore.Util.sendMessage(p, "§6Welcome to Capture the Flag!", plugin.messagePrefix);
				plugin.INCore.Util.sendMessage(p, "§aThe Chosen Map is §2" + plugin.INCore.Util.friendlyify(plugin.gs.getMap().name()), plugin.messagePrefix);
				plugin.GameHandler.checkPlayerCount();
			}
		} else {
			p.kickPlayer("§cYou cannot join ingame matches!");
			return;
		}
	
		plugin.ScoreboardHandler.updateScoreboard();
		p.setGameMode(GameMode.SURVIVAL);
		
		plugin.GameHandler.giveItems(p);
		BarAPI.setMessage(p, "§7Welcome to §eCTF§7! §3Server: §f" + plugin.serverID, 10);
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		final Player p = event.getEntity();
		
		event.getDrops().clear();

		if (plugin.hasFlag.containsKey(p.getName())) {
			Team t = plugin.gs.getTeam(p.getName());
			Team oT = plugin.hasFlag.get(p.getName());
			
			if (t == Team.RED) {
				for (Player target : Bukkit.getOnlinePlayers())
					plugin.INCore.Util.sendMessage(target, "§a§kAAAAA §r§c" + p.getName() + " §r§6DIED WITH §r§9BLUE'S§6 FLAG! §r§a§kAAAAA", plugin.messagePrefix);
			} else {
				for (Player target : Bukkit.getOnlinePlayers())
					plugin.INCore.Util.sendMessage(target, "§a§kAAAAA §r§9" + p.getName() + " §r§6DIED WITH §r§cRED'S§6 FLAG! §r§a§kAAAAA", plugin.messagePrefix);
			}
			
			plugin.hasFlag.remove(p.getName());
			plugin.resetFlag(oT);
		}
		
		if (p.getKiller() != null) {
			plugin.INCore.Coin.addCoins(p.getKiller().getName(), 1);
			event.setDeathMessage(formatDeathMessage(p, p.getKiller(), event.getDeathMessage()));
		} else event.setDeathMessage(formatDeathMessage(p, event.getDeathMessage()));
	}
	
    public String formatDeathMessage(Player victim, Player killer, String original) {
		Team killerTeam = plugin.gs.getTeam(killer.getName());
		String killerColor = killerTeam == Team.RED ? "§c" : "§9";
		String killerName = killerColor + killer.getName() + "§7";

		String message = "§7" + formatDeathMessage(victim, original);
		message = message.replace(killer.getName(), killerName);

		return message;
	}

	public String formatDeathMessage(Player victim, String original) {
		Team victimTeam = plugin.gs.getTeam(victim.getName());
		
		String victimColor = victimTeam == Team.RED ? "§c" : "§9";
		String victimName = victimColor + victim.getName() + "§7";
		
		String message = "§7" + original;
		message = message.replace(victim.getName(), victimName);

		return message;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getInventory().getName().startsWith("§0Team")) {
			Player p = (Player) event.getWhoClicked();
			
			if (!p.hasPermission("donor.game.teamselector")) {
				plugin.INCore.Util.sendMessage(p, "§cOnly VIP's can use this feature!", plugin.messagePrefix);
				plugin.INCore.Util.sendMessage(p, "§eBuy VIP and more at http://www.ImpulseNetwork.org", plugin.messagePrefix);
				event.setCancelled(true);
				p.closeInventory();
				return;
			}
			
			if (event.getCurrentItem() != null) {
				if (event.getCurrentItem().getType() == Material.WOOL) {
					if (event.getCurrentItem().getData().getData() == (byte) 11) {
						int red = 0;
						int blue = 0;
						
						for (Player t : Bukkit.getOnlinePlayers()) {
							if (plugin.gs.getTeams().containsKey(t.getName())) {
								if (plugin.gs.getTeam(p.getName()) == Team.RED) {
									red++;
								} else {
									blue++;
								}
							}
						}
						
						if (red + blue > 4) {
							plugin.INCore.Util.sendMessage(p, "§7To keep it balanced, no more players can specify what team they would like to join.", plugin.messagePrefix);
						} else {
							plugin.INCore.Util.sendMessage(p, "§7You are on the §9Blue§7 team.", plugin.messagePrefix);
							plugin.gs.addToTeam(p, Team.BLUE);
							plugin.ScoreboardHandler.sb.getTeam("Blue").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
						}
					} else {
						int red = 0;
						int blue = 0;
						
						for (Player t : Bukkit.getOnlinePlayers()) {
							if (plugin.gs.getTeams().containsKey(t.getName())) {
								if (plugin.gs.getTeam(p.getName()) == Team.RED) {
									red++;
								} else {
									blue++;
								}
							}
						}
						
						if (red + blue > 4) {
							plugin.INCore.Util.sendMessage(p, "§7To keep it balanced, no more players can specify what team they would like to join.", plugin.messagePrefix);
						} else {
							plugin.INCore.Util.sendMessage(p, "§7You are on the §cRed§7 team.", plugin.messagePrefix);
							plugin.gs.addToTeam(p, Team.RED);
							plugin.ScoreboardHandler.sb.getTeam("Red").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
						}
					}
					
					p.closeInventory();
				}
			}
		}
		
		if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().contains("Armor"))
			event.setCancelled(true);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (plugin.gs.getGameState() == Gamestate.INGAME) return;
		
		if (event.getAction().name().startsWith("RIGHT")) {
			if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.ENCHANTED_BOOK) {
				event.setCancelled(true);
				plugin.openTeamSelection(event.getPlayer());
			}
			
			if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getTypeId() == 351) {
				event.setCancelled(true);
				
				String hub = "HUB_01";
				
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);
				
				try {
					out.writeUTF("Connect");
					out.writeUTF(hub);
				} catch (IOException ex) {
					
				}
				
				event.getPlayer().sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
			}
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		plugin.GameHandler.giveItems(event.getPlayer());
		
		if (plugin.gs.getGameState() == Gamestate.INGAME) {
			event.setRespawnLocation(plugin.INCore.Location.getLocation("CaptureTheFlag_" + plugin.gs.getMap().name() + "_" + plugin.gs.getTeam(event.getPlayer().getName()) + "_Spawn"));
		} else {
			event.getPlayer().teleport(plugin.INCore.Location.getLocation("GLOBAL_LOBBY"));
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		
		event.setCancelled(true);
		Player p = event.getPlayer();
		
		if (event.getBlock().getType() != Material.WOOL)
			return;
		
		if (plugin.redFlag.distance(event.getBlock().getLocation()) < 5) {
			if (plugin.gs.getTeam(event.getPlayer().getName()) == Team.RED) {
				plugin.INCore.Util.sendMessage(event.getPlayer(), "§cYou cannot break your own team's flag!", plugin.messagePrefix);
				event.getPlayer().damage(1D);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 5));
			} else {
				for (Player target : Bukkit.getOnlinePlayers()) {
					plugin.INCore.Util.sendMessage(target, "§a§kLOLOL §r§9" + p.getName() + " §r§6TOOK §cRED'S§r§6 FLAG! §r§a§kROFLL", plugin.messagePrefix);
					BarAPI.setMessage(target, "§9" + p.getName() + " §r§6TOOK §cRED'S§r§6 FLAG!", 10);
				}
				plugin.takeFlag(p, Team.RED);
			}
		}
		if (plugin.blueFlag.distance(event.getBlock().getLocation()) < 5) {
			if (plugin.gs.getTeam(event.getPlayer().getName()) == Team.BLUE) {
				plugin.INCore.Util.sendMessage(event.getPlayer(), "§cYou cannot break your own team's flag!", plugin.messagePrefix);
				event.getPlayer().damage(1D);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 5));
			} else {
				for (Player target : Bukkit.getOnlinePlayers()) {
					plugin.INCore.Util.sendMessage(target, "§a§kLOLOL §r§c" + p.getName() + " §r§6TOOK §9BLUE'S§r§6 FLAG! §r§a§kROFLL", plugin.messagePrefix);	
					BarAPI.setMessage(target, "§c" + p.getName() + " §r§6TOOK §9BLUE'S§r§6 FLAG!", 10);
				}
				plugin.takeFlag(p, Team.BLUE);
			}
		}
	}
	
	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		if (plugin.hasFlag.containsKey(p.getName())) {
			Team t = plugin.gs.getTeam(p.getName());
			Team oT = plugin.hasFlag.get(p.getName());
			
			if (t == Team.RED) {
				if (p.getLocation().distance(plugin.redFlag) < 10) {
					for (Player target : Bukkit.getOnlinePlayers()) {
						plugin.INCore.Util.sendMessage(target, 
								"§a§kLOLOL §r§c" + p.getName() + " §r§6CAPTURED §r§9BlUE'S§6 FLAG! §r§a§kROFLL", plugin.messagePrefix);
						
						BarAPI.setMessage(target, "§c" + p.getName() + " §r§6CAPTURED §r§9BlUE'S§6 FLAG!", 10);
						
						if (plugin.gs.getTeam(target.getName()) == oT) {
							target.playSound(target.getLocation(), Sound.EXPLODE, 1, 1);
						} else target.playSound(target.getLocation(), Sound.LEVEL_UP, 1, 1);
					}
					
					plugin.INCore.Coin.addCoins(p.getName(), 50);
					
					plugin.hasFlag.remove(p.getName());
					plugin.resetFlag(oT);
					plugin.addCapture(t);
					p.getInventory().clear();
					plugin.GameHandler.giveItems(p);
				}
			} else {
				if (p.getLocation().distance(plugin.blueFlag) < 10) {
					for (Player target : Bukkit.getOnlinePlayers()) {
						plugin.INCore.Util.sendMessage(target, 
								"§a§kLOLOL §r§9" + p.getName() + " §r§6CAPTURED §r§cRED'S§6 FLAG! §r§a§kROFLL", plugin.messagePrefix);
						
						BarAPI.setMessage(target, "§9" + p.getName() + " §r§6CAPTURED §r§cRED'S§6 FLAG!", 10);
						
						if (plugin.gs.getTeam(target.getName()) == oT) {
							target.playSound(target.getLocation(), Sound.EXPLODE, 1, 1);
						} else target.playSound(target.getLocation(), Sound.LEVEL_UP, 1, 1);
					}
					
					plugin.INCore.Coin.addCoins(p.getName(), 50);
					
					plugin.hasFlag.remove(p.getName());
					plugin.resetFlag(oT);
					plugin.addCapture(t);
					p.getInventory().clear();
					plugin.GameHandler.giveItems(p);
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (plugin.gs.getGameState() != Gamestate.INGAME)
				event.setCancelled(true);
		} else {
			event.getEntity().remove();
		}
		
		if (event.getCause() == DamageCause.BLOCK_EXPLOSION)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Entity projectile = event.getEntity();
		
		if(!(projectile instanceof Arrow))
	        return;

		Arrow arrow = (Arrow)projectile;
	    arrow.remove();
	}
}