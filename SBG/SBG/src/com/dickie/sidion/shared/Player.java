package com.dickie.sidion.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dickie.sidion.shared.order.StandOrder;

public class Player extends GameComponentImpl {
	
	public Player(){
		validAttributes = Arrays.asList("LKEY", "COLOR", "PASSWORD", "IS_ADMIN", "MANA", "INF", "GOLD", "ARTIFACTS", "TURN_FINISHED", "TURNORDER", "NPC");
	}
	
	int playerOrder = 0;
	
	
	public int getPlayerOrder(){
		return Integer.valueOf(getValue("TURNORDER"));
	}
	
	public void setPlayerOrder(int x){
		setValue("TURNORDER", Integer.toString(x));
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
	
	public int getGold(){
		return getResource("GOLD");
	}
	
	public int getMana(){
		return getResource("MANA");
	}
	
	public int getInf(){
		return getResource("INF");
	}
	
	public int getArtifacts(){
		return getResource("ARTIFACTS");
	}
	
	private int getResource(String type){
		if (getValue(type) == null){
			return 0;
		}
		return Integer.valueOf(getValue(type));
	}
	
	public void addResource(String type, int number){
		int current = getResource(type);
		current += number;
		if (current < 0){
			throw new RuntimeException("Setting resource " + type + " to less than 0");
		}
		setValue(type, Integer.toString(current));
	}


	public void setPassword(String password) {
		setValue("PASSWORD", password);
	}
	
	public boolean isTurnFinshed(){
		if (attributes.containsKey("TURN_FINISHED")){
			return Boolean.valueOf(getValue("TURN_FINISHED"));
		}
		return false;
	}
	
	public void setTurnFinished(boolean b){
		setValue("TURN_FINISHED", Boolean.toString(b));
	}
	
	public boolean hasExcecutableOrders(Game g){
		for (Order o : g.getOrders()){
			if (o.getPlayer(g) == this && o.isExecutable(g) && !(o instanceof StandOrder)){
				return true;
			}
		}
		return false;
	}


	public String getName() {
		return getValue("LKEY");
	}


	public void setName(String name) {
		setValue("LKEY", name);
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
			if (count == 0){
				p.setValue("NPC", "false");
			} else {
				p.setValue("NPC", "true");
			}
			p.setColor(colors[count]);
			p.setName(name);
			p.setAdmin(false);
			p.addResource("GOLD", 5);
			p.addResource("MANA", 3);
			p.addResource("INF", 0);
			players.put(names[count],p);
			p.setPlayerOrder(count);
			System.out.println(p);
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
		return Boolean.valueOf(getValue("IS_ADMIN"));
	}

	public boolean isNpc() {
		return Boolean.valueOf(getValue("NPC"));
	}
	
	public void setNpc(boolean b){
		setValue("NPC", Boolean.toString(b));
	}

}
