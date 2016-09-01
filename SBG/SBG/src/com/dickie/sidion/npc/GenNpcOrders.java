package com.dickie.sidion.npc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dickie.sidion.shared.Game;
import com.dickie.sidion.shared.Hero;
import com.dickie.sidion.shared.Order;
import com.dickie.sidion.shared.Player;
import com.dickie.sidion.shared.Town;
import com.dickie.sidion.shared.order.BidOrder;
import com.dickie.sidion.shared.order.ConvertOrder;
import com.dickie.sidion.shared.order.ImproveOrder;
import com.dickie.sidion.shared.order.ImproveTownOrder;
import com.dickie.sidion.shared.order.LockOrder;
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
				System.out.println("NPC ORDER: " + o);
				o.execute();  
				game.addGameComponent(o);
			}
			p.setTurnFinished(true);
		}
	}

	boolean didOneLock = false;
	boolean bidOnArt = false;
	public List<Order> genNpcOrders(Player player, Game game){
		didOneLock=false;
		bidOnArt = false;
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
		for (Order o : list){
			System.out.println("NPC ORDER: " + o);
		}
		return list;
	}

	private Order heroOrders(Hero h, Game g) {
		if (g.isArtifactUp() && !bidOnArt){
			BidOrder bo = new BidOrder();
			bo.setHero(h);
			Player p = h.getOwner(g);
			bo.setOwner(p);
			bo.addBid(
					(int) (p.getResource("GOLD") * java.lang.Math.random()), 
					(int) (p.getResource("INF") * java.lang.Math.random()), 
					(int) (p.getResource("MANA") * java.lang.Math.random()));
		}
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
		if (h.getLocation(g).getLevel() < h.getLevel() && h.getOwner(g).getResource("GOLD") < h.getLocation(g).getUpgradeCost()){
			ImproveTownOrder ito = new ImproveTownOrder();
			ito.setHero(h);
			ito.setPlayer(h.getOwner(g));
			return ito;
		}
		if (h.getOwner(g).getResource("INF") > 10 && !didOneLock){
			LockOrder lo = new LockOrder();
			lo.setHero(h);
			lo.setPlayer(h.getOwner(g));
			didOneLock = true;
			return lo;
		}
		List<Town> close = town.getNeighbors(g);
		if (!town.getHeros(g).get(0).equals(h)){
			for (Town t : close){
				if (t.getLevel() > town.getLevel()){
					MoveOrder mo = new MoveOrder();
					mo.setHero(h);
					mo.setOwner(h.getOwner(g));
					mo.setTown(t);
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
		if (h.getLocation(g).getLevel() < 3 && h.getLocation(g).getUpgradeCost() < h.getOwner(g).getResource("GOLD")){
			ImproveTownOrder ito = new ImproveTownOrder();
			ito.setHero(h);
			ito.setPlayer(h.getOwner(g));
			return ito;
		}
		if (h.getOwner(g).getResource("MANA") > 8){
			ConvertOrder co = new ConvertOrder();
			co.setHero(h);
			co.setNumber(h.getOwner(g).getResource("MANA"));
			co.addType("INF");
		}
		if (h.getLocation(g).getHeros(g).size() > 1){
			Hero h2 = null;
			for (Hero htemp: h.getLocation(g).getHeros(g)){
				if (htemp != h){
					h2 = htemp;
					break;
				}
			}
			if (h2.getLevel() < 2 && h.getOwner(g).getResource("GOLD") > 4){
				ImproveOrder ito = new ImproveOrder();
				ito.setHero(h);
				ito.setPlayer(h.getOwner(g));
				ito.addTargetHero(h2);
				return ito;
			}
		}
		List<Town> close = town.getNeighbors(g);
		Collections.shuffle(close);
		for (Town t : close){
			if (t == null){
				throw new RuntimeException("The town is null???");
			}
			List<Hero> tHeros= t.getHeros(g);
			int totalThere = 0;
			for (Hero tHero : tHeros){
				totalThere+= tHero.getLevel();
			}
			if (t.hasHero() && totalThere < 3){
				MoveOrder mo = new MoveOrder();
				mo.setHero(h);
				mo.setOwner(h.getOwner(g));
				mo.setTown(t);
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
//				mo.execute();
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
