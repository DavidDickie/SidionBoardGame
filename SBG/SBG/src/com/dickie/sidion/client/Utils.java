package com.dickie.sidion.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Order;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Utils {
	
	private final static GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	private static DisplayHtmlDialog popupDialog = DisplayHtmlDialog.getInstance();
	

	public static void displayMessage(String message) {
		popupDialog.display("Notification", message);
	}
	
	public static void logMessage(String s) {
		greetingService.logMessage(s, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				
			}

		});
	}
	
//	private static Map<String, List<Order>> orders = new HashMap<String, List<Order>>();
//	
//	public static void addOrderToQueue(Order order, String game){
//		if (orders.containsKey(game)){
//			orders.get(game).add(order);
//		} else {
//			ArrayList<Order> list = new ArrayList<Order>();
//			list.add(order);
//			orders.put(game,  list);
//		}
//	}
//	
//	public static void setOrders(){
//		for (String game : orders.keySet()){
//			for (Order o : orders.get(game)){
//				Utils.logMessage("Sending order " + o);
//				sendOrderToServer(o, game);
//			}
//		}
//	}
	
	public static String sendOrderToServer(Order order, Game game){
		
		greetingService.executeSingleOrder(game.getName(), order, new AsyncCallback<String> (){

			@Override
			public void onFailure(Throwable caught) {
				Utils.logMessage("RPC call failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				if (result != null ){
					displayMessage("result: " + result);
					Utils.logMessage("Result of order was " + result);
				} else {
					//displayMessage("Order accepted");	
					Utils.logMessage("Order accepted");
				}
				Utils.logMessage("Order processing complete");
			}});
		return null;
	}

}
