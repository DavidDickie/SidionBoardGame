package com.dickie.sidion.client;

import java.util.HashMap;
import java.util.Map;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.GameComponentListener;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.order.StandOrder;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PlayerPanel extends VerticalPanel implements GameComponentListener, LoadEventListener, java.io.Serializable{

	private Player player = null;
	final ListBox lb = new ListBox();
	
	public PlayerPanel(){
		this.setSize("150px", "150px");
	}
	
	public void displayPlayer(Player p, final Game g){
		
		player = p;
		
		this.clear();
		this.setBorderWidth(2);
		
		String s = p.getName();
		if (g.getCurrentPlayer() == p){
			s += "[*]";
		}
		if (g.getStartingPlayer() == p){
			s += "[+]";
		}
		if (p.isNpc()){
			s += "[NPC]";
		}
		Label l = new Label(s);
		l.setStyleName("H1", true);
		l.getElement().getStyle().setBackgroundColor(p.getColor());
		this.add(l);
		HorizontalPanel hp = new HorizontalPanel();
		VerticalPanel vp1 = new VerticalPanel();
		vp1.setBorderWidth(2);
		Label vp1_l1 = new Label("GOLD");
		Label vp1_l2 = new Label(Integer.toString(p.getResource("GOLD")));
		vp1.add(vp1_l1);
		vp1.add(vp1_l2);
		hp.add(vp1);
		VerticalPanel vp2 = new VerticalPanel();
		vp2.setBorderWidth(2);
		Label vp2_l1 = new Label("MANA");
		Label vp2_l2 = new Label(Integer.toString(p.getResource("MANA")));
		vp2.add(vp2_l1);
		vp2.add(vp2_l2);
		hp.add(vp2);
		VerticalPanel vp3 = new VerticalPanel();
		vp3.setBorderWidth(3);
		Label vp3_l1 = new Label("INF");
		Label vp3_l2 = new Label(Integer.toString(p.getResource("INF")));
		vp3.add(vp3_l1);
		vp3.add(vp3_l2);
		hp.add(vp3);
		VerticalPanel vp4 = new VerticalPanel();
		vp4.setBorderWidth(3);
		Label vp4_l1 = new Label("ARTF");
		Label vp4_l2 = new Label(Integer.toString(p.getResource("ARTIFACTS")));
		vp4.add(vp4_l1);
		vp4.add(vp4_l2);
		hp.add(vp4);
		this.add(hp);
		if (p.isTurnFinshed()){
			Label didOrders = new Label("SUBMITTED");
			this.add(didOrders);
		} else {
			Label didOrders = new Label("WAITING");
			this.add(didOrders);
		}
		for (Order o : g.getOrders()){
			heroOrderMap.put(o.getHero(g), o);
		}
		lb.setVisibleItemCount(6);
		Label lbLabel = new Label("Heroes without orders");
		lb.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				Utils.logMessage("Client:  Player selected hero " + lb.getSelectedItemText());
				heroOrderMap.get(g.getHero(lb.getSelectedItemText())).selectedForOrder();
			}
			
		});
		this.add(lbLabel);
		this.add(lb);
	}
	
	public boolean setPossibleHeros(Game g, Player p){
		lb.clear();
		if (!g.getCurrentPlayer().equals(p) && g.getGameState() != Game.ORDER_PHASE){
			return true;  // not this player's turn, so return but don't return false or it will look like a finish turn
		}
		boolean foundOne = false;
		for (Hero h : g.getHeros()){
			if (h.getOwner(g).equals(p)){
				Utils.logMessage("Client:  Checking hero " + h);
				if (heroOrderMap.get(h) == null){
					Utils.logMessage("Hero " + h.getName() + " has no orders!!!!!");
					h.setOrder(true);
				} else if (g.getGameState() == g.ORDER_PHASE){
					if (!h.hasOrder()){
						lb.addItem(h.getName());
						foundOne = true;
					}
				} else if (heroOrderMap.get(h) != null && heroOrderMap.get(h) instanceof StandOrder){
					Utils.logMessage("Client: " + h.getName() + " has stand order; no need for more user input");
					h.setOrder(true);
				} else if (!h.hasOrder()){
					Utils.logMessage("Client: " +h.getName() + " has no orders");
					if (heroOrderMap.get(h).isExecutable(g)){
						Utils.logMessage("Client: " +h.getName() + " is queued for " + heroOrderMap.get(h));
						lb.addItem(h.getName());
						foundOne = true;
					} else {
						Utils.logMessage("Client: " +h.getName() + " unexecutable order " + heroOrderMap.get(h));
					}
				} 
			}
		}
		if (!foundOne) {
			Utils.logMessage("Client:  The player has no more orders, finish order should be send next");
			return false;
		}
		return true;
	}
	
	private Map<Hero, Order> heroOrderMap = new HashMap<Hero, Order>();
	
	public Order getOrder(Hero h){
		return heroOrderMap.get(h);
	}
	
	public Player getPlayer(){
		return player;
	}
	
	@Override
	public void LoadEvent(String event, Object loaded) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentEvent(String event, GameComponent gc) {
		// TODO Auto-generated method stub
		
	}

}
