package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.VarString;

public class CreateGameOrder extends OrderImpl{
	
	public CreateGameOrder(){
		precursors.put("KEY", new VarString());
	}
	
	public String validateOrder(Game game) {
		super.validateOrder(game);
		if (precursors.get("KEY") == null || precursors.get("KEY").getValue("KEY") == null){
			return "no name";
		}
		if ( Game.getInstance(precursors.get("KEY").getValue("KEY")) != null){
			return "Game already exists";
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
