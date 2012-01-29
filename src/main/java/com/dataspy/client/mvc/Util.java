package com.dataspy.client.mvc;

import java.util.List;

import com.dataspy.client.DataSpy;
import com.dataspy.client.DataSpyServiceAsync;
import com.dataspy.shared.model.Database;
import com.extjs.gxt.ui.client.Registry;

public class Util {
	
	public static DataSpyServiceAsync getDataSpyService () {
		return (DataSpyServiceAsync) Registry.get(DataSpy.DATASPY_SERVICE);
	}
	
    public static List<Database> getDatabases () {
    	return (List<Database>) Registry.get( "databases" );
    }

}
