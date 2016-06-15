package com.dickie.sidion.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player implements GameComponent {

	public String getKey() {
		// TODO Auto-generated method stub
		return name;
	}


	public Town location(Game game) {
		// TODO Auto-generated method stub
		return null;
	}

	public Player owner(Game game) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setValue(String field, Object value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void selected() {
		// TODO Auto-generated method stub
		
	}
	
	public String getColor() {
		return color;
	}


	public void setColor(String color) {
		this.color = color;
	}

	private String color;
	private String name;
	private boolean admin = false;
	public boolean isAdmin() {
		return admin;
	}


	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	private String password = "password";
	
	
	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public static Map<String, Player> createPlayers(Game game, String []names){
		Map<String, Player> players = new HashMap<String, Player>();
		int count=0;
		for (String name : names){
			Player p = new Player();
			p.setColor(colors[count]);
			p.setName(names[count]);
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
