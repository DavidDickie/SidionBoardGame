package com.dickie.sidion.client;

import java.util.ArrayList;
import java.util.List;

import com.dickie.sidion.shared.FieldVerifier;
import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Town;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SBG implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		MapPanel mapPanel = new MapPanel();
		DecoratorPanel dp = new DecoratorPanel();
		dp.add(mapPanel.getCanvas());
		RootPanel rootPanel = RootPanel.get("display");
		rootPanel.add(dp);
		Game game = new Game("test");
		Draw draw = new Draw();
		draw.setMp(mapPanel);
		for (Town t : game.getTowns()){
			draw.draw(t);
		}
		NavPanel vp = new NavPanel();
		vp.initialize();
		RootPanel navPanel = RootPanel.get("navbar");
		navPanel.add(vp);


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

		
		greetingService.get("test", Hero.class.getName(),
				new AsyncCallback<List<GameComponent>>() {
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
}
