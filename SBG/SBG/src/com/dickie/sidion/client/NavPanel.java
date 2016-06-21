package com.dickie.sidion.client;

import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.GameComponentListener;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Path;
import com.dickie.sidion.shared.order.EditOrder;
import com.dickie.sidion.shared.order.OrderImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NavPanel extends VerticalPanel implements GameComponentListener, java.io.Serializable{

	private String userName;
	private String password;
	boolean authenticated = false;
	private Game game = null;;
	private Player player = null;
	private Draw draw = null;
	private MapPanel mapPanel = null;
	private final static GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	final TextArea scratchpad = new TextArea();
	final Button refresh = new Button("refresh");
	
	public NavPanel(){
		
	}
	
	public void initialize(Draw draw, MapPanel mapPanel) {
		this.draw = draw;
		this.mapPanel = mapPanel;
		mapPanel.AddClickListener(this);
		scratchpad.setSize("100px", "400px");
		refresh.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				getGameFromServer(game);
			}});
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
						Utils.displayMessage("Game " + gnTextBox.getText() + " doesn't exist, try again");
						return;
					}
					draw.setMp(mapPanel);
					draw.setGame(game);
					
					if (userTextBox.getText().equals("admin")) {
						if (passwordTextBox.getText().equals("adminpassword")) {
							player = new Player();
							player.setAdmin(true);
							userName = "admin";
							password = "adminpassword";
							Utils.displayMessage("Administrator logged on");
							adminState();
							return;
						}
					}
					Player p = game.getPlayer(userTextBox.getText());
					if (p == null) {
						Utils.displayMessage("That is not a valid username");
						return;
					}
					if (!p.getPassword().equals(passwordTextBox.getText())){
						Utils.displayMessage("Wrong password, try again");
						return;
					};
					userName = userTextBox.getText();
					password = passwordTextBox.getText();
					playerLoggedInState();
				}
			}
		});
		add(passwordTextBox);
	}

	private void adminState() {
		this.clear();
		this.add(refresh); 
//		currentOrder = new EditOrder();
//	
//		try{
////			Utils.sendOrderToServer(currentOrder, game);
//			Map<String, GameComponent> parameters = currentOrder.getPrecursors();
//			renderOrder(currentOrder, game.getName());
//		}
//		catch (Throwable t){
//			Utils.displayMessage("error rendering order:"  + t.getMessage());
//		}
	}
	
	private void playerLoggedInState() {
		this.clear();
		this.add(scratchpad);
	}
	
	public void writeToScratchPad(String s){
		scratchpad.setText(s);
	}
	
	private void getGameFromServer(final Game game){
		greetingService.get(game.getName(), null, new AsyncCallback<List<GameComponent>>(){

			@Override
			public void onFailure(Throwable caught) {
				Utils.displayMessage(caught.getMessage());
			}

			@Override
			public void onSuccess(List<GameComponent> result) {
				int count = 0;
				try{
					for (GameComponent gc : result){
						game.addGameComponent(gc);
						gc.addObserver(NavPanel.this);
						count++;
					}
					Utils.logMessage(count + " objects loaded to game");
					draw.drawMap();
				} catch (Throwable t){
					Utils.displayMessage(t.getMessage());
				}
			}
			
		});
	}

	
	Order currentOrder = null;

	public void rawClick(int x, int y) {
		if (currentOrder == null || currentOrder.getPrecursors().get("X") == null){
			return;
		}
		currentOrder.setX(x);
		orderTextBoxMap.get("X").setText(Integer.toString(x));
		currentOrder.setY(y);
		orderTextBoxMap.get("Y").setText(Integer.toString(y));
	}
	
	private Map<String, TextBox> orderTextBoxMap = new HashMap<String, TextBox>();

	private void renderOrder(Order order, String gameName){
		Map<String, GameComponent> parameters = order.getPrecursors();
		for (String key : parameters.keySet()){
			this.add(new Label(key));
			TextBox tb = new TextBox();
			tb.setReadOnly(true);
			orderTextBoxMap.put(key, tb);
			this.add(tb);
		}
		//sendOrder();
		Button b = new Button("EXECUTE");
		b.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				NavPanel.this.sendOrder();
			}});
		this.add(b);
			
	}
	
	private void sendOrder(){
		try{
			String s = currentOrder.validateOrder(game);
			if (s  != null){
				Utils.displayMessage("Validation failed: " + s);
				return;
			}
			currentOrder.execute();
			Utils.displayMessage(Utils.sendOrderToServer(currentOrder, game));
		} catch (Throwable t){
			Utils.displayMessage("error: " + t.getMessage());
		}
	}

	@Override
	public void componentEvent(String event, GameComponent gc) {
		if (currentOrder == null){
			return;
		}
		if (gc instanceof Hero && currentOrder.getPrecursors().get("HERO") != null){
			currentOrder.setHero((Hero)gc);
			orderTextBoxMap.get("HERO").setText(gc.getKey());
		} else if (gc instanceof Town ){  //&& currentOrder.getPrecursors().get("TOWN") != null
			currentOrder.setTown((Town)gc);
			orderTextBoxMap.get("TOWN").setText(gc.getKey());
		} else if (gc instanceof Path && currentOrder.getPrecursors().get("PATH") != null){
			currentOrder.setPath((Path)gc);
			orderTextBoxMap.get("PATH").setText(gc.getKey());
		} 
	}

}
