package com.dickie.sidion.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Hero extends GameComponentImpl {

	public Hero() {
		validAttributes = Arrays.asList("PLAYER", "LOC", "IS_PRINCE", "LEVEL", "LKEY", "HASORDER");
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

	public void setOrder() {
		setValue("HASORDER", "true");
	}

	public static Map<String, Hero> createHeros(Game game) {
		Map<String, Hero> heros = new HashMap<String, Hero>();
		int count = 0;
		int count2 = 0;
		int playerCount = 0;

		int towner = (int) java.lang.Math.sqrt(game.getTowns().size());
		Iterator<Town> iTown = game.getTowns().iterator();
		for (int x = 0; x < towner; x++) {
			for (int y = 0; y < towner; y++) {
				if (!iTown.hasNext()) {
					break;
				}
				Town t = iTown.next();
				if ((x == 1 && y == 1) || (x == towner - 2 && y == towner - 2) || (x == towner - 2 && y == 1)
						|| (x == 1 && y == towner - 2)) {
					count = 0;
					Hero hero = new Hero();
					hero.setName("Prince_" + count2++);
					hero.setLevel(3);
					hero.setOwner(game.getPlayers().toArray(new Player[0])[playerCount]);
					hero.setIsPrince(true);
					hero.setLocation(t);
					t.setGold(1);
					t.setMana(1);
					;
					t.setInf(1);
					;
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
			}
		}

		return heros;

	}

}
