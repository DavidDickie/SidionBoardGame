package com.dickie.sidion.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShortPath {

	private final List<Town> nodes;
	private final List<Path> Paths;
	private Set<Town> settledNodes;
	private Set<Town> unSettledNodes;
	private Map<Town, Town> predecessors;
	private Map<Town, Integer> distance;

	private Game game = null;

	public ShortPath(Game game) {
		// create a copy of the array so that we can operate on this array
		this.game = game;
		this.nodes = new ArrayList<Town>(game.getTowns());
		this.Paths = new ArrayList<Path>(game.getPaths());
	}
	
	public int getDistBetweenTowns(Town t, Town t2){
		if (t == t2){
			return 0;
		}
		execute(t);
		LinkedList<Town> path = getPath(t2);
		if (path == null){
			throw new RuntimeException("No path");
		}
		return path.size()-1;
	}

	public void execute(Town source) {
		settledNodes = new HashSet<Town>();
		unSettledNodes = new HashSet<Town>();
		distance = new HashMap<Town, Integer>();
		predecessors = new HashMap<Town, Town>();
		distance.put(source, 0);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			Town node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);
		}
	}

	private void findMinimalDistances(Town node) {
		List<Town> adjacentNodes = getNeighbors(node);
		for (Town target : adjacentNodes) {
			if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
				distance.put(target, getShortestDistance(node) + getDistance(node, target));
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}

	}

	private int getDistance(Town node, Town target) {
		for (Path Path : Paths) {
			if (Path.getTown1(game).equals(node) && Path.getTown2(game).equals(target) ||
					Path.getTown2(game).equals(node) && Path.getTown1(game).equals(target)) {
				return 1;
			}
		}
		throw new RuntimeException("Should not happen");
	}

	private List<Town> getNeighbors(Town node) {
		List<Town> neighbors = new ArrayList<Town>();
		for (Path Path : Paths) {
			if (Path.getTown1(game).equals(node) && !isSettled(Path.getTown2(game))) {
				neighbors.add(Path.getTown2(game));
			}
			if (Path.getTown2(game).equals(node) && !isSettled(Path.getTown1(game))) {
				neighbors.add(Path.getTown1(game));
			}
		}
		return neighbors;
	}

	private Town getMinimum(Set<Town> Townes) {
		Town minimum = null;
		for (Town Town : Townes) {
			if (minimum == null) {
				minimum = Town;
			} else {
				if (getShortestDistance(Town) < getShortestDistance(minimum)) {
					minimum = Town;
				}
			}
		}
		return minimum;
	}

	private boolean isSettled(Town Town) {
		return settledNodes.contains(Town);
	}

	private int getShortestDistance(Town destination) {
		Integer d = distance.get(destination);
		if (d == null) {
			return Integer.MAX_VALUE;
		} else {
			return d;
		}
	}

	/*
	 * This method returns the path from the source to the selected target and
	 * NULL if no path exists
	 */
	public LinkedList<Town> getPath(Town target) {
		LinkedList<Town> path = new LinkedList<Town>();
		Town step = target;
		// check if a path exists
		if (predecessors.get(step) == null) {
			return null;
		}
		path.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
		}
		// Put it into the correct order
		Collections.reverse(path);
		return path;
	}

}
