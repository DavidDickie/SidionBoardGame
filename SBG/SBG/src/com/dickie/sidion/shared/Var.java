package com.dickie.sidion.shared;

import java.util.Arrays;

public class Var extends GameComponentImpl{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Var(){
		validAttributes = Arrays.asList("LKEY");
	}
	
	public Var(int x){
		validAttributes = Arrays.asList("LKEY");
		setKey(Integer.toString(x));
	}
	
	public void setValue(int x){
		setKey(Integer.toString(x));
	}
	
	public int getValue(){
		return Integer.valueOf(getKey());
	}

}
