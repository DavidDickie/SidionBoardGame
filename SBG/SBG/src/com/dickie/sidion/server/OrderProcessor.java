package com.dickie.sidion.server;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.order.CreateGameOrder;
import com.dickie.sidion.shared.order.EditOrder;

public class OrderProcessor {
	
	DAO dao = new DAO();

	public String processOrder(Order order, Game game) {
		System.out.println("Processing " + order);
		if (game.getGameState() == game.ORDER_PHASE &&
				!(order instanceof EditOrder || order instanceof CreateGameOrder)){
			System.out.println("Game state is order phase; storing order");
			game.getOrders().add(order);
			return "order accepted";
		}
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
