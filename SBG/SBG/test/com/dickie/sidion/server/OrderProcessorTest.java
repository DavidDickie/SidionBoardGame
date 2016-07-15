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
	}

}
