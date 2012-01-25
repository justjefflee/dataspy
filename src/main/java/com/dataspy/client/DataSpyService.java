package com.dataspy.client;

import java.util.List;

import com.dataspy.shared.model.Database;
import com.dataspy.shared.model.RowData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dataspy-service")
public interface DataSpyService extends RemoteService {

	public Database getDatabase ();
	
	public List<RowData> getSampleData (String tableName);
	
	public List<RowData> getData(String tableName, String columnName, String columnType, String data);
		
	public List<RowData> execute (String sql);
	
}
