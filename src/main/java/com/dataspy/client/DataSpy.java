package com.dataspy.client;

import com.dataspy.client.mvc.AppController;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class DataSpy implements EntryPoint {
	public static final String DATASPY_SERVICE = "dataSpyService";
 
	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		DataSpyServiceAsync dataSpyService = (DataSpyServiceAsync) GWT.create(DataSpyService.class);
		ServiceDefTarget dataSpyEndpoint = (ServiceDefTarget) dataSpyService;
		String dataSpyModuleRelativeURL = DATASPY_SERVICE;
		dataSpyEndpoint.setServiceEntryPoint(dataSpyModuleRelativeURL);
		Registry.register(DATASPY_SERVICE, dataSpyService);
		
		dataSpyService.init( new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(Void v) {
			}
		});
		
		Dispatcher dispatcher = Dispatcher.get();
		dispatcher.addController(new AppController());
		dispatcher.dispatch( AppEvents.Init );

		GXT.hideLoadingPanel("loading");
	}
}

