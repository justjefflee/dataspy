package com.dataspy.client.mvc;

import java.util.List;

import com.dataspy.client.AppEvents;
import com.dataspy.client.DataSpy;
import com.dataspy.client.DataSpyServiceAsync;
import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.TableColumn;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StoreUtil {
	
	public static void openParentFK (final TableColumn parentColumn, String data) {
		DataSpyServiceAsync dataSpyService = (DataSpyServiceAsync) Registry.get( DataSpy.DATASPY_SERVICE );
		dataSpyService.getData( parentColumn.getTable().getName(), parentColumn.getName(), parentColumn.getType(), data, new AsyncCallback<List<RowData>>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(List<RowData> data) {
			}
		});
	}

}
