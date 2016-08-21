package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;

public class StandOrder extends OrderImpl{
	
	public StandOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (getHero(game) == null){
			return "No hero is set";
		}
		return null;
	}
	
	@Override
	public boolean isExecutable(Game game) {
		// stand order can be executed during any phase...
		// during the magic phase, it will replace the original order
		// during the physical phase, it just leaves the hero in place
		// during the retreat phase, it just leaves the hero where they are
		return true;
	}

	@Override
	public void executeOnServer(Game game) {
		game.addGameComponent(this);
		getHero(game).setOrder(true);
//		game.addMessage(getHero(game).getName() + " [" + 
//				getPlayer(game).getName() + "] did nothing");
	}

	@Override
	public void addDoOrderParams() {
		// TODO Auto-generated method stub
		
	}



}
