package com.dataspy.shared.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class TableNode extends BaseTreeModel {

	public TableNode() {
		set("type", "table" );
	}

	public TableNode(Database database, Table table) {
		set( "database", database );
		set("table", table);
		set("type", "table" );
		set( "name", table.getName() );
	}

	public Database getDatabase() {
		return (Database) get( "database" );
	}
	
	public Table getTable() {
		return (Table) get("table");
	}
	
	/*
	public String getName () {
		return getTable().getName();
	}
	*/

	public String toString() {
		return getDatabase().getName() + " " + getTable().getName();
	}
}
