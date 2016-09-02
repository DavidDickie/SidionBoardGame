package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Town;

public class LockOrder extends OrderImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LockOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (checkForHero(game) != null){
			return checkForHero(game);
		}
		if (game.getGameState() == Game.RETREAT && getHero(game).getOwner(game).getInf() < getInfCost(getHero(game).getLocation(game))){
			return "You do not have "  + getInfCost(getHero(game).getLocation(game)) + " influence";
		}
		return null;
	}
	
	public static int getInfCost(Town t){
		return (t.getLevel()+1) * 5;
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == Game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == Game.RETREAT ){
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
		Hero h = getHero(game);
		Town t = this.getHero(game).getLocation(game);
		t.setOwner(getHero(game).getOwner(game));
		getHero(game).getOwner(game).addResource("INF", -getInfCost(t));
		game.addMessage(h.getName() + "[" + h.getOwner(game).getName() + "] locks " + t.getName());
		game.removeGameComponent(getHero(game)); 
	}

}
