package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Var;

public class BidOrder extends OrderImpl {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BidOrder(){
		super.addPrecursors(false, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		
		super.validateOrder(game);
		if (checkForHero(game) != null){
			return checkForHero(game);
		}
		if (getHero(game).getOwner(game) != getPlayer(game)){
			return "Hero owner and order owner are not the same?";
		}

		if (game.getGameState() == Game.RETREAT){
			if (getGoldBid() > this.getPlayer(game).getGold()){
				return "Insufficient gold for bid";
			}
			if (getManaBid() > this.getPlayer(game).getMana()){
				return "Insufficient mana for bid";
			}
			if (getInfBid() > this.getPlayer(game).getInf()){
				return "Insufficient influence for bid";
			}
			if (getGoldBid() + getManaBid() + getInfBid() < 1){
				return "Bid must be at least 1 of something";	
			}
		}
		return null;
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
		precursors.put("MANA", new Var(0));
		precursors.put("GOLD", new Var(0));
		precursors.put("INF", new Var(0));
	}
	
	public void addBid(int gold, int inf, int mana){
		Var v = new Var();
		v.setValue(gold);
		precursors.put("GOLD", v);
		Var v2 = new Var();
		v2.setValue(inf);
		precursors.put("INF", v2);
		Var v3 = new Var();
		v3.setValue(mana);
		precursors.put("MANA", v3);
	}
	
	public int getGoldBid(){
		return  ((Var)getPrecursors().get("GOLD")).getValue();
	}
	
	public int getManaBid(){
		return  ((Var)getPrecursors().get("MANA")).getValue();
	}
	
	public int getInfBid(){
		return  ((Var)getPrecursors().get("INF")).getValue();
	}
	
	@Override
	public void executeOnServer(Game game){
		// special case; we have to evaluate all bids at the end and pick the winner!
		// so just store this order
		game.addGameComponent(this);
	}
	
	public void executeWinner(Game game){
		Player player = getHero(game).getOwner(game);
		player.addResource("GOLD", -getGoldBid());
		player.addResource("MANA", -getManaBid());
		player.addResource("INF", -getInfBid());
		player.addResource("ARTIFACTS", 1);
		game.addMessage(getHero(game).getName() + "[" + getHero(game).getOwner(game).getDisplayName() + "] wins an artifact with a "+
				(getGoldBid() + getManaBid() + getInfBid()) + " bid");
		
	}

}
