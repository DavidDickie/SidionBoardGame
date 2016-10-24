package com.dickie.sidion.shared.order;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.VarString;

public class CreateGameOrder extends OrderImpl{
	
	public CreateGameOrder(){
		precursors.put("LKEY", new VarString());
	}
	
	public String validateOrder(Game game) {
		super.validateOrder(game);
		if (precursors.get("LKEY") == null || precursors.get("LKEY").getValue("LKEY") == null){
			return "no name";
		}
		return null;
	}
	
	@Override
	public void executeOnServer(Game game) {
		String gameName = ((VarString)precursors.get("LKEY")).getValue();
		if (gameName.startsWith("-")){
			gameName = gameName.replace("-", "");
			String[] names = gameName.split(";");
			Game g = Game.getGame(names[0]);
			g.setName(names[1]);
			Game.addGame(g);
		} else {
			Game.createGame(gameName);
		}
	}
	
	@Override
	public boolean isExecutable(Game game) {
		return true;
	}

	@Override
	public void addDoOrderParams() {
		return;
	}

}
