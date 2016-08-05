package com.dickie.sidion.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DisplayHtmlDialog extends DialogBox{
	private static DisplayHtmlDialog dialog = new DisplayHtmlDialog();
	Label lblNewLabel = new Label("New label");
	HTML htmlNewHtml = new HTML("<b> this is the unintialized HTML widget that is going to be filled with some sort of text </b>", true);
	Button btnNewButton = new Button("Close");
	public static DisplayHtmlDialog getInstance(){
		return dialog;
	}
	private DisplayHtmlDialog() {
		setWidth("400px");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		setWidget(verticalPanel);
		verticalPanel.setSize("400px", "350px");
		lblNewLabel.setStyleName("ms-color2-main");
		lblNewLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.add(lblNewLabel);
		lblNewLabel.setSize("100%", "20px");
		
		ScrollPanel scrollPanel = new ScrollPanel();
		verticalPanel.add(scrollPanel);
		scrollPanel.setSize("100%", "300px");
		
		
		scrollPanel.setWidget(htmlNewHtml);
		htmlNewHtml.setSize("100%", "100%");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.add(horizontalPanel);
		horizontalPanel.setSize("100%", "20px");
		btnNewButton.setStyleName("button pink");
		
		
		btnNewButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialog.hide();
			}
		});
		horizontalPanel.add(btnNewButton);
	}
	
	public void display(String title, String html){
		lblNewLabel.setText(title);
		htmlNewHtml.setHTML(html);
		btnNewButton.setFocus(true);
		dialog.show();
	}
}
