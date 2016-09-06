package com.dickie.sidion.server;

import java.util.ArrayList;
import java.util.List;

import com.dickie.sidion.npc.GenNpcOrders;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.order.BidOrder;
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
			return null;
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
					System.out.print("All orders in, orders are:\n" );
					for (Order o : game.getOrders()){
						System.out.println("\t" + o);
					}
					// set the original owners for the next combat round
					
					ge.flagOriginalOwner(game);
					
					game.clearMessages();
					game.setGameState(game.MAGIC_PHASE);
					for (Player p: game.getPlayers()){
						p.setTurnFinished(false);
					}
				}
				postOrderCheck(game, game.getCurrentPlayer(), false);
			}
			return null;
		}
		
		if (order.validateOrder(game) != null){
			return order.validateOrder(game);
		}
		if (!order.getPlayer(game).equals(game.getCurrentPlayer())){
			return "it is not " + order.getPlayer(game).getName() + "'s turn; order ignored";
		}
		try{
			order.executeOnServer(game);//
			// after each order, see if a town is conflicted and zero out player orders accordingly
			ge.resetOrderOnConflict(game);
			if (game.ordersSubmitted(order.getPlayer(game))){
				// see if we can shift to the next player / phase / turn
				// we know the current player is done, so skip the check
				// to see if they still have orders
				postOrderCheck(game, order.getPlayer(game), true);
			}
		} catch (Throwable t){
			t.printStackTrace();
			return t.getMessage();
		}
		return null;
	}
	
	private void postOrderCheck(Game game, Player player, boolean forceIt){
		if (!player.isTurnFinshed() && player.hasExcecutableOrders(game) && !forceIt){
			// there's something for this player to do, don't move on
			return;
		}
		if (game.shiftCurrentToNextPlayer()){
			shiftToNextPhase(game, player);
		} else {
			npcCheck(game);
		}
	}
	
	private void npcCheck(Game game){

		if (game.getCurrentPlayer().isNpc()){
			System.out.println("Player " + game.getCurrentPlayer().getName() + " is a NPC, executing  orders and moving on");
			for (Order o : game.getOrders()){
				if (o.getPlayer(game) == game.getCurrentPlayer()){
					if (o.isExecutable(game)){
						// and isExecutable is going to add the order params,
						// so we need to reset the precursors
						o.setPrecursors(game);
						if (o.validateOrder(game) == null){
							o.executeOnServer(game);
						} else {
							System.out.println("*********NPC order failed: " + o.validateOrder(game) + " " + o.toString());
						}
					}
				}
			}
			FinishTurn fo = new FinishTurn();
			fo.setPlayer(game.getCurrentPlayer());
			this.processOrder(fo, game);
		}
	}
	
	private void shiftToNextPhase(Game game, Player player){
		// if true, all players have moved
		System.out.println("Moving to next phase from " + Game.phaseDef[game.getGameState()]);
		// start by clearing flags
		for (Player p: game.getPlayers()){
			p.setTurnFinished(false);
		}

		//
		// need to record if artifact was up this round...
		boolean artifactWasUp = game.isArtifactUp();
		if (game.shiftToNextGameState()){ // true if it's end of a round
			shiftToNextRound(game, player,artifactWasUp);
		} else {
			if (game.getGameState() == Game.RETREAT){
				// should queue up retreat orders if there's been combat
				ge.resolveCombat(game);
			}
			// check if the first player has no orders for this phase and if so, 
			// move on!  This will always end if we move to the next turn
			if (!game.getCurrentPlayer().hasExcecutableOrders(game)){
				System.out.println("No executable orders for " + game.getCurrentPlayer().getName());
				if (game.shiftCurrentToNextPlayer()){
					shiftToNextPhase(game, player);
				} else {
					System.out.println("Waiting for " + game.getCurrentPlayer().getName());
					npcCheck(game);
				}
			} else {
				System.out.println("Waiting on " + game.getCurrentPlayer().getName());
				npcCheck(game);
			}
		}
	}
	
	private void shiftToNextRound(Game game, Player player, boolean artWasUp){
		System.out.println("Moving to next round");
		
		// wipe out heros that did not retreat
		System.out.println("Determining results of retreats");
		ArrayList<Hero> heros = new ArrayList<Hero>(game.getHeros());
		for (Hero h: heros){
			
			if (h.mustRetreat()){
				game.addMessage("Hero " + h.getName() + " did not retreat, eliminated");
				game.removeGameComponent(h);
			}
		}
		// figure out if people bid for an artifact, and if so, give it to them
		if (artWasUp){
			doBidOrders(game);
		}
		
		// wipe out orders, replace with "standorder"
		for (Hero h : game.getHeros()){
			Order o = new StandOrder();
			o.setOwner(h.getOwner(game));
			o.setHero(h);
			game.addGameComponent(o);
		}
		
		// produce
		
		ge.produce(game);
		GenNpcOrders gno = new GenNpcOrders();
		for (Player p : game.getPlayers()){
			if (p.isNpc()){
				List<Order> orders = gno.genNpcOrders(p, game);
				for (Order o : orders){
					o.execute();  // serialize the order
					game.addGameComponent(o);
					o.getHero(game).getOwner(game).setTurnFinished(true);
				}
			}
		}
		
		//
		
		System.out.println("******************************************\n                              New turn\n******************************************\n\nGamestate is:\n" + game);
	}
	
	public void doBidOrders(Game game){
		List<BidOrder> bids = new ArrayList<BidOrder>();
		for (Order o : game.getOrders()){
			if (o instanceof BidOrder){
				if (o.validateOrder(game) != null){
					game.addMessage("Bid by " + o.getHero(game).getName() + " failes; " + o.validateOrder(game));
					continue;
				}
				bids.add((BidOrder) o);
			}
		}
		if (bids.size() == 0){
			game.addMessage("No valid bid on the artifact");
		} else {
			BidOrder winner = null;
			int maxBid = 0;
			boolean tie = true;
			for (BidOrder o : bids){
				o.setPrecursors(game);
				int total = o.getGoldBid() + o.getManaBid() + o.getInfBid();
				if (total > maxBid){
					maxBid = o.getGoldBid() + o.getManaBid() + o.getInfBid();
					winner = o;
					tie = false;
				} else if (total == maxBid){
					tie = true;
				}
			}
			if (tie){
				game.addMessage("Tie bid on artifact, no winner!");
			} else {
				winner.executeWinner(game);
			}
		}
	}
	

}
