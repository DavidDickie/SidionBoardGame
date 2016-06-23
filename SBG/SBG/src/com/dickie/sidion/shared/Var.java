package com.dickie.sidion.shared;

import java.util.Arrays;

public class Var extends GameComponentImpl{

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
