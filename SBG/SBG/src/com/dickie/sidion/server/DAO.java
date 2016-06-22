package com.dickie.sidion.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
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
			game.setGameState((int)e.getProperty("GameState"));
		} catch (Exception e) {
			game.setCurrentPlayer(game.getPlayers().iterator().next().getName());
			game.setGameState(game.ORDER_PHASE);
			game.setStartingPlayer(game.getCurrentPlayer().getName());
			saveGameStatus(game);
		}
	}
	
	public void saveGame(Game game){
		saveGameStatus(game);
		this.saveData(game.getName(), new ArrayList<GameComponent>(game.getPaths()));
		this.saveData(game.getName(), new ArrayList<GameComponent>(game.getHeros()));
		this.saveData(game.getName(), new ArrayList<GameComponent>(game.getTowns()));
		this.saveData(game.getName(), new ArrayList<GameComponent>(game.getPlayers()));
	}
	
	public Game loadGame(String gameName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException{
		Game game = new Game(gameName);
		
		List<GameComponent> list = getData(gameName, null);
		if (list.size() == 0){
			throw new RuntimeException("No game " + gameName);
		}
		for (GameComponent gc : list){
			game.addGameComponent(gc);
		}
		loadGameStatus(game);
		game.addGame(game);
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
		List<Entity> entities = entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
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
		if (component != null){
			entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		} else {
			
		}
		for (Entity e : entities){
			if (e.getKind().equals("Game")){
				continue;
			}
			GameComponent gc = (GameComponent) Class.forName(e.getKind()).newInstance();
			setAttributes(gc, e);
			list.add(gc);
		}
		return list;
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
		Entity e = new Entity (type, gc.getKey(), key);
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
