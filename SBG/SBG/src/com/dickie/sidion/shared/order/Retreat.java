package com.dickie.sidion.shared.order;

import java.util.List;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;

public class Retreat  extends OrderImpl {
	
	public Retreat(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (getHero(game) == null){
			return "No hero is set";
		}
		if (getOwner(game).getResource("GOLD") < 1){
			return "Player has no gold";
		}
		List<Town> towns = getHero(game).getLocation(game).getNeighbors(game);
		boolean retreatSite = false;
		for (Town t : towns){
			if (t.getTempOwner(game) == null || t.getTempOwner(game).equals(getOwner(game))){
				retreatSite = true;
				break;
			}
		}
		if (!retreatSite){
			return "No place to retreat to";
		}
		if (game.getGameState() == game.RETREAT && getTown() != null){
			retreatSite = false;
			for (Town t : towns){
				if (t.equals(getTown())){
					retreatSite = true;
					break;
				}
			}
			if (!retreatSite){
				return getTown().getName() + " is not a valid retreat site";
			}
		}

		return null;
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == game.RETREAT){
			return true;
		}
		return false;
	}

	@Override
	public void addDoOrderParams() {
		precursors.put("TOWN", new Town());
	}
	
	@Override
	public void executeOnServer(Game game){
		getHero(game).setLocation(getTown());
		getHero(game).getOwner(game).addResource("GOLD", -1);
		getHero(game).setMustRetreat(false);
		game.addMessage(getHero(game).getName() + " [" + 
				getPlayer(game).getName() + "] retreated to " + getTown().getName());
	}
	
}
