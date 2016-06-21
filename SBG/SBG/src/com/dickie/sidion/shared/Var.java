package com.dickie.sidion.shared;

import java.util.Arrays;

public class Var extends GameComponentImpl{

	public Var(){
		validAttributes = Arrays.asList("KEY");
	}
	
	public void setValue(int x){
		setKey(Integer.toString(x));
	}
	
	public int getValue(){
		return Integer.valueOf(getKey());
	}

}
