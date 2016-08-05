package com.dickie.sidion.client;

import java.util.List;

import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.GameComponentListener;
import com.dickie.sidion.shared.Message;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GameInfoPanel extends VerticalPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TextArea ta = new TextArea();
	String gameName = "not set";
	int lastMessage = 0;
	
	public GameInfoPanel(){
		ta.setCharacterWidth(80);
		ta.setVisibleLines(10);
		this.add(ta);
	}
	
	public void addMessage(String message){
		ta.setText(ta.getText() + "\n" + message);
	}
	
	public void clear(){
		ta.setText("");
	}
	
	public void addMessages(List<Message> messages){
		StringBuffer sb = new StringBuffer();
		for (Message s : messages){
			sb.append(s.getMessage()).append("\n");
		}
		addMessage(sb.toString());
	}
	

}
