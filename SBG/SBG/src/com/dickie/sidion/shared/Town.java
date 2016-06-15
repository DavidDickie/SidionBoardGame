package com.dickie.sidion.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Town extends GameComponentImpl {

	public Town(){
		validAttributes = Arrays.asList("KEY", "X", "Y", "GOLD", "MANA", "INF", "PLAYER");
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
		return getValue("KEY");
	}

	public void setName(String town) {
		setValue("KEY", town);
	}

	
	public boolean isLocked(){
		return getValue("PLAYER") != null;
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
