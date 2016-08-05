package com.dickie.sidion.shared;

import java.util.Arrays;

import com.dickie.sidion.shared.Resource.Rtypes;

public class Message extends GameComponentImpl {
	
	public Message(){
		validAttributes = Arrays.asList("LKEY", "MESSAGE");
	}
	
	public Message(Game g, String message){
		validAttributes = Arrays.asList("LKEY", "MESSAGE");
		setKey("MESS1");
		setMessage(message);
	}
	
	public String getMessage(){
		return getValue("MESSAGE");
	}
	
	public void setMessage(String message){
		setValue("MESSAGE", message);
	}
	
	public static String getNextKey(Game game){
		int i = 0;
		for (Message m : game.getMessages()){
			int mNum = Integer.valueOf(m.getValue("LKEY").substring(4));
			if (mNum > i){
				i = mNum;
			}
		}
		return "MESS" + (i+1);
	}

}
