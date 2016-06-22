package com.dickie.sidion.server;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.dickie.sidion.client.GreetingService;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Order;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private DAO dao = new DAO();
	
	{
		List<String> games = dao.getGames();
		if (games.size() == 0){
			System.out.println("No games loaded????");
		}
		for (String gameName : games){
			try {
				System.out.println("Loaded " + gameName);
				dao.loadGame(gameName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String greetServer(String input) throws IllegalArgumentException {
		Game game = new Game(input);
		dao.saveGame(game);
		return input;
		
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
	
	
}
