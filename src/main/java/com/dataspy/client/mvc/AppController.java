package com.dataspy.client.mvc;

import java.util.Map;

import com.dataspy.client.AppEvents;
import com.dataspy.client.DataSpy;
import com.dataspy.client.DataSpyServiceAsync;
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
		registerEventTypes(AppEvents.OpenParentFKData );
	}

	public void handleEvent(AppEvent event) {
		EventType type = event.getType();
		if (type == AppEvents.Init) {
			onInit(event);
		} else if (type == AppEvents.Error) {
			onError(event);
		} else if (type == AppEvents.OpenTable) {
			openTable( (String) event.getData() );
		} else if (type == AppEvents.OpenParentFKData) {
			Map<String,String> map = (Map<String,String>) event.getData();
           	String tableName = map.get( "tableName" );
           	String columnName = map.get( "columnName" );
           	String columnType = map.get( "columnType" );
           	String data = map.get( "data" );
			openParentFK( tableName, columnName, columnType, data );
		}
	}
	
	private void openParentFK (String tableName, String columnName, String columnType, String data) {
		DataSpyServiceAsync dataSpyService = (DataSpyServiceAsync) Registry.get( DataSpy.DATASPY_SERVICE );
		dataSpyService.getTable( tableName, columnName, columnType, data, new AsyncCallback<Table>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(Table table) {
				appView.openTable( table );
			}
		});
	}

	private void openTable (String tableName) {
		DataSpyServiceAsync dataSpyService = (DataSpyServiceAsync) Registry.get( DataSpy.DATASPY_SERVICE );
		dataSpyService.getTable( tableName, new AsyncCallback<Table>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(Table table) {
				appView.openTable( table );
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
