package com.dickie.sidion.shared.order;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Town;

public class ImproveTownOrderTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		ImproveTownOrder ro = new ImproveTownOrder();
		Hero prince = game.getHero("Prince_2");
		game.setGameState(Game.MAGIC_PHASE);
		prince.getOwner(game).addResource("GOLD",10);
		Town target = game.getTown("Vonnie");
		prince.setLocation(target);
		int gold = prince.getOwner(game).getResource("GOLD");
		int townLev = target.getLevel();
		HashMap<String, GameComponent> ht = new HashMap<String, GameComponent>();
		ht.put("TARGET_HERO", target);
		OrderTestUtil.executeOrder(ro, prince, game, ht);
		System.out.println(gold + " " + prince.getOwner(game).getResource("GOLD"));
		assertTrue(prince.getOwner(game).getResource("GOLD") == gold-((townLev+1)*(townLev+1)));
		assertTrue(target.getLevel() == townLev + 1);  
	} //

}
