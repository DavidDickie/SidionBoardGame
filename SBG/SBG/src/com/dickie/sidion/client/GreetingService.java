package com.dickie.sidion.client;

import java.util.List;
import java.util.Map;

import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Order;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;
	Void logMessage(String input);
	List<GameComponent> get(String name, String type) throws IllegalArgumentException;
	String sendOrders(String name, List<Order> orders);
	String executeSingleOrder(String name, Order order);
	Map<String, GameComponent> getGameAttrs(String name);
}
