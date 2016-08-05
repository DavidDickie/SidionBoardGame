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
import com.dickie.sidion.shared.order.BlockPathOrder;
import com.dickie.sidion.shared.order.ConvertOrder;
import com.dickie.sidion.shared.order.CreateGameOrder;
import com.dickie.sidion.shared.order.EditOrder;
import com.dickie.sidion.shared.order.FinishTurn;
import com.dickie.sidion.shared.order.ImproveOrder;
import com.dickie.sidion.shared.order.ImproveTownOrder;
import com.dickie.sidion.shared.order.LockOrder;
import com.dickie.sidion.shared.order.MoveOrder;
import com.dickie.sidion.shared.order.RecruitOrder;
import com.dickie.sidion.shared.order.StandOrder;
import com.dickie.sidion.shared.order.TeleportOrder;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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

	enum UiState {
		ORDER_ASSIGMENT, ADMIN, MAGIC_ORDERS, PHYS_ORDERS, RETREATS
	};
	
	private GameInfoPanel gip = null;

	Button refresh = new Button("refresh");

	public void initialize(Draw draw, final MapPanel mapPanel, GameInfoPanel gip) {
		this.setSize("150px", "100px");
		this.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		this.draw = draw;
		this.mapPanel = mapPanel;
		this.gip = gip;
		mapPanel.AddClickListener(this);
		initNewOrders();
		refresh.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				game.clear();
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
		if (order instanceof StandOrder){ // this is a cheat, since we never have to render StandOrders
											// so it means "pick an order for a hero"
			renderPickOrder(order.getHero(game));
			return;
		}
		Utils.logMessage("Client: " +"Rendering " + order);
		if (order instanceof CreateGameOrder || order instanceof EditOrder){
			// do nothing; these are admin orders
		} else {
			Label heroLable = new Label(order.getHero(game).getKey() + 
					" [" + order.getHero(game).getLocation(game).getName() + "] - " + order.getClass().getSimpleName());
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
				Utils.logMessage("Client: " +"Validation failed: " + s);
				Utils.displayMessage("Order failed: " + s);
				return;
			} else {
				Utils.logMessage("Client: order validated");
			}
			// one special case
			if (order instanceof CreateGameOrder){
				Utils.createGame(((VarString)order.getPrecursors().get("LKEY")).getValue());
				return;
			}
			// make changes to local copy if this is not order assignment phase
			if (game.getGameState() != Game.ORDER_PHASE){
				order.executeOnServer(game);
			}
			
			order.execute();
			Order copy = null;
			if (order instanceof ConvertOrder){
				copy = new ConvertOrder();
			} else if (order instanceof BlockPathOrder){
				copy = new ImproveOrder();
			} else if (order instanceof ImproveOrder){
				copy = new MoveOrder();
			} else if (order instanceof MoveOrder){
				copy = new MoveOrder();
			} else if (order instanceof RecruitOrder){
				copy = new RecruitOrder();
			} else if (order instanceof TeleportOrder){
				copy = new TeleportOrder();
			} else if (order instanceof LockOrder){
				copy = new LockOrder();
			} else {
				copy = order;
			}
			for (String theKey : order.getKeys()){
				copy.setValue(theKey, order.getValue(theKey));
			}
				
			Utils.logMessage("Client:  Exectuting on client: " + copy);
			displayMessage(Utils.sendOrderToServer(copy, game));
			try{
				copy.getHero(game).setOrder(true);
			} catch(Throwable t){
				// some orders do not have a hero
			}
			updateHeroOrderList();
		} catch (Throwable t) {
			displayMessage("error: " + t.getMessage());
		}

	}
	
	private void updateHeroOrderList() {
		this.clear();
		this.add(refresh);
		for (PlayerPanel pp : playerPanels){
			if (pp.getPlayer().equals(player)){
				Utils.logMessage("Client: " +"Display orders for player " + player);
				if (!game.getCurrentPlayer().equals(player) && game.getGameState() != 0){
					displayMessage("It is not your turn");
					return;
				}
				if (!pp.setPossibleHeros(game, player)) { // there are no more orders
					Utils.logMessage("Client: " +"Sending finish order for " + pp.getPlayer().getName());
					game.setCurrentPlayer(game.getNextPlayer().getName());
					Utils.logMessage("Client: " +"Finishing turn for player " + player.getName());
					FinishTurn ft = new FinishTurn();
					ft.setPlayer(player);
					ft.execute();
					displayMessage(Utils.sendOrderToServer(ft, game));
					displayMessage("You have entered all orders");
					Timer timer = new Timer()
			        {
			            @Override
			            public void run()
			            {
			            	Utils.getGameFromServer(game, NavPanel.this, NavPanel.this);
			            }
			        };

			        timer.schedule(2000);
					
				} else {
					Utils.logMessage("Client: " +player.getName() + " orders displayed" );
				}
			}
		}
		
	}

	public void getMessagesFromServer(){
		gip.addMessage("Messages from game " + game.getName());
		gip.addMessage("Game is in " + Game.phaseDef[game.getGameState()]);
		gip.addMessage("Player up is : " + game.getCurrentPlayer().getName());
		gip.addMessage("");
		gip.addMessages(game.getMessages());
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
			this.clear();
			renderOrder((Order)gc);
		}
		for (TextBox tb : textBoxCompType.keySet()) {
			if (gc.getClass().getName().equals(textBoxCompType.get(tb))) {
				Order currentOrder = textBoxToOrder.get(tb);
				Utils.logMessage("Client: " +"Precursor is " + currentOrder.getPrecursors().get(orderTextBoxMap.get(tb)));
				currentOrder.getPrecursors().put(orderTextBoxMap.get(tb), gc);
				tb.setText(gc.getKey());
			}
		}
	}
	
	public void displayMessage(String s){
		gip.addMessage(s);
	}
	
	

	@Override
	public void LoadEvent(String event, Object loaded) {
//		Utils.logMessage("Client: " +"Received event " + event + " for " + loaded);
		if (event.equals("GAMEOBJECTS LOADED")) {
			mapPanel.clear();
			draw.setMp(mapPanel);
			draw.drawMap(game);
			if (player != null){
				player = game.getPlayer(player.getName());
			} else {
				player = game.getPlayer(userTextBox.getText());
			}
			gip.clear();
			getMessagesFromServer();
			
			if (playerPanels.size() == 0){
				for (Player p : game.getPlayers()){
					try{
						PlayerPanel pp = new PlayerPanel();
						playerPanels.add(pp);
						RootPanel pPanelRoot = RootPanel.get(p.getName());
						pPanelRoot.add(pp);
						pp.displayPlayer(p, game);
					} catch (Throwable t){
						Utils.logMessage("Client: " +"Could not display player panel: " + t.getMessage());
					}
				}
			} else {
				for (PlayerPanel pp : NavPanel.this.playerPanels){
					pp.displayPlayer(game.getPlayer(pp.getPlayer().getName()), game);
				}
			}
			updateHeroOrderList();
			
			switch (game.getGameState()){
				case 0:  
					gip.addMessage("Assigning orders to heros");
					break;
				case 1:
					gip.addMessage("Executing magic orders");
					break;
				case 2:
					gip.addMessage("Executing physical orders");
					break;
				case 3:
					gip.addMessage("Doing retreats (if any)");
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
				Utils.logMessage("Client: " +"Administrator logged on");
				adminState();
				return;
			}
		}
		
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
		Utils.logMessage("Client: " +"Player " + player.getName() + " logged in");
		updateHeroOrderList();
	}
	
	private Map<String, Order> newOrders = new HashMap<String, Order>();
	
	private void initNewOrders(){
		newOrders.put("STAND", new StandOrder());
		newOrders.put("Convert", new ConvertOrder());
		newOrders.put("Teleport", new TeleportOrder());
		newOrders.put("Block path", new BlockPathOrder());
		newOrders.put("Move", new MoveOrder());
		newOrders.put("Recruit", new RecruitOrder());
//		newOrders.put("BID");
		newOrders.put("Improve Town", new ImproveTownOrder());
		newOrders.put("Improve Hero", new ImproveOrder());
		newOrders.put("Lock Town", new LockOrder());
//		newOrders.put("RETREAT");
	}

	private void renderPickOrder(final Hero hero) {
		try {
			this.clear();
			this.add(refresh);
			Utils.logMessage("Client: " +"Rendering a select order dialog");
			Label lb = new Label(hero.getName() + "[" + hero.getLocation(game).getName() + "]");
			this.add(lb);
			final ListBox cb = new ListBox();
			for (String order : newOrders.keySet()) {				
				cb.addItem(order);
			}
			this.add(cb);
			Button exBut = new Button("EXECUTE");
			exBut.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Order o = newOrders.get(cb.getSelectedItemText());
					o.setOwner(player);
					o.setHero(hero);
					hero.setOrder(true);
					sendOrder(o);
				}
			});
			add(exBut);
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
//		 state = UiState.ADMIN;
	}

}
