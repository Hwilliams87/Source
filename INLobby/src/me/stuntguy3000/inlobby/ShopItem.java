package me.stuntguy3000.inlobby;

public enum ShopItem {
	FORTWARS_PERK_EXPLOSIVE_ARROWS("", 5000),
	FORTWARS_PERK_SHEARS_3("", 5000),
	FORTWARS_PERK_SHEARS_5("", 5000),
	RUSH_KIT_ARCHER("", 15000),
	RUSH_KIT_GUARD("", 15000),
	RUSH_KIT_KNIGHT("", 15000),
	RUSH_KIT_PRIEST("", 15000),
	RUSH_KIT_SCOUT("", 15000);
	
	private final String name;
	private final int cost;
	
	ShopItem(String name, int cost) {
		this.name = name;
		this.cost = cost;
	}
	
	public String getName() {
		return name;
	}

	public int getCost() {
		return cost;
	}
}
