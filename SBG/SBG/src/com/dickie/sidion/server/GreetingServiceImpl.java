package com.dickie.sidion.server;

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
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	public String  executeSingleOrder(String name, Order order){
		try{
			System.out.println("received new order");
			OrderProcessor op = new OrderProcessor();
			return op.processOrder(order, name);
		} catch (Throwable t){
			return(t.getMessage());
		}
		
	}
	
	public String sendOrders(String name, List<Order> orders) throws IllegalArgumentException {
		try{
			System.out.println("received new orders");
			OrderProcessor op = new OrderProcessor();
			for (Order o : orders){
				String s = o.validateOrder();
				if (s != null){
					return s;
				}
				return op.processOrder(o, name);
			}
		} catch (Throwable t){
			throw new IllegalArgumentException(t);
		}
		return "nothing happened";
	}
	
	
}
