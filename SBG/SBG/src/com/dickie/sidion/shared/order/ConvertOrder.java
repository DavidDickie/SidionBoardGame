package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Var;
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
	
		if (getHero(game) == null){
			return "No wizard is set";
		}
		if (getHero(game).getLevel() == 0){
			return getHero(game).getName() + " is " + getHero(game).getLevel() + "; they must be at least level 1";
		}
		if (game.getGameState() == game.MAGIC_PHASE){
			VarString vs = (VarString) precursors.get("TYPE");
			Var numToConvert = (Var) precursors.get("NUM_TO_CONVERT");
			if (numToConvert == null){
				return "No number to convert set";
			}
			int iNumToConvert = numToConvert.getValue();
			if (vs == null){
				return "No type to convert mana to";
			}
			if (!(vs.getValue().equals("GOLD") || vs.getValue().equals("INF"))){
				return "Invalid type " + precursors.get("TYPE").getKey();
			}
			if ((4-getHero(game).getLevel().intValue())*iNumToConvert - getHero(game).getOwner(game).getResource("MANA") < 0){
				return "insufficient mana to do transfer; need " + (4-getHero(game).getLevel().intValue())*iNumToConvert;
			}
		}
		return null;
	}
	
	public void addType(String type){
		VarString vs = new VarString();
		vs.setValue(type);
		precursors.put(type, vs);
	}
	
	public void addAmountToConvert(int amount){
		Var v = new Var();
		v.setValue(amount);
		precursors.put("NUM_TO_CONVERT", v);
	}
	
	@Override
	public void execute() {
		super.execute(); 
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
		precursors.put("TYPE", new VarString());
		precursors.put("NUM_TO_CONVERT", new Var());
	}
	
	@Override
	public void executeOnServer(Game game){
		if (game.getGameState() == game.ORDER_PHASE){
			return;
		}
		String rType = precursors.get("RESOURCE").getKey();
		int numToConvert = Integer.parseInt(precursors.get("NUM_TO_CONVERT").getKey());
		game.addMessage(getHero(game).getName() + " [" + 
				getPlayer(game).getName() + "] converted " + (getHero(game).getLevel().intValue()-4) + " mana into 1 " + rType);
		getPlayer(game).addResource("MANA", (getHero(game).getLevel().intValue()-4)*numToConvert);
		getPlayer(game).addResource(rType, numToConvert);
	}

}
