package me.stuntguy3000.incore.enums;

public class StatusSign {
	private String status;
	private int players;
	private int max;
	private String id;
	
	public StatusSign(String id, String s, int p, int m) {
		this.id = id;
		this.status = s;
		this.players = p;
		this.max = m;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getPlayers() {
		return players;
	}
	
	public void setPlayers(int players) {
		this.players = players;
	}
	
	public int getMax() {
		return max;
	}
	
	public void setMax(int max) {
		this.max = max;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
