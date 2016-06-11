package com.dickie.sidion.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.dickie.sidion.client.GreetingService;
import com.dickie.sidion.shared.FieldVerifier;
import com.dickie.sidion.shared.GameComponent;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void setAttributes (Entity e, Object t)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Method[] gettersAndSetters = t.getClass().getMethods();
		for (int i = 0; i < gettersAndSetters.length; i++) {
			String methodName = gettersAndSetters[i].getName();
			if (methodName.equals("getClass")) {
				continue;
			}
			if (!(methodName.startsWith("get") || methodName.startsWith("is"))) {
				continue;
			}
			Object o = gettersAndSetters[i].invoke(t, null);
			if (o == null){
				continue;
			}

			if (methodName.startsWith("get")) {
				e.setProperty(methodName.substring(3).toUpperCase(), o);
			} else if (methodName.startsWith("is")) {
				e.setProperty(methodName.substring(2).toUpperCase(), o);
			}
		}
	}
	
	public void setAttributes (GameComponent t, Entity e)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		for (String s : e.getProperties().keySet()){
			t.setValue(s, e.getProperty(s));
		}
		
	}
	
	public List<GameComponent> getData(String gameName, String component) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException{
		ArrayList<GameComponent> list = new ArrayList<GameComponent> ();
		Query query = new Query(component).setAncestor(getKey(gameName));
		List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		for (Entity e : entities){
			GameComponent gc = (GameComponent) Class.forName(component).newInstance();
			setAttributes(gc, e);
			list.add(gc);
		}
		return list;
	}
	
	public void saveData(String gameName, List<GameComponent> components){
		System.out.println("yep");
		if (components.size() == 0){
			return;
		}
		Key key = getKey(gameName);
		String type = components.get(0).getClass().getName();
		for (GameComponent gc : components){
			Entity e = new Entity (type, gc.getKey(), key);
			try {
				setAttributes(e, gc);
				datastore.put(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
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

	public List<GameComponent> get(String name, String type) throws IllegalArgumentException {
		try {
			return this.getData(name, type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public void set(String name, List<GameComponent> comps) {
		this.saveData(name, comps);
	}
}
