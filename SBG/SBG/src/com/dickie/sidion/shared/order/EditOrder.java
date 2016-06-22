package com.dickie.sidion.shared.order;

import com.dickie.sidion.client.Utils;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;

public class EditOrder extends OrderImpl implements Order {
	
	public EditOrder (){
		super.addPrecursors(false, true, false, false, true, true, false);
	}
	
	public String validateOrder(Game game) {
		super.validateOrder(game);
		Town t = (Town) precursors.get("TOWN");
		if (t == null)
			return "no town";
		if (precursors.get("X").getKey()== null || precursors.get("Y").getKey() == null) {
			return "No coordinates to put town";
		}
		return null;
	}
	
	@Override
	public void execute() {
		super.execute(); 
	}
	
	@Override
	public boolean isExecutable(Game game, Player player) {
		if (player.isAdmin()) return true;
		return false;
	}

	@Override
	public void addDoOrderParams() {
		return;
	}
}
