package me.stuntguy3000.infortwars.handler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.confuser.barapi.BarAPI;
import me.stuntguy3000.infortwars.INFortWars;
import me.stuntguy3000.infortwars.enums.GameBlock;
import me.stuntguy3000.infortwars.enums.Gamestate;
import me.stuntguy3000.infortwars.enums.Team;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.BlockIterator;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class EventsHandler implements Listener {
	private INFortWars plugin;
	
	public EventsHandler(INFortWars instance) {
		this.plugin = instance;
	}
	
	public int taskID;
	
	@EventHandler
	public void onConnect(AsyncPlayerPreLoginEvent event) {
		if (plugin.gs.getGameState() == Gamestate.BUILDING || plugin.gs.getGameState() == Gamestate.BATTLE) {
			event.disallow(Result.KICK_OTHER, "§cYou cannot join ingame matches!");
		} else {
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
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		plugin.gs.removeFromTeam(p.getName());
		Location lobby = plugin.INCore.Location.getLocation("GLOBAL_LOBBY");
		if (lobby.getWorld() == null) {
			if (!p.hasPermission("staff.administration.bypass.nolocation")) p.kickPlayer("§cError! Try again later.");
		} else {
			p.teleport(lobby);
			plugin.GameHandler.checkPlayerCount();
		}
	
		plugin.ScoreboardHandler.updateScoreboard();
		p.setGameMode(GameMode.SURVIVAL);
		
		plugin.GameHandler.giveItems(p);
		BarAPI.setMessage(p, "§7Welcome to §eFortWars§7! §3Server: §f" + plugin.serverID, 10);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (plugin.gs.getGameState().name().startsWith("B")) return;
		
		if (event.getAction().name().startsWith("RIGHT")) {
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
			
			if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.ENCHANTED_BOOK) {
				event.setCancelled(true);
				plugin.openTeamSelection(event.getPlayer());
			}
		}
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
	public void onPickup(PlayerPickupItemEvent event) {
		event.getItem().remove();
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		final Player p = event.getEntity();
		
		event.getDrops().clear();
		
		if (p.getKiller() != null) {
			Team k = plugin.gs.getTeam(p.getKiller().getName());
			
			if (k == Team.YELLOW) 
				plugin.ScoreboardHandler.yellowKills.setScore(plugin.ScoreboardHandler.yellowKills.getScore() + 1);
			else plugin.ScoreboardHandler.greenKills.setScore(plugin.ScoreboardHandler.greenKills.getScore() + 1);
			
			plugin.INCore.Coin.addCoins(p.getKiller().getName(), 1);
			event.setDeathMessage(formatDeathMessage(p, p.getKiller(), event.getDeathMessage()));
			
		} else event.setDeathMessage(formatDeathMessage(p, event.getDeathMessage()));
	}
		
	public String formatDeathMessage(Player victim, Player killer, String original) {
		Team killerTeam = plugin.gs.getTeam(killer.getName());
		String killerColor = killerTeam == Team.GREEN ? "§a" : "§e";
		String killerName = killerColor + killer.getName() + "§7";

		String message = "§7" + formatDeathMessage(victim, original);
		message = message.replace(killer.getName(), killerName);

		return message;
	}

	public String formatDeathMessage(Player victim, String original) {
		Team victimTeam = plugin.gs.getTeam(victim.getName());
			
		String victimColor = victimTeam == Team.YELLOW ? "§e" : "§a";
		String victimName = victimColor + victim.getName() + "§7";
			
		String message = "§7" + original;
		message = message.replace(victim.getName(), victimName);
		message = message.replace("was slain by", "was killed by");
		
		return message;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getInventory().getName().startsWith("§0Team")) {
			Player p = (Player) event.getWhoClicked();
			event.setCancelled(true);
			
			if (!p.hasPermission("donor.game.teamselector")) {
				plugin.INCore.Util.sendMessage(p, "§cOnly VIP's can use this feature!", plugin.messagePrefix);
				plugin.INCore.Util.sendMessage(p, "§eBuy VIP and more at http://www.ImpulseNetwork.org", plugin.messagePrefix);
				p.closeInventory();
				return;
			}
			
			if (event.getCurrentItem() != null) {
				if (event.getCurrentItem().getType() == Material.WOOL) {
					if (event.getCurrentItem().getData().getData() == (byte) 5) {
						int YELLOW = 0;
						int GREEN = 0;
						
						for (Player t : Bukkit.getOnlinePlayers()) {
							if (plugin.gs.getTeams().containsKey(t.getName())) {
								if (plugin.gs.getTeam(p.getName()) == Team.YELLOW) {
									YELLOW++;
								} else {
									GREEN++;
								}
							}
						}
						
						if (YELLOW + GREEN > 4) {
							plugin.INCore.Util.sendMessage(p, "§7To keep it balanced, no more players can specify what team they would like to join.", plugin.messagePrefix);
						} else {
							plugin.INCore.Util.sendMessage(p, "§7You are on the §aGreen§7 team.", plugin.messagePrefix);
							plugin.gs.addToTeam(p, Team.GREEN);
							plugin.ScoreboardHandler.sb.getTeam("Green").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
						}
					} else {
						int YELLOW = 0;
						int GREEN = 0;
						
						for (Player t : Bukkit.getOnlinePlayers()) {
							if (plugin.gs.getTeams().containsKey(t.getName())) {
								if (plugin.gs.getTeam(p.getName()) == Team.YELLOW) {
									YELLOW++;
								} else {
									GREEN++;
								}
							}
						}
						
						if (YELLOW + GREEN > 4) {
							plugin.INCore.Util.sendMessage(p, "§7To keep it balanced, no more players can specify what team they would like to join.", plugin.messagePrefix);
						} else {
							plugin.INCore.Util.sendMessage(p, "§7You are on the §eYellow§7 team.", plugin.messagePrefix);
							plugin.gs.addToTeam(p, Team.YELLOW);
							plugin.ScoreboardHandler.sb.getTeam("Yellow").addPlayer(Bukkit.getOfflinePlayer(p.getName()));
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
	public void onRespawn(PlayerRespawnEvent event) {
		plugin.GameHandler.giveItems(event.getPlayer());
		
		event.setRespawnLocation(plugin.INCore.Location.getLocation("FortWars_" + plugin.gs.getMap().name().toLowerCase() + "_" + plugin.gs.getTeam(event.getPlayer().getName()) + "_Spawn"));
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Entity projectile = event.getEntity();
		
		if(!(projectile instanceof Arrow))
	        return;

		Arrow arrow = (Arrow)projectile;
	    arrow.remove();
		
	    if(!(arrow.getShooter() instanceof Player))
	        return;

	    Player player = (Player) arrow.getShooter();
	    World world = arrow.getWorld();
	    BlockIterator iterator = new BlockIterator(world, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);
	    Block hitBlock = null;

	    while(iterator.hasNext()) {
	        hitBlock = iterator.next();
	        if(hitBlock.getTypeId()!=0) //Check all non-solid blockid's here.
	            break;
	    }

	    if(hitBlock.getTypeId()==35) {
	    	if (plugin.gs.getTeam(player.getName()) == Team.YELLOW) {
	    		if (hitBlock.getData() == (byte) 5) {
	    			plugin.GameHandler.gb.remove(hitBlock.getLocation());
					hitBlock.setType(Material.AIR);
	    		}
	    	} else {
	    		if (hitBlock.getData() == (byte) 4) {
	    			plugin.GameHandler.gb.remove(hitBlock.getLocation());
					hitBlock.setType(Material.AIR);
	    		}
	    	}
	    }
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
		
		if (event.getBlock().getLocation().getY() > 112) {
			BarAPI.setMessage(event.getPlayer(), "§c§lYou can't build here!", 5);
			return;
		}
		
		boolean hasStone = false;
		boolean find = false;
		int below = 0;
		
		while (!find) {
			below = below + 1;
			
			if (event.getBlock().getLocation().subtract(0, below, 0).getBlock() != null
					&& event.getBlock().getLocation().subtract(0, below, 0).getBlock().getType() == Material.STONE)
				hasStone = true;
			
			if (below == 20) {
				find = true;
			}
		}
		
		if (!hasStone) {
			BarAPI.setMessage(event.getPlayer(), "§c§lYou can't build here!", 5);
			return;
		}
		
		if (event.getBlock().getType() == Material.WOOL) {
			plugin.GameHandler.gb.put(event.getBlock().getLocation(), new GameBlock(event.getBlock().getLocation(), event.getPlayer().getName(), plugin.gs.getTeam(event.getPlayer().getName())));
			event.setCancelled(false);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		event.setCancelled(true);
		
		if (event.getBlock().getType() == Material.WOOL) {
			if (plugin.gs.getGameState() == Gamestate.BUILDING) {
				Team t = plugin.GameHandler.gb.get(event.getBlock().getLocation()).getTeam();
				
				if (plugin.gs.getTeam(event.getPlayer().getName()) == t) {
					plugin.GameHandler.gb.remove(event.getBlock().getLocation());
					event.getBlock().setType(Material.AIR);
				} else {
					event.getPlayer().damage(0D);
					plugin.INCore.Util.sendMessage(event.getPlayer(), "§cYou cannot the other team's defences yet!", plugin.messagePrefix);
				}
			} else if (plugin.gs.getGameState() == Gamestate.BATTLE) {
				Team t = plugin.GameHandler.gb.get(event.getBlock().getLocation()).getTeam();
				
				if (plugin.gs.getTeam(event.getPlayer().getName()) != t) {
					plugin.GameHandler.gb.remove(event.getBlock().getLocation());
					event.getBlock().setType(Material.AIR);
				} else {
					event.getPlayer().damage(0D);
					plugin.INCore.Util.sendMessage(event.getPlayer(), "§cYou cannot destroy your own defences!", plugin.messagePrefix);
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (!(plugin.gs.getGameState() == Gamestate.BATTLE))
				event.setCancelled(true);
		}
		
		if (event.getCause() == DamageCause.BLOCK_EXPLOSION)
			event.setCancelled(true);
		
		if (event.getCause() == DamageCause.FALL)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}
}