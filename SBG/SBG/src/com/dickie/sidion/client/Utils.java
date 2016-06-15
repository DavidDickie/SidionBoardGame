package com.dickie.sidion.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Utils {
	
	private final static GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	
	public static void logMessage(String s) {
		greetingService.logMessage(s, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Void result) {
				
			}

		});
	}

}
