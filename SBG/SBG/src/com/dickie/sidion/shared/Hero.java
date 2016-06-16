package com.dickie.sidion.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Hero extends GameComponentImpl{
	
	public Hero() {
		validAttributes = Arrays.asList("PLAYER", "LOC", "IS_PRINCE", "LEVEL", "KEY");
	}

	public Town getLocation(Game game) {
		return game.getTown(getValue("LOC"));
	}
	
	public String getName() {
		return getValue("KEY");
	}

	public void setName(String name) {
		setValue("KEY", name);
	}

	public void setLocation(Town location) {
		setValue("LOC", location.getKey());
	}
	
	public Integer getLevel() {
		return Integer.parseInt(getValue("LEVEL"));
	}

	public void setLevel(Integer level) {
		setValue("LEVEL", level.toString());
	}
	
	public Boolean getIsPrince() {
		return Boolean.valueOf(getValue("IS_PRINCE"));
	}

	public void setIsPrince(Boolean isPrince) {
		setValue("IS_PRINCE", isPrince.toString());
	}
	
	public static Map<String,Hero> createHeros(Game game){
		Map<String,Hero> heros = new HashMap<String,Hero>();
		int count = 0;
		int count2 = 0;
		int playerCount = 0;
		int split = game.getTowns().size()/game.getPlayers().size();
		for (Town t: game.getTowns()){
			if (count == split){
				count = 0;
				Hero hero = new Hero();
				hero.setName("Prince_" + count2++);
				hero.setLevel(3);
				hero.setOwner(game.getPlayers().toArray(new Player[0])[playerCount]);
				hero.setIsPrince(true);
				hero.setLocation(t);
				t.setGold(1);
				t.setMana(1);;
				t.setInf(1);;
				t.setOwner(game.getPlayers().toArray(new Player[0])[playerCount]);
				heros.put(hero.getName(), hero);
				hero = new Hero();
				hero.setName("Hero_" + count2++);
				hero.setLevel(0);
				hero.setOwner(game.getPlayers().toArray(new Player[0])[playerCount]);
				hero.setIsPrince(false);
				hero.setLocation(t);
				heros.put(hero.getName(), hero);
				playerCount++;
			}
			count++;
		}
		return heros;
		
	}

}
