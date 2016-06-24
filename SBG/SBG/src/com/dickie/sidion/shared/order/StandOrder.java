package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;

public class StandOrder extends OrderImpl{
	
	public StandOrder(){
		super.addPrecursors(false, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (getHero(game) == null){
			return "No hero is set";
		}
		return null;
	}
	
	@Override
	public boolean isExecutable(Game game, Player player) {
		if (player.isAdmin()){
			return false;
		}
		if (game.getGameState() == game.ORDER_PHASE){
			return true;
		}
		return false;
	}

	@Override
	public void executeOnServer(Game game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDoOrderParams() {
		// TODO Auto-generated method stub
		
	}



}
