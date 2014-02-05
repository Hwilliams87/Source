package me.stuntguy3000.inlobby.event;

import java.net.InetSocketAddress;

public class ServerInfo {
    private String name;
    private int playersOnline = 0;
    private int maxPlayers = 0;
    private String motd;
    private boolean online = false;
    private InetSocketAddress address;
    private String displayname;
    
	public String getDisplayname() {
		return displayname;
	}
	
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public String getMotd() {
		return motd;
	}

	public void setMotd(String motd) {
		this.motd = motd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPlayersOnline() {
		return playersOnline;
	}

	public void setPlayersOnline(int playersOnline) {
		this.playersOnline = playersOnline;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
    
}