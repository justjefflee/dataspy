package com.dataspy.client;

import java.util.List;
import java.util.Map;

import com.dataspy.shared.model.FileModel;
import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Table;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dataspy-service")
public interface DataSpyService extends RemoteService {

    public Map<String,Table> getTableMap ();
    	
	public List<RowData> getSampleData (String tableName);
	
	public List<RowData> getData(String tableName, String columnName, String columnType, String data);
		
    public List<FileModel> getFolderChildren(FileModel fileModel);
	
}
