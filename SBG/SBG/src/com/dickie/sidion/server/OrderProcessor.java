package com.dickie.sidion.server;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.order.EditOrder;
import com.dickie.sidion.shared.order.FinishTurn;
import com.dickie.sidion.shared.order.StandOrder;

public class OrderProcessor {
	
	DAO dao = new DAO();

	public String processOrder(Order order, Game game) {
		System.out.println("Processing " + order);
		order.setPrecursors(game);
		if (game.getGameState() == game.ORDER_PHASE){
			System.out.println("Game state is order phase; storing order");
			
			if (order instanceof EditOrder || order instanceof FinishTurn){
				order.executeOnServer(game);
			} else {
				game.addGameComponent(order);
			}
			if (game.ordersSubmitted(order.getPlayer(game))){
				boolean allDone = true;
				for (Player p : game.getPlayers()){
					if (!game.ordersSubmitted(p)){
						allDone = false;
						System.out.println("Player " + p.getName() + " has not submitted orders");
						break;
					}
				}
				if (allDone){
					GreetingServiceImpl.getMessageList(game.getName()).add("All orders are in, shifting to magic phase");
					System.out.println("All orders are in, shifting to magic phase");
					game.setGameState(game.MAGIC_PHASE);
					for (Player p: game.getPlayers()){
						p.setTurnFinished(false);
					}
				}
			}
			return "order accepted";
		}
		
		if (order.validateOrder(game) != null){
			return order.validateOrder(game);
		}
		if (!order.getPlayer(game).equals(game.getCurrentPlayer())){
			return "it is not " + order.getPlayer(game).getName() + "'s turn; order ignored";
		}
		try{
			order.executeOnServer(game);
			if (game.ordersSubmitted(order.getPlayer(game))){
				if (game.shiftCurrentToNextPlayer()){
					// if true, all players have moved
					System.out.println("Moving to next phase");
					for (Player p: game.getPlayers()){
						p.setTurnFinished(false);
					}
					if (game.shiftToNextGameState()){ // true if it's end of a round
						System.out.println("Moving to next round");
						// wipe out orders, replace with "standorder"
						for (Hero h : game.getHeros()){
							Order o = new StandOrder();
							o.setOwner(h.getOwner(game));
							o.setHero(h);
							game.addGameComponent(o);
						}
					}
				} else {
					System.out.println("Moved to next player");
				}
			}
		} catch (Throwable t){
			return t.getMessage();
		}
		return "Order executed";
	}
}
