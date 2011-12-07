package com.dataspy.server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.schemaspy.Config;
import net.sourceforge.schemaspy.SchemaAnalyzer;
import net.sourceforge.schemaspy.model.Database;
import net.sourceforge.schemaspy.model.ForeignKeyConstraint;
import net.sourceforge.schemaspy.model.InvalidConfigurationException;
import net.sourceforge.schemaspy.model.ProcessExecutionException;
import net.sourceforge.schemaspy.model.Table;

import com.dataspy.client.DataSpyService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DataSpyServiceImpl extends RemoteServiceServlet implements DataSpyService {
    public static void main(String[] argv) {
    	try {
    		DataSpyService s = new DataSpyServiceImpl();
    		s.init();
    	
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
		   

	public void init () {
		SchemaAnalyzer analyzer = new SchemaAnalyzer();
        String cmdLine = "-dp jtds-1.2.5.jar -i \"T_P.*\" -t mssql-jtds -host localhost -port 1433 -noschema -db legato2 -u legato -p legato -o \\output";
        try {
        	String[] argv = cmdLine.split( " " );
            Config config = new Config(argv);
            config.setHtmlGenerationEnabled( false );
            Database database = analyzer.analyze( config );
            for (Table table : database.getTables()) {
            	System.out.println( table.getName() );
            	System.out.println( "  num children: " + table.getNumChildren() );
            	System.out.println( "  num parents: " + table.getNumParents() );
            	for (ForeignKeyConstraint fk : table.getForeignKeys()) {
            		System.out.println( "    " + fk );
            	}
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
	
}
