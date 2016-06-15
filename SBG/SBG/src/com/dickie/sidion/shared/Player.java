package com.dickie.sidion.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Player extends GameComponentImpl {
	
	public Player(){
		validAttributes = Arrays.asList("KEY", "COLOR", "PASSWORD", "IS_ADMIN");
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


	public void setPassword(String password) {
		setValue("PASSWORD", password);
	}


	public String getName() {
		return getValue("KEY");
	}


	public void setName(String name) {
		setValue("KEY", name);
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
			"#000080",
			"#008000",
			"#00FFFF",
			"#4B0082",
			"#7FFFD4",
			"#800000",
			"#8B0000"
	};
	


}
