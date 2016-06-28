package com.dickie.sidion.server;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dickie.sidion.client.GreetingService;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Var;
import com.dickie.sidion.shared.VarString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private DAO dao = new DAO();
	
	private static Map<String, ArrayList<String>> gameMessages = new HashMap<String, ArrayList<String>>();
	
	{
		
		List<String> games = dao.getGames();
		if (games.size() == 0){
			System.out.println("No games loaded????");
		}
		for (String gameName : games){
			ArrayList<String> messages = new ArrayList<String>();
			messages.add("Start");
			gameMessages.put(gameName, messages);
			try {
				System.out.println("Loaded " + gameName);
				dao.loadGame(gameName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static synchronized List<String> getMessageList(String game){
		return gameMessages.get(game);
	}
	
	
	public String greetServer(String input) throws IllegalArgumentException {
		dao.saveGame(Game.createGame(input));
		return input;
	}
	
	public List<String> getLatestMessagesFromServer(String game, int lastMessage){
		System.out.println("Getting messages from " + lastMessage + "; " + gameMessages.size() + " in queue");
		ArrayList<String> messages =  new ArrayList<String>();
		for (int i = lastMessage + 1; i < gameMessages.get(game).size(); i++){
			messages.add(gameMessages.get(game).get(i));
		}
		return messages;
	}
	
	public Void logMessage(String input){
		System.out.println(input);
		return null;
	}
	
	public List<GameComponent> get(String gameName, String type) throws IllegalArgumentException {
		try {
			return dao.getData(gameName, type);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	public String  executeSingleOrder(String name, Order order){
		try{	
			System.out.println("received new order: " + order);
			OrderProcessor op = new OrderProcessor();
			op.processOrder(order, Game.getInstance(name));
			dao.saveGame(Game.getInstance(name));
			return "order executed";
		} catch (Throwable t){
			t.printStackTrace();
			return("ERROR: " + t.getMessage());
		}
	}
	
	public String sendOrders(String name, List<Order> orders) {
		try{
			Game game = Game.getInstance(name);
			OrderProcessor op = new OrderProcessor();
			for (Order o : orders){
				op.processOrder(o, game);
			}
			dao.saveGame(game);
			return "orders processed";
		} catch (Throwable t){
			t.printStackTrace();
			return "ERROR: " + t.getMessage();
		}
	}
	
	public Map<String, GameComponent> getGameAttrs(String name){
		HashMap<String, GameComponent> map = new HashMap<String, GameComponent>();
		Game game = Game.getInstance(name);
		Var var = new Var(game.getGameState());
		map.put("GAMESTATE", var);
		VarString var2 = new VarString(game.getCurrentPlayer().getName());
		map.put("CURRENTPLAYER", var2);
		VarString var3 = new VarString(game.getStartingPlayer().getName());
		map.put("STARTINGPLAYER", var3);
		return map;
	}
	
	
}
