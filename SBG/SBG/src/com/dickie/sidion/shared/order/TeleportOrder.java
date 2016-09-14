package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Town;

public class TeleportOrder extends OrderImpl{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TeleportOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		super.validateOrder(game);
		if (checkForHero(game) != null){
			return checkForHero(game);
		}
		if (game.getGameState() == Game.MAGIC_PHASE){
			Hero target = (Hero) precursors.get("TARGET_HERO");
			if (target == null){
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
			for (Order order : game.getOrders()){
				if (order.getHero(game) != null && order.getHero(game).equals(target)){
					if (!(order instanceof StandOrder)){
						return "Target hero must not have other orders";
					}
				}
			}
			int dist = distance(game);
			
			if (dist > wizard.getLevel()){
				return "Distance between towns is too great";
			}
			if (dist > wizard.getOwner(game).getMana()){
				return "insufficent mana";
			}
			if (this.getPlayer(game).getMana() < 1){
				return "Insufficient mana";
			}
		}
		return null;
	}
	
	private int distance(Game game){
		Town startLoc = ((Hero)precursors.get("TARGET_HERO")).getLocation(game);
		Town endLoc = this.getTown();
		return Town.getDistance(startLoc, endLoc, game);
	}
	
	@Override
	public boolean isExecutable(Game game) {
		if (game.getGameState() == Game.ORDER_PHASE){
			return true;
		}
		if (game.getGameState() == Game.MAGIC_PHASE){
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
		Hero target = ((Hero)precursors.get("TARGET_HERO"));
		int dist = distance(game);
		target.setLocation(getTown());
		if (getTown().getTempOwner(game) == null){
			getTown().setTempOwner(target.getOwner(game));
		}
		getPlayer(game).addResource("MANA", -dist);
		game.addMessage(getHero(game).getName() + " [" + 
				getPlayer(game).getName() + "] teleported " + target.getName() + " to " + getTown());
	}

}
