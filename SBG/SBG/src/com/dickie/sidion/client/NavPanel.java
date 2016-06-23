package com.dickie.sidion.client;

import java.util.HashMap;
import java.util.Map;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.GameComponentListener;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.VarString;
import com.dickie.sidion.shared.order.ConvertOrder;
import com.dickie.sidion.shared.order.CreateGameOrder;
import com.dickie.sidion.shared.order.EditOrder;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NavPanel extends VerticalPanel implements GameComponentListener, java.io.Serializable{

	boolean authenticated = false;
	private Game game = null;;
	private Player player = null;
	private Draw draw = null;
	private MapPanel mapPanel = null;
	
	enum UiState {ORDER_ASSIGMENT, ADMIN, MAGIC_ORDERS, PHYS_ORDERS, RETREATS};
	
	private UiState state = null;

	final Button refresh = new Button("refresh");
	
	public void initialize(final Draw draw, MapPanel mapPanel) {
		this.draw = draw;
		this.mapPanel = mapPanel;
		mapPanel.AddClickListener(this);

		refresh.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				Utils.getGameFromServer(game, NavPanel.this, draw);
			}});
		initialState();
	}

	final TextBox gnTextBox = new TextBox();
	final TextBox userTextBox = new TextBox();
	final TextBox passwordTextBox = new TextBox();
	
	
	private void initialState() {
		this.clear();
		add(new Label("Game name:"));
		add(gnTextBox);
		add(new Label("Username:"));
		add(userTextBox);
		add(new Label("Password:"));
		passwordTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					game = new Game();
					game.setName(gnTextBox.getText());
					Utils.getGameFromServer(game, NavPanel.this, draw);
					if (game == null) {
						Utils.displayMessage("Game " + gnTextBox.getText() + " doesn't exist, try again");
						return;
					}
					draw.setMp(mapPanel);
					draw.setGame(game);
					
					
				}
			}
		});
		add(passwordTextBox);
	}
	
	public void userLogin(){
		if (userTextBox.getText().equals("admin")) {
			if (passwordTextBox.getText().equals("adminpassword")) {
				player = new Player();
				player.setAdmin(true);
				Utils.displayMessage("Administrator logged on");
				
				adminState();
				return;
			}
		}
		player = game.getPlayer(userTextBox.getText());
		if (player == null) {
			String s = "";
			for (Player p2 : game.getPlayers()){
				s += p2.getName() + "/";
			}
			Utils.displayMessage("That is not a valid username; " + s);
			return;
		}
//		if (!p.getPassword().equals(passwordTextBox.getText())){
//			Utils.displayMessage("Wrong password, try again");
//			return;
//		};
		playerLoggedInState();
	}

	private void adminState() {
		this.clear();
		this.add(refresh); 
		this.renderOrder(new CreateGameOrder());
		this.renderOrder(new EditOrder());
		state = UiState.ADMIN;
	}
	
	private void playerLoggedInState() {
		if (game.getGameState() == game.ORDER_PHASE){
			playerOrderState();
		}
		playerOrderState();
	}
	
	
	public void rawClick(int x, int y) {
		for (TextBox tb : orderTextBoxMap.keySet()){
			if (orderTextBoxMap.get(tb).equals("X")){
				tb.setText(Integer.toString(x));
				textBoxToOrder.get(tb).setX(x);
			} 
			if (orderTextBoxMap.get(tb).equals("Y")){
				tb.setText(Integer.toString(y));		
				textBoxToOrder.get(tb).setY(y);
			}
		}
	}
	
	private Map<TextBox, String> orderTextBoxMap = new HashMap<TextBox, String>();
	private Map<TextBox, Order> textBoxToOrder = new HashMap<TextBox, Order>();
	private Map<TextBox, String> textBoxCompType = new HashMap<TextBox, String>();

	private void renderOrder(final Order order){
		Map<String, GameComponent> parameters = order.getPrecursors();
		for (String key : parameters.keySet()){
			this.add(new Label(key));
			final TextBox tb = new TextBox();
			if (!(parameters.get(key) instanceof VarString)){
				tb.setReadOnly(true);
			} else {
				tb.addKeyUpHandler(new KeyUpHandler(){

					@Override
					public void onKeyUp(KeyUpEvent event) {
						order.getPrecursors().get(orderTextBoxMap.get(tb)).setKey(tb.getText());
					}
					
				});
			}
			if (parameters.get(key).getKey() != null){
				tb.setText(parameters.get(key).getKey());
			}
			orderTextBoxMap.put(tb, key);
			textBoxCompType.put(tb, parameters.get(key).getClass().getName());
			textBoxToOrder.put(tb, order);
			this.add(tb);
		}
		//sendOrder();
		Button b = new Button("EXECUTE");
		b.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				NavPanel.this.sendOrder(order);
			}});
		this.add(b);
			
	}
	
	private void playerOrderState(){
		try{
			this.clear();
			this.add(refresh); 
			boolean foundOne = false;
			for (Hero h : game.getHeros()){
				if (!h.getOwner(game).equals(player)){
					continue;
				}
				if (h.hasOrder()){
					continue;
				}
				foundOne = true;
				Label l = new Label(h.getName());
				add(l);
				final Hero hero = h;
				final ListBox cb = new ListBox();
				cb.addItem("STAND");
				cb.addItem("CONVERT");
				cb.addItem("TELEPORT");
				cb.addItem("BLOCKPATH");
				cb.addItem("MOVE");
				cb.addItem("LOCK");
				cb.addItem("BID");
				cb.addItem("IMPROVETOWN");
				cb.addItem("IMPROVEHERO");
				cb.addItem("RETREAT");
				add(cb);
				Button exBut = new Button("EXECUTE");
				exBut.addClickHandler(new ClickHandler(){
	
					@Override
					public void onClick(ClickEvent event) {
						addOrderState(hero, cb.getSelectedItemText());
					}
				
				});
				add(exBut);
				state = UiState.ORDER_ASSIGMENT;
				break;
			}
			if (!foundOne){
				Utils.displayMessage("You have no heros left to give orders to");
			}
			//Utils.displayMessage(sb.toString());
		} catch (Throwable t){
			Utils.displayMessage(t.getMessage());
		}
	}
	
	private void addOrderState(Hero hero, String order){
		this.clear();
		this.add(refresh); 
		if (order.equals("STAND")){
			hero.setOrder();
		}
		if (order.equals("CONVERT")){
			hero.setOrder();
			ConvertOrder co = new ConvertOrder();
			co.setPlayer(player);
			co.setHero(hero);
			co.execute();
			Utils.sendOrderToServer(co, game);
		}
		playerOrderState();
	}
	
	private void sendOrder(Order order){
		try{
			Utils.logMessage("Doing " + order.toString());
			String s = order.validateOrder(game);
			if (s  != null){
				Utils.displayMessage("Validation failed: " + s);
				return;
			}
			order.execute();
			Utils.logMessage("Exectuted on client: " + order);
			Utils.displayMessage(Utils.sendOrderToServer(order, game));
		} catch (Throwable t){
			Utils.displayMessage("error: " + t.getMessage());
		}
		if (state == UiState.ADMIN){
			this.adminState();
		} else if (state == UiState.ORDER_ASSIGMENT){
			this.playerOrderState();
		}
	}

	@Override
	public void componentEvent(String event, GameComponent gc) {
		for (TextBox tb : textBoxCompType.keySet()){
			if (gc.getClass().getName().equals(textBoxCompType.get(tb))){
				Utils.logMessage("GameComp is " + gc);
				Utils.logMessage("Looking up " + orderTextBoxMap.get(tb));
				Utils.logMessage("Key is " + gc.getKey());
				Order currentOrder = textBoxToOrder.get(tb);
				Utils.logMessage("Precursor is " + currentOrder.getPrecursors().get(orderTextBoxMap.get(tb)));
				currentOrder.getPrecursors().put(orderTextBoxMap.get(tb),gc);
				tb.setText(gc.getKey());
			} 
		}
	}

}
