package com.dickie.sidion.shared;

import java.util.Arrays;

public class VarString extends GameComponentImpl{

	public VarString(){
		validAttributes = Arrays.asList("KEY");
	}
	
	public void setValue(String s){
		setKey(s);
	}
	
	public String getValue(){
		return getKey();
	}

}
