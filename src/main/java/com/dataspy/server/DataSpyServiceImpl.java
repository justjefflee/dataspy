package com.dataspy.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.schemaspy.Config;
import net.sourceforge.schemaspy.model.Database;
import net.sourceforge.schemaspy.model.ForeignKeyConstraint;
import net.sourceforge.schemaspy.model.InvalidConfigurationException;
import net.sourceforge.schemaspy.model.ProcessExecutionException;
import net.sourceforge.schemaspy.model.Table;
import net.sourceforge.schemaspy.model.TableColumn;

import com.dataspy.client.DataSpyService;
import com.dataspy.shared.model.FileModel;
import com.dataspy.shared.model.FolderModel;
import com.dataspy.shared.model.RowData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DataSpyServiceImpl extends RemoteServiceServlet implements DataSpyService {
    private static Database database;
    private static Map<String,com.dataspy.shared.model.Table> tableMap = new TreeMap<String,com.dataspy.shared.model.Table>();
    private static Map<String,com.dataspy.shared.model.TableColumn> tableColumnMap = new HashMap<String,com.dataspy.shared.model.TableColumn>();
    
	static {
		initDatabase();
	}
	
    public static void main(String[] argv) {
    	try {
    		DataSpyService s = new DataSpyServiceImpl();
    		//s.init();
    	
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

	static void initDatabase () {
		MySchemaAnalyzer analyzer = new MySchemaAnalyzer();
        String cmdLine = "-i \"T_.*\" -t mssql-jtds -host localhost -port 1433 -noschema -db legato2 -u legato -p legato -o \\output";
        //String cmdLine = "-i \"t_.*\" -t mysql -host localhost -port 3306 -noschema -db newdemo -u legato -p legato -o \\output";
        try {
        	String[] argv = cmdLine.split( " " );
            Config config = new Config(argv);
            database = analyzer.analyze( config );
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
            		System.out.println( "      num of parents " + column.getParents().size() );
            		System.out.println( "      num of children " + column.getChildren().size() );
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
	}
	
    public Map<String,com.dataspy.shared.model.Table> getTableMap () {
    	return tableMap;
    }
    
	public List<RowData> getData(String tableName, String columnName, String columnType, String data) {
		com.dataspy.shared.model.Table table = tableMap.get(tableName);
		List<RowData> result = new ArrayList<RowData>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = database.getConnection().prepareStatement(
					"select * from " + table.getName() + " where " + columnName + " = ?");
			ps.setObject(1, data);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 0; i < 10 && rs.next(); i++) {
				RowData rowData = new RowData();
				for (int j = 1; j <= rsmd.getColumnCount(); j++) {
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
	
	public List<RowData> getSampleData (String tableName) {
		com.dataspy.shared.model.Table table = tableMap.get( tableName );
		List<RowData> result = new ArrayList<RowData>();
      	PreparedStatement ps = null;
      	ResultSet rs = null;
      	try {
       		ps = database.getConnection().prepareStatement( "select * from " + table.getName() );
       		rs = ps.executeQuery();
       		ResultSetMetaData rsmd = rs.getMetaData();
       		//for (int i = 1; i <= rsmd.getColumnCount(); i++) {
       		//	System.out.println( "getData " + rsmd.getColumnName( i ) );
       		//}
       		for (int i = 0; i < 10 && rs.next(); i++) {
       			RowData rowData = new RowData();
       			for (int j = 1; j <= rsmd.getColumnCount(); j++) {
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
	
	public class TableComparable implements Comparator<Table>{
	    @Override
	    public int compare(Table o1, Table o2) {
	    	return o1.getName().compareTo( o2.getName() );
	    }
	}

    public List<FileModel> getFolderChildren(FileModel fileModel) {
    	System.out.println( "getFolderChildren " + fileModel );
    	List<FileModel> result = new ArrayList<FileModel>();
    	if (fileModel == null) {
    		result.add( new FolderModel( database.getName(), "/", "root" ) );
    		
    	} else if ("/".equals(fileModel.getPath())) {
   			result.add( new FolderModel( "Tables", "/tables", "tables" ) );
   			
    	} else if ("/tables".equals(fileModel.getPath())) {
    		List<Table> tables = new ArrayList<Table>();
    		tables.addAll( database.getTables() );
    		Collections.sort( tables, new TableComparable() );
    		
            for (Table table : tables) {
            	result.add( new FileModel( table.getName(), "table", "table" ) );
            }
    	}
    	return result;
    }
}
	