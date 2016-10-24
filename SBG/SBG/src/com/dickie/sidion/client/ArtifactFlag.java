package com.dickie.sidion.client;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ArtifactFlag extends VerticalPanel {
	static Label isArt = new Label("No Artifact");

	public ArtifactFlag() {
		setSize("20px", "20px");
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		isArt.getElement().getStyle().setBackgroundColor("white");
		add(isArt);
	}

	public static void setIsArtifact(boolean isUp) {
		String s = isUp ? "Artifact Available":"No Artifact";
		if (isUp){
			isArt.getElement().getStyle().setBackgroundColor("pink");
		}
		isArt.setText(s);
	}
}
