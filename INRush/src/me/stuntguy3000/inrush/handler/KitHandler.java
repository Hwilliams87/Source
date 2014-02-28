package me.stuntguy3000.inrush.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.stuntguy3000.inrush.INRush;
import me.stuntguy3000.inrush.enums.Kit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KitHandler {
	private INRush plugin;
	
	public HashMap<String, Kit> selectedKits = new HashMap<String, Kit>();
	
	public KitHandler(INRush instance) {
		this.plugin = instance;
	}
	
	public Kit getKit(String username) {
		return selectedKits.get(username);
	}
	
	public void openKit(Player p) {
		Inventory selector = Bukkit.createInventory(p, 9, "§0Current Kit - " + plugin.INCore.Util.friendlyify(getKit(p.getName()).name()));
		
		selector.setItem(2, plugin.INCore.Util.createItem(Material.BOW, 1, "§bArcher", getLore(p, Kit.ARCHER)));
		selector.setItem(3, plugin.INCore.Util.createItem(Material.CHAINMAIL_CHESTPLATE, 1, "§bGuard", getLore(p, Kit.GUARD)));
		selector.setItem(4, plugin.INCore.Util.createItem(Material.IRON_SWORD, 1, "§bKnight", getLore(p, Kit.KNIGHT)));
		selector.setItem(5, plugin.INCore.Util.createItem(Material.NETHER_STAR, 1, "§bPriest", getLore(p, Kit.PRIEST)));
		selector.setItem(6, plugin.INCore.Util.createItem(Material.POTION, 1, (byte) 8258, "§bScout", getLore(p, Kit.SCOUT)));
		
		p.openInventory(selector);
	}
 
	private String[] getLore(Player p, Kit kit) {
		List<String> lore = new ArrayList<String>();
		
		if (kit == Kit.ARCHER) {
			lore.add("§2Take out your Bow. Aim. Fire!");
			lore.add("§2Use your long lost archery skills to knock");
			lore.add("§2those opponents into the sky!");
			lore.add("     ");
			lore.add("§4This kit contains:");
			lore.add(" §e1§2x§6Wood Sword §b(Sharpness 1)");
			lore.add(" §e1§2x§6Bow §b(Infinity 1)");
			lore.add(" §e1§2x§6Arrow");
			lore.add(" §e1§2x§6Instant Health 1 Splash Potion");
			lore.add(" §e1§2x§6Full Leather Armor");
		}
		
		if (kit == Kit.GUARD) {
			lore.add("§2Be a leader, be a guard!");
			lore.add("§2Push your team to victory, every step of the way.");
			lore.add("     ");
			lore.add("§4This kit contains:");
			lore.add(" §e1§2x§6Stone Sword §b(Knockback 1)");
			lore.add(" §e1§2x§6Instant Health 1 Splash Potion");
			lore.add(" §e1§2x§6Swiftness §7(Speed)§6 1 Splash Potion");
			lore.add(" §e1§2x§6Full Leather Armor");
		}
		
		if (kit == Kit.KNIGHT) {
			lore.add("§2Follow the footsteps of the Guard");
			lore.add("§2and be the first attackers.");
			lore.add("§2Use your tools to make a impact");
			lore.add("§2 in the other team's defences.");
			lore.add("     ");
			lore.add("§4This kit contains:");
			lore.add(" §e1§2x§6Wood Sword §b(Knockback 1)");
			lore.add(" §e1§2x§6Regeneration 1 Splash Potion");
			lore.add(" §e1§2x§6Leather Helmet, Leggings and Boots");
			lore.add(" §e1§2x§6Chain Chestplate");
		}
		
		if (kit == Kit.PRIEST) {
			lore.add("§2Help strengthen your defences, ");
			lore.add("§2and be the backbone of the team.");
			lore.add("     ");
			lore.add("§4This kit contains:");
			lore.add(" §e1§2x§6Wood Sword");
			lore.add(" §e1§2x§6Full Chain Armor");
		}
		
		if (kit == Kit.SCOUT) {
			lore.add("§2You have the need for speed, apparently.");
			lore.add("     ");
			lore.add("§4This kit contains:");
			lore.add(" §e1§2x§6Wood Sword");
			lore.add(" §e1§2x§6Extended Swiftness §7(Speed)§6 1 Splash Potion");
			lore.add(" §e1§2x§6Leather Chestplate, Leggings and Boots");
			lore.add(" §e1§2x§6Iron Helmet");
			lore.add(" §aAbility: §eDouble Jump");
		}
		
		lore.add("     ");
		lore.add("§8----------------");
		
		if (getKit(p.getName()).equals(kit)) {
			lore.add("§bCurrently Selected");
		} else if (p.hasPermission("INSiege.kit." + kit.name().toLowerCase())) {
			lore.add("§aAvailable for use");
		} else {
			lore.add("§cLocked. Unlock with Coins at http://www.ImpulseNetwork.org");
		}
		
		return lore.toArray(new String[lore.size()]);		
	}

	public void giveItems(Player p) {
		giveKit(p, getKit(p.getName()));
	}
	
	public void selectKit(Player p, Kit kit) {
		if (p.hasPermission("game.siege." + kit.name().toLowerCase())) {
			plugin.INCore.Util.sendMessage(p, "§7Selected Kit: §a" + plugin.INCore.Util.friendlyify(kit.name()), plugin.messagePrefix);
			p.closeInventory();
			selectedKits.put(p.getName(), kit);
		}
	}
	
	public void giveKit(Player p, Kit kit) {
		if (kit == Kit.ARCHER) {
			ItemStack sword = new ItemStack(Material.WOOD_SWORD, 1);
			sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
			p.getInventory().addItem(sword);
			
			ItemStack bow = new ItemStack(Material.BOW, 1);
			bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			p.getInventory().addItem(bow);
			p.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			p.getInventory().addItem(plugin.INCore.Util.createItem(Material.POTION, 1, (short) 16453));
		}
		
		if (kit == Kit.GUARD) {
			ItemStack sword = new ItemStack(Material.STONE_SWORD, 1);
			sword.addEnchantment(Enchantment.KNOCKBACK, 1);
			p.getInventory().addItem(sword);
			
			ItemStack bow = new ItemStack(Material.BOW, 1);
			bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			p.getInventory().addItem(plugin.INCore.Util.createItem(Material.POTION, 1, (short) 16453));
			p.getInventory().addItem(plugin.INCore.Util.createItem(Material.POTION, 1, (short) 16386));
		}
		
		if (kit == Kit.KNIGHT) {
			ItemStack sword = new ItemStack(Material.WOOD_SWORD, 1);
			sword.addEnchantment(Enchantment.KNOCKBACK, 1);
			p.getInventory().addItem(sword);
			
			p.getInventory().addItem(plugin.INCore.Util.createItem(Material.POTION, 1, (short) 16449));
			p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
		}
		
		if (kit == Kit.PRIEST) {
			p.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
			p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
			p.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
			p.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
		}
		
		if (kit == Kit.SCOUT) {
			p.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
			p.getInventory().addItem(plugin.INCore.Util.createItem(Material.POTION, 1, (short) 16450));
			p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
		}
	}
}
