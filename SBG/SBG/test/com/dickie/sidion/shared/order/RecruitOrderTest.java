package com.dickie.sidion.shared.order;

import static org.junit.Assert.*;

import org.junit.Test;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;

public class RecruitOrderTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		RecruitOrder ro = new RecruitOrder();
		Hero prince = game.getHero("Prince_2");
		ro.setHero(prince);
		ro.setOwner(prince.getOwner(game));
		System.out.println(ro.validateOrder(game));
		assertTrue(ro.validateOrder(game) != null);
		game.getHero("Hero_3").setLocation(game.getTown("Beoma"));
		assertTrue(ro.validateOrder(game) == null);
		game.setGameState(game.RETREAT);
		assertTrue(ro.validateOrder(game) == null);
		int noHeros = game.getHeros().size();
		ro.executeOnServer(game);
		assertTrue(game.getHeros().size() == noHeros + 1);
		
	}

}
