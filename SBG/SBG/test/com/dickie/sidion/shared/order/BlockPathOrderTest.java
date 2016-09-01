package com.dickie.sidion.shared.order;

import static org.junit.Assert.*;

import java.util.HashMap;


import org.junit.Test;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Path;
import com.dickie.sidion.shared.Town;

public class BlockPathOrderTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		Hero h = game.getHero("Prince_0");
		h.setLocation(game.getTown("Bran"));

		game.setGameState(Game.MAGIC_PHASE);
		
		BlockPathOrder bpo = new BlockPathOrder();
		HashMap <String, GameComponent> ht = new HashMap<String, GameComponent>();
		ht.put("PATH",Path.getPath(game.getTown("Maya"), game.getTown("Shawna"), game));
		OrderTestUtil.executeOrder(bpo, h, game, ht);
		assertTrue(Path.getPath(game.getTown("Maya"), game.getTown("Shawna"), game).getBlocked());
	} 

}
