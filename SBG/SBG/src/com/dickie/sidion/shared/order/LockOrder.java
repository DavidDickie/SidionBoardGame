package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Town;

public class LockOrder extends OrderImpl {
	
	public LockOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (getHero(game) == null){
			return "No hero is set";
		}
		if (game.getGameState() == Game.RETREAT && getHero(game).getOwner(game).getResource("INF") < getInfCost(getHero(game).getLocation(game))){
			return "You do not have "  + getInfCost(getHero(game).getLocation(game)) + " influence";
		}
		return null;
	}
	
	private int getInfCost(Town t){
		if (t.getInf() > 0)
			return 10;
		return 5;
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == game.RETREAT ){
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
		Town t = this.getHero(game).getLocation(game);
		t.setOwner(getHero(game).getOwner(game));
		getHero(game).getOwner(game).addResource("INF", -getInfCost(t));
		game.removeGameComponent(getHero(game));
		game.addMessage(getHero(game).getName() + "[" + getHero(game).getOwner(game).getName() + "] locks " + t.getName());
		game.removeGameComponent(getHero(game)); 
	}

}
