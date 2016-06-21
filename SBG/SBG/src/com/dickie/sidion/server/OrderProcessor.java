package com.dickie.sidion.server;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.order.EditOrder;

public class OrderProcessor {
	
	DAO dao = new DAO();

	public String processOrder(Order order, String gameName) {
		if (Game.getGame(gameName) == null){
			return "Bad game name " + gameName;
		}
		order.setPrecursors(order.getValue("PRECURSORS"), Game.getGame(gameName));
		if (order.validateOrder(Game.getGame(gameName)) != null){
			return order.validateOrder(Game.getGame(gameName));
		}
		if (order.getClass().equals(EditOrder.class)){
			System.out.println("Server execution: " + order);
			Town t = (Town) order.getPrecursors().get("TOWN");
			t.setX(order.getX());
			t.setY(order.getY());
			dao.saveGameComponent(t, gameName);
			return "Town moved";
		}
		// otherwise, verify they match the full list of expected orders and that it's the player's turn
		return "Unknown order " + order.getClass();
	}
}
