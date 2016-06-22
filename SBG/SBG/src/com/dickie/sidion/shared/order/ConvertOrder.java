package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.VarString;

public class ConvertOrder extends OrderImpl{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConvertOrder(){
		super.addPrecursors(true, false, false, false, false, false, false);
	}
	
	public String validateOrder(Game game) {
		super.validateOrder(game);
		if (getHero() == null){
			return "No wizard is set";
		}
		if (getHero().getLevel() == 0){
			return getHero().getName() + " must be at least level 1";
		}
		if (game.getGameState() == game.MAGIC_PHASE){
			if (precursors.get("TYPE") == null){
				return "No type to convert mana to";
			}
			if (getPlayer(game).getResource(precursors.get("TYPE").toString()) - 3 + getHero().getLevel() < 0){
				return "insufficient mana to do transfer; need " + (4 - getHero().getLevel());
			}
		}
		return null;
	}
	
	@Override
	public void execute() {
		super.execute(); 
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
		precursors.put("TYPE", new VarString());
	}
	
	@Override
	public void executeOnServer(Game game){
		String rType = precursors.get("TYPE").getKey();
		getPlayer(game).addResource("MANA", getHero().getLevel().intValue()-4);
		getPlayer(game).addResource(rType, 4-getHero().getLevel().intValue());
	}

}