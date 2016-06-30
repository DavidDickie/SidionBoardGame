package com.dickie.sidion.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dickie.sidion.client.Utils;

public class Town extends GameComponentImpl {

	public Town(){
		validAttributes = Arrays.asList("LKEY", "X", "Y", "GOLD", "MANA", "INF", "PLAYER");
	}


	public Integer getX() {
		return Integer.valueOf(getValue("X"));
	}

	public void setX(Integer x) {
		setValue("X", x.toString());
	}

	public Integer getY() {
		return Integer.valueOf(getValue("Y"));
	}

	public void setY(Integer y) {
		setValue("Y", y.toString());
	}

	public Integer getGold() {
		return Integer.valueOf(getValue("GOLD"));
	}

	public void setGold(Integer i) {
		setValue("GOLD", i.toString());
	}

	public Integer getMana() {
		return Integer.valueOf(getValue("MANA"));
	}

	public void setMana(Integer i) {
		setValue("MANA", i.toString());
	}

	public Integer getInf() {
		return Integer.valueOf(getValue("INF"));
	}

	public void setInf(Integer i) {
		setValue("INF", i.toString());
	}

	public String getName() {
		return getValue("LKEY");
	}

	public void setName(String town) {
		setValue("LKEY", town);
	}

	
	public boolean isLocked(){
		return getValue("PLAYER") != null;
	}
	
	public List<Hero> getHeros(Game game){
		ArrayList<Hero> list = new ArrayList<Hero>();
		Iterator<Hero> iHero = game.getHeros().iterator();
		while (iHero.hasNext()){
			Hero h = iHero.next();
			if (h.getLocation(game).equals(this)){
				list.add(h);
			}
		}
		return list;
	}
	
	private static final int towner = 5;
	
	private static String[][] distance = new String[towner][towner];
	
	public static int getDistance(Town t1, Town t2){
		
		int distance = java.lang.Math.abs(x1 -x2) + java.lang.Math.abs(y1 - y2);
		if (distance == 0) throw new RuntimeException("could not find towns!");
		return distance;
	}
	
	public static Map<String,Town> createTowns(Game game){
		Map<String,Town> towns = new HashMap<String,Town>();
		int count = 0;
		for (int x = 0; x < towner; x++){
			for (int y = 0; y < towner; y++){
				Town t = new Town();
				t.setX(100 + x * 100);
				t.setY(100 + y * 100);
				t.setName(townNames[count]);
				distance[x][y] = townNames[count];
				int production = (int) (java.lang.Math.random() * 10);
				if (production < 3 ){
					t.setGold(1);
					t.setInf(0);
					t.setMana(0);
				} else if (production < 5){
					t.setGold(0);
					t.setInf(1);
					t.setMana(0);
				} else if (production < 7){
					t.setGold(0);
					t.setInf(0);
					t.setMana(1);
				} else if (production < 8){
					t.setGold(1);
					t.setInf(1);
					t.setMana(0);
				} else if (production < 9){
					t.setGold(1);
					t.setInf(0);
					t.setMana(1);
				}  else {
					t.setGold(0);
					t.setInf(0);
					t.setMana(0);
				}
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
