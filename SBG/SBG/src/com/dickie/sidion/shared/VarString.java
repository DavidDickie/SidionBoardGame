package com.dickie.sidion.shared;

import java.util.Arrays;

public class VarString extends GameComponentImpl{

	public VarString(){
		validAttributes = Arrays.asList("KEY");
	}
	
	public VarString(String s){
		validAttributes = Arrays.asList("KEY");
		setValue(s);
	}
	
	public void setValue(String s){
		setKey(s);
	}
	
	public String getValue(){
		return getKey();
	}

}
