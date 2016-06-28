package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;

public class TeleportOrder extends OrderImpl{
	
	public TeleportOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		if (game.getGameState() == game.ORDER_PHASE){
			super.validateOrder(game);
			if (precursors.get("HERO") == null){
				return "No wizard is set";
			}
			return null;
		}
		
		if (precursors.get("TARGET_HERO") == null){
			return "No one to teleport";
		}
		if (precursors.get("TOWN") == null){
			return "No target town selected";
		}
		Hero wizard = this.getHero(game);
		Town startLoc = ((Hero)precursors.get("TARGET_HERO")).getLocation(game);
		Town endLoc = this.getTown();
		if (Town.getDistance(startLoc, endLoc) > wizard.getLevel()){
			return "Distance between towns is too great";
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
		precursors.put("TARGET_HERO", new Hero());
		precursors.put("TOWN", new Town());
	}
	
	@Override
	public void executeOnServer(Game game){
		game.addGameComponent(this);
		getHero(game).setOrder(true);
		if (game.getGameState() == game.ORDER_PHASE){
			return;
		}
		((Hero)precursors.get("TARGET_HERO")).setLocation(getTown());
	}

}
