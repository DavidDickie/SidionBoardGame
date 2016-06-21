package com.dickie.sidion.shared;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Game {
	
	private String name;


	private Map<String, Player> players = new HashMap<String, Player>();
	private Map<String, Hero> heros = new HashMap<String, Hero>();
	private Map<String, Town> towns = new HashMap<String, Town>();
	private Map<String, Path> paths = new HashMap<String, Path>();
	private static Map<String, Game> games = new HashMap<String, Game>();
	
	private String currentPlayer;
	private String startingPlayer;  
	
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

	
	public static Game getInstance(String name){
		return games.get(name);
	}
	
	public Game(String name){
		setName(name);
		games.put(name, this);
	}
	
	public static Game createGame(String name){
		if (games.containsKey(name)){
			return games.get(name);
		}
		Game game = new Game(name);
		game.players = Player.createPlayers(game, new String []{"Player1", "Player2", "Player3", "Player4"});
		game.towns = Town.createTowns(game);
		game.heros = Hero.createHeros(game);
		game.paths = Path.createPath(game);;
		return game;
	}
 

	public Game() {
	}
	
	public void addGame(Game g){
		games.put(g.getName(), g);
	}
	
	public static Game getGame(String name){
		return games.get(name);
	}

	public Collection<Hero> getHeros() {
		return heros.values();
	}
	
	public final int ORDER_PHASE = 0;
	public final int MAGIC_PHASE = 1;
	public final int PHYS_PHASE = 2;
	
	private int gameState = ORDER_PHASE;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public int getGameState() {
		return gameState;
	}

	public void setGameState(int gameState) {
		if (gameState < ORDER_PHASE || gameState > PHYS_PHASE){
			throw new RuntimeException("Illegal game state");
		}
		this.gameState = gameState;
	}
	
	
	public Player getCurrentPlayer(){
		return getPlayer(currentPlayer);
	}

	public void setCurrentPlayer(String currentPlayer) {
		this.currentPlayer = currentPlayer;
	} 
	
	public Player getStartingPlayer(){
		return getPlayer(startingPlayer);
	}

	public void setStartingPlayer(String player) {
		this.startingPlayer = player;
	} 

}
