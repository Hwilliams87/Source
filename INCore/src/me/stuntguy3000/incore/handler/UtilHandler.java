package me.stuntguy3000.incore.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import me.stuntguy3000.incore.INCore;
import me.stuntguy3000.incore.enums.Schematic;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.data.DataException;

public class UtilHandler {
	
	public String messagePrefix = "&8[&aImpulse&8] &7";
	public String noPermission = messagePrefix + "&cYou do not have permission to perform this action!";
	
	public UtilHandler (INCore plugin) {
		
	}
	
	public void noPerm(Player p) {
		sendMessage(p, noPermission, false);
	}
	
	public void sendMessage(Player p, String message, Boolean usePrefix) {
		if (p == null || message == null)
			return;
		
		if (usePrefix)
			message = messagePrefix + message;
		
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public void sendMessage(Player p, String message, String prefix) {
		if (p == null || message == null)
			return;
		
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
	}
	
	public void sendMessage(CommandSender p, String message, String prefix) {
		if (p == null || message == null)
			return;
		
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
	}
	
	public void sendMessage(CommandSender p, String message, Boolean usePrefix) {
		if (p == null || message == null)
			return;
		
		if (usePrefix)
			message = messagePrefix + message;
		
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public String c(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}
	
	public String MD5(String md5) {
		try {
		    java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		    byte[] array = md.digest(md5.getBytes());
		    StringBuffer sb = new StringBuffer();
		    for (int i = 0; i < array.length; ++i) {
		    	sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		    }
		    
		    return sb.toString();
		
		} catch (java.security.NoSuchAlgorithmException e) {
			return md5;
		}
	}
	
	public void helpMenu(Player p, String command, String desc) {
		sendMessage(p, "&f" + command, true);
		sendMessage(p, " &7&o" + desc, true);
	}
	
	public void helpMenu(CommandSender p, String command, String desc) {
		sendMessage(p, "&f" + command, true);
		sendMessage(p, " &7&o" + desc, true);
	}
	
	public void helpMenu(CommandSender p, String command, String desc, String prefix) {
		sendMessage(p, "&f" + command, prefix);
		sendMessage(p, " &7&o" + desc, prefix);
	}
	
	public void helpMenu(Player p, String command, String desc, String prefix) {
		sendMessage(p, "&f" + command, prefix);
		sendMessage(p, " &7&o" + desc, prefix);
	}
	
	public ItemStack createItem(Material mat, int amount, String name, String ... lore) {
		ItemStack is = new ItemStack(mat, amount);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		
		List<String> newLore = new ArrayList<String>();
		
		for (String l : lore)
			newLore.add(ChatColor.translateAlternateColorCodes('&', l));
		
		im.setLore(newLore);
		
		is.setItemMeta(im);
		return is;
	}
	
	public ItemStack createItem(Material mat, int amount, Byte b, String name, String ... lore) {
		ItemStack is = new ItemStack(mat, amount, b);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		
		List<String> newLore = new ArrayList<String>();
		
		for (String l : lore)
			newLore.add(ChatColor.translateAlternateColorCodes('&', l));
		
		im.setLore(newLore);
		
		is.setItemMeta(im);
		return is;
	}
	
	public ItemStack createItem(Material mat, int amount, Byte b) {
		return new ItemStack(mat, amount, b);
	}
	
	public ItemStack createItem(Material mat, int amount, Short b) {
		return new ItemStack(mat, amount, b);
	}
	
	public ItemStack createItem(Material mat, int amount, int r, int g, int b, String name, String ... lore) {
		ItemStack is = new ItemStack(mat, amount);
		LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
		
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		
		List<String> newLore = new ArrayList<String>();
		
		for (String l : lore)
			newLore.add(ChatColor.translateAlternateColorCodes('&', l));
		
		im.setLore(newLore);
		im.setColor(Color.fromRGB(r, g, b));
		
		is.setItemMeta(im);
		return is;
	}
	
	public String getTime(int Time){
		String time = null;
    	
		int minutes = Time/60;
		int seconds = Time%60;
		
		if (minutes == 0)
		{   		
			if (seconds < 10)
    		{
    			time = "0:0" + seconds;
    		} else {
    			time = "0:" + seconds;
    		}
		} else {
			if (seconds < 10)
    		{
    			time = minutes + ":0" + seconds;
    		} else {
    			time = minutes + ":" + seconds;
    		}
			
		}
    	
    	return time;
	}
	
	public String getTimeHours(int Time){
		String time = null;
    	
		int hours = Time/3600;
		int minutes = Time/60;
		int seconds = Time%60;
		
		if (seconds < 10)
		{
			if (minutes < 10)
			{
				time = hours + ":0" + minutes + ":0" + seconds;
			} else {
				time = hours + ":" + minutes + ":0" + seconds;
			}
		} else {
			if (minutes < 10)
			{
				time = hours + ":0" + minutes + ":" + seconds;
			} else {
				time = hours + ":" + minutes + ":" + seconds;
			}
		}
		
		return time;
	}
	
	public Entity[] getNearbyEntities(Location l, int radius){
		int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
		HashSet<Entity> radiusEntities = new HashSet<Entity>();
	    	
		for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
        	for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
            	int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
            	for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
            		if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
            	}
            }
    	}
	    	
