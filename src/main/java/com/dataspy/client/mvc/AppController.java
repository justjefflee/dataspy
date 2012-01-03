package com.dataspy.client.mvc;

import java.util.List;
import java.util.Map;

import com.dataspy.client.AppEvents;
import com.dataspy.client.DataSpy;
import com.dataspy.client.DataSpyServiceAsync;
import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Table;
import com.dataspy.shared.model.TableColumn;
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
			Map<String,Object> map = (Map<String,Object>) event.getData();
			TableColumn parentColumn = (TableColumn) map.get( "parent" );
           	String data = (String) map.get( "data" );
			openParentFK( parentColumn, data );
		}
	}
	
	private void openParentFK (TableColumn parentColumn, String data) {
		DataSpyServiceAsync dataSpyService = (DataSpyServiceAsync) Registry.get( DataSpy.DATASPY_SERVICE );
		dataSpyService.getTable( parentColumn.getTable().getName(), parentColumn.getName(), parentColumn.getType(), data, new AsyncCallback<Table>() {
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

	private void openTable (final String tableName) {
		appView.openTable( Util.getTableMap().get( tableName ) );
		DataSpyServiceAsync dataSpyService = (DataSpyServiceAsync) Registry.get( DataSpy.DATASPY_SERVICE );
		dataSpyService.getSampleData( tableName, new AsyncCallback<List<RowData>>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(List<RowData> data) {
				appView.addData( tableName, data );
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
