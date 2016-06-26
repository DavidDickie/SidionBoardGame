package com.dickie.sidion.client;

import java.util.List;
import java.util.Map;

import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Order;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void logMessage(String input, AsyncCallback<Void> callback);
	void get(String name, String type, AsyncCallback<List<GameComponent>> callback) 
			throws IllegalArgumentException;
	void sendOrders(String name, List<Order> orders, AsyncCallback<String> callback);
	void executeSingleOrder(String name, Order order, AsyncCallback<String> callback);	
	void getGameAttrs(String name, AsyncCallback<Map<String, GameComponent>> callback);
	void getLatestMessagesFromServer(String game, int lastMessage, AsyncCallback<List<String>> callback);
}
