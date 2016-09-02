package com.dickie.sidion.server;

import static org.junit.Assert.*;

import org.junit.Test;

import com.dickie.sidion.npc.GenNpcOrders;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Message;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.order.ConvertOrder;
import com.dickie.sidion.shared.order.FinishTurn;
import com.dickie.sidion.shared.order.MoveOrder;

public class OrderProcessorTest {

	@Test
	public void test() {
		Game game = Game.createGame("junit");
		OrderProcessor op = new OrderProcessor();
		// normally the game create command creates npc orders, but that's in the higher level routine
		GenNpcOrders gno = new GenNpcOrders();
			gno.genNpcOrders(game);
		game.getPlayer("Player2").setTurnFinished(true);
		game.getPlayer("Player3").setTurnFinished(true);
		game.getPlayer("Player4").setTurnFinished(true);
		assertTrue(game.getGameState() == Game.ORDER_PHASE);
		Player p = game.getPlayer("Player1");
		Hero h = game.getHero("Prince_6");
		ConvertOrder mo = new ConvertOrder();
		mo.setHero(h);
		mo.setPlayer(p);
		mo.addType("GOLD");
		mo.addAmountToConvert(1);
		mo.execute();
		op.processOrder(mo, game);
		FinishTurn fo = new FinishTurn();
		fo.setPlayer(p);
		op.processOrder(fo, game);
		// game should be in magic orders
		assertTrue(game.getGameState() == Game.MAGIC_PHASE);
		// now, execute player1's orders... the other guys
		// are NPCs and we should cycle through them 
		// and end up back at the order entry phse
		op.processOrder(mo, game);
		op.processOrder(fo, game);
		assertTrue(game.getGameState() == Game.ORDER_PHASE);
	
	}
	
	@Test
	public void test2() {
		Game game = Game.createGame("junit");
		OrderProcessor op = new OrderProcessor();
		
		// queue up a move order to stop on that player during the "physical" round
		
		Player player = game.getPlayer("Player1");
		Hero h = game.getHero("Prince_6");
		MoveOrder mo = new MoveOrder();
		mo.setHero(h);
		mo.setPlayer(player);
		op.processOrder(mo, game);
		
		// mark everyone as having moved
		for (Player p : game.getPlayers()){
			if (p.getPlayerOrder() != 4){
				FinishTurn fo = new FinishTurn();
				fo.setPlayer(p);
				op.processOrder(fo, game);
			}
		}
		
		// with the final finish order for order entry, we should shift to the magic phase
		// and roll right through it to physical orders
		assertTrue(game.getGameState() == Game.PHYS_PHASE);
		
		// so now do the move order and we should go through retreat and end up back in the order phase
		mo.setTown(game.getTown("Beoma"));
		op.processOrder(mo, game);
		FinishTurn fo = new FinishTurn();
		fo.setPlayer(player);
		op.processOrder(fo, game);
		// game should be in generate order
		
		assertTrue(game.getGameState() == Game.ORDER_PHASE);
		// 
	}
	
	@Test
	public void fullTurnTest(){
		Game game = Game.createGame("junit");
		OrderProcessor op = new OrderProcessor();
		game.getHero("Hero_3").setLocation(game.getTown("Beoma"));
		game.getHero("Hero_7").setLocation(game.getTown("Vonnie"));		
		
		// normally the game create command creates npc orders, but that's in the higher level routine
		GenNpcOrders gno = new GenNpcOrders();
			gno.genNpcOrders(game);
			
		game.getPlayer("Player2").setTurnFinished(true);
		game.getPlayer("Player3").setTurnFinished(true);
		game.getPlayer("Player4").setTurnFinished(true);
		
		Player p = game.getPlayer("Player1");
		Hero h = game.getHero("Prince_6");
		ConvertOrder co = new ConvertOrder();
		co.setHero(h);
		co.setPlayer(p);
		co.addType("GOLD");
		co.addAmountToConvert(1);
		co.execute();
		// queue up a process order
		op.processOrder(co, game);
		
		Hero hero1 = game.getHero("Hero_1");
		hero1.setLocation(game.getTown("Mira"));
		Hero prince = game.getHero("Prince_0");
		prince.setLocation(game.getTown("Mira"));
		MoveOrder mo = new MoveOrder();
		mo.setPlayer(hero1.getOwner(game));
		mo.setHero(hero1);
		mo.setTown(game.getTown("Vonnie"));
		mo.setHero(prince);
		mo.execute();
		// queue up a move order
		op.processOrder(mo, game);


		FinishTurn fo = new FinishTurn();
		fo.setPlayer(p);
		op.processOrder(fo, game);
		System.out.println(game);
		// game should be in magic orders
		assertTrue(game.getGameState() == Game.MAGIC_PHASE);
		// now, execute player1's orders... the other guys
		// are NPCs and we should cycle through them 
		// and end up back at the order entry phse
		op.processOrder(co, game);
		op.processOrder(fo, game);
		// after this, only NPC moves are queued, so we should shift through
		// the physical phase, do combat, produce resources, etc
		assertTrue(game.getGameState() == Game.ORDER_PHASE);
		for (Message m : game.getMessages()){
			System.out.println(m);
		}
	}

}
