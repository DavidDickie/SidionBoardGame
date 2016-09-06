package com.dickie.sidion.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Hero extends GameComponentImpl {

	public Hero() {
		validAttributes = Arrays.asList("PLAYER", "LOC", "IS_PRINCE", "LEVEL", "LKEY", "HASORDER", "MUST_RETREAT");
	}
	
	public void setMustRetreat(boolean retreat){
		setValue("MUST_RETREAT", Boolean.toString(retreat));
	}
	
	public boolean mustRetreat(){
		if (attributes.containsKey("MUST_RETREAT")){
			return Boolean.valueOf(getValue("MUST_RETREAT"));
		}
		return false;
	}

	public Town getLocation(Game game) {
		return game.getTown(getValue("LOC"));
	}

	public String getName() {
		return getValue("LKEY");
	}

	public void setName(String name) {
		setValue("LKEY", name);
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

	public Boolean isPrince() {
		return Boolean.valueOf(getValue("IS_PRINCE"));
	}

	public void setIsPrince(Boolean isPrince) {
		setValue("IS_PRINCE", isPrince.toString());
	}

	public boolean hasOrder() {
		if (this.attributes.containsKey("HASORDER")) {
			return Boolean.valueOf(attributes.get("HASORDER"));
		}
		return false;
	}

	public void setOrder(boolean b) {
		setValue("HASORDER", Boolean.toString(b));
	}

	public static Map<String, Hero> createHeros(Game game) {
		Map<String, Hero> heros = new HashMap<String, Hero>();
		int count2 = 0;
		int playerCount = 0;
		for (; playerCount < 4; playerCount++){
			Town t = game.getTown(startTowns[playerCount]);
			t.setHasHero(false);
			Hero hero = new Hero();
			hero.setName("Prince_" + count2++);
			hero.setLevel(3);
			hero.setOwner(game.getPlayers().toArray(new Player[0])[playerCount]);
			hero.setIsPrince(true);
			hero.setLocation(t);
			t.setGold(1);
			t.setMana(1);
			t.setInf(1);
			t.setOwner(game.getPlayers().toArray(new Player[0])[playerCount]);
			heros.put(hero.getName(), hero);
			hero = new Hero();
			hero.setName("Hero_" + count2++);
			hero.setLevel(1);
			hero.setOwner(game.getPlayers().toArray(new Player[0])[playerCount]);
			hero.setIsPrince(false);
			hero.setLocation(t);
			heros.put(hero.getName(), hero);
		}

		return heros;

	}
	
	private static String[] startTowns = {"Vonnie", "Myrle", "Naoma", "Loree"};

}
