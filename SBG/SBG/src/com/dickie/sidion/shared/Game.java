package com.dickie.sidion.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dickie.sidion.npc.GenNpcOrders;

public class Game {
	
	private String name;


	private Map<String, Player> players = new HashMap<String, Player>();
	private Map<String, Hero> heros = new HashMap<String, Hero>();
	private Map<String, Town> towns = new HashMap<String, Town>();
	private Map<String, Path> paths = new HashMap<String, Path>();
	private static Map<String, Game> games = new HashMap<String, Game>();
	private Map<String, Order> orders = new HashMap<String, Order>();
	private Map<String, Message> messages = new HashMap<String, Message>();
	
	private String currentPlayer;
	private String startingPlayer;  
	
	public Message getMessage(String key){
		return messages.get(key);
	}
	
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
		for (Order p: orders.values()){
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
			orders.put(gc.getKey(),(Order)gc);
		} else if (gc instanceof Message){
			messages.put(gc.getKey(), (Message) gc);
		}
	}
	
	public void removeGameComponent(GameComponent gc){
		if (gc instanceof Player){
			players.remove(gc.getKey());
		} else if (gc instanceof Hero){
			heros.remove(gc.getKey());
		} else if (gc instanceof Town){
			towns.remove(gc.getKey());
		} else if (gc instanceof Path){
			paths.remove(gc.getKey());
		} else if (gc instanceof Order){
			orders.remove(gc.getKey());
		} else if (gc instanceof Message){
			messages.remove(gc.getKey());
		}
	}
	
	public List<Message>getMessages(){
		ArrayList<Message> mList = new ArrayList<Message>();
		for (Message m : messages.values()){
			mList.add(m);
		}
		return mList;
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
		return orders.values();
	}
	
	public Order getOrder(String hero){
		return orders.get(hero);
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
		game.messages = new HashMap<String,Message>();
		Message m = new Message();
		m.setKey("MESS1");
		m.setMessage("Game " + name + " created");
		game.addGameComponent(m);
		Game.games.put(name, game);
		
		game.orders.clear();
		return game;
	}
	
	public  void clear(){
		Game game = new Game(name);
		game.players.clear();
		game.towns.clear();
		game.heros.clear();
		game.paths.clear();
		game.orders.clear();
	}
	
	public void addMessage(String message){
		Message m = new Message();
		m.setKey(Message.getNextKey(this));
		m.setMessage(message);
		this.addGameComponent(m);
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
	
	public final static int ORDER_PHASE = 0;
	public final static int MAGIC_PHASE = 1;
	public final static int PHYS_PHASE = 2;
	public final static int RETREAT = 3;
	
	public final static String[] phaseDef = {"ORDER", "MAGIC", "PHYSICAL", "RETREAT"};
	
	public final static int FINAL = 3;
	
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
		if (gameState < ORDER_PHASE || gameState > FINAL){
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
		if (p == null)
			return false;
		return p.isTurnFinshed();
	}
	
	public void shiftPlayersToNextRound(){
		currentPlayer = getNextPlayer().getName();
		startingPlayer = currentPlayer;
	}
	
	// returns true if all players have moved
	public boolean shiftCurrentToNextPlayer(){
		currentPlayer = getNextPlayer().getName(); // move to the original starting player....
		if (currentPlayer.equals(getStartingPlayer().getName())){
			System.out.println("We are back to starting player");
			return true;
		}
		System.out.println("Shifting current player to " + currentPlayer);
		return false;
	}
	
	public Player getNextPlayer(){
		int i = 0;
		Player nextPlayer = null;
		for (Player p : players.values()){
			if (p.equals(getCurrentPlayer())){
				i = getCurrentPlayer().getPlayerOrder();
				break;
			}
		}
		if (i == players.size()-1){
			i = -1;
		}
		i++;
		for (Player p : players.values()){
			if (p.getPlayerOrder() == i){
				nextPlayer = p;
				if (!nextPlayer.getName().equals(startingPlayer)){
					if (!nextPlayer.hasExcecutableOrders(this)){
						System.out.println(nextPlayer.getName() + " has no executable orders, setting to turn finished");
						nextPlayer.setTurnFinished(true);
					}
					if (nextPlayer.isTurnFinshed()){
						System.out.println(nextPlayer.getName() + " is finished skipping to next player");
						currentPlayer = nextPlayer.getName();
						return getNextPlayer();
					}
				}
				break;
			}
		}
		return nextPlayer;
	}

	public boolean shiftToNextGameState() {
		if (getGameState() == FINAL){
			setGameState(0);
			shiftPlayersToNextRound();
			return true;
		} else {
			setGameState(getGameState() + 1);
		}
		return false;
			
	}

	public void clearMessages() {
		messages.clear();
		Message m = new Message(this, "Starting new turn");
	}

}
