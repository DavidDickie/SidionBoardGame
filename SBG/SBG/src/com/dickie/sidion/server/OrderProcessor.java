package com.dickie.sidion.server;

import java.util.List;

import com.dickie.sidion.npc.GenNpcOrders;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.order.EditOrder;
import com.dickie.sidion.shared.order.FinishTurn;
import com.dickie.sidion.shared.order.StandOrder;

public class OrderProcessor {
	
	DAO dao = new DAO();
	GameEngine ge = new GameEngine();

	public String processOrder(Order order, Game game) {
		System.out.println("Processing " + order);
		order.setPrecursors(game);
		if (order instanceof EditOrder ){
			order.executeOnServer(game);
			return "order executed";
		} else if (game.getGameState() == game.ORDER_PHASE){
			if (order instanceof FinishTurn){
				System.out.println("Finish turn order");
				order.executeOnServer(game);
			} else { 
				System.out.println("Game state is order phase; storing order");
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
//					GreetingServiceImpl.getMessageList(game.getName()).add("All orders are in, shifting to magic phase");
					System.out.println("All orders are in, shifting to magic phase");
					System.out.println("Orders are" + game.getOrders());
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
			// after each order, see if a town is conflicted and zero out player orders accordingly
			ge.resetOrderOnConflict(game);
			if (game.ordersSubmitted(order.getPlayer(game))){
				if (game.shiftCurrentToNextPlayer()){
					// if true, all players have moved
					System.out.println("Moving to next phase from " + Game.phaseDef[game.getGameState()]);
					// start by clearing flags
					for (Player p: game.getPlayers()){
						p.setTurnFinished(false);
					}

					
					if (game.shiftToNextGameState()){ // true if it's end of a round
						System.out.println("Moving to next round");
						
						// wipe out heros that did not retreat
						System.out.println("Determining results of retreats");
						for (Hero h: game.getHeros()){
							
							if (h.mustRetreat()){
								System.out.println("Hero " + h.getName() + " did not retreat, removed");
								game.removeGameComponent(h);
							}
						}
						// wipe out orders, replace with "standorder"
						for (Hero h : game.getHeros()){
							Order o = new StandOrder();
							o.setOwner(h.getOwner(game));
							o.setHero(h);
							game.addGameComponent(o);
						}
						// set the original owners for the next combat round
						
						ge.flagOriginalOwner(game);
						
						// produce
						
						ge.produce(game);
						GenNpcOrders gno = new GenNpcOrders();
						for (Player p : game.getPlayers()){
							if (p.isNpc()){
								List<Order> orders = gno.genNpcOrders(p, game);
							}
						}
					} else {
						// set player to orders submitted  if the player has no executable orders for this phase
						clearPlayerWithNoExecOrders(game);
						// if it is the end of the physical round, do combat
						if (game.getGameState() == Game.RETREAT){
							ge.resolveCombat(game);
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
	
	private void clearPlayerWithNoExecOrders(Game game){
		for (Player p : game.getPlayers()){
			boolean finished = true;
			for (Order o : game.getOrders()){
				if (!(o instanceof StandOrder) && 
						o.getOwner(game) == game.getCurrentPlayer() && 
						o.isExecutable(game)){
					finished = false;
					break;
				}
			}
			if (finished){
				p.setTurnFinished(true);
			}
		}
	}

}
