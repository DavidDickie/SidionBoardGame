package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;

public class FindOrder extends OrderImpl {
	public FindOrder() {
		super.addPrecursors(false, false, false, false, false, false, false);
	}

	public String validateOrder(Game game) {

		super.validateOrder(game);
		if (checkForHero(game) != null) {
			return checkForHero(game);
		}
		Hero prince = getHero(game);
		if (!prince.isPrince()){
			return "Only a prince can find a hero";
		}
		if (prince.getLocation(game).isLocked()){
			return "Heros can only be found in unowned cities";
		}
		if (prince.getLocation(game).hasHero()){
			return "This city already has a hero";
		}
		if (game.getGameState() == Game.RETREAT) {

		}
		return null;
	}

	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == Game.ORDER_PHASE) {
			return true;
		}
		if (game.getGameState() == Game.RETREAT) {
			addDoOrderParams();
			return true;
		}
		return false;
	}

	@Override
	public void addDoOrderParams() {
	}

	@Override
	public void executeOnServer(Game game) {
		getHero(game).getLocation(game).setHasHero(true);
		game.addMessage("Prince " + getHero(game).getName() + "[" + 
				getOwner(game).getDisplayName() + "] finds a new hero in " + 
				getHero(game).getLocation(game).getName());
		
	}
}
