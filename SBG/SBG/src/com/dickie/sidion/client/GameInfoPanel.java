package com.dickie.sidion.client;

import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.GameComponentListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GameInfoPanel extends VerticalPanel implements GameComponentListener, LoadEventListener, java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TextArea ta = new TextArea();
	String gameName = "not set";
	int lastMessage = 0;
	
	public GameInfoPanel(){
		ta.setCharacterWidth(100);
		ta.setVisibleLines(10);
		this.add(ta);
		Button b = new Button("REFRESH");
		b.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				Utils.getMessages(gameName, lastMessage, GameInfoPanel.this);
			}
			
		});
		this.add(b);
	}
	
	public void getMessages(String gameName){
		this.gameName = gameName;
		Utils.getMessages(gameName, lastMessage, GameInfoPanel.this);
	}
	@Override
	public void componentEvent(String event, GameComponent gc) {
		ta.setText(ta.getText() + "\n" + "Selected " + gc.getKey());
	}
	@Override
	public void LoadEvent(String event, Object loaded) {
		ta.setText(ta.getText() + "\n" + event);
		lastMessage++;
	}

}
