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

	private Map<String, Player> players = new HashMap<String, Player>();
	private Map<String, Hero> heros = new HashMap<String, Hero>();
	private Map<String, Town> towns = new HashMap<String, Town>();
	private Map<String, Path> paths = new HashMap<String, Path>();
	private static Map<String, Game> games = new HashMap<String, Game>();
	
	private String currentPlayer;
	
	public Path getPath(String key){
		return paths.get(key);
	}
	
	public void addGameComponent(GameComponent gc){
		if (gc instanceof Player){
			players.put(gc.getKey(), (Player) gc);
		} else if (gc instanceof Hero){
			heros.put(gc.getKey(), (Hero) gc);
		} else if (gc instanceof Town){
			towns.put(gc.getKey(), (Town)gc);
		} else if (gc instanceof Path){
			paths.put(gc.getKey(), (Path)gc);
		}
	}
	
	public Collection<Path> getPaths(){
		return paths.values();
	}
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
		paths = Path.createPath(this);;
		games.put(name, this);
	}
 

	public Game() {
		// TODO Auto-generated constructor stub
	}
	
	public static Game getGame(String name){
		return games.get(name);
	}

	public Collection<Hero> getHeros() {
		return heros.values();
	}

}