		return radiusEntities.toArray(new Entity[radiusEntities.size()]);
	}
	
	public Firework spawnFirework(Location loc) {
		Random colour = new Random();
		
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fwMeta = fw.getFireworkMeta();
		
		Type fwType = Type.BALL_LARGE;
		
		int c1i = colour.nextInt(17) + 1;
		int c2i = colour.nextInt(17) + 1;
		
		Color c1 = getFWColor(c1i);
		Color c2 = getFWColor(c2i);
		
		
		FireworkEffect effect = FireworkEffect.builder().withFade(c2).withColor(c1).with(fwType).withTrail().build();
		
		fwMeta.addEffect(effect);
		fwMeta.setPower(1);
		fw.setFireworkMeta(fwMeta);
		
		return fw;
	}
	
	public Firework spawnFirework(Location loc, int power) {
		Random colour = new Random();
		
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fwMeta = fw.getFireworkMeta();
		
		Type fwType = Type.BALL_LARGE;
		
		int c1i = colour.nextInt(17) + 1;
		int c2i = colour.nextInt(17) + 1;
		
		Color c1 = getFWColor(c1i);
		Color c2 = getFWColor(c2i);
		
		
		FireworkEffect effect = FireworkEffect.builder().withFade(c2).withColor(c1).with(fwType).withTrail().build();
		
		fwMeta.addEffect(effect);
		fwMeta.setPower(power);
		fw.setFireworkMeta(fwMeta);
		
		return fw;
	}
	
	public Firework spawnFirework(Location loc, Color c, int power) {
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fwMeta = fw.getFireworkMeta();
		
		Type fwType = Type.BALL_LARGE;
		
		Color c1 = c;
		Color c2 = c;
		
		FireworkEffect effect = FireworkEffect.builder().withFade(c2).withColor(c1).with(fwType).withTrail().build();
		
		fwMeta.addEffect(effect);
		fwMeta.setPower(power);
		fw.setFireworkMeta(fwMeta);

		return fw;
	}
	
	public Firework spawnFirework(Location loc, Color c) {
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fwMeta = fw.getFireworkMeta();
		
		Type fwType = Type.BALL_LARGE;
		
		Color c1 = c;
		Color c2 = c;
		
		FireworkEffect effect = FireworkEffect.builder().withFade(c2).withColor(c1).with(fwType).withTrail().build();
		
		fwMeta.addEffect(effect);
		fwMeta.setPower(1);
		fw.setFireworkMeta(fwMeta);

		return fw;
	}
	
	public Color getFWColor(int c) {
		switch (c) {
		case 1:
			return Color.TEAL;
		default:
		case 2:
			return Color.WHITE;
		case 3:
			return Color.YELLOW;
		case 4:
			return Color.AQUA;
		case 5:
			return Color.BLACK;
		case 6:
			return Color.BLUE;
		case 7:
			return Color.FUCHSIA;
		case 8:
			return Color.GRAY;
		case 9:
			return Color.GREEN;
		case 10:
			return Color.LIME;
		case 11:
			return Color.MAROON;
		case 12:
			return Color.NAVY;
		case 13:
			return Color.OLIVE;
		case 14:
			return Color.ORANGE;
		case 15:
			return Color.PURPLE;
		case 16:
			return Color.RED;
		case 17:
			return Color.SILVER;
		}
	}
	
	public String friendlyify(String name) {
		String n = name;
		
		n = n.replaceAll("_", "");
		n = n.substring(0,1).toUpperCase() + n.substring(1).toLowerCase();
		
		return n;
	}
	
	 public List<Location> circle (Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere, int plus_y) {
         List<Location> circleblocks = new ArrayList<Location>();
         int cx = loc.getBlockX();
         int cy = loc.getBlockY();
         int cz = loc.getBlockZ();
         for (int x = cx - r; x <= cx +r; x++)
             for (int z = cz - r; z <= cz +r; z++)
                 for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                     double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                     if (dist < r*r && !(hollow && dist < (r-1)*(r-1))) {
                         Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                         circleblocks.add(l);
                         }
                     }
  
         return circleblocks;
     }
	 
	    @SuppressWarnings("deprecation")
		public void pasteSchematic(World world, Location loc, Schematic schematic)
	    {
	        byte[] blocks = schematic.getBlocks();
	        byte[] blockData = schematic.getData();
	 
	        short length = schematic.getLenght();
	        short width = schematic.getWidth();
	        short height = schematic.getHeight();
	 
	        for (int x = 0; x < width; ++x) {
	            for (int y = 0; y < height; ++y) {
	                for (int z = 0; z < length; ++z) {
	                    int index = y * width * length + z * width + x;
	                    Block block = new Location(world, x + loc.getX(), y + loc.getY(), z + loc.getZ()).getBlock();
	                    block.setTypeIdAndData(blocks[index], blockData[index], true);
	                }
	            }
	        }
	    }
	 
	    public Schematic loadSchematic(File file) throws IOException
	    {
	        FileInputStream stream = new FileInputStream(file);
	        NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(stream));
	 
	        CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
	        if (!schematicTag.getName().equals("Schematic")) {
	        	nbtStream.close();
	            throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
	        }
	 
	        Map<String, Tag> schematic = schematicTag.getValue();
	        if (!schematic.containsKey("Blocks")) {
	        	nbtStream.close();
	        	throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
	        }
	 
	        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
	        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
	        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();
	 
	        String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
	        if (!materials.equals("Alpha")) {
	        	nbtStream.close();
	        	throw new IllegalArgumentException("Schematic file is not an Alpha schematic");
	        }
	 
	        byte[] blocks = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
	        byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
	        nbtStream.close();
	        return new Schematic(blocks, blockData, width, length, height);
	    }
	 
	    /**
	    * Get child tag of a NBT structure.
	    *
	    * @param items The parent tag map
	    * @param key The name of the tag to get
	    * @param expected The expected type of the tag
	    * @return child tag casted to the expected type
	    * @throws DataException if the tag does not exist or the tag is not of the
	    * expected type
	    */
	    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws IllegalArgumentException
	    {
	        if (!items.containsKey(key)) {
	            throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
	        }
	        Tag tag = items.get(key);
	        if (!expected.isInstance(tag)) {
	            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
	        }
	        return expected.cast(tag);
	    }
 }
