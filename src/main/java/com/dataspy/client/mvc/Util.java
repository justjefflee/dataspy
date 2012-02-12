package com.dataspy.client.mvc;

import java.util.List;

import com.dataspy.client.AppEvents;
import com.dataspy.client.DataSpy;
import com.dataspy.client.DataSpyServiceAsync;
import com.dataspy.shared.model.Database;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Util {
	
	public static DataSpyServiceAsync getDataSpyService () {
		return (DataSpyServiceAsync) Registry.get(DataSpy.DATASPY_SERVICE);
	}
	
	public static void init () {
		System.out.println( "DataSpy: getting database info .." );
		getDataSpyService().getDatabases( new AsyncCallback<List<Database>>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(List<Database> databases) {
				System.out.println( "got database " + databases.size() );
				Registry.register( "databases", databases );
			}
		});
	}
	
    public static List<Database> getDatabases () {
    	return (List<Database>) Registry.get( "databases" );
    }

}
