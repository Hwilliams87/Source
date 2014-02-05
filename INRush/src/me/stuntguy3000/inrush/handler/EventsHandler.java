package me.stuntguy3000.inrush.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.confuser.barapi.BarAPI;
import me.stuntguy3000.inrush.INRush;
import me.stuntguy3000.inrush.enums.CaptureZone;
import me.stuntguy3000.inrush.enums.Gamestate;
import me.stuntguy3000.inrush.enums.Kit;
import me.stuntguy3000.inrush.enums.Team;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class EventsHandler implements Listener {
	private INRush plugin;
	
	public EventsHandler(INRush instance) {
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
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		plugin.gs.removeFromTeam(p.getName());
		
		if (plugin.gs.getGameState() == Gamestate.WAITING || plugin.gs.getGameState() == Gamestate.WARMUP) {
			Location lobby = plugin.INCore.Location.getLocation("GLOBAL_LOBBY");
			if (lobby.getWorld() == null) {
				if (!p.hasPermission("INSiege.admin.bypass.nolobby")) p.kickPlayer("§cError! Try again later.");
				return;
			} else {
				p.teleport(lobby);
				plugin.INCore.Util.sendMessage(p, "§6Welcome to Rush!", plugin.messagePrefix);
				plugin.INCore.Util.sendMessage(p, "§aThe Chosen Map is §2" + plugin.INCore.Util.friendlyify(plugin.gs.getMap().name()), plugin.messagePrefix);
				plugin.GameHandler.checkPlayerCount();
			}
			
			plugin.ScoreboardHandler.updateScoreboard();
			plugin.GameHandler.giveItems(p);
			plugin.KitHandler.selectedKits.put(p.getName(), Kit.ARCHER);
			BarAPI.setMessage(p, "§7Welcome to §eRush§7! §3Server: §f" + plugin.serverID, 10);
		} else p.kickPlayer("§cThis game is already in process!");
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				plugin.GameHandler.checkPlayerCount();
			}
			
		}, 20L);
	}
	
	public List<String> flight = new ArrayList<String>();
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		if (event.getTo().getBlock() != null && event.getTo().getBlock().getType() == Material.IRON_PLATE
				&& event.getTo().getBlock().getRelative(0, -1, 0).getType() == Material.EMERALD_BLOCK) {
			p.setVelocity(p.getLocation().getDirection().setY(2).setX(0).setZ(0));
            p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 1.0F, 1.0F);
            p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 0);
		} else {
			if (!(plugin.gs.getGameState() == Gamestate.INGAME)) return;
			if (flight.contains(p.getName())) return;
			if (!(plugin.KitHandler.getKit(p.getName()) == Kit.SCOUT)) return;
			
			if ((event.getPlayer().getGameMode() != GameMode.CREATIVE) && (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR))
				event.getPlayer().setAllowFlight(true);
		}
	}
	
	@EventHandler
	public void onFly(PlayerToggleFlightEvent event) {
		final Player p = event.getPlayer();
		
		if (p.getGameMode() != GameMode.CREATIVE) {
			flight.add(p.getName());
			event.setCancelled(true);
		    p.setAllowFlight(false);
		    p.setFlying(false);
		    p.setVelocity(p.getLocation().getDirection().multiply(1.6D).setY(1.0D));	
		    p.getLocation().getWorld().playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1.0F, -5.0F);
		    
		    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					flight.remove(p.getName());
					plugin.INCore.Util.sendMessage(p, "&7Doublejump recharged.", plugin.messagePrefix);
				}
		    }, 20 * 10);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (plugin.gs.getGameState() == Gamestate.INGAME) {
			if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.COMPASS) {
				event.setCancelled(true);
				
				Random generator = new Random();
				Object[] values = plugin.zones.values().toArray();
				Object randomValue = values[generator.nextInt(values.length)];
				
				CaptureZone cz = (CaptureZone) randomValue;
				String zone = cz.getZoneName();
				
				ItemStack c = plugin.GameHandler.COMPASS;
				ItemMeta cs = c.getItemMeta();
				cs.setDisplayName("§7Pointing to Zone §e" + zone );
				
				c.setItemMeta(cs);
				
				event.getPlayer().setCompassTarget(cz.getLocation());
				event.getPlayer().setItemInHand(c);
			}
			return;
		}
		
		if (event.getAction().name().startsWith("RIGHT")) {
			if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.EMERALD) {
				event.setCancelled(true);
				plugin.KitHandler.openKit(event.getPlayer());
			}
			
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
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		final Player p = event.getEntity();
		
		event.getDrops().clear();
		
		if (p.getKiller() != null) {
			plugin.INCore.Coin.addCoins(p.getKiller().getName(), 1);
			event.setDeathMessage(formatDeathMessage(p, p.getKiller(), event.getDeathMessage()));
		} else event.setDeathMessage(formatDeathMessage(p, event.getDeathMessage()));
	}
	
    public String formatDeathMessage(Player victim, Player killer, String original) {
		Team killerTeam = plugin.gs.getTeam(killer.getName());
		String killerColor = killerTeam == Team.ROYAL ? "§6" : "§c";
		String killerName = killerColor + killer.getName() + "§7";

		String message = "§7" + formatDeathMessage(victim, original);
		message = message.replace(killer.getName(), killerName);

		return message;
	}

	public String formatDeathMessage(Player victim, String original) {
		Team victimTeam = plugin.gs.getTeam(victim.getName());
		
		String victimColor = victimTeam == Team.ROYAL ? "§6" : "§c";
		String victimName = victimColor + victim.getName() + "§7";
		
		String message = "§7" + original;
		message = message.replace(victim.getName(), victimName);

		return message;
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		final Player p = event.getPlayer();
		
		if (plugin.gs.getGameState() == Gamestate.INGAME) {
			event.setRespawnLocation(plugin.SpawnHandler.spawnGetLocation(p));
		} else {
			Location lobby = plugin.INCore.Location.getLocation("GLOBAL_LOBBY");
			if (lobby.getWorld() == null) {
				if (!p.hasPermission("staff.administration.bypass.nolocation")) p.kickPlayer("§cError! Try again later.");
			} else {
				p.teleport(lobby);
			}
		}
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				plugin.GameHandler.giveItems(p);
			}
			
		}, 1L);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		event.getItem().remove();
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		event.setCancelled(true);
		
		if (p.getGameMode() == GameMode.CREATIVE && p.hasPermission("staff.administration.building"))
			event.setCancelled(false);
		
		p.updateInventory();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		event.setCancelled(true);
		
		if (p.getGameMode() == GameMode.CREATIVE && p.hasPermission("staff.administration.building"))
			event.setCancelled(false);
		
		p.updateInventory();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getInventory().getName().startsWith("§0Current Kit")) {
			event.setCancelled(true);
			Player p = (Player) event.getWhoClicked();
			
			if (event.getCurrentItem() != null) {
				if (event.getCurrentItem().getType() == Material.BOW) plugin.KitHandler.selectKit(p, Kit.ARCHER);
				if (event.getCurrentItem().getType() == Material.CHAINMAIL_CHESTPLATE) plugin.KitHandler.selectKit(p, Kit.GUARD);
				if (event.getCurrentItem().getType() == Material.IRON_SWORD) plugin.KitHandler.selectKit(p, Kit.KNIGHT);
				if (event.getCurrentItem().getType() == Material.NETHER_STAR) plugin.KitHandler.selectKit(p, Kit.PRIEST);
				if (event.getCurrentItem().getType() == Material.POTION) plugin.KitHandler.selectKit(p, Kit.SCOUT);
			}
		}
		
		if (event.getInventory().getName().startsWith("§0Team")) {
			event.setCancelled(true);
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
					if (event.getCurrentItem().getData().getData() == (byte) 1) {
						int royal = 0;
						int bandit = 0;
						
						for (Player t : Bukkit.getOnlinePlayers()) {
							if (plugin.gs.getTeams().containsKey(t.getName())) {
								if (plugin.gs.getTeam(p.getName()) == Team.ROYAL) {
									royal++;
								} else {
									bandit++;
								}
							}
						}
						
						if (royal + bandit > 4) {
							plugin.INCore.Util.sendMessage(p, "§7To keep it balanced, no more players can specify what team they would like to join.", plugin.messagePrefix);
						} else {
							plugin.INCore.Util.sendMessage(p, "§7You are a §6Royal§7.", plugin.messagePrefix);
							plugin.gs.addToTeam(p, Team.ROYAL);
							plugin.ScoreboardHandler.sb.getTeam("Royal").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
						}
					} else {
						int royal = 0;
						int bandit = 0;
						
						for (Player t : Bukkit.getOnlinePlayers()) {
							if (plugin.gs.getTeams().containsKey(t.getName())) {
								if (plugin.gs.getTeam(p.getName()) == Team.ROYAL) {
									royal++;
								} else {
									bandit++;
								}
							}
						}
						
						if (royal + bandit > 4) {
							plugin.INCore.Util.sendMessage(p, "§7To keep it balanced, no more players can specify what team they would like to join.", plugin.messagePrefix);
						} else {
							plugin.INCore.Util.sendMessage(p, "§7You are a §cBandit§7.", plugin.messagePrefix);
							plugin.gs.addToTeam(p, Team.BANDIT);
							plugin.ScoreboardHandler.sb.getTeam("Bandit").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
						}
					}
					
					p.closeInventory();
				}
			}
		}
		
		if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().contains("Armor"))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (plugin.gs.getGameState() != Gamestate.INGAME)
			event.setCancelled(true);
		
		if (event.getCause() == DamageCause.FALL) event.setCancelled(true);
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