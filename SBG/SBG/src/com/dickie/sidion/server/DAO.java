package com.dickie.sidion.server;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.order.StandOrder;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class DAO {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void saveGameStatus(Game game){
		Key key = getKey(game.getName());
		try {
			Entity e = datastore.get(key);
			e.setProperty("CurrentPlayer", game.getCurrentPlayer().getName());
			e.setProperty("StartingPlayer", game.getStartingPlayer().getName());
			e.setProperty("GameState", game.getGameState());
			e.setProperty("Artifact", game.isArtifactUp());
			datastore.put(e);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadGameStatus(Game game){
		Key key = getKey(game.getName());
		try {
			Entity e = datastore.get(key);
			game.setCurrentPlayer((String)e.getProperty("CurrentPlayer"));
			game.setStartingPlayer((String)e.getProperty("StartingPlayer"));
			game.setGameState(((Long)e.getProperty("GameState")).intValue());
			game.setArtifactUp(((Boolean)e.getProperty("Artifact")).booleanValue());
		} catch (Exception e) {
			System.out.println("No attributes are set, forcing to defaults");
			String playerName = "Player1";
			game.setCurrentPlayer(playerName);
			game.setStartingPlayer(playerName);
			game.setArtifactUp(false);
			game.setGameState(0);
		}
	}
	
	public void saveGame(Game game){
		saveGameStatus(game);
		// clear out old info
		deleteGame(game.getName(), false);
		saveData(game.getName(), new ArrayList<GameComponent>(game.getPaths()));
		saveData(game.getName(), new ArrayList<GameComponent>(game.getHeros()));
		saveData(game.getName(), new ArrayList<GameComponent>(game.getTowns()));
		saveData(game.getName(), new ArrayList<GameComponent>(game.getPlayers()));
		saveData(game.getName(), new ArrayList<GameComponent>(game.getOrders()));
		saveData(game.getName(), new ArrayList<GameComponent>(game.getMessages()));
	}
	
	public Game loadGame(String gameName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException{
		Game game = new Game(gameName);
		
		loadGameStatus(game);
		List<GameComponent> list = getData(gameName, null);
		if (list.size() == 0){
			if (gameName.equals("test")){
				// generate a basic game
				game = Game.createGame("test");
				saveGame(game);
			} else {
				throw new RuntimeException("No game " + gameName);
			}
		}
		for (GameComponent gc : list){
			game.addGameComponent(gc);
		}
		for (Hero h: game.getHeros()){
			h.setOrder(false);
			if (game.getOrder(h.getName()) == null){
				StandOrder so = new StandOrder();
				so.setHero(h);
				so.setOwner(h.getOwner(game));
				game.addGameComponent(so);
			}
		}
		Game.addGame(game);
		return game;
	}
	
	public void setAttributes (Entity e, GameComponent t) {
		for (String s : t.getKeys()){
			if (t.getValue(s) != null){
				e.setProperty(s, t.getValue(s));
			}
		}
	}
	
	
	
	public void setAttributes (GameComponent t, Entity e) {
		for (String s : e.getProperties().keySet()){
			t.setValue(s, e.getProperty(s).toString());
		}
	}
	
	public List<String> getGames(){
		ArrayList<String> games = new ArrayList<String>();
		Query query = new Query("Game");
		List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		for (Entity e: entities){
			games.add((String)e.getProperty("Name"));
		}
		return games;
	}
	
	public List<GameComponent> getData(String gameName, String component) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException{
		ArrayList<GameComponent> list = new ArrayList<GameComponent> ();
		Query query;
		if (component != null){
			query = new Query(component).setAncestor(getKey(gameName)); 
		} else {
			query = new Query().setAncestor(getKey(gameName));			
		}
		
		List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		
		for (Entity e : entities){
			if (e.getKind().equals("Game")){
				continue;
			}
			GameComponent gc = (GameComponent) Class.forName(e.getKind()).newInstance();
			setAttributes(gc, e);
			if (gc instanceof Order){
				((Order)gc).clearAttrs();
			}
			list.add(gc);
		}
		return list;
	}
	
	public void deleteOrders(String gameName) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		Query query = new Query().setAncestor(getKey(gameName));			
		List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		for (Entity e: entities){
			GameComponent gc = (GameComponent) Class.forName(e.getKind()).newInstance();
			if (gc instanceof Order){
				datastore.delete(e.getKey());
			}
		}
	}
	
	public void deleteGame(String gameName, boolean includeKey) {
		Query query;
		query = new Query().setAncestor(getKey(gameName));			
		List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		for (Entity e : entities){
			if (e.getKey().equals(getKey(gameName))){
				continue;
			}
			datastore.delete(e.getKey());
		}
		if (includeKey){
			datastore.delete(getKey(gameName));
		}
	}
	
	public void saveData(String gameName, List<GameComponent> components){
		if (components.size() == 0){
			return;
		}
		for (GameComponent gc : components){
			saveGameComponent(gc, gameName);
		}
	}
	
	public void saveGameComponent(GameComponent gc, String gameName){
		Key key = getKey(gameName);
		String type = gc.getClass().getName();
		Entity e = null;
		if (gc.getKey() != null){
			e = new Entity (type, gc.getKey(), key);
		} else {
			e = new Entity (type,  key);
		}
		try {
			setAttributes(e, gc);
			datastore.put(e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	}
	
	private Key getKey(String gameName){
		Query q = new Query("Game");
		PreparedQuery pq = datastore.prepare(q);
		Key key = null;
		for (Entity result : pq.asIterable()){
			if (result.getProperty("Name").equals(gameName)){
				key = result.getKey();
			}
		}
		if (key == null){
			Entity newGame = new Entity("Game", gameName);
			newGame.setProperty("Name", gameName);
			datastore.put(newGame);
			key = newGame.getKey();
		}
		return key;
	}


}
