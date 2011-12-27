package com.dataspy.shared.model;

import com.extjs.gxt.ui.client.data.BeanModel;

public class TableColumn extends BeanModel {
	
	public String getName() {
		return get( "name" );
	}
	public void setName(String name) {
		set( "name", name );
	}
	public String getType() {
		return get( "type" );
	}
	public void setType(String type) {
		set( "type", type );
	}
	public String getLength() {
		return get( "length" );
	}
	public void setLength(String length) {
		set( "length", length );
	}
	
	public String getParentTable() {
		return get( "parentTable" );
	}
	public void setParentTable(String parentTable) {
		set( "parentTable", parentTable );
	}
	public String getParentColumn() {
		return get( "parentColumn" );
	}
	public void setParentColumn(String parentColumn) {
		set( "parentColumn", parentColumn );
	}
	public String getParentType() {
		return get( "parentType" );
	}
	public void setParentType(String parentType) {
		set( "parentType", parentType );
	}

}
