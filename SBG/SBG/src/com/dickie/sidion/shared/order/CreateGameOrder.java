package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.VarString;

public class CreateGameOrder extends OrderImpl{
	
	public CreateGameOrder(){
		precursors.put("LKEY", new VarString());
	}
	
	public String validateOrder(Game game) {
		super.validateOrder(game);
		if (precursors.get("LKEY") == null || precursors.get("LKEY").getValue("LKEY") == null){
			return "no name";
		}
		return null;
	}
	
	@Override
	public void executeOnServer(Game game) {
		Game.createGame(((VarString)precursors.get("LKEY")).getValue());
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
