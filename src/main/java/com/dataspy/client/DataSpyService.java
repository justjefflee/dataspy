package com.dataspy.client;

import java.util.List;

import com.dataspy.shared.model.DataSpyException;
import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Sql;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dataspy-service")
public interface DataSpyService extends RemoteService {

	public List<com.dataspy.shared.model.Database> getDatabases ();
	
	public List<RowData> getSampleData (String databaseName, String tableName);
	
	public List<RowData> getData(String databaseName, String tableName, String columnName, String columnType, String data);
		
	public List<RowData> execute (String databaseName, String sql, List<String> params, List<String> data) throws DataSpyException;
	
	public void saveDatabaseParams (String key, String params) throws DataSpyException;
	
	public Sql saveDatabaseSql (String dbKey, String key, String sql) throws DataSpyException;
	
	public void removeDatabaseSql (String dbKey, String key) throws DataSpyException;
	
	public String formatSql (String sql);
	
	public void refresh ();
	
}
