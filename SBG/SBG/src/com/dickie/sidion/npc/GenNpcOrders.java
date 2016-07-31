package com.dickie.sidion.npc;

import java.util.ArrayList;
import java.util.List;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.order.MoveOrder;
import com.dickie.sidion.shared.order.RecruitOrder;
import com.dickie.sidion.shared.order.StandOrder;

public class GenNpcOrders {
	
	public void genNpcOrders(Game game){
		for (Player p : game.getPlayers()){
			if (!p.isNpc()){
				continue;
			}
			List<Order> list = genNpcOrders(p, game);
			for (Order o : list){
				o.execute();  // serialize the order
				game.addGameComponent(o);
			}
			p.setTurnFinished(true);
		}
	}

	public List<Order> genNpcOrders(Player player, Game game){
		ArrayList<Order> list = new ArrayList<Order>();
		for (Hero h : game.getHeros()){
			if (h.getOwner(game) != player){
				continue;
			}
			if (h.isPrince()){
				list.add(doPrinceOrders(h, game));
			} else {
				list.add(heroOrders(h, game));
			}
		}
		return list;
	}

	private Order heroOrders(Hero h, Game g) {
		Town town = h.getLocation(g);
		if (town.getHeros(g).size() > 1){
			for (Hero lHero : town.getHeros(g)){
				if (lHero.isPrince()){
					StandOrder so = new StandOrder();
					so.setHero(h);
					so.setOwner(h.getOwner(g));
					return so;
				}
			}
		}
		List<Town> close = town.getNeighbors(g);
		if (!town.getHeros(g).get(0).equals(h)){
			for (Town t : close){
				if (t.getLevel() > town.getLevel()){
					MoveOrder mo = new MoveOrder();
					mo.setHero(h);
					mo.setOwner(h.getOwner(g));
					mo.setTown(t);
					mo.execute();
					if (mo.validateOrder(g) == null){
						return mo;
					}
				}
			}
		}
		StandOrder so = new StandOrder();
		so.setHero(h);
		so.setOwner(h.getOwner(g));
		return so;
	}

	private Order doPrinceOrders(Hero h, Game g) {
		Town town = h.getLocation(g);
		if (town.hasHero()){
			RecruitOrder ro = new RecruitOrder();
			ro.setHero(h);
			ro.setOwner(h.getOwner(g));
			if (ro.validateOrder(g) == null){
				return ro;
			}
		}
		List<Town> close = town.getNeighbors(g);
		for (Town t : close){
			if (t == null){
				throw new RuntimeException("The town is null???");
			}
			if (t.hasHero()){
				MoveOrder mo = new MoveOrder();
				mo.setHero(h);
				mo.setOwner(h.getOwner(g));
				mo.setTown(t);
				mo.execute();
				if (mo.validateOrder(g) == null){
					return mo;
				}
			}
		}
		if (town.getHeros(g).size() > 1){
			
			for (Town t : close){
				if (t == null){
					throw new RuntimeException("The town is null???");
				}
				MoveOrder mo = new MoveOrder();
				mo.setHero(h);
				mo.setOwner(h.getOwner(g));
				mo.setTown(t);
				mo.execute();
				if (mo.validateOrder(g) == null){
					return mo;
				}
			}
		}
		StandOrder so = new StandOrder();
		so.setHero(h);
		so.setOwner(h.getOwner(g));
		return so;
	}
}
