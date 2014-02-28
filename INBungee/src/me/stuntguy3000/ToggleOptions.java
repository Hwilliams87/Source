package me.stuntguy3000;

public class ToggleOptions {
	
	private boolean allowPrivateMessages = true;
	private boolean allowServerLookup = true;
	
	public ToggleOptions() {
		
	}
	
	public boolean isAllowPrivateMessages() {
		return allowPrivateMessages;
	}
	public void setAllowPrivateMessages(boolean allowPrivateMessages) {
		this.allowPrivateMessages = allowPrivateMessages;
	}
	public boolean isAllowServerLookup() {
		return allowServerLookup;
	}
	public void setAllowServerLookup(boolean allowServerLookup) {
		this.allowServerLookup = allowServerLookup;
	}
	

}
