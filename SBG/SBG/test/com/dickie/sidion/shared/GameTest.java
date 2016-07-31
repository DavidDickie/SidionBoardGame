package com.dickie.sidion.shared;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

public class GameTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		Assert.assertTrue(game.getNextPlayer().getName().equals("Player1"));
		game.shiftCurrentToNextPlayer();
		Assert.assertTrue(game.getNextPlayer().getName().equals("Player1"));
	}

}
