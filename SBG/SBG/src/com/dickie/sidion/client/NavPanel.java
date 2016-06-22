package com.dickie.sidion.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
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

	final Button refresh = new Button("refresh");
	
	public void initialize(Draw draw, MapPanel mapPanel) {
		this.draw = draw;
		this.mapPanel = mapPanel;
		mapPanel.AddClickListener(this);

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
						String s = "";
						for (Player p2 : game.getPlayers()){
							s += p2.getName() + "/";
						}
						Utils.displayMessage("That is not a valid username; " + s);
						return;
					}
//					if (!p.getPassword().equals(passwordTextBox.getText())){
//						Utils.displayMessage("Wrong password, try again");
//						return;
//					};
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
		this.renderOrder(new CreateGameOrder());
		this.renderOrder(new EditOrder());
	}
	
	private void playerLoggedInState() {
		if (game.getGameState() == game.ORDER_PHASE){
			playerOrderState();
		}
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
		this.clear();
		this.add(refresh); 
		for (Hero h : game.getHeros()){
			if (!h.getOwner(game).equals(player)){
				continue;
			}
			if (h.hasOrder()){
				continue;
			}
			Label l = new Label(h.getName());
			add(l);
			final Hero hero = h;
			final ListBox cb = new ListBox();
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
		}
	}
	
	private void addOrderState(Hero hero, String order){
		this.clear();
		this.add(refresh); 
		if (order.equals("CONVERT")){
			ConvertOrder co = new ConvertOrder();
			co.setPlayer(player);
			co.setHero(hero);
			renderOrder(co);
		}
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
				currentOrder.getPrecursors().get(orderTextBoxMap.get(tb)).setKey(gc.getKey());
				tb.setText(gc.getKey());
			} 
		}
	}

}
