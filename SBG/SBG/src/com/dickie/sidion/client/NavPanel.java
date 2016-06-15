package com.dickie.sidion.client;

import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Var;

import java.util.List;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NavPanel extends VerticalPanel {

	private String userName;
	private String password;
	boolean authenticated = false;
	private Game game = null;;
	private Player player = null;
	private DisplayHtmlDialog popupDialog = DisplayHtmlDialog.getInstance();
	private Draw draw = null;
	private MapPanel mapPanel = null;
	private final static GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public void initialize(Draw draw, MapPanel mapPanel) {
		this.draw = draw;
		this.mapPanel = mapPanel;
		initialState();
	}

	private void initialState() {
		this.clear();
		add(new Label("Game name:"));
		final TextBox gnTextBox = new TextBox();
		add(gnTextBox);
		add(new Label("Username:"));
		final TextBox userTextBox = new TextBox();
		add(userTextBox);
		add(new Label("Password:"));
		final TextBox passwordTextBox = new TextBox();
		passwordTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					game = new Game();
					game.setName(gnTextBox.getText());
					getGameFromServer(game);
					if (game == null) {
						displayMessage("Game " + gnTextBox.getText() + " doesn't exist, try again");
						return;
					}
					draw.setMp(mapPanel);
					draw.setGame(game);
					
					if (userTextBox.getText().equals("admin")) {
						if (passwordTextBox.getText().equals("adminpassword")) {
							player = new Player();
							player.setAdmin(true);
							displayMessage("Administrator logged on");
							adminState();
							return;
						}
					}
					Player p = game.getPlayer(userTextBox.getText());
					if (p == null) {
						displayMessage("That is not a valid username");
					}
				}
			}
		});
		add(passwordTextBox);
	}

	private void adminState() {
		this.clear();
	}
	
	private void getGameFromServer(final Game game){
		greetingService.get(game.getName(), null, new AsyncCallback<List<GameComponent>>(){

			@Override
			public void onFailure(Throwable caught) {
				Utils.logMessage(caught.getMessage());
			}

			@Override
			public void onSuccess(List<GameComponent> result) {
				int count = 0;
				try{
					for (GameComponent gc : result){
						game.addGameComponent(gc);
						count++;
					}
					Utils.logMessage(count + " objects loaded to game");
					draw.drawMap();
				} catch (Throwable t){
					Utils.logMessage(t.getMessage());
				}
			}
			
		});
	}

	public void displayMessage(String message) {
		popupDialog.display("Notification", message);
	}

	private void renderPrecursors() {
		// create a read only text box
		// stick it in a Map from precursor to text box called PrecurMap
		// add an execute button
	}
	
	private Order order = null;

	public void gameCompSelected(com.dickie.sidion.shared.GameComponent gc) {
		if (gc instanceof Player) {
		//	Player gc1 = (Player) .get("_player");
			// TextBox tb = precurMap.get(gc1);
			// tb.setText(gc1.getName()
		}

	}

	public void rawClick() {
		int x = 0;
		int y = 0;
		Var v = new Var(); // (Var) precursors.get("_x");
		if (v == null) {
			return;
		}
		v.setKey(Integer.toString(x));
		//((Var) precursors.get("_y")).setKey(Integer.toString(y));

	}

}
