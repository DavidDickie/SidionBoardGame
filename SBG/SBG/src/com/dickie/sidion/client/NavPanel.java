package com.dickie.sidion.client;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NavPanel extends VerticalPanel{
	
	private String userName;
	private String password;
	boolean authenticated = false;
	private Game game = null;; 
	private Player player = null;
	private DisplayHtmlDialog popupDialog = DisplayHtmlDialog.getInstance();
	
	public void initialize(){
		initialState();
	}
	
	private void initialState(){
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
					game = Game.getGame(gnTextBox.getText());
					if (game == null){
						displayMessage("Game " + gnTextBox.getText() + " doesn't exist, try again");
					}
					if (userTextBox.getText().equals("admin")){
						if (passwordTextBox.getText().equals("adminpassword")){
							player = new Player();
							player.setAdmin(true);
							displayMessage("Administrator logged on");
							adminState();
							return;
						}
					}
					Player p = game.getPlayer(userTextBox.getText());
					if (p == null){
						displayMessage("That is not a valid username");
					}
				}
			}
		});
		add(passwordTextBox);
	}
	
	private void adminState(){
		this.clear();
	}
	
	
	public void displayMessage(String message) {
		popupDialog.display("Notification", message);
	}

}
