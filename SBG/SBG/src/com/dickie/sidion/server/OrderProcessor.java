package com.dickie.sidion.server;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Order;

public class OrderProcessor {
	
	DAO dao = new DAO();

	public String processOrder(Order order, Game game) {
		System.out.println("Processing " + order);
		order.setPrecursors(order.getValue("PRECURSORS"), game);
		if (order.validateOrder(game) != null){
			return order.validateOrder(game);
		}
		try{
			order.executeOnServer(game);
		} catch (Throwable t){
			return t.getMessage();
		}
		return "Order executed";
	}
}
