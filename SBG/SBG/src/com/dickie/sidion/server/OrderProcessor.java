package com.dickie.sidion.server;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.order.CreateGameOrder;
import com.dickie.sidion.shared.order.EditOrder;

public class OrderProcessor {
	
	DAO dao = new DAO();

	public String processOrder(Order order, Game game) {
		System.out.println("Processing " + order);
		order.setPrecursors(game);
		if (game.getGameState() == game.ORDER_PHASE &&
				!(order instanceof EditOrder || order instanceof CreateGameOrder)){
			System.out.println("Game state is order phase; storing order");
			game.getOrders().add(order);
			order.getHero(game).setOrder(true);
			if (game.ordersSubmitted(order.getPlayer(game))){
				boolean allDone = true;
				for (Player p : game.getPlayers()){
					if (!game.ordersSubmitted(p)){
						allDone = false;
						break;
					}
				}
				if (allDone){
					System.out.println("All orders are in, shifting to magic phase");
					game.setGameState(game.MAGIC_PHASE);
					for (Hero h: game.getHeros()){
						h.setOrder(false);
					}
				}
			}
			return "order accepted";
		}
		
		if (order.validateOrder(game) != null){
			return order.validateOrder(game);
		}
		try{
			order.executeOnServer(game);
			order.getHero(game).setOrder(true);
			if (game.ordersSubmitted(order.getPlayer(game))){
				System.out.println("Player has all orders in, shifting to next player");
				if (game.shiftCurrentToNextPlayer()){
					// if true, all players have moved
					System.out.println("Moving to next phase");
					if (game.getGameState() == game.MAGIC_PHASE){
						game.setGameState(game.PHYS_PHASE);
						for (Hero h: game.getHeros()){
							h.setOrder(false);
						}
					}
				}
					
			}
		} catch (Throwable t){
			return t.getMessage();
		}
		return "Order executed";
	}
}
