package com.dataspy.client.mvc;

import java.util.Map;

import com.dataspy.client.DataSpy;
import com.dataspy.shared.model.Table;
import com.extjs.gxt.ui.client.Registry;

public class Util {
	
    public static Map<String,Table> getTableMap () {
    	return (Map<String,Table>) Registry.get( DataSpy.TABLE_MAP );
    }

}
