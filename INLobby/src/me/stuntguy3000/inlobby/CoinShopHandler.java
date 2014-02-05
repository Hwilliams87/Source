package me.stuntguy3000.inlobby;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CoinShopHandler implements Listener {

	public HashMap<String, ShopItem> items = new HashMap<String, ShopItem>();
	
	public enum ShopType {
		FORTWARS,
		RUSH,
		CTF;
	}
	
	public enum ShopItem {
		FORTWARS_PERK_EXPLOSIVE_ARROWS("§bPerk: Explosive Arrows", "game.fortwars.explosivearrows", 5000, Material.ARROW),
		FORTWARS_PERK_SHEARS_3("§bPerk: Efficiency 3 Shears", "game.fortwars.fastershears.shears.3", 5000, Material.SHEARS),
		FORTWARS_PERK_SHEARS_5("§bPerk: Efficiency 6 Shears", "game.fortwars.fastershears.shears.5", 5000, Material.SHEARS),
		RUSH_KIT_ARCHER("§aKit: Archer", "game.siege.archer", 15000, Material.BOW),
		RUSH_KIT_GUARD("§aKit: Guard", "game.siege.guard", 15000, Material.CHAINMAIL_CHESTPLATE),
		RUSH_KIT_KNIGHT("§aKit: Knight", "game.siege.knight", 15000, Material.IRON_SWORD),
		RUSH_KIT_PRIEST("§aKit: Priest", "game.siege.priest", 15000, Material.NETHER_STAR),
		RUSH_KIT_SCOUT("§aKit: Scout", "game.siege.scout", 15000, Material.POTION);
		
		private final String name;
		private final String node;
		private final int cost;
		private final Material material;
		
		ShopItem(String name, String node, int cost, Material material) {
			this.name = name;
			this.cost = cost;
			this.node = node;
			this.material = material;
		}
		
		public String getName() {
			return name;
		}

		public String getNode() {
			return node;
		}
		
		public int getCost() {
			return cost;
		}
		
		public Material getMaterial() {
			return material;
		}
	}
	
	private INLobby plugin;
	
	public CoinShopHandler(INLobby instance) {
		this.plugin = instance;
		for (ShopItem i : ShopItem.values()) {
			items.put(i.getName(), i);
		}
	}

	public void openShop(ShopType s, Player p) {
		Inventory inv = Bukkit.createInventory(p, 9, "§8Shop §9»" + " §8" + plugin.INCore.Util.friendlyify(s.name()));
		
		if (s == ShopType.FORTWARS) {
			inv.setItem(2, createItem(ShopItem.FORTWARS_PERK_EXPLOSIVE_ARROWS, p));
			inv.setItem(4, createItem(ShopItem.FORTWARS_PERK_SHEARS_3, p));
			inv.setItem(6, createItem(ShopItem.FORTWARS_PERK_SHEARS_5, p));
		} else if (s == ShopType.RUSH) {
			inv.setItem(0, createItem(ShopItem.RUSH_KIT_ARCHER, p));
			inv.setItem(2, createItem(ShopItem.RUSH_KIT_GUARD, p));
			inv.setItem(4, createItem(ShopItem.RUSH_KIT_KNIGHT, p));
			inv.setItem(6, createItem(ShopItem.RUSH_KIT_PRIEST, p));
			inv.setItem(8, createItem(ShopItem.RUSH_KIT_SCOUT, p));
		} else if (s == ShopType.CTF) {
			
		} else {
			return;
		}
		
		p.openInventory(inv);
	}

	private ItemStack createItem(ShopItem si, Player p) {
		List<String> lore = new ArrayList<String>();
		
		ItemStack is = new ItemStack(si.getMaterial(), 1);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName(si.getName());
		
		lore.add("§6Cost: §e" + si.getCost() + (si.getCost() == 1 ? " Coin" : " Coins"));
		lore.add("§8§m---------------------------");
		try {
			int playerCoins = Integer.parseInt(plugin.INCore.Coin.getCoins(p.getName()));
			
			if (p.hasPermission(si.getNode())) {
				lore.add("§bYou already own this item!");
			} else if (playerCoins >= si.getCost()) {
				lore.add("§aYou can purchase this item!");
			} else {
				int needed = si.getCost() - playerCoins;
				lore.add("§cYou need §b" + needed + " §cmore Coin" + (needed == 1 ? "" : "s") +"!");
				lore.add("§cPurchase Coins at");
				lore.add("§chttp://impulsenetwork.org/donate");
			}
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		
		im.setLore(lore);
		is.setItemMeta(im);
		
		return is;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		
		if (event.getInventory().getName().startsWith("§8Shop §9»")) {
			event.setCancelled(true);
			if (event.getCurrentItem() == null) {
				p.closeInventory();
				return;
			}
			if (!event.getCurrentItem().hasItemMeta()) {
				p.closeInventory();
				return;
			}
			if (event.getCurrentItem().hasItemMeta() && !event.getCurrentItem().getItemMeta().hasLore()) {
				p.closeInventory();
				return;
			}
			
			if (event.getCurrentItem().getItemMeta().getLore().contains("§aYou can purchase this item!")) {
				ShopItem si = items.get(event.getCurrentItem().getItemMeta().getDisplayName());
				
				if (si != null) {
					try {
						plugin.INCore.Coin.setAllCoins(p.getName(), Integer.parseInt(plugin.INCore.Coin.getCoins(p.getName())) - si.getCost());
						plugin.INCore.Util.sendMessage(p, "§bYou have purchased this perk!", "§8[§bCoins§8] §a");
						PermissionUser pp = PermissionsEx.getUser(p.getName());
						pp.addPermission(si.getNode());
					} catch (Exception e) {
						e.printStackTrace();
						plugin.INCore.Util.sendMessage(p, "§cError purchasing item!", "§8[§bCoins§8] §a");
					}
				} else {
					plugin.INCore.Util.sendMessage(p, "§cError purchasing item!", "§8[§bCoins§8] §a");
				}
			} else if (event.getCurrentItem().getItemMeta().getLore().contains("§bYou already own this item!")) {
				plugin.INCore.Util.sendMessage(p, "§cYou already own this perk!", "§8[§bCoins§8] §a");
			} else if (event.getCurrentItem().getItemMeta().getLore().contains("§cPurchase Coins at")){
				plugin.INCore.Util.sendMessage(p, "§cYou cannot afford this perk!", "§8[§bCoins§8] §a");
			}
			
			p.closeInventory();
		}
	}
}
