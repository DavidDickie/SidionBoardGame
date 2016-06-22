package com.dickie.sidion.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Player extends GameComponentImpl {
	
	public Player(){
		validAttributes = Arrays.asList("KEY", "COLOR", "PASSWORD", "IS_ADMIN", "MANA", "INF", "GOLD", "ARTIFACTS");
	}
	
	public String getColor() {
		return getValue("COLOR");
	}


	public void setColor(String color) {
		setValue("COLOR", color);
	}

	public void setAdmin(Boolean admin) {
		setValue("IS_ADMIN", admin.toString());
	}	
	
	public String getPassword() {
		return getValue("PASSWORD");
	}
	
	public int getResource(String type){
		return Integer.valueOf(getValue(type));
	}
	
	public void addResource(String type, int number){
		int current = getResource(type);
		current += number;
		setValue(type, Integer.toString(current));
	}


	public void setPassword(String password) {
		setValue("PASSWORD", password);
	}


	public String getName() {
		return getValue("KEY");
	}


	public void setName(String name) {
		setValue("KEY", name);
	}
	
	public List<Hero> getHeros(Game game){
		ArrayList<Hero> list = new ArrayList<Hero>();
		Iterator<Hero> iHero = game.getHeros().iterator();
		while (iHero.hasNext()){
			Hero h = iHero.next();
			if (h.getOwner(game).equals(this)){
				list.add(h);
			}
		}
		return list;
	}


	public static Map<String, Player> createPlayers(Game game, String []names){
		Map<String, Player> players = new HashMap<String, Player>();
		int count=0;
		for (String name : names){
			Player p = new Player();
			p.setColor(colors[count]);
			p.setName(name);
			p.setAdmin(false);
			players.put(names[count],p);
			count++;
		}
		return players;
	}
	
	static String[] colors = {
			"#009999",
			"#6666FF",
			"#993366",
			"#990000",
			"#7FFFD4",
			"#CC0000",
			"#CC9933"
	};

	public boolean isAdmin() {
		return isAdmin();
	}
	


}
