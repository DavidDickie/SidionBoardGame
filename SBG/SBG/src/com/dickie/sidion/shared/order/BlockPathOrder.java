package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Path;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;

public class BlockPathOrder extends OrderImpl{

	public BlockPathOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (checkForHero(game) != null){
			return checkForHero(game);
		}
		Hero h = getHero(game);
		

		if (game.getGameState() == game.MAGIC_PHASE){
			if (getPath() == null){
				return "no path set";
			}
//			Town t = h.getLocation(game);
//			Town t1 = getPath().getTown1(game);
//			Town t2 = getPath().getTown2(game);
//			int distance = java.lang.Math.min(Town.getDistance(t, t2, game), Town.getDistance(t, t1, game));
//			if (distance > h.getLevel()+1){
//				return "Distance " + distance + " is too far for hero level " + h.getLevel();
//			}
		}
		
		return null;
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == game.MAGIC_PHASE){
			addDoOrderParams();
			return true;
		}
		return false;
	}

	@Override
	public void addDoOrderParams() {
		precursors.put("PATH", new Path());
	}
	
	@Override
	public void executeOnServer(Game game){
		getPath().setBlocked(true);
		game.addMessage(getHero(game).getName() + " [" + 
		getPlayer(game).getName() + "] Blocked path between" + getPath().getTown1(game).getName() + 
		" and " +  getPath().getTown2(game).getName());
	}

}

