package com.dickie.sidion.shared;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Game {
	
	private String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private Map<String, Player> players;
	private Map<String, Hero> heros;
	private Map<String, Town> towns;
	private static Map<String, Game> games = new HashMap<String, Game>();
	
	private String currentPlayer;
	
	public Town getTown(String townName){
		return towns.get(townName);
	}
	
	public Collection<Town> getTowns(){
		return towns.values();
	}
	
	public Hero getHero(String heroName){
		return heros.get(heroName);
	}
	
	public Player getPlayer(String playerName){
		return players.get(playerName);
	}
	
	public Collection<Player> getPlayers(){
		return players.values();
	}
	
	public Player getCurrentPlayer(){
		return getPlayer(currentPlayer);
	}
	
	public static Game getInstance(String name){
		return games.get(0);
	}
	
	public Game(String name){
		if (games.containsKey(name)){
			return;
		}
		setName(name);
		players = Player.createPlayers(this, new String []{"Player1", "Player2", "Player3", "Player4"});
		towns = Town.createTowns(this);
		heros = Hero.createHeros(this);
		games.put("test", this);
	}

	public Game() {
		// TODO Auto-generated constructor stub
	}

}
