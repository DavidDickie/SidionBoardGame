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
		if (game.getGameState() == game.MAGIC_PHASE){
			if (precursors.get("TARGET_HERO") == null){
				return "No one to teleport";
			}
			Town t = (Town) precursors.get("TOWN");
					
			if (t == null){
				return "No target town selected";
			}
			if (t.isLocked() && t.getOwner(game) != getPlayer(game)){
				return "Cannot teleport someone into a locked town";
			}
			Hero wizard = this.getHero(game);
			
			if (distance(game) > wizard.getLevel()){
				return "Distance between towns is too great";
			}
			if (this.getPlayer(game).getResource("MANA") < 1){
				return "Insufficient mana";
			}
		}
		return null;
	}
	
	private int distance(Game game){
		Town startLoc = ((Hero)precursors.get("TARGET_HERO")).getLocation(game);
		Town endLoc = this.getTown();
		return Town.getDistance(startLoc, endLoc);
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
		getPlayer(game).addResource("MANA", -distance(game));
	}

}
