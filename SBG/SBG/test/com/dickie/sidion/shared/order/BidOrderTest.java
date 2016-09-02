package com.dickie.sidion.shared.order;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import com.dickie.sidion.server.OrderProcessor;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Var;

public class BidOrderTest {

	@Test
	public void test() {

		Game game = Game.createGame("junit");
		BidOrder ro = new BidOrder();
		Hero prince = game.getHero("Prince_2");

		int gold = prince.getOwner(game).getGold();
		HashMap<String, GameComponent> ht = new HashMap<String, GameComponent>();
		ht.put("MANA", new Var(1));
		ht.put("GOLD", new Var(4));
		ht.put("INF", new Var(0));
		OrderTestUtil.executeOrder(ro, prince, game, ht);
		game.addGameComponent(ro);
		
		BidOrder ro2 = new BidOrder();
		Hero prince2 = game.getHero("Prince_4");

		int gold2 = prince2.getOwner(game).getGold();
		HashMap<String, GameComponent> ht2 = new HashMap<String, GameComponent>();
		ht2.put("MANA", new Var(1));
		ht2.put("GOLD", new Var(3));
		ht2.put("INF", new Var(0));
		OrderTestUtil.executeOrder(ro2, prince2, game, ht2);
		game.addGameComponent(ro2);

		OrderProcessor op = new OrderProcessor();
		op.doBidOrders(game);
		
		System.out.println(game);
		
		assertTrue(prince.getOwner(game).getGold() == gold-4);
		assertTrue(prince2.getOwner(game).getGold() == gold2);
		
		
	} 

}
