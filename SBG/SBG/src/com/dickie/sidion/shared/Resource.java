package com.dickie.sidion.shared;

import java.util.Arrays;

public class Resource extends GameComponentImpl{
	
	public Resource(){
		validAttributes = Arrays.asList("KEY", "TYPE", "VALUE");
	}

	public enum Rtypes {GOLD, INF, MANA};
	public Rtypes getType(){
		return Rtypes.valueOf(getValue("TYPE"));
	}
	public void setType(Rtypes t){
		setValue("TYPE", t.toString());
	}
	
	public int getValue(){
		return Integer.parseInt(getValue("VALUE"));
	}
	
	public void setValue(int value){
		setValue("VALUE", Integer.toString(value));
	}

}
