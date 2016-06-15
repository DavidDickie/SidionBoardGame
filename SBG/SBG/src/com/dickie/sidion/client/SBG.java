package com.dickie.sidion.client;

import java.util.ArrayList;
import java.util.List;

import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SBG implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final static GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	private Draw draw = new Draw();
	/**
	 * This is the entry point method.
	 */
	
	boolean init = false;
	
	public void onModuleLoad() {
		try{
			if (init){
				createGame("test");
			}
			MapPanel mapPanel = new MapPanel();
			DecoratorPanel dp = new DecoratorPanel();
			dp.add(mapPanel.getCanvas());
			RootPanel rootPanel = RootPanel.get("display");
			rootPanel.add(dp);
			NavPanel vp = new NavPanel();
			vp.initialize(draw,mapPanel);
			RootPanel navPanel = RootPanel.get("navbar");
			navPanel.add(vp);
		} catch (Throwable t){
			logMessage(t.getMessage());
		}

	}
	


	public static void logMessage(String s) {
		greetingService.logMessage(s, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				
			}

		});
	}

	List<GameComponent> list2 = null;

	public void test() {

		ArrayList<GameComponent> list = new ArrayList<GameComponent>();
		for (int i = 0; i < 5; i++) {
			Hero h = new Hero();
			h.setName("hero" + i);
			h.setLevel(1);
			h.setOwner("Blue");
			h.setLocation("tacky");
			list.add(h);
		}

		greetingService.set("test", list, new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				caught.printStackTrace();
			}

			public void onSuccess(Void x) {
			}
		});

		greetingService.get("test", Hero.class.getName(), new AsyncCallback<List<GameComponent>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<GameComponent> result) {
				list2 = result;
				System.out.println(list2);
			}
		});

	}
	
	private void createGame(String name){
		greetingService.greetServer(name, new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
}
