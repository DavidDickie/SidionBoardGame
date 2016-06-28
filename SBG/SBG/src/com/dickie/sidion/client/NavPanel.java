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
import com.dickie.sidion.shared.Var;
import com.dickie.sidion.shared.VarString;
import com.dickie.sidion.shared.order.ConvertOrder;
import com.dickie.sidion.shared.order.CreateGameOrder;
import com.dickie.sidion.shared.order.EditOrder;
import com.dickie.sidion.shared.order.FinishTurn;
import com.dickie.sidion.shared.order.StandOrder;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NavPanel extends VerticalPanel implements GameComponentListener, LoadEventListener, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean authenticated = false;
	private Game game = null;;
	private Player player = null;
	private Draw draw = null;
	private MapPanel mapPanel = null;
	private List<PlayerPanel> playerPanels = new ArrayList<PlayerPanel>();

	private final static GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	enum UiState {
		ORDER_ASSIGMENT, ADMIN, MAGIC_ORDERS, PHYS_ORDERS, RETREATS
	};

	private UiState state = null;
	
	private GameInfoPanel gip = null;

	Button refresh = new Button("refresh");

	public void initialize(Draw draw, MapPanel mapPanel, GameInfoPanel gip) {
		this.draw = draw;
		this.mapPanel = mapPanel;
		this.gip = gip;
		mapPanel.AddClickListener(this);

		refresh.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Utils.getGameFromServer(game, NavPanel.this, NavPanel.this);
				Utils.setGameAttrs(game, NavPanel.this);
			}
		});
		initialState();
	}


	TextBox gnTextBox = new TextBox();
	TextBox userTextBox = new TextBox();
	TextBox passwordTextBox = new TextBox();


	public void rawClick(int x, int y) {
		for (TextBox tb : orderTextBoxMap.keySet()) {
			if (orderTextBoxMap.get(tb).equals("X")) {
				tb.setText(Integer.toString(x));
				textBoxToOrder.get(tb).setX(x);
			}
			if (orderTextBoxMap.get(tb).equals("Y")) {
				tb.setText(Integer.toString(y));
				textBoxToOrder.get(tb).setY(y);
			}
		}
	}

	private Map<TextBox, String> orderTextBoxMap = new HashMap<TextBox, String>();
	private Map<TextBox, Order> textBoxToOrder = new HashMap<TextBox, Order>();
	private Map<TextBox, String> textBoxCompType = new HashMap<TextBox, String>();

	public void renderOrder(final Order order) {
		Utils.logMessage("Rendering " + order);
		if (order instanceof CreateGameOrder || order instanceof EditOrder){
			// do nothing; these are admin orders
		} else {
			Label heroLable = new Label(order.getHero(game).getKey());
			this.add(heroLable);
		}
		Map<String, GameComponent> parameters = order.getPrecursors();
		for (String key : parameters.keySet()) {
			this.add(new Label(key));
			final TextBox tb = new TextBox();
			if (!(parameters.get(key) instanceof VarString || parameters.get(key) instanceof Var)) {
				tb.setReadOnly(true);
			} else {
				tb.addKeyUpHandler(new KeyUpHandler() {

					@Override
					public void onKeyUp(KeyUpEvent event) {
						order.getPrecursors().get(orderTextBoxMap.get(tb)).setKey(tb.getText());
					}

				});
			}
			if (parameters.get(key).getKey() != null) {
				tb.setText(parameters.get(key).getKey());
			}
			orderTextBoxMap.put(tb, key);
			textBoxCompType.put(tb, parameters.get(key).getClass().getName());
			textBoxToOrder.put(tb, order);
			this.add(tb);
		}
		// sendOrder();
		Button b = new Button("EXECUTE");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				NavPanel.this.sendOrder(order);
				
			}
		});
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(b);
		Button b2 = new Button("STAND");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				StandOrder so = new StandOrder();
				so.setPlayer(player);
				so.setHero(order.getHero(game));
				NavPanel.this.sendOrder(so);
			}
		});
		hp.add(b2);
		this.add(hp);

	}

	private void sendOrder(Order order) {
		try {
			Utils.logMessage("Client; doing " + order.toString());
			String s = order.validateOrder(game);
			if (s != null) {
				Utils.logMessage("Validation failed: " + s);
				displayMessage("Order failed: " + s);
				return;
			}
			// one special case
			if (order instanceof CreateGameOrder){
				Utils.createGame(((VarString)order.getPrecursors().get("LKEY")).getValue());
				return;
			}
			try{
				order.getHero(game).setOrder(true);
			} catch(Throwable t){
				// some orders do not have a hero
			}
			order.execute();
			Utils.logMessage("Exectuted on client: " + order);
			displayMessage(Utils.sendOrderToServer(order, game));
			updateHeroOrderList();
		} catch (Throwable t) {
			displayMessage("error: " + t.getMessage());
		}

	}
	
	private void updateHeroOrderList() {
		for (PlayerPanel pp : playerPanels){
			if (pp.getPlayer().equals(player)){
				Utils.logMessage("Display orders for player " + player);
				if (!pp.setPossibleHeros(game, player)) { // there are no more orders
					Utils.logMessage("Sending finish order");
					game.setCurrentPlayer(game.getNextPlayer().getName());
					Utils.logMessage("Finishing turn for player " + player.getName());
					FinishTurn ft = new FinishTurn();
					ft.setPlayer(player);
					ft.execute();
					displayMessage(Utils.sendOrderToServer(ft, game));
					displayMessage("You have entered all orders");
					getMessagesFromServer();
				} else {
					Utils.logMessage(player.getName() + " orders displayed" );
				}
			}
		}
		
	}

	public void getMessagesFromServer(){
		gip.getMessages(game.getName());
	}
	
	
	
	/******************************************
	 * 
	 * event listenters
	 * 
	 * 
	 *****************************************/

	@Override
	public void componentEvent(String event, GameComponent gc) {
		
		if (event.equals("SELECTED_FOR_ORDER")){
			renderOrder((Order)gc);
		}
		gc.addObserver(gip);
		for (TextBox tb : textBoxCompType.keySet()) {
			if (gc.getClass().getName().equals(textBoxCompType.get(tb))) {
				Order currentOrder = textBoxToOrder.get(tb);
				Utils.logMessage("Precursor is " + currentOrder.getPrecursors().get(orderTextBoxMap.get(tb)));
				currentOrder.getPrecursors().put(orderTextBoxMap.get(tb), gc);
				tb.setText(gc.getKey());
			}
		}
	}
	
	public void displayMessage(String s){
		NavPanel.this.gip.LoadEvent(s,null);
	}
	
	

	@Override
	public void LoadEvent(String event, Object loaded) {
		Utils.logMessage("Received event " + event + " for " + loaded);
		if (event.equals("GAMEOBJECTS LOADED")) {
			draw.setMp(mapPanel);
			draw.drawMap(game);
			gip.getMessages(game.getName());
			if (playerPanels.size() == 0){
				for (Player p : game.getPlayers()){
					try{
						PlayerPanel pp = new PlayerPanel();
						playerPanels.add(pp);
						RootPanel pPanelRoot = RootPanel.get(p.getName());
						pPanelRoot.add(pp);
						pp.displayPlayer(p, game);
					} catch (Throwable t){
						Utils.logMessage("Could not display player panel: " + t.getMessage());
					}
				}
			} 

			updateHeroOrderList();

			
			this.gip.LoadEvent("Game loaded; " + game.getCurrentPlayerAsString() + " is the current player", null);
			switch (game.getGameState()){
				case 0:  
					this.gip.LoadEvent("Assigning orders to heros", null);
					break;
				case 1:
					this.gip.LoadEvent("Executing magic orders", null);
					break;
				case 2:
					this.gip.LoadEvent("Executing physical orders", null);
					break;
				case 3:
					this.gip.LoadEvent("Doing retreats (if any)", null);
					break;
			}
			userLoginState();
		}
	}

	/******************************************
	 * 
	 * STATES
	 * 
	 * 
	 ******************************************/

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
					if (gnTextBox.getText()!= null && !gnTextBox.getText().equals("")){
						game.setName(gnTextBox.getText());
						Utils.getGameFromServer(game, NavPanel.this, NavPanel.this);
						Utils.setGameAttrs(game, NavPanel.this);
					} else {
						userLoginState();
					}
				}
			}
		});
		add(passwordTextBox);
	}
	
	public void userLoginState() {
		if (userTextBox.getText().equals("admin")) {
			if (passwordTextBox.getText().equals("adminpassword")) {
				player = new Player();
				player.setAdmin(true);
				Utils.logMessage("Administrator logged on");
				adminState();
				return;
			}
		}
		player = game.getPlayer(userTextBox.getText());
		if (player == null) {
			String s = "";
			for (Player p2 : game.getPlayers()) {
				s += p2.getName() + "/";
			}
			Utils.displayMessage("That is not a valid username; " + s);
			return;
		}
		// if (!p.getPassword().equals(passwordTextBox.getText())){
		// Utils.displayMessage("Wrong password, try again");
		// return;
		// };
		Utils.logMessage("Player " + player.getName() + " logged in");
		playerLoggedInState();
	}

	private void addOrderState(Hero hero, String order) {
		this.clear();
		this.add(refresh);
		if (order.equals("STAND")) {
			StandOrder so = new StandOrder();
			so.setPlayer(player);
			so.setHero(hero);
			so.execute();
			Utils.sendOrderToServer(so, game);
		}
		if (order.equals("CONVERT")) {
			
			ConvertOrder co = new ConvertOrder();
			co.setPlayer(player);
			co.setHero(hero);
			co.execute();
			Utils.logMessage("Sending to server: " + co);
			Utils.sendOrderToServer(co, game);
		}
		hero.setOrder(true);
		playerOrderState();
	}

	private void playerOrderState() {
		try {
			this.clear();
			this.add(refresh);
			boolean foundOne = false;
			for (Hero h : game.getHeros()) {
				if (!h.getOwner(game).equals(player)) {
					continue;
				}
				if (h.hasOrder()) {
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
				exBut.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						addOrderState(hero, cb.getSelectedItemText());
					}

				});
				add(exBut);
				state = UiState.ORDER_ASSIGMENT;
				break;
			}
			if (!foundOne) {
				Utils.logMessage("Finishing turn for player " + player.getName());
				FinishTurn ft = new FinishTurn();
				ft.setPlayer(player);
				sendOrder(ft);
			}
			// Utils.displayMessage(sb.toString());
		} catch (Throwable t) {
			displayMessage(t.getMessage());
		}
	}

	private void adminState() {
		this.clear();
		this.add(refresh);
		try{
			this.renderOrder(new CreateGameOrder());
			this.renderOrder(new EditOrder());
		} catch (Exception e){
			Utils.displayMessage("Could not enter adminstate: " + e.getMessage());
		}
		 state = UiState.ADMIN;
	}
	
	

	
	private void playerLoggedInState() {
		this.clear();
		if (game.getGameState() == game.ORDER_PHASE) {
			Utils.logMessage("Player is defining orders");
			playerOrderState();
		} else if (game.getGameState() == game.MAGIC_PHASE){
			Utils.logMessage("Player is executing Magic orders");
			state = UiState.MAGIC_ORDERS;
		}
		updateHeroOrderList();
	}

}
