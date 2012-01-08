package com.dataspy.client;

import java.util.Map;

import com.dataspy.client.mvc.AppController;
import com.dataspy.shared.model.Table;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class DataSpy implements EntryPoint {
	public static final String DATASPY_SERVICE = "dataSpyService";
	public static final String TABLE_MAP = "tableMap";
 
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
		
		System.out.println( "DataSpy: getting table map .." );
		dataSpyService.getTableMap( new AsyncCallback<Map<String,Table>>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(Map<String,Table> tableMap) {
				Registry.register( TABLE_MAP, tableMap );
				System.out.println( "DataSpy: got table map" );
				Dispatcher dispatcher = Dispatcher.get();
				dispatcher.addController(new AppController());
				dispatcher.dispatch( AppEvents.Init );
				GXT.hideLoadingPanel("loading");
			}
		});
		
	}
}

