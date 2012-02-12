package com.dataspy.shared.model;

import java.io.Serializable;

public class Sql implements Serializable {
	private String key;
	private String sql;
	
	public Sql () {
	}
	
	public Sql (String key, String sql) {
		this.key = key;
		this.sql = sql;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}

}
