package com.dickie.sidion.shared.order;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Var;
import com.dickie.sidion.shared.VarString;

public class ConvertOrderTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		ConvertOrder ro = new ConvertOrder();
		Hero prince = game.getHero("Prince_2");
		game.setGameState(Game.MAGIC_PHASE);
		int gold = prince.getOwner(game).getResource("GOLD");
		int mana = 7;
		prince.getOwner(game).addResource("MANA",4);
		System.out.println(prince.getOwner(game));
		HashMap<String, GameComponent> ht = new HashMap<String, GameComponent>();
		ht.put("TYPE", new VarString("GOLD"));
		ht.put("NUM_TO_CONVERT", new Var(4));
		OrderTestUtil.executeOrder(ro, prince, game, ht);
		System.out.println(prince.getOwner(game));
		assertTrue(prince.getOwner(game).getResource("GOLD") == gold+2);
		assertTrue(prince.getOwner(game).getResource("MANA") == mana-4);
	} 

}
