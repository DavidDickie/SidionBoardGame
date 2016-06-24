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
		if (getHero(game) == null){
			return "No hero is set";
		}

		if (game.getGameState() == game.MAGIC_PHASE){
			
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
		if (game.getGameState() == game.MAGIC_PHASE && player.equals(game.getCurrentPlayer())){
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
