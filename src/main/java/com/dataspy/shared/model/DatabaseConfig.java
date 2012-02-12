package com.dataspy.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConfig implements Serializable {
	private String key;
	private String params;
	private List<Sql> sqls = new ArrayList<Sql>();

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public Sql findSql (String key) {
		for (Sql sql : sqls) {
			if (sql.getKey().equals( key ))
				return sql;
		}
		return null;
	}
	public void removeSql (String key) {
		sqls.remove( findSql( key ) );
	}
	public void addSql (Sql sql) {
		sqls.add( sql );
	}
	public List<Sql> getSqls() {
		return sqls;
	}
	public void setSqls(List<Sql> sqls) {
		this.sqls = sqls;
	}
	
}
