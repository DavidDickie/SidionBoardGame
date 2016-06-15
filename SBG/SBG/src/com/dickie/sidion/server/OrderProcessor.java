package com.dickie.sidion.server;

import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.Var;

public class OrderProcessor {
	
	DAO dao = new DAO();

	public void processOrder(Order order) {

		if (order.getOrderType().equals("Edit")) {
			Town t = (Town) order.precursorFetch().get("_town");
			Var xVal = (Var) order.precursorFetch().get("_x");
			int x = Integer.valueOf(xVal.getKey());
			Var yVal = (Var) order.precursorFetch().get("_x");
			int y = Integer.valueOf(xVal.getKey());
			t.setX(x);
			t.setY(y);
			dao.saveGameComponent(t, order.getGameName());
		}
	}
}
