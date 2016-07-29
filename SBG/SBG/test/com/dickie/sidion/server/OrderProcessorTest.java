package com.dickie.sidion.server;

import static org.junit.Assert.*;

import org.junit.Test;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.order.ConvertOrder;
import com.dickie.sidion.shared.order.FinishTurn;
import com.dickie.sidion.shared.order.MoveOrder;

public class OrderProcessorTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		OrderProcessor op = new OrderProcessor();
		for (Player p : game.getPlayers()){
			if (p.isNpc()){
				FinishTurn fo = new FinishTurn();
				fo.setPlayer(p);
				op.processOrder(fo, game);
			}
			
			
		}
		// game should be ready to go to magic orders
		assertTrue(game.getGameState() == Game.ORDER_PHASE);
		Player p = game.getPlayer("Player1");
		Hero h = game.getHero("Prince_6");
		ConvertOrder mo = new ConvertOrder();
		mo.setHero(h);
		mo.setPlayer(p);
		op.processOrder(mo, game);
		FinishTurn fo = new FinishTurn();
		fo.setPlayer(p);
		op.processOrder(fo, game);
		// game should be in magic orders
		assertTrue(game.getGameState() == Game.MAGIC_PHASE);
		
		// 
	}
	
	@Test
	public void test2() {
		Game game = Game.createGame("junit");
		OrderProcessor op = new OrderProcessor();
		
		// queue up a move order to stop on that player during the "physical" round
		
		Player player = game.getPlayer("Player4");
		Hero h = game.getHero("Prince_0");
		MoveOrder mo = new MoveOrder();
		mo.setHero(h);
		mo.setPlayer(player);
		op.processOrder(mo, game);
		
		// mark everyone as having moved
		for (Player p : game.getPlayers()){
			if (p.getPlayerOrder() != 4){
				FinishTurn fo = new FinishTurn();
				fo.setPlayer(p);
				op.processOrder(fo, game);
			}
		}
		
		// with the final finish order for order entry, we should shift to the magic phase
		// and roll right through it to physical orders
		assertTrue(game.getGameState() == Game.PHYS_PHASE);
		
		// so now do the move order and we should go through retreat and end up back in the order phase
		mo.setTown(game.getTown("Beoma"));
		op.processOrder(mo, game);
		FinishTurn fo = new FinishTurn();
		fo.setPlayer(player);
		op.processOrder(fo, game);
		// game should be in generate order
		
		assertTrue(game.getGameState() == Game.ORDER_PHASE);
		// 
	}

}
