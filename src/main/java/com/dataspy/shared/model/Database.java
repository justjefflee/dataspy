package com.dataspy.shared.model;

import java.io.Serializable;
import java.util.Map;

public class Database implements Serializable {
	private DatabaseConfig databaseConfig;
	private String name;
    private Map<String,com.dataspy.shared.model.Table> tableMap;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DatabaseConfig getDatabaseConfig() {
		return databaseConfig;
	}
	public void setDatabaseConfig(DatabaseConfig databaseConfig) {
		this.databaseConfig = databaseConfig;
	}
	public Map<String, com.dataspy.shared.model.Table> getTableMap() {
		return tableMap;
	}
	public void setTableMap(Map<String, com.dataspy.shared.model.Table> tableMap) {
		this.tableMap = tableMap;
	}

}
