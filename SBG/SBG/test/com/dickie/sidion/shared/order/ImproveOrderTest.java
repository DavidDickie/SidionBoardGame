package com.dickie.sidion.shared.order;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Var;
import com.dickie.sidion.shared.VarString;

public class ImproveOrderTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		ImproveOrder ro = new ImproveOrder();
		Hero prince = game.getHero("Prince_2");
		game.setGameState(Game.MAGIC_PHASE);
		prince.getOwner(game).addResource("GOLD",10);
		Hero target = game.getHero("Hero_3");
		int gold = prince.getOwner(game).getResource("GOLD");
		int heroLev = target.getLevel();
		HashMap<String, GameComponent> ht = new HashMap<String, GameComponent>();
		ht.put("TARGET_HERO", target);
		OrderTestUtil.executeOrder(ro, prince, game, ht);
		System.out.println(gold + " " + prince.getOwner(game).getResource("GOLD"));
		assertTrue(prince.getOwner(game).getResource("GOLD") == gold-4);
		assertTrue(target.getLevel() == heroLev + 1);
	}

}
