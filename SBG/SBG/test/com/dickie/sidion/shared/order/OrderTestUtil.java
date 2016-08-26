package com.dickie.sidion.shared.order;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import com.dickie.sidion.server.GreetingServiceImpl;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;

public class OrderTestUtil {
	
	public static void executeOrder(Order bpo, Hero h, Game game, HashMap<String, GameComponent> params){
		bpo.setHero(h);
		bpo.setOwner(h.getOwner(game));
		game.setCurrentPlayer(h.getOwner(game).getName());
		bpo.isExecutable(game); // set the precursors
		for (String s : params.keySet()){
			bpo.getPrecursors().put(s, params.get(s));
		}
		String s = bpo.validateOrder(game);
		if (s != null){
			System.out.println(s);
		}
		bpo.execute();
		GreetingServiceImpl gsi = new GreetingServiceImpl(false);
		gsi.executeSingleOrder("junit", bpo);
	}

}
