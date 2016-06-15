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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class DAO {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void saveGame(Game game){
		this.saveData(game.getName(), new ArrayList<GameComponent>(game.getPaths()));
		this.saveData(game.getName(), new ArrayList<GameComponent>(game.getHeros()));
		this.saveData(game.getName(), new ArrayList<GameComponent>(game.getTowns()));
		this.saveData(game.getName(), new ArrayList<GameComponent>(game.getPlayers()));
	}
	
	public Game loadGame(String gameName){
		Game game = new Game();
		game.setName(gameName);
		try {
			List<GameComponent> list = getData(gameName, null);
			for (GameComponent gc : list){
				game.addGameComponent(gc);
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
