package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;

public class OrderTemplate extends OrderImpl {
	
	public OrderTemplate(){
		super.addPrecursors(false, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (checkForHero(game) != null){
			return checkForHero(game);
		}

		if (game.getGameState() == game.MAGIC_PHASE){
			
		}
		return null;
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == game.MAGIC_PHASE ){
			addDoOrderParams();
			return true;
		}
		return false;
	}

	@Override
	public void addDoOrderParams() {
		//precursors.put("TOWN", new Town());
	}
	
	@Override
	public void executeOnServer(Game game){
	}

}
