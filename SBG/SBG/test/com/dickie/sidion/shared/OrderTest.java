package com.dickie.sidion.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import com.dickie.sidion.shared.order.EditOrder;

public class OrderTest {

	@Test
	public void testOrder() {
		EditOrder eo = new EditOrder();
		Town t = new Town();
		t.setName("testTown");
		Game game = new Game();
		game.addGameComponent(t);
		eo.setTown(t);
		eo.setX(40);
		eo.setY(40);
		eo.validateOrder(game);
		eo.execute();
		eo.setPrecursors(eo.getValue("PRECURSORS"), game);
		eo.executeOnServer(game);
	}
}
