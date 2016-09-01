package com.dickie.sidion.shared.order;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

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
		game.setGameState(Game.RETREAT);
		int gold = prince.getOwner(game).getResource("GOLD");
		HashMap<String, GameComponent> ht = new HashMap<String, GameComponent>();
		ht.put("MANA", new Var(1));
		ht.put("GOLD", new Var(4));
		ht.put("INF", new Var(0));
		OrderTestUtil.executeOrder(ro, prince, game, ht);
		System.out.println(prince.getOwner(game).getResource("GOLD") + " " + gold);
		assertTrue(prince.getOwner(game).getResource("GOLD") == gold-4);
	} 

}
