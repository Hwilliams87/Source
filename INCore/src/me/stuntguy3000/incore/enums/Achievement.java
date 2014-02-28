package me.stuntguy3000.incore.enums;

public enum Achievement {
	GLOABL_FIRST_JOIN("Be a newbie to the ImpulseNetwork!"),
	GLOABL_FIRST_CHAT_MESSAGE("Say something in chat for the first time!"),
	HUB_PARKOUR_DRAGON("Complete the Dragon parkour in the Hub!"),
	HUB_PARKOUR_ICE("Complete the Ice parkour in the Hub!"),
	TOKEN_FIRST_EARN("Earn some Tokens!"),
	TOKEN_EARN_1000("Achieve 1000 Tokens!"),
	TOKEN_EARN_5000("Achieve 5000 Tokens!"),
	TOKEN_EARN_10000("Achieve 10000 Tokens!"),
	SIEGE_FIRST_KILL("Kill a player in Siege!"),
	SIEGE_FIRST_CAPTURE("Capture a control point in Siege!"),
	SIEGE_FIRST_WIN("Win a match in Siege!"),
	CTF_FIRST_PLAY("Play a game of Capture The Flag!"),
	CTF_FIRST_KILL("Kill a player in Capture The Flag!"),
	CTF_FIRST_FLAG_STEAL("Steal the other team's flag in Capture The Flag!"),
	CTF_FIRST_FLAG_CAPTURE("Capture the other team's flag in Capture The Flag!"),
	CTF_FIRST_WIN("Steal the other team's flag in Capture The Flag!"),
	FORTWARS_FIRST_PLAY("Play a game of Fort Wars!"),
	FORTWARS_FIRST_KILL("Kill a player in Fort Wars!"),
	FORTWARS_FIRST_DESTROY_ENEMY("Destroy a part of the enemies base in Fort Wars!");
	
	private final String achievementMessage;
	
	Achievement(String achievementMessage) {
		this.achievementMessage = achievementMessage;
	}
	
	public String getMessage() {
		return achievementMessage;
	}
	
	public static boolean isValid(String input) {
		for (Achievement className : Achievement.values())
			if (className.name().equalsIgnoreCase(input)) return true;
	    return false;
	}
}
