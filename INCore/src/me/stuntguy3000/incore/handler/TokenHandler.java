package me.stuntguy3000.incore.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.stuntguy3000.incore.INCore;
import me.stuntguy3000.incore.enums.Achievement;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TokenHandler implements Listener {
	private INCore plugin;
	
	public TokenHandler (INCore plugin) {
		this.plugin = plugin;
	}

	public String getTokens(String username) {
		String coins = null;
		plugin.DB.close();
		plugin.DB.open();
			
		ResultSet rs = plugin.DB.query("SELECT * FROM in_users WHERE `username`='" + username + "'", true).getResultSet();
		
		try {
			if (rs.isBeforeFirst())
			{
				try {
					while (rs.next()) coins = String.valueOf(rs.getInt("tokens"));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return coins;
	}
	
	public void setAllTokens(String username, int tokens) {
		plugin.DB.query("UPDATE in_users SET `tokens`='"+ tokens +"' WHERE `username`='" + username + "'");
	}
	
	public void addTokens(String username, int value) {
		Player p = Bukkit.getPlayer(username);
		int tokens = value;
		
		if (p == null) {
			plugin.DB.query("UPDATE in_users SET "
					+ "`tokens`= `tokens` + "+ tokens +", `tokensEarned`=`tokensEarned` + "+ tokens +" WHERE `username`='" + username + "'");
			return;
		}
		
		if (p.hasPermission("incore.doubleTokens"))
			tokens = tokens * 2;
		
		plugin.DB.query("UPDATE in_users SET "
				+ "`tokens`= `tokens` + "+ tokens +", `tokensEarned`=`tokensEarned` + " + tokens + " WHERE `username`='" + username + "'");
		
		if (tokens > 1) {
			plugin.Util.sendMessage(p, "§a+" + tokens + " Coins", false);
		} else plugin.Util.sendMessage(p, "§a+" + tokens + " Coin", false);
		
		plugin.Achievement.giveAchievement(p, Achievement.TOKEN_FIRST_EARN);
	}
	
	public void takeTokens(String username, int value) {
		Player p = Bukkit.getPlayer(username);
		int tokens = value;
		
		if (p == null) {
			plugin.DB.query("UPDATE in_users SET `tokens`= `tokens` - "+ tokens +", `tokensSpent`=`tokensSpent` + " + tokens + " WHERE `username`='" + username + "'");
			return;
		}
		
		if (p.hasPermission("incore.doubleTokens"))
			tokens = tokens * 2;
		
		plugin.DB.query("UPDATE in_users SET `tokens`= `tokens` - "+ tokens +" WHERE `username`='" + username + "'");
		
		if (tokens > 1) {
			plugin.Util.sendMessage(p, "§c-" + tokens + " Coins", false);
		} else plugin.Util.sendMessage(p, "§c-" + tokens + " Coin", false);
	}
	
	/* @SuppressWarnings("deprecation")
	public Inventory getShopCats(Player p) {
		ResultSet catsRS = plugin.DB.query("SELECT * FROM  `GON_coinshopcats` ORDER BY  `id` ASC").getResultSet();
		
		plugin.coinShopCats.clear();
		plugin.coinShopCatsNames.clear();
		plugin.coinShopPerms.clear();
		try {
			while (catsRS.next()) {
				ItemStack is = new ItemStack(Material.AIR, 1);
				Material item = Material.getMaterial(catsRS.getInt("item"));
				
				String name = catsRS.getString("name");
				String desc = catsRS.getString("description");
				
				is.setType(item);
				
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
				im.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', desc).split("#n")));
				is.setItemMeta(im);
				
				plugin.coinShopCats.put(is, catsRS.getInt("id"));
				plugin.coinShopCatsNames.put(catsRS.getInt("id"), catsRS.getString("friendlyName"));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		int cCount = 0;
		Inventory inv = Bukkit.getServer().createInventory(p, 9, ChatColor.translateAlternateColorCodes('&', "&lSpend your " + plugin.Coins.getCoins(p.getName()) + " coins!"));
		
		for (ItemStack is : plugin.coinShopCats.keySet()) {
			inv.setItem(cCount, is);
			cCount ++;
		}
		
		return inv;
	}
	
	public Inventory getShop(Player p, Integer id, String name) {
		String title = "";
		
		if (name != null) title = " (" + name + ")";
		
		Inventory inv = Bukkit.getServer().createInventory(p, 9 * 6, ChatColor.translateAlternateColorCodes('&', "&lShop" + title));
		int userCoins = -1000;
		
		try {
			userCoins = Integer.parseInt(getCoins(p.getName()));
		} catch (NumberFormatException ex) {
			ResultSet itemRS = plugin.DB.query("SELECT * FROM  `GON_coinshop` ORDER BY `id` DESC ").getResultSet();
			
			plugin.coinShop.clear();
			plugin.coinShopCosts.clear();
			try {
				while (itemRS.next()) {
					ItemStack is = new ItemStack(Material.AIR, 1);
					Material item = Material.getMaterial(itemRS.getInt("item"));
					
					String iName = itemRS.getString("name");
					String desc = itemRS.getString("description");
					String cat = itemRS.getString("cat");
					String perm = itemRS.getString("perm");
					
					if (cat.equals(name)) {
						is.setType(item);
						
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(ChatColor.translateAlternateColorCodes('&', iName));
						im.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', desc).split("#n")));
						is.setItemMeta(im);
						
						plugin.coinShop.put(is, itemRS.getInt("id"));
						plugin.coinShopCosts.put(ChatColor.translateAlternateColorCodes('&', iName), itemRS.getInt("cost"));	
						plugin.coinShopPerms.put(ChatColor.translateAlternateColorCodes('&', iName), perm);	
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			int cCount = 0;
			
			for (ItemStack is : plugin.coinShop.keySet()) {
				ItemStack i = is;
				ItemMeta im = i.getItemMeta();
				List<String> lore = im.getLore();
				lore.add(ChatColor.translateAlternateColorCodes('&', ""));
				lore.add(ChatColor.translateAlternateColorCodes('&', "&aCost: " + plugin.coinShopCosts.get(ChatColor.translateAlternateColorCodes('&', im.getDisplayName())) + " Coins"));
				lore.add(ChatColor.translateAlternateColorCodes('&', "&aYou have " + userCoins + " Coins"));
				lore.add(ChatColor.translateAlternateColorCodes('&', ""));
				if (p.hasPermission(plugin.coinShopPerms.get(ChatColor.translateAlternateColorCodes('&', im.getDisplayName())))) {
					lore.add(ChatColor.translateAlternateColorCodes('&', "&6You already own this item!"));
				} else {
					if (userCoins >= plugin.coinShopCosts.get(ChatColor.translateAlternateColorCodes('&', im.getDisplayName()))) {
						lore.add(ChatColor.translateAlternateColorCodes('&', "&bYou can purchase this item"));
					} else {
						lore.add(ChatColor.translateAlternateColorCodes('&', "&cYou cannot purchase this item"));
					}
				}
				
				im.setLore(lore);
				i.setItemMeta(im);
				
				inv.setItem(cCount, i);
				cCount ++;
			}
		}
		
		return inv;
	}
	

	private Inventory openConfirm(Player p, ItemStack is, int cost) {
		Inventory inv = Bukkit.getServer().createInventory(p, 9, ChatColor.translateAlternateColorCodes('&', "&lConfirm Purchase"));
		
		ItemStack c = new ItemStack(Material.WOOL, 1, (byte) 5);
		ItemStack d = new ItemStack(Material.WOOL, 1, (byte) 14);
		ItemMeta cM = c.getItemMeta();
		ItemMeta dM = d.getItemMeta();
		List<String> lore = new ArrayList<String>();
		
		cM.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aConfirm & Buy"));
		dM.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cCancel Purchase"));
		
		lore.add(ChatColor.translateAlternateColorCodes('&', "&6&oPurchase Specifications"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eItem: &b" + is.getItemMeta().getDisplayName()));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eCost: &b" + plugin.coinShopCosts.get(ChatColor.translateAlternateColorCodes('&', is.getItemMeta().getDisplayName())) + "&r&b Coins"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&eYour Coins: &b" + getCoins(p.getName())));
		
		cM.setLore(lore);
		dM.setLore(lore);
		c.setItemMeta(cM);
		d.setItemMeta(dM);
		
		inv.setItem(2, c);
		inv.setItem(6, d);
		
		return inv;
	}
	
	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (event.getInventory().getName().startsWith(ChatColor.translateAlternateColorCodes('&', "&lSpend your "))) {
			event.setCancelled(true);
			
			if (event.getCurrentItem() != null) {
				ItemStack clicked = event.getCurrentItem();
				
				for (ItemStack is : plugin.coinShopCats.keySet()) {
					if (is.getType() == clicked.getType()) {
						p.closeInventory();
						p.openInventory(getShop(p, plugin.coinShopCats.get(is), plugin.coinShopCatsNames.get(plugin.coinShopCats.get(is))));
					}
				}
			}
		}
		
		if (event.getInventory().getName().startsWith(ChatColor.translateAlternateColorCodes('&', "&lShop ("))) {
			event.setCancelled(true);
			int userCoins = Integer.parseInt(getCoins(p.getName()));
			
			if (event.getCurrentItem() != null) {
				ItemStack clicked = event.getCurrentItem();
				
				for (ItemStack is : plugin.coinShop.keySet()) {
					int cost = plugin.coinShopCosts.get(ChatColor.translateAlternateColorCodes('&', is.getItemMeta().getDisplayName()));
					
					if (is.getItemMeta().getLore().equals(clicked.getItemMeta().getLore())) {
						if (p.hasPermission(plugin.coinShopPerms.get(plugin.coinShopPurchases.get(p.getName()).getItemMeta().getDisplayName()))) {
							plugin.Util.sendMessage(p, "&cYou already have this item!", true);
							return;
						}
						
						if (cost > userCoins) {
							plugin.Util.sendMessage(p, "&cYou need more coins to purchase a " + clicked.getItemMeta().getDisplayName() + "&r&c!", true);
						} else {
							p.closeInventory();
							p.openInventory(openConfirm(p, is, cost));
							plugin.coinShopPurchases.remove(p.getName());
							plugin.coinShopPurchases.put(p.getName(), clicked);
					}
				}
			}
		}
		
		if (event.getInventory().getName().startsWith(ChatColor.translateAlternateColorCodes('&', "&lConfirm Purchase"))) {
			event.setCancelled(true);
			int userCoins = Integer.parseInt(getCoins(p.getName()));
			
			if (event.getCurrentItem() != null) {
				ItemStack clicked = event.getCurrentItem();
				
				if (clicked.getDurability() == (short) 14) {
					p.closeInventory();
					plugin.Util.sendMessage(p, "&cPurchase Terminated", true);
				}
				
				if (clicked.getDurability() == (short) 5) {
					p.closeInventory();
					int cost = plugin.coinShopCosts.get(ChatColor.translateAlternateColorCodes('&', plugin.coinShopPurchases.get(p.getName()).getItemMeta().getDisplayName()));
					
					plugin.Util.sendMessage(p, "&aYou have bought a(n) " + plugin.coinShopPurchases.get(p.getName()).getItemMeta().getDisplayName(), true);
					plugin.Coins.setCoins(p.getName(), userCoins - cost);
					plugin.Util.sendMessage(p, "&bYou have " + (userCoins - cost) + " coins", true);
					plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + p.getName() + " add " + plugin.coinShopPerms.get(plugin.coinShopPurchases.get(p.getName()).getItemMeta().getDisplayName()));
				}
			}
		}
	} */
}
