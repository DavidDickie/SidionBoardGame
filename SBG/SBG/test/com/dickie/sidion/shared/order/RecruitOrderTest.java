package com.dickie.sidion.shared.order;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;

public class RecruitOrderTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		RecruitOrder ro = new RecruitOrder();
		Hero prince = game.getHero("Prince_2");
		prince.setLocation(game.getTown("Vonnie"));
		game.setGameState(game.RETREAT);
		int noHeros = game.getHeros().size();
		HashMap <String, GameComponent> ht = new HashMap<String, GameComponent>();
		OrderTestUtil.executeOrder(ro, prince, game, ht);
		assertTrue(game.getHeros().size() == noHeros + 1);
	} 

}
