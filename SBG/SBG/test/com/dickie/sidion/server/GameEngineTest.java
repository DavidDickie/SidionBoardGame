package com.dickie.sidion.server;

import static org.junit.Assert.*;

import org.junit.Test;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.order.MoveOrder;
import com.dickie.sidion.shared.order.Retreat;

public class GameEngineTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		System.out.println(game.getTown("Vonnie").getHeros(game));
		System.out.println(game.getTown("Vonnie").getNeighbors(game));
		
		game.getHero("Hero_7").setLocation(game.getTown("Vonnie"));
		game.getHero("Prince_6").setLocation(game.getTown("Vonnie"));
		
		Hero hero1 = game.getHero("Hero_1");
		hero1.setLocation(game.getTown("Mira"));
		
		GameEngine ge = new GameEngine();
		ge.flagOriginalOwner(game);
		
		MoveOrder mo = new MoveOrder();
		mo.setPlayer(hero1.getOwner(game));
		mo.setHero(hero1);
		mo.setTown(game.getTown("Vonnie"));
		mo.executeOnServer(game);
		assertTrue(hero1.getLocation(game).equals(game.getTown("Vonnie")));
		
		ge.resolveCombat(game);
		
		Retreat r = (Retreat) game.getOrder("Hero_1");
		r.setTown(game.getTown("Beoma"));
		
		game.setGameState(Game.RETREAT);
		assertTrue (r.validateOrder(game) == null);
		int gold = hero1.getOwner(game).getGold();
		r.executeOnServer(game);
		assertTrue( hero1.getOwner(game).getGold() == gold - 1);
		assertTrue(hero1.getLocation(game).getName().equals("Beoma"));
		
	}
	
	@Test
	public void tieTest() {
		Game game = Game.createGame("junit");
		//System.out.println(game);
		System.out.println(game.getTown("Vonnie").getHeros(game));
		System.out.println(game.getTown("Vonnie").getNeighbors(game));
		
		
		Hero hero1 = game.getHero("Hero_1");
		hero1.setLocation(game.getTown("Mira"));
		Hero prince = game.getHero("Prince_0");
		prince.setLocation(game.getTown("Mira"));
		
		game.getHero("Hero_7").setLocation(game.getTown("Vonnie"));
		game.getHero("Prince_6").setLocation(game.getTown("Vonnie"));
		
		int gold = hero1.getOwner(game).getGold();
		
		
		
		GameEngine ge = new GameEngine();
		ge.flagOriginalOwner(game);
		
		MoveOrder mo = new MoveOrder();
		mo.setPlayer(hero1.getOwner(game));
		mo.setHero(hero1);
		mo.setTown(game.getTown("Vonnie"));
		mo.executeOnServer(game);
		assertTrue(hero1.getLocation(game).equals(game.getTown("Vonnie")));
		
		mo.setHero(prince);
		mo.executeOnServer(game);
		assertTrue(prince.getLocation(game).equals(game.getTown("Vonnie")));
		assertTrue(hero1.getOwner(game).getGold() == gold - 2);
		
		
		ge.resolveCombat(game);
		
		Retreat r = (Retreat) game.getOrder("Hero_1");
		r.setTown(game.getTown("Beoma"));
		
		game.setGameState(Game.RETREAT);
		assertTrue (r.validateOrder(game) == null);
		
		r.executeOnServer(game);
		assertTrue( hero1.getOwner(game).getGold() == gold - 3);
		assertTrue(hero1.getLocation(game).getName().equals("Beoma"));
		
	}
	
	@Test
	public void loseTest() {
		Game game = Game.createGame("junit");
		game.getHero("Hero_3").setLocation(game.getTown("Beoma"));
		System.out.println(game.getTown("Vonnie").getHeros(game));
		System.out.println(game.getTown("Vonnie").getNeighbors(game));
		
		
		
		Hero hero1 = game.getHero("Hero_1");
		hero1.setLocation(game.getTown("Mira"));
		Hero prince = game.getHero("Prince_0");
		prince.setLocation(game.getTown("Mira"));
		game.getHero("Hero_7").setLocation(game.getTown("Vonnie"));		
		int gold = hero1.getOwner(game).getGold();
		
		
		
		GameEngine ge = new GameEngine();
		ge.flagOriginalOwner(game);
		
		MoveOrder mo = new MoveOrder();
		mo.setPlayer(hero1.getOwner(game));
		mo.setHero(hero1);
		mo.setTown(game.getTown("Vonnie"));
		mo.executeOnServer(game);
		assertTrue(hero1.getLocation(game).equals(game.getTown("Vonnie")));
		
		mo.setHero(prince);
		mo.executeOnServer(game);
		assertTrue(prince.getLocation(game).equals(game.getTown("Vonnie")));
		assertTrue(hero1.getOwner(game).getGold() == gold - 2);
		
		
		ge.resolveCombat(game);
	}


}
