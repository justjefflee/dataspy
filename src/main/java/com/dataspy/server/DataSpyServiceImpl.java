package com.dataspy.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.hibernate.pretty.Formatter;

import net.sourceforge.schemaspy.Config;
import net.sourceforge.schemaspy.model.Database;
import net.sourceforge.schemaspy.model.ForeignKeyConstraint;
import net.sourceforge.schemaspy.model.InvalidConfigurationException;
import net.sourceforge.schemaspy.model.ProcessExecutionException;
import net.sourceforge.schemaspy.model.Table;
import net.sourceforge.schemaspy.model.TableColumn;
import net.sourceforge.schemaspy.model.TableIndex;

import com.dataspy.client.DataSpyService;
import com.dataspy.shared.model.DataSpyException;
import com.dataspy.shared.model.DatabaseConfig;
import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Sql;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DataSpyServiceImpl extends RemoteServiceServlet implements DataSpyService {
    private static List<DbInfo> dbInfos = new ArrayList<DbInfo>();
    private static boolean dirty = false;
    
    static class DbInfo {
    	com.dataspy.shared.model.Database db;
    	Database database;
    	
    	public DbInfo (Database database, com.dataspy.shared.model.Database db) {
    		this.database = database;
    		this.db = db;
    	}
    }
    
    static void saveProperties (Properties props) throws Exception {
		String configFilePath = System.getenv( "DATASPY_CONFIG" );
		props.store( new FileOutputStream( configFilePath ), "" );
		dirty = true;
    }
    
    static Properties getProperties () {
		String configFilePath = System.getenv( "DATASPY_CONFIG" );
		System.out.println( "DATASPY_COONFIG: " + configFilePath );
		Properties props = new Properties();
		try {
			props.load( new FileInputStream( configFilePath ) );
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
    }
		
    static String getNextKey () {
		Properties props = getProperties();
		for (int i = 1; ; i++) {
			String key = "dataspy.db." + i;
			if (props.getProperty( key ) == null)
				return "dataspy.db." + i;
		}
    }
    
    static String getNextSqlKey (String dbKey) {
		Properties props = getProperties();
		for (int i = 1; ; i++) {
			String key = dbKey + ".sql." + i;
			if (props.getProperty( key ) == null)
				return dbKey + ".sql." + i;
		}
    }
    
	static void initDatabase () {
		dbInfos.clear();
		Properties props = getProperties();
		for (int i = 1; ; i++) {
			String key = "dataspy.db." + i;
			String cmdLine = props.getProperty( key );
			if (cmdLine == null)
				break;
			
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setKey( key );
            dbConfig.setParams( cmdLine );
            
            for (int j = 1; j < 100; j++) {
            	String sqlKey = key + ".sql." + j;
            	String sqlLine = props.getProperty( sqlKey );
				if (sqlLine == null)
					continue;
				dbConfig.addSql( new Sql( sqlKey, sqlLine ) );
            }
			dbInfos.add( initDatabase( dbConfig ) );
		}
		dirty = false;
	}
	
	static DbInfo initDatabase (DatabaseConfig dbConfig) {
		Map<String,com.dataspy.shared.model.Table> tableMap = new TreeMap<String,com.dataspy.shared.model.Table>();
		Map<String,com.dataspy.shared.model.TableColumn> tableColumnMap = new HashMap<String,com.dataspy.shared.model.TableColumn>();
		MySchemaAnalyzer analyzer = new MySchemaAnalyzer();
        try {
        	String[] argv = dbConfig.getParams().split( " " );
            Config config = new Config(argv);
            Database database = analyzer.analyze( config );
            for (Table table : database.getTables()) {
            	com.dataspy.shared.model.Table t = new com.dataspy.shared.model.Table();
            	t.setName( table.getName() );
            	tableMap.put( t.getName(), t );
            	
            	for (TableColumn column : table.getColumns()) {
            		com.dataspy.shared.model.TableColumn tc = new com.dataspy.shared.model.TableColumn();
            		tableColumnMap.put( table.getName()+"."+column.getName(), tc );
            		tc.setName( column.getName() );
            		tc.setType( column.getType() );
            		tc.setLength( column.getLength()+"" );
            		tc.setTable( t );
            		t.addTableColumn( tc );
            	}
       	
            	System.out.println( table.getName() );
            	System.out.println( "  num children: " + table.getNumChildren() );
            	System.out.println( "  num parents: " + table.getNumParents() );
            	for (ForeignKeyConstraint fk : table.getForeignKeys()) {
            		System.out.println( "    " + fk );
            	}
            	List<TableColumn> columns = table.getColumns();
            	for (TableColumn column : columns) {
            		System.out.println( "    " + column.getName() + ", " + column.getType() + ", " + column.getLength() );
            		if (column.getParents().size() > 0) {
            			System.out.println( "      num of parents " + column.getParents() );
            		}
            		if (column.getChildren().size() > 0) {
            			System.out.println( "      num of children " + column.getChildren() );
            		}
            	}
            }
            for (Table table : database.getTables()) {
            	com.dataspy.shared.model.Table t = tableMap.get( table.getName() );
            	int i = 0;
            	for (TableColumn column : table.getColumns()) {
            		List<com.dataspy.shared.model.TableColumn> parents = new ArrayList<com.dataspy.shared.model.TableColumn>();
            		t.getColumns().get(i).setParents( parents );
            		for (TableColumn parentTableColumn : column.getParents()) {
            			parents.add( tableColumnMap.get( parentTableColumn.getTable().getName()+"."+parentTableColumn.getName() ));
            			System.out.println( "add " + parentTableColumn.getTable().getName()+"."+parentTableColumn.getName() + " to " + t.getName() );
            		}
            		i++;
            	}
            	i = 0;
            	for (TableColumn column : table.getColumns()) {
            		List<com.dataspy.shared.model.TableColumn> children = new ArrayList<com.dataspy.shared.model.TableColumn>();
            		t.getColumns().get(i).setChildren( children );
            		for (TableColumn parentTableColumn : column.getChildren()) {
            			children.add( tableColumnMap.get( parentTableColumn.getTable().getName()+"."+parentTableColumn.getName() ));
            		}
            		i++;
            	}
            }
            for (Table table : database.getTables()) {
            	com.dataspy.shared.model.Table t = tableMap.get( table.getName() );
            	List<com.dataspy.shared.model.TableIndex> indexes =
            		new ArrayList<com.dataspy.shared.model.TableIndex>();
            	t.setIndexes( indexes );
            	for (TableIndex tableIndex : table.getIndexes()) {
            		com.dataspy.shared.model.TableIndex ti = new com.dataspy.shared.model.TableIndex();
            		indexes.add( ti );
            		ti.setName( tableIndex.getName() );
            		ti.setPrimary( tableIndex.isPrimaryKey() );
            		ti.setUnique( tableIndex.isUnique() );
            		List<com.dataspy.shared.model.TableColumn> columns = new ArrayList<com.dataspy.shared.model.TableColumn>();
            		ti.setColumns(columns);
            		for (TableColumn c : tableIndex.getColumns()) {
            			columns.add( t.getColumn( c.getName() ) );
            		}
            	}
            }
            
            com.dataspy.shared.model.Database db = new com.dataspy.shared.model.Database();
            db.setDatabaseConfig( dbConfig );
            db.setName( database.getName() );
            db.setTableMap( tableMap );
            return new DbInfo( database, db );
            
        } catch (InvalidConfigurationException badConfig) {
            System.err.println();
            if (badConfig.getParamName() != null)
                System.err.println("Bad parameter specified for " + badConfig.getParamName());
            System.err.println(badConfig.getMessage());
            if (badConfig.getCause() != null && !badConfig.getMessage().endsWith(badConfig.getMessage()))
                System.err.println(" caused by " + badConfig.getCause().getMessage());
        } catch (ProcessExecutionException badLaunch) {
            System.err.println(badLaunch.getMessage());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return null;
	}
	
	public void refresh () {
		initDatabase();
	}
	
	public void removeDatabaseSql (String dbKey, String key) throws DataSpyException {
		try {
			Properties props = getProperties();
			props.remove( key );
			saveProperties( props );
		} catch (Exception e) {
			throw new DataSpyException( e );
		}
	}
	
	public String formatSql (String sql) {
		return new Formatter(sql).format();
	}
	
	public Sql saveDatabaseSql (String dbKey, String key, String sql) throws DataSpyException {
		try {
			Properties props = getProperties();
			if (key == null)
				key = getNextSqlKey( dbKey );
			props.setProperty( key, sql );
			saveProperties( props );
			return new Sql( key, sql );
		} catch (Exception e) {
			throw new DataSpyException( e );
		}
	}
	
	public void saveDatabaseParams (String key, String params) throws DataSpyException {
		try {
			Properties props = getProperties();
			if (key == null)
				key = getNextKey();
			props.setProperty( key, params );
			saveProperties( props );
		} catch (Exception e) {
			throw new DataSpyException( e );
		}
	}
	
	public List<com.dataspy.shared.model.Database> getDatabases () {
		if (dirty) {
			initDatabase();
			dirty = false;
		}
		List<com.dataspy.shared.model.Database> result =
			new ArrayList<com.dataspy.shared.model.Database>();
		for (DbInfo dbInfo : dbInfos) {
			result.add( dbInfo.db );
		}
		return result;
	}
	
	public List<RowData> getData(String databaseName, String tableName, String columnName, String columnType, String data) {
		com.dataspy.shared.model.Table table = getDbInfo( databaseName ).db.getTableMap().get(tableName);
		List<RowData> result = new ArrayList<RowData>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from " + table.getName() + " where " + columnName + " = ?";
			System.out.println( "getData " + sql );
			ps = getDbInfo( databaseName ).database.getConnection().prepareStatement( sql );
			ps.setObject( 1, data );
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 0; i < 10 && rs.next(); i++) {
				RowData rowData = new RowData();
				for (int j = 1; j <= rsmd.getColumnCount(); j++) {
					if (rsmd.getColumnType(j) == Types.BLOB ||
						rsmd.getColumnType(j) == Types.LONGVARBINARY)
						continue;
					rowData.set(rsmd.getColumnName(j), rs.getString(j));
				}
				result.add(rowData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { rs.close(); } catch (Exception ex) {}
			try { ps.close(); } catch (Exception ex) {}
		}
		return result;
	}
	
	public List<RowData> execute (String databaseName, String sql, List<String> params, List<String> data) throws DataSpyException {
		List<RowData> result = new ArrayList<RowData>();
      	PreparedStatement ps = null;
      	ResultSet rs = null;
      	
      	try {
      		if (params != null && data != null) {
      			for (String param : params) {
      				sql = sql.replaceFirst( param, "?" );
      			}
      			System.out.println( sql );
      		}
       		ps = getDbInfo( databaseName ).database.getConnection().prepareStatement( sql );
       		
      		if (params != null && data != null) {
      			int index = 1;
      			for (String d : data) {
      				ps.setString( index, d );
      				index++;
      			}
      		}
       		rs = ps.executeQuery();
       		
  			ResultSetMetaData rsmd = rs.getMetaData();
       		{
   				RowData rowData = new RowData();
   				for (int j = 1; j <= rsmd.getColumnCount(); j++) {
   					System.out.println( "execute " + rsmd.getTableName(j) + "." + rsmd.getColumnName( j ) );
   					rowData.set( "c"+(j-1), rsmd.getTableName(j)+"."+rsmd.getColumnName(j) );
   				}
   				result.add( rowData );
       		}
       		for (int i = 0; i < 10 && rs.next(); i++) {
       			RowData rowData = new RowData();
       			for (int j = 1; j <= rsmd.getColumnCount(); j++) {
					String key = rsmd.getTableName(j)+" "+rsmd.getColumnName(j);
					if (rsmd.getColumnType(j) == Types.BLOB ||
						rsmd.getColumnType(j) == Types.LONGVARBINARY) {
						rowData.set( key, "" );
					} else {
						System.out.println( "set " + key + " " + rs.getString(j) );
						rowData.set( key, rs.getString( j ) );
					}
       			}
       			result.add( rowData );
       		}
       	} catch (Exception e) {
       		e.printStackTrace();
       		throw new DataSpyException( e );
       	} finally {
       		try { rs.close(); } catch (Exception ex) {}
       		try { ps.close(); } catch (Exception ex) {}
       	}
       	return result;
	}
	
	public List<RowData> getSampleData (String databaseName, String tableName) {
		com.dataspy.shared.model.Table table = getDbInfo( databaseName ).db.getTableMap().get( tableName );
		List<RowData> result = new ArrayList<RowData>();
      	PreparedStatement ps = null;
      	ResultSet rs = null;
      	try {
       		ps = getDbInfo( databaseName ).database.getConnection().prepareStatement( "select * from " + table.getName() );
       		rs = ps.executeQuery();
       		ResultSetMetaData rsmd = rs.getMetaData();
       		//for (int i = 1; i <= rsmd.getColumnCount(); i++) {
       		//	System.out.println( "getData " + rsmd.getColumnName( i ) );
       		//}
       		for (int i = 0; i < 10 && rs.next(); i++) {
       			RowData rowData = new RowData();
       			for (int j = 1; j <= rsmd.getColumnCount(); j++) {
					if (rsmd.getColumnType(j) == Types.BLOB ||
						rsmd.getColumnType(j) == Types.LONGVARBINARY)
						continue;
       				rowData.set( rsmd.getColumnName(j), rs.getString( j ) );
       			}
       			result.add( rowData );
       		}
       		rs.close();
       		ps.close();
       	} catch (Exception e) {
       		e.printStackTrace();
       	}
       	return result;
	}
	
	public DbInfo getDbInfo (String databaseName) {
		for (DbInfo dbInfo : dbInfos) {
			if (dbInfo.database.getName().equals( databaseName ))
				return dbInfo;
		}
		return null;
	}
	
	public class TableComparable implements Comparator<Table>{
	    @Override
	    public int compare(Table o1, Table o2) {
	    	return o1.getName().compareTo( o2.getName() );
	    }
	}

}
	