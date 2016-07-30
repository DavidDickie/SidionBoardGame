package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Path;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;

public class MoveOrder  extends OrderImpl {
	
	public MoveOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (getHero(game) == null){
			return "No hero is set";
		}

		if (game.getGameState() == game.PHYS_PHASE){
			if (getTown() == null){
				return "No town?";
			}
			Path p = Path.getPath(getTown(), getHero(game).getLocation(game), game);
			
			if (p == null){
				return "You can only move to a connected town";
			}
			if (p.getBlocked()){
				return "Path is blocked, you cannot move here";
			}
			if (getPlayer(game).getResource("GOLD") < 1){
				return "Insufficient gold for move";
			}
		}
		return null;
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == game.PHYS_PHASE){
			addDoOrderParams();
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
		if (getTown().getTempOwner(game) == null){
			getTown().setTempOwner(getHero(game).getOwner(game));
		}
		getPlayer(game).addResource("GOLD", -1);
		System.out.println("Moved " + getHero(game).getName() + " to " + getTown().getName());
	}
	
	
}
