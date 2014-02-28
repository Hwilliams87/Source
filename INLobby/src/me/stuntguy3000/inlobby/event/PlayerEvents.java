package me.stuntguy3000.inlobby.event;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.confuser.barapi.BarAPI;
import me.stuntguy3000.inlobby.CoinShopHandler.ShopType;
import me.stuntguy3000.inlobby.INLobby;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PlayerEvents implements Listener {

	private INLobby plugin;
	
	public PlayerEvents(INLobby instance) {
		this.plugin = instance;
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		if (!p.hasPermission("staff.administration.building")) event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (!p.hasPermission("staff.administration.building")) event.setCancelled(true);
		
		if (plugin.setSign.containsKey(p.getName())) {
			event.setCancelled(true);
			
			String id = plugin.setSign.remove(p.getName());
			
			if (event.getBlock().getType() == Material.WALL_SIGN || event.getBlock().getType() == Material.SIGN_POST) {
				plugin.INCore.Util.sendMessage(p, "Sign created", true);
				plugin.addSign(event.getBlock().getLocation(), id);
				
			} else plugin.INCore.Util.sendMessage(p, "&cProcess terminated.", true);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		plugin.giveItems(p);
		
		if (!p.hasPermission("staff.administration.bypass.lobby.vanish")) {
			for (String name : plugin.noSee)
				if (Bukkit.getPlayerExact(name) != null) Bukkit.getPlayerExact(name).hidePlayer(p);
		}
		
		p.setGameMode(GameMode.ADVENTURE);
		
		plugin.ScoreboardHandler.setScoreboard();
		plugin.checkpoints.remove(p.getName());
		
		PermissionUser user  = PermissionsEx.getUser(p);
		String group = user.getGroups()[0].getName();
		
		plugin.ScoreboardHandler.tags.get(group.toLowerCase()).addPlayer(Bukkit.getOfflinePlayer(p.getName()));
		BarAPI.setMessage(p, "§eWelcome to the §6ImpulseNetwork§e!");
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		PermissionUser user  = PermissionsEx.getUser(p);
		String group = user.getGroups()[0].getName();
		
		plugin.ScoreboardHandler.tags.get(group.toLowerCase()).removePlayer(Bukkit.getOfflinePlayer(p.getName()));
	
		plugin.noSee.remove(p.getName());
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.VOID) {
			if (event.getEntity() instanceof Player) {
				Player p = (Player) event.getEntity();
				
				event.setCancelled(true);
				
				if (plugin.checkpoints.containsKey(p.getName())) {
					p.teleport(plugin.checkpoints.get(p.getName()));
					plugin.INCore.Util.sendMessage(p, plugin.INCore.Util.c("&7Teleported to your last checkpoint."), true);
				} else {
					Location loc = plugin.INCore.Location.getLocation("Hub");
					
					if (loc.getWorld() == null) {
						if (p.hasPermission("inlobby.admin"))
							plugin.INCore.Util.sendMessage(p, "&c&lError: No spawnpoint set!", true);
						else {
							p.kickPlayer(plugin.INCore.Util.c("&c&lError: Please try again later!"));
							return;
						}
					} else p.teleport(loc);
				}
			}
		} else event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player)
			if (((Player) event.getDamager()).getGameMode() == GameMode.CREATIVE) event.setCancelled(false);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		
		Location loc = plugin.INCore.Location.getLocation("Hub");
		
		if (loc.getWorld() == null) {
			if (p.hasPermission("inlobby.admin"))
				plugin.INCore.Util.sendMessage(p, "&c&lError: No spawnpoint set!", true);
			else p.kickPlayer(plugin.INCore.Util.c("&c&lError: Please try again later!"));
			return;
		}
		
		event.setRespawnLocation(loc);
		plugin.giveItems(p);
		
		if (!p.hasPermission("inlobby.scrollBypass")) {
			for (String name : plugin.noSee)
				Bukkit.getPlayerExact(name).hidePlayer(p);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		event.setCancelled(true);
		event.getItem().remove();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		if (p.getGameMode() == GameMode.CREATIVE) return;
		
		if (event.getAction() == Action.PHYSICAL) {
			if (event.getClickedBlock().getRelative(0, -1, 0).getType() == Material.EMERALD_BLOCK) {
				Vector direction = p.getLocation().getDirection();
				p.setVelocity(p.getLocation().getDirection().multiply(30.0));
				direction.multiply(2);
				direction.setY( direction.getY() + 30 ); p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
				p.setVelocity(direction);
			}
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK
				|| event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			event.setCancelled(true);
			if (event.getClickedBlock() != null) {
				if (event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) {
					Sign s = (Sign) event.getClickedBlock().getState();
					if (!s.getLine(0).equals("") && !s.getLine(2).contains("Restart") &&
						!s.getLine(1).equals("") &&
						!s.getLine(2).equals("") &&
						!s.getLine(3).equals("")) {
						
						ByteArrayOutputStream b = new ByteArrayOutputStream();
						DataOutputStream out = new DataOutputStream(b);
						
						String server;
						
						try {
							server = ChatColor.stripColor(s.getLine(3));
						} catch (StringIndexOutOfBoundsException ex) {
							return;
						}
						
						try {
							out.writeUTF("Connect");
							out.writeUTF(server);
						} catch (IOException ex) {
							
						}
						
						p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
					}
				}
			}
			
			if (p.getItemInHand() != null) {
				if (p.getItemInHand().getType() == Material.COMPASS) {
					event.setCancelled(true);
					
					Inventory gameSelector = Bukkit.createInventory(p, 9, "         §0Choose a game!");
					
					gameSelector.setItem(2, plugin.INCore.Util.createItem(Material.BEACON, 1, "&eRush", "&6Invade the castle,", "&r&6and capture all the zones!"));
					gameSelector.setItem(4, plugin.INCore.Util.createItem(Material.WOOL, 1, (byte) 11, "&eCapture the Flag", "&6Steal the other teams flag","&r&6to come out on top!"));
					gameSelector.setItem(6, plugin.INCore.Util.createItem(Material.CHAINMAIL_CHESTPLATE, 1, "&eFort Wars", "&6Team up and build your base", "&r&6and prevent its destruction!"));
					
					p.openInventory(gameSelector);
				}
				
				if (p.getItemInHand().getType() == Material.ENCHANTED_BOOK) {
					event.setCancelled(true);
					
					if (plugin.cooldownScroll.containsKey(p.getName())) {
						if (plugin.cooldownScroll.get(p.getName()) == 1) 
							plugin.INCore.Util.sendMessage(p, "You can use this item in 1 second.", true);
						else plugin.INCore.Util.sendMessage(p, "You can use this item in " + plugin.cooldownScroll.get(p.getName()) + " seconds.", true);
						return;
					}
					
					if (plugin.noSee.contains(p.getName())) {
						plugin.INCore.Util.sendMessage(p, "All players are now visible.", true);
						plugin.noSee.remove(p.getName());
						
						for (Player name : Bukkit.getOnlinePlayers())
							p.showPlayer(name);
					} else {
						plugin.INCore.Util.sendMessage(p, "All players have been hidden.", true);
						
						for (Player name : Bukkit.getOnlinePlayers())
							if (!name.hasPermission("inlobby.scrollBypass")) p.hidePlayer(name);
						
						plugin.noSee.add(p.getName());
					}
					
					if (!p.hasPermission("inlobby.cooldownbypass")) plugin.cooldownScroll.put(p.getName(), 5);
				}
				
				if (p.getItemInHand().getType() == Material.POTION || p.getItemInHand().getType() == Material.GLASS_BOTTLE) {
					event.setCancelled(true);
					
					if (plugin.cooldownSpeed.containsKey(p.getName())) {
						if (plugin.cooldownSpeed.get(p.getName()) == 1) 
							plugin.INCore.Util.sendMessage(p, "You can use this item in 1 second.", true);
						else plugin.INCore.Util.sendMessage(p, "You can use this item in " + plugin.cooldownSpeed.get(p.getName()) + " seconds.", true);
						return;
					}
					
					boolean speed = false;
					
					for (PotionEffect e : p.getActivePotionEffects()) {
						if (e.getType().equals(PotionEffectType.SPEED)) speed = true;
					}
					
					if (!speed) {
						plugin.INCore.Util.sendMessage(p, "§7You have been given Speed 2.", true);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
						p.getInventory().setItemInHand(plugin.INCore.Util.createItem(Material.POTION, 1, (byte) 8226, "&c&lSpeed Boost &r&7(Right click to use)", "&3Give yourself Speed 2."));
					} else {
						plugin.INCore.Util.sendMessage(p, "§7All Speed effects have been removed!", true);
						p.removePotionEffect(PotionEffectType.SPEED);
						p.getInventory().setItemInHand(plugin.INCore.Util.createItem(Material.GLASS_BOTTLE, 1, "&c&lSpeed Boost &r&7(Right click to use)", "&3Give yourself Speed 2."));
					}
					
					if (!p.hasPermission("inlobby.cooldownbypass")) plugin.cooldownSpeed.put(p.getName(), 5);
				}
				
				if (p.getItemInHand().getType() == Material.SKULL_ITEM) {
					event.setCancelled(true);
					
					if (plugin.cooldownSkull.containsKey(p.getName())) {
						if (plugin.cooldownSkull.get(p.getName()) == 1) 
							plugin.INCore.Util.sendMessage(p, "You can use this item in 1 second.", true);
						else plugin.INCore.Util.sendMessage(p, "You can use this item in " + plugin.cooldownSkull.get(p.getName()) + " seconds.", true);
						return;
					}
					
					plugin.openArmorInventory(p);
					p.openInventory(plugin.armorSelector);
					
					if (!p.hasPermission("inlobby.cooldownbypass")) plugin.cooldownSkull.put(p.getName(), 5);
				}
				
				if (p.getItemInHand().getType() == Material.getMaterial(351)) {
					event.setCancelled(true);
					
					if (plugin.cooldownBackToLobby.containsKey(p.getName())) {
						if (plugin.cooldownBackToLobby.get(p.getName()) == 1) 
							plugin.INCore.Util.sendMessage(p, "You can use this item in 1 second.", true);
						else plugin.INCore.Util.sendMessage(p, "You can use this item in " + plugin.cooldownBackToLobby.get(p.getName()) + " seconds.", true);
						return;
					}
					
					Location loc = plugin.INCore.Location.getLocation("Hub");
					
					if (loc.getWorld() == null) {
						if (p.hasPermission("inlobby.admin"))
							plugin.INCore.Util.sendMessage(p, "&c&lError: No spawnpoint set!", true);
						else {
							p.kickPlayer(plugin.INCore.Util.c("&c&lError: Please try again later!"));
							return;
						}
					} else p.teleport(loc);
					
					if (!p.hasPermission("inlobby.cooldownbypass")) plugin.cooldownBackToLobby.put(p.getName(), 5);
				}
				
				if (p.getItemInHand().getType() == Material.FIREWORK) {
					event.setCancelled(true);
					
					if (plugin.cooldownFirework.containsKey(p.getName())) {
						if (plugin.cooldownFirework.get(p.getName()) == 1) 
							plugin.INCore.Util.sendMessage(p, "You can use this item in 1 second.", true);
						else plugin.INCore.Util.sendMessage(p, "You can use this item in " + plugin.cooldownFirework.get(p.getName()) + " seconds.", true);
						return;
					}
					
					Firework f = plugin.INCore.Util.spawnFirework(p.getLocation(), 2);
					f.setPassenger(p);
					
					if (!p.hasPermission("inlobby.cooldownbypass")) plugin.cooldownFirework.put(p.getName(), 30);
				}
			}
		}
		
		p.updateInventory();
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setFoodLevel(20);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		
		boolean ignore = false;
		
		if (p.getGameMode() == GameMode.CREATIVE)
			ignore = true;
		
		event.setCancelled(true);
		
		if (event.getInventory().getTitle().equals("         §0Choose a game!")) {
			ignore = false;
			
			if (event.getCurrentItem() != null) {
				if (event.getCurrentItem().getType() == Material.WOOL) {
					p.closeInventory();
					
					Location loc = plugin.INCore.Location.getLocation("Hub_CTF");
					
					if (loc.getWorld() == null) {
						if (p.hasPermission("inlobby.admin"))
							plugin.INCore.Util.sendMessage(p, "&cError: No location for \"Hub_CTF\" set!", true);
						else plugin.INCore.Util.sendMessage(p, "&cError: Please try again later!", true);
						return;
					} 
					
					p.teleport(loc);
				}
				
				if (event.getCurrentItem().getType() == Material.BEACON) {
					p.closeInventory();
					
					Location loc = plugin.INCore.Location.getLocation("Hub_Rush");
					
					if (loc.getWorld() == null) {
						if (p.hasPermission("inlobby.admin"))
							plugin.INCore.Util.sendMessage(p, "&cError: No location for \"Hub_Rush\" set!", true);
						else plugin.INCore.Util.sendMessage(p, "&cError: Please try again later!", true);
						return;
					} 
					
					p.teleport(loc);
				}
				
				if (event.getCurrentItem().getType() == Material.CHAINMAIL_CHESTPLATE) {
					p.closeInventory();
					
					Location loc = plugin.INCore.Location.getLocation("Hub_FortWars");
					
					if (loc.getWorld() == null) {
						if (p.hasPermission("inlobby.admin"))
							plugin.INCore.Util.sendMessage(p, "&cError: No location for \"Hub_FortWars\" set!", true);
						else plugin.INCore.Util.sendMessage(p, "&cError: Please try again later!", true);
						return;
					} 
					
					p.teleport(loc);
				}
			}
		}
		
		if (event.getInventory().getTitle().equals("§0Choose your outfit!")) {
			ignore = false;
			
			if (event.getCurrentItem() != null) {
				if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§aRandomize")) {
					p.closeInventory();
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new gear!", true);
					
					p.getInventory().setHelmet(getRandomHelmet());
					p.getInventory().setChestplate(getRandomChestplate());
					p.getInventory().setLeggings(getRandomLeggings());
					p.getInventory().setBoots(getRandomBoots());
					return;
				}
				
				if (event.getCurrentItem().getType() == Material.WOOL) {
					if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equals("§cClear your armor!")) {
						p.getInventory().setArmorContents(null);
						plugin.INCore.Util.sendMessage(p, "Armor cleared.", true);
					}
				}
				
				if (event.getCurrentItem().getType() == Material.LEATHER_HELMET) {
					p.getInventory().setHelmet(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new helmet!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.LEATHER_CHESTPLATE) {
					p.getInventory().setChestplate(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new chestplate!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.LEATHER_LEGGINGS) {;
					p.getInventory().setLeggings(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new leggings!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.LEATHER_BOOTS) {
					p.getInventory().setBoots(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new boots!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.IRON_HELMET) {
					p.getInventory().setHelmet(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new helmet!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.IRON_CHESTPLATE) {
					p.getInventory().setChestplate(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new chestplate!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.IRON_LEGGINGS) {;
					p.getInventory().setLeggings(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new leggings!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.IRON_BOOTS) {
					p.getInventory().setBoots(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new boots!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.GOLD_HELMET) {
					p.getInventory().setHelmet(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new helmet!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.GOLD_CHESTPLATE) {
					p.getInventory().setChestplate(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new chestplate!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.GOLD_LEGGINGS) {;
					p.getInventory().setLeggings(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new leggings!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.GOLD_BOOTS) {
					p.getInventory().setBoots(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new boots!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.DIAMOND_HELMET) {
					p.getInventory().setHelmet(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new helmet!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.DIAMOND_CHESTPLATE) {
					p.getInventory().setChestplate(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new chestplate!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.DIAMOND_LEGGINGS) {;
					p.getInventory().setLeggings(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new leggings!", true);
				}
				
				if (event.getCurrentItem().getType() == Material.DIAMOND_BOOTS) {
					p.getInventory().setBoots(clearItem(event.getCurrentItem()));
					plugin.INCore.Util.sendMessage(p, "&aEnjoy your new boots!", true);
				}
			}
		}
		
		if (ignore) {
			event.setCancelled(false);
			return;
		}
	}

	private ItemStack clearItem(ItemStack currentItem) {
		ItemMeta im = currentItem.getItemMeta();
		im.setLore(null);
		currentItem.setItemMeta(im);
		
		return currentItem;
	}

	private ItemStack getRandomHelmet() {
		Random r = new Random();
		List<ItemStack> materials = new ArrayList<ItemStack>();
		
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_HELMET, 1, 255, 0, 0,"&4Red Helmet", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_HELMET, 1, 0, 255, 0,"&2Green Helmet", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_HELMET, 1,  0, 0, 255,"&1Blue Helmet", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_HELMET, 1, 255, 255, 0,"&eYellow Helmet", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.IRON_HELMET, 1, "&7Iron Helmet", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.GOLD_HELMET, 1, "&6Gold Helmet", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.DIAMOND_HELMET, 1, "&bDiamond Helmet", "&fClick here to apply this item"));
		
		return clearItem(materials.get(r.nextInt(materials.size())));
	}
	
	private ItemStack getRandomChestplate() {
		Random r = new Random();
		List<ItemStack> materials = new ArrayList<ItemStack>();
		
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 255, 0, 0,"&4Red Chestplate", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 0, 255, 0,"&2Green Chestplate", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1,  0, 0, 255,"&1Blue Chestplate", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_CHESTPLATE, 1, 255, 255, 0,"&eYellow Chestplate", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.IRON_CHESTPLATE, 1, "&7Iron Chestplate", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.GOLD_CHESTPLATE, 1, "&6Gold Chestplate", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.DIAMOND_CHESTPLATE, 1, "&bDiamond Chestplate", "&fClick here to apply this item"));
		
		return clearItem(materials.get(r.nextInt(materials.size())));
	}
	
	private ItemStack getRandomLeggings() {
		Random r = new Random();
		List<ItemStack> materials = new ArrayList<ItemStack>();
		
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 255, 0, 0,"&4Red Leggings", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 0, 255, 0,"&2Green Leggings", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1,  0, 0, 255,"&1Blue Leggings", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_LEGGINGS, 1, 255, 255, 0,"&eYellow Leggings", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.IRON_LEGGINGS, 1, "&7Iron Leggings", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.GOLD_LEGGINGS, 1, "&6Gold Leggings", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.DIAMOND_LEGGINGS, 1, "&bDiamond Leggings", "&fClick here to apply this item"));
		
		return clearItem(materials.get(r.nextInt(materials.size())));
	}
	
	private ItemStack getRandomBoots() {
		Random r = new Random();
		List<ItemStack> materials = new ArrayList<ItemStack>();
		
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 255, 0, 0,"&4Red Boots", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 0, 255, 0,"&2Green Boots", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_BOOTS, 1,  0, 0, 255,"&1Blue Boots", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.LEATHER_BOOTS, 1, 255, 255, 0,"&eYellow Boots", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.IRON_BOOTS, 1, "&7Iron Boots", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.GOLD_BOOTS, 1, "&6Gold Boots", "&fClick here to apply this item"));
		materials.add(plugin.INCore.Util.createItem(Material.DIAMOND_BOOTS, 1, "&bDiamond Boots", "&fClick here to apply this item"));
		
		return clearItem(materials.get(r.nextInt(materials.size())));
	}
	
	@EventHandler
	public void shop(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Villager) {
			event.setCancelled(true);
			Villager v = (Villager) event.getRightClicked();
			
			if (v.getCustomName().contains("CTF")) {
				plugin.CoinShopHandler.openShop(ShopType.CTF, event.getPlayer());
			} else if (v.getCustomName().contains("Rush")) {
				plugin.CoinShopHandler.openShop(ShopType.RUSH, event.getPlayer());
			} else if (v.getCustomName().contains("FortWars")) {
				plugin.CoinShopHandler.openShop(ShopType.FORTWARS, event.getPlayer());
			} else {
				plugin.INCore.Util.sendMessage(event.getPlayer(), "§cThis shop is closed! Come back soon!", "§8[§aShop§8] ");
			}
		}
	}
}
