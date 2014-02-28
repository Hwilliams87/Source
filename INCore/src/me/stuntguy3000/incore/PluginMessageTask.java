package me.stuntguy3000.incore;

import java.io.ByteArrayOutputStream;

import org.bukkit.Bukkit;

public class PluginMessageTask implements Runnable{

    private INCore plugin;
    private String channel = null;
    private ByteArrayOutputStream bytes = null;

    public PluginMessageTask(INCore plugin, String channel, ByteArrayOutputStream bytes){
    	this.plugin = plugin;
    	this.channel = channel;
    	this.bytes = bytes;
    }

    public void run(){
    	Bukkit.getServer().sendPluginMessage(plugin, channel, bytes.toByteArray());
    }
}
