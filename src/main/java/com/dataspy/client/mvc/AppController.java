package com.dataspy.client.mvc;

import java.util.List;

import com.dataspy.client.AppEvents;
import com.dataspy.client.DataSpy;
import com.dataspy.client.DataSpyServiceAsync;
import com.dataspy.shared.model.Database;
import com.dataspy.shared.model.DatabaseConfig;
import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Table;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AppController extends Controller {

	private AppView appView;

	public AppController() {
		registerEventTypes(AppEvents.Init);
		registerEventTypes(AppEvents.Error);
		registerEventTypes(AppEvents.OpenTable);
		registerEventTypes(AppEvents.NewQuery);
		registerEventTypes(AppEvents.EditDatabase);
		registerEventTypes(AppEvents.AddDatabase);
	}

	public void handleEvent(AppEvent event) {
		EventType type = event.getType();
		
		if (type == AppEvents.Init) {
			onInit(event);
			
		} else if (type == AppEvents.Error) {
			onError(event);
			
		} else if (type == AppEvents.OpenTable) {
			openTable( (Database) ((Object[])event.getData())[0], (Table) ((Object[])event.getData())[1] );
			
		} else if (type == AppEvents.NewQuery) {
			appView.openQuery( (Database) ((Object[])event.getData())[0] );
			
		} else if (type == AppEvents.EditDatabase) {
			DatabaseConfig databaseConfig = (DatabaseConfig) ((Object[])event.getData())[0];
			DatabaseConfigWindow w = new DatabaseConfigWindow();
			w.setDatabaseConfig( databaseConfig );
			w.show();
			
		} else if (type == AppEvents.AddDatabase) {
			DatabaseConfigWindow w = new DatabaseConfigWindow();
			w.setDatabaseConfig( new DatabaseConfig() );
			w.show();
		}
	}
	
	private void openTable (final Database database, final Table table) {
		appView.openTable( database, table );
		DataSpyServiceAsync dataSpyService = (DataSpyServiceAsync) Registry.get( DataSpy.DATASPY_SERVICE );
		dataSpyService.getSampleData( database.getName(), table.getName(), new AsyncCallback<List<RowData>>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(List<RowData> data) {
				appView.addData( database, table, data );
			}
		});
	}
	
	public void initialize() {
		appView = new AppView(this);
	}

	protected void onError(AppEvent ae) {
		System.out.println("error: " + ae.<Object> getData());
	}

	private void onInit(AppEvent event) {
		forwardToView(appView, event);
	}

}
