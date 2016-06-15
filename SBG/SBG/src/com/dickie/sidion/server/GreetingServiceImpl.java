package com.dickie.sidion.server;

import java.util.List;

import com.dickie.sidion.client.GreetingService;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private DAO dao = new DAO();
	
	public String greetServer(String input) throws IllegalArgumentException {
		Game game = new Game(input);
		dao.saveGame(game);
		return input;
		
	}
	
	public Void logMessage(String input){
		System.out.println(input);
		return null;
	}
	
	public List<GameComponent> get(String gameName, String type) throws IllegalArgumentException {
		try {
			return dao.getData(gameName, type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public void set(String name, List<GameComponent> comps) {
		dao.saveData(name, comps);
	}
	
	
}
