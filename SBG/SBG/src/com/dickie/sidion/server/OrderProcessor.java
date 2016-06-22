package com.dickie.sidion.server;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.order.EditOrder;

public class OrderProcessor {
	
	DAO dao = new DAO();

	public String processOrder(Order order, String gameName) {
		System.out.println("Processing " + order);
		Game g = Game.getGame(gameName);
		if (g == null){
			return "Bad game name " + gameName;
		}
		order.setPrecursors(order.getValue("PRECURSORS"), g);
		if (order.validateOrder(Game.getGame(gameName)) != null){
			return order.validateOrder(g);
		}
		try{
			order.executeOnServer(g);
		} catch (Throwable t){
			return t.getMessage();
		}
		return "Order executed";
	}
}
