package me.stuntguy3000.incore.enums;

public enum Statistic {
	HUB_PARKOUR_CHECKPOINT,
	GLOBAL_SERVER_JOINS,
	SIEGE_CAPTURES,
	SIEGE_WINS,
	SIEGE_LOSSES,
	SIEGE_KILLS,
	SIEGE_DEATHS,
	SIEGE_GAMEPLAYS,
	CTF_CAPTURES,
	CTF_WINS,
	CTF_LOSSES,
	CTF_KILLS,
	CTF_DEATHS,
	CTF_GAMEPLAYS,
	FORTWARS_BLOCKS_PLACED,
	FORTWARS_BLOCKS_DESTROYED,
	FORTWARS_WINS,
	FORTWARS_LOSSES,
	FORTWARS_KILLS,
	FORTWARS_DEATHS,
	FORTWARS_GAMEPLAYS;
	
	public static boolean isValid(String input) {
		for (Statistic className : Statistic.values())
			if (className.name().equalsIgnoreCase(input)) return true;
	    return false;
	}
}
