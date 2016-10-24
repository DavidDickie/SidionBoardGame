package com.dickie.sidion.server;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dickie.sidion.client.GreetingService;
import com.dickie.sidion.npc.GenNpcOrders;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Message;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Path;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.Var;
import com.dickie.sidion.shared.VarString;
import com.dickie.sidion.shared.order.StandOrder;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private DAO dao = new DAO();
	
	private static Map<String, ArrayList<String>> gameMessages = new HashMap<String, ArrayList<String>>();
	
	public GreetingServiceImpl(){
		GreetingServiceImpl(true);
	}
	
	public  GreetingServiceImpl(boolean b){
		GreetingServiceImpl(b);
	}

	public void GreetingServiceImpl(boolean init) {
		if (!init){
			return;
		}
		List<String> games = dao.getGames();
		if (games.size() == 0){
			System.out.println("No games loaded????");
		}
		for (String gameName : games){
			ArrayList<String> messages = new ArrayList<String>();
			messages.add("Start");
			gameMessages.put(gameName, messages);
			try {
				System.out.println("Loading " + gameName);
				dao.loadGame(gameName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static synchronized List<String> getMessageList(String game){
		return gameMessages.get(game);
	}
	
	
	public String greetServer(String input) throws IllegalArgumentException {
		Game game =null;
		if (input.startsWith("+")){
			input = input.replace("+", "");
			String[] names = input.split(";");
			game = Game.getGame(names[0]);
			game.setName(names[1]);
			int playerNum = 2;
			for (Player p : game.getPlayers()){
				p.setDisplayName(names[playerNum++]);
				String password = names[playerNum++];
				if (password == null || password.equals("")){
					
				} else {
					p.setPassword(password);
					p.setNpc(false);
				}
			}
			Game.addGame(game);
			try {
				Game g2 = dao.loadGame(names[0]);
				for (Town t : g2.getTowns()){
					if (game.getTown(t.getKey()) == null){
						System.out.println("Could not find town " + t.getName());
					}
				}
				for (Hero t : g2.getHeros()){
					if (game.getHero(t.getKey()) == null){
						System.out.println("Could not find hero " + t.getName());
					}
				}
				for (Path t : g2.getPaths()){
					if (game.getPath(t.getKey()) == null){
						System.out.println("Could not find path " + t.getKey());
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if (input.startsWith("-")){
			dao.deleteGame(input, true);
			return "Game " + input + " deleted";
		} else {
			game = Game.createGame(input);
		}
		// create dummy orders
		for (Hero h : game.getHeros()){
			Order o = new StandOrder();
			o.setOwner(h.getOwner(game));
			o.setHero(h);
			game.addGameComponent(o);
		}
		GenNpcOrders gno = new GenNpcOrders();
		gno.genNpcOrders(game);
		System.out.println(game);
		dao.saveGame(game);
		return input;
	}
	
	public List<Message> getLatestMessagesFromServer(String game, int lastMessage){
		return Game.getGame(game).getMessages();
	}
	
	public Void logMessage(String input){
		System.out.println(input);
		return null;
	}
	
	public List<GameComponent> get(String gameName, String type) throws IllegalArgumentException {
		try {
			return dao.getData(gameName, type);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	public String  executeSingleOrder(String name, Order order){
		try{	
			System.out.println("received new order: " + order);
			OrderProcessor op = new OrderProcessor();
			Game g = Game.getInstance(name);
			String s = op.processOrder(order, g);
			if (s!= null){
				g.addMessage("Order " + order + " failed: " + s);
			}
			if (!name.equals("junit")){
				dao.saveGame(Game.getInstance(name));
			}
			Hero heroName = order.getHero(Game.getInstance(name));
			if (heroName == null){
				heroName = new Hero();
				heroName.setName("[NOBODY]");
			}
			return "order " + order.getClass().getSimpleName() + " executed for " + heroName.getName();
		} catch (Throwable t){
			t.printStackTrace();
			return("ERROR: " + t.getMessage());
		}
	}
	
	public String sendOrders(String name, List<Order> orders) {
		try{
			Game game = Game.getInstance(name);
			OrderProcessor op = new OrderProcessor();
			for (Order o : orders){
				op.processOrder(o, game);
			}
			dao.saveGame(game);
			return "orders processed";
		} catch (Throwable t){
			t.printStackTrace();
			return "ERROR: " + t.getMessage();
		}
	}
	
	public Map<String, GameComponent> getGameAttrs(String name){
		HashMap<String, GameComponent> map = new HashMap<String, GameComponent>();
		Game game = Game.getInstance(name);
		Var var = new Var(game.getGameState());
		map.put("GAMESTATE", var);
		VarString var2 = new VarString(game.getCurrentPlayer().getName());
		map.put("CURRENTPLAYER", var2);
		VarString var3 = new VarString(game.getStartingPlayer().getName());
		map.put("STARTINGPLAYER", var3);
		VarString var4= new VarString(Boolean.toString(game.isArtifactUp()));
		map.put("ARTIFACT", var4);
		return map;
	}
	
	
}
