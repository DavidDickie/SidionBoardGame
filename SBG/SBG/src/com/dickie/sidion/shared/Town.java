package com.dickie.sidion.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Town implements GameComponent {

	public String getKey() {
		return name;
	}

	public Town location(Game game) {
		return game.getTown(name);
	}


	public Player owner(Game game) {
		return game.getPlayer(owner);
	}

	@Override
	public void setValue(String field, Object value) {
		// TODO Auto-generated method stub
		
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	private String owner = "";
	private String name = "";
	public Integer getGold() {
		return gold;
	}

	public void setGold(Integer gold) {
		this.gold = gold;
	}

	public Integer getMana() {
		return mana;
	}

	public void setMana(Integer mana) {
		this.mana = mana;
	}

	public Integer getInf() {
		return inf;
	}

	public void setInf(Integer inf) {
		this.inf = inf;
	}

	private Integer gold = 0;
	private Integer mana = 0;
	private Integer inf = 0;
	
	public String getName() {
		return name;
	}

	public void setName(String town) {
		this.name = town;
	}

	private Integer x = 0;
	private Integer y = 0;
	
	public boolean isLocked(){
		return !owner.equals("");
	}

	@Override
	public void selected() {
		System.out.println(getName() + " has been seleced");
	}
	
	public static Map<String,Town> createTowns(Game game){
		Map<String,Town> towns = new HashMap<String,Town>();
		int count = 0;
		for (int x = 0; x < 5; x++){
			for (int y = 0; y < 5; y++){
				Town t = new Town();
				t.setX(100 + x * 100);
				t.setY(100 + y * 100);
				t.setName(townNames[count]);
				towns.put(townNames[count],t);
				count++;
			}
		}
		return towns;
	}
	
	static String [] townNames = {
			"Vonnie",
			"Mira",
			"Germa",
			"Bran",
			"Naoma",
			"Beoma",
			"Joletta",
			"Robbi",
			"Jacqui",
			"Lyndon",
			"Johannisburg",
			"Loris",
			"Teofila",
			"Matha",
			"Vernetta",
			"Star",
			"Shaunta",
			"Angletown",
			"Maya",
			"Jetta",
			"Myrle",
			"Cristown",
			"Juliann",
			"Shawna",
			"Loree",
			"Hellsbells",
			"Talisha",
			"Carliton",
			"Emilia",
			"Rosenda",
			"Garrensburg",
			"Talisman",
			"Diddsburg",
			"Paddleton",
			"Hurrfs",
			"Quentinville",
			"Bollywood"};

			
	

}
