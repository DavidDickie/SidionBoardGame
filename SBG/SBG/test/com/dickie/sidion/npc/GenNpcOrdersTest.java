package com.dickie.sidion.npc;

import static org.junit.Assert.*;

import org.junit.Test;

import com.dickie.sidion.server.OrderProcessor;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.order.FinishTurn;

public class GenNpcOrdersTest {

	OrderProcessor op = new OrderProcessor();
	
	@Test
	public void test() {
		Game game = Game.createGame("junit");
		game.getPlayer("Player1").setValue("NPC", "true");
		
		// normally the game create command creates npc orders, but that's in the higher level routine
		GenNpcOrders gno = new GenNpcOrders();
			gno.genNpcOrders(game);
		for (Player p: game.getPlayers()){
			finish(p, game);
		}
		for (int i = 0; i < 5; i++){
			Player p = game.getCurrentPlayer();
			finish(p, game);
			checkWinnerStats(game);
		}
			
	}
	
	private boolean checkWinnerStats(Game g){
		boolean winner = false;
		for (Player p : g.getPlayers()){
			System.out.print(p.getName() + " has ");
			int i = 0;
			for (Town t : g.getTowns()){
				if (t.isLocked() && t.getOwner(g) == p){
					i++;
				}
			}
			System.out.print(i + " locked towns and " + p.getArtifacts() + " artifacts.");
			System.out.println("  Resources g/i/m: " + p.getGold() + "/" + p.getInf() + "/" + p.getMana());
			if (i == 7){
				winner = true;
			}
		}
		return winner;
	}
	
	private void finish(Player p, Game g){
		FinishTurn ft = new FinishTurn();
		ft.setOwner(p);
		op.processOrder(ft, g);
	}

}
