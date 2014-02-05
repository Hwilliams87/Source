package me.stuntguy3000.incore.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import me.stuntguy3000.incore.INCore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;

public class RulesCommand implements CommandExecutor {
	public INCore plugin;
	
	public RulesCommand(INCore instance) {
		this.plugin = instance;
	}
	
	@EventHandler
	public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		 
		 Bukkit.getScheduler().runTask(plugin, new Runnable() {
			 @Override
			public void run() {
				 try {
						BufferedReader bufferedReader = new BufferedReader( 
				                new InputStreamReader( 
				                     new URL("http://www.ipnk.co/rules/")
				                         .openConnection()
				                         .getInputStream() ));

						 StringBuilder sb = new StringBuilder();
						 String line = null;
						 while( ( line = bufferedReader.readLine() ) != null ) {
							 sb.append( line ) ;
						 }

						 bufferedReader.close();
						
						List<String> rules = Arrays.asList(sb.toString().split(":"));
						
						plugin.Util.sendMessage(sender, "§2Rules:", true);
						for (String rule : rules) 
							plugin.Util.sendMessage(sender, "§7" + rule, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		 });
		
		return false;
	}

}
