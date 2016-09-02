package com.dickie.sidion.shared.order;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;

public class LockOrderTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		LockOrder ro = new LockOrder();
		Hero hero = game.getHero("Hero_3");
		Player p = hero.getOwner(game);
		game.setGameState(Game.RETREAT);
		hero.getOwner(game).addResource("INF",10);
		Town target = game.getTown("Vonnie");
		hero.setLocation(target);
		int inf = hero.getOwner(game).getInf();
		HashMap<String, GameComponent> ht = new HashMap<String, GameComponent>();
		OrderTestUtil.executeOrder(ro, hero, game, ht);
		assertTrue(hero.getOwner(game).getInf() == inf-LockOrder.getInfCost(target));
		assertTrue(game.getHero(hero.getName()) == null);
		assertTrue(target.getOwner(game) == p); 
	}

}
