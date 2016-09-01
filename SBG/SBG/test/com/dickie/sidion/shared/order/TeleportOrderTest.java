package com.dickie.sidion.shared.order;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;

public class TeleportOrderTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		TeleportOrder ro = new TeleportOrder();
		Hero hero = game.getHero("Prince_2");
		Hero target = game.getHero("Hero_3");
		Town targetTown = game.getTown("Juliann");
		Player p = hero.getOwner(game);
		game.setGameState(Game.RETREAT);
		int mana = hero.getOwner(game).getResource("MANA");
		int d = targetTown.getDistance(hero.getLocation(game), targetTown, game);
		HashMap<String, GameComponent> ht = new HashMap<String, GameComponent>();
		ht.put("TARGET_HERO", target);
		ht.put("TOWN", targetTown);
		OrderTestUtil.executeOrder(ro, hero, game, ht);
		assertTrue(hero.getOwner(game).getResource("MANA") == mana-d);	
		assertTrue(target.getLocation(game) == targetTown);
	}

}
