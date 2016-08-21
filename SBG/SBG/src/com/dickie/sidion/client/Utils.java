package com.dickie.sidion.client;

import java.util.List;
import java.util.Map;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.GameComponentListener;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Var;
import com.dickie.sidion.shared.VarString;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Utils {

	private final static GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	private static DisplayHtmlDialog popupDialog = DisplayHtmlDialog.getInstance();

	public static void displayMessage(String message) {
		popupDialog.display("Notification", message);
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

	public static void getGameFromServer(final Game game, final LoadEventListener listener,
			final GameComponentListener gcList) {
		greetingService.get(game.getName(), null, new AsyncCallback<List<GameComponent>>() {

			@Override
			public void onFailure(Throwable caught) {
				Utils.displayMessage(caught.getMessage());
			}

			@Override
			public void onSuccess(List<GameComponent> result) {
				int count = 0;
				try {
					for (GameComponent gc : result) {
						game.addGameComponent(gc);

						if (gcList != null) {
							gc.addObserver(gcList);
						}
						count++;
					}

					// now unpack order paramaters

					for (Order o : game.getOrders()) {
						o.setPrecursors(game);
					}
					Utils.logMessage("Client: " +count + " objects loaded to game");
					
					// clear the order flag... we will set it as they add orders
					for (Hero h : game.getHeros()){
						h.setOrder(false);
					}

					listener.LoadEvent("GAMEOBJECTS LOADED", game);
				} catch (Throwable t) {
					Utils.displayMessage(t.getMessage());
				}
			}

		});
	}

	public static void setGameAttrs(final Game game, final LoadEventListener listener) {
		greetingService.getGameAttrs(game.getName(), new AsyncCallback<Map<String, GameComponent>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Map<String, GameComponent> result) {
				game.setCurrentPlayer(((VarString) result.get("CURRENTPLAYER")).getValue());
				game.setGameState(((Var) result.get("GAMESTATE")).getValue());
				game.setStartingPlayer(((VarString) result.get("STARTINGPLAYER")).getValue());
				game.setArtifactUp(Boolean.valueOf(((VarString) result.get("ARTIFACT")).getValue()));
				listener.LoadEvent("GAMEATTRS LOADED", game);
			}

		});
	}

	public static String sendOrderToServer(Order order, Game game) {

		greetingService.executeSingleOrder(game.getName(), order, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Utils.logMessage("Client: " +"RPC call failed: " + caught.getMessage());
				displayMessage("RPC call failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				if (result != null) {
					Utils.logMessage("Client: " +"Result of order was " + result);
				} 
				Utils.logMessage("Client: " +"Order processing complete");
			}
		});
		return null;
	}

	public static void createGame(String gameName) {
		greetingService.greetServer(gameName, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Utils.displayMessage(caught.getMessage());

			}

			@Override
			public void onSuccess(String result) {
				Utils.displayMessage(result);

			}

		});
	}

	static int lastMessage = 0;

	public static void getMessages(final String gameName, final int lastMessage, final LoadEventListener listener) {
		return;
		
//		greetingService.getLatestMessagesFromServer(gameName, lastMessage, new AsyncCallback<List<String>>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onSuccess(List<String> result) {
//				for (String s : result) {
//					listener.LoadEvent(s, null);
//				}
//			}
//
//		});
	}
}
