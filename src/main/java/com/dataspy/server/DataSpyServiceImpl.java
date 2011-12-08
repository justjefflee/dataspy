package com.dataspy.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

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

	private static void initDatabase () {
		MySchemaAnalyzer analyzer = new MySchemaAnalyzer();
        String cmdLine = "-dp jtds-1.2.5.jar -i \"T_P.*\" -t mssql-jtds -host localhost -port 1433 -noschema -db legato2 -u legato -p legato -o \\output";
        try {
        	String[] argv = cmdLine.split( " " );
            Config config = new Config(argv);
            database = analyzer.analyze( config );
            for (Table table : database.getTables()) {
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
            	/*
            	PreparedStatement ps = database.getConnection().prepareStatement( "select count(*) from " + table.getName() );
            	ResultSet rs = null;
            	try {
            		rs = ps.executeQuery();
            		if (rs.next())
            			System.out.println( "  row count: " + rs.getInt(1));
            		rs.close();
            		ps.close();
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            	*/
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
	
	public com.dataspy.shared.model.Table getTable (String tableName) {
		com.dataspy.shared.model.Table table = new com.dataspy.shared.model.Table();
		for (Table t : database.getTables()) {
			if (t.getName().equals( tableName )) {
				table.setName( t.getName() );
            	for (TableColumn column : t.getColumns()) {
            		com.dataspy.shared.model.TableColumn tc = new com.dataspy.shared.model.TableColumn();
            		tc.setName( column.getName() );
            		tc.setType( column.getType() );
            		tc.setLength( column.getLength()+"" );
            		table.addTableColumn( tc );
            		for (TableColumn p : column.getParents()) {
            			System.out.println( "parent: " + p );
            		}
            		for (TableColumn p : column.getChildren()) {
            			System.out.println( "children: " + p );
            		}
            	}
            	getData( table );
				return table;
			}
		}
		return null;
	}
	
	private void getData (com.dataspy.shared.model.Table table) {
      	PreparedStatement ps = null;
      	ResultSet rs = null;
      	try {
       		ps = database.getConnection().prepareStatement( "select * from " + table.getName() );
       		rs = ps.executeQuery();
       		ResultSetMetaData rsmd = rs.getMetaData();
       		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
       			System.out.println( "getData " + rsmd.getColumnName( i ) );
       		}
       		for (int i = 0; i < 10 && rs.next(); i++) {
       			RowData rowData = new RowData();
       			for (int j = 1; j <= rsmd.getColumnCount(); j++) {
       				rowData.set( rsmd.getColumnName(j), rs.getString( j ) );
       			}
       			table.addRowData( rowData );
       		}
       		rs.close();
       		ps.close();
       	} catch (Exception e) {
       		e.printStackTrace();
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
            for (Table table : database.getTables()) {
            	result.add( new FileModel( table.getName(), "table", "table" ) );
            }
    	}
    	return result;
    }
}
	