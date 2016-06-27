package com.dickie.sidion.shared;

import java.util.ArrayList;
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
	private static List<Order> orders = new ArrayList<Order>();
	
	private String currentPlayer;
	private String startingPlayer;  
	
	public Path getPath(String key){
		return paths.get(key);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Game " + getName() + " state: " + getGameState() + " current player: " +isNull( getCurrentPlayer().getName()));
		sb.append("\nPlayers");
		for (Player p: players.values()){
			sb.append("\t" + p + "\n");
		}
		sb.append("\nHeroes");
		for (Hero p: heros.values()){
			sb.append("\t" + p + "\n");
		}
		sb.append("\nTowns");
		for (Town p: towns.values()){
			sb.append("\t" + p + "\n");
		}
		sb.append("\nOrders");
		for (Order p: orders){
			sb.append("\t" + p + "\n");
		}
		return sb.toString();
	}
	
	public String isNull(Object o){
		if (o == null){
			return "not set";
		}
		return o.toString();
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
		} else if (gc instanceof Order){
			orders.add((Order)gc);
		}
	}
	
	public Collection<Path> getPaths(){
		return paths.values();
	}
	public Town getTown(String townName){
		if (towns.get(townName) == null){
			throw new RuntimeException ("No town " + townName);
		}
		return towns.get(townName);
	}
	
	public Collection<Order> getOrders(){
		return orders;
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
		if (games.get(name) == null){
			throw new RuntimeException ("No game with name " + name);
		}
		return games.get(name);
	}
	
	public Game(String name){
		setName(name);
		games.put(name, this);
	}
	
	public static Game createGame(String name){
		Game game = new Game(name);
		game.players = Player.createPlayers(game, new String []{"Player1", "Player2", "Player3", "Player4"});
		game.setCurrentPlayer("Player1");
		game.setGameState(0);
		game.setStartingPlayer("Player1");
		game.towns = Town.createTowns(game);
		game.heros = Hero.createHeros(game);
		game.paths = Path.createPath(game);
		Game.games.put(name, game);
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
	public final int FINAL = 2;
	
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
	
	public String getCurrentPlayerAsString(){
		return currentPlayer;
	}

	public void setCurrentPlayer(String currentPlayer) {
		this.currentPlayer = currentPlayer;
	} 
	
	public Player getStartingPlayer(){
		return getPlayer(startingPlayer);
	}
	
	public String getStartingPlayerAsString(){
		return startingPlayer;
	}

	public void setStartingPlayer(String player) {
		this.startingPlayer = player;
	} 
	
	public boolean ordersSubmitted(Player p){
		return p.isTurnFinshed();
	}
	
	// returns true if all players have moved
	public boolean shiftCurrentToNextPlayer(){
		Player next = getNextPlayer();
		if (next.equals(getStartingPlayer())){
			currentPlayer = next.getName(); // move to the original starting player....
			currentPlayer = getNextPlayer().getName();  // and one beyond
			startingPlayer = currentPlayer;
			return true;
		}
		currentPlayer = next.getName();
		return false;
	}
	
	public Player getNextPlayer(){
		int i = 0;
		Player nextPlayer = null;
		for (Player p : players.values()){
			if (p.equals(getCurrentPlayer())){
				break;
			}
			i++;
		}
		if (i == players.size()-1){
			i = -1;
		}
		i++;
		nextPlayer = (Player) players.values().toArray()[i];
		return nextPlayer;
	}

	public boolean shiftToNextGameState() {
		if (getGameState() == FINAL){
			setGameState(0);
			return true;
		} else {
			setGameState(getGameState() + 1);
		}
		return false;
			
	}

}
