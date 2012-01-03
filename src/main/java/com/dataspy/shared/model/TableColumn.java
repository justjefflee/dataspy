package com.dataspy.shared.model;

import java.util.List;

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
	public void setTable (Table table) {
		set( "table", table );
	}
	public Table getTable () {
		return get( "table" );
	}
	
	public String getParentType() {
		return get( "parentType" );
	}
	public void setParentType(String parentType) {
		set( "parentType", parentType );
	}
	
	public void setParents (List<TableColumn> parents) {
		set( "parents", parents );
	}
	public List<TableColumn> getParents () {
		return get( "parents" );
	}
	public void setChildren (List<TableColumn> children) {
		set( "children", children );
	}
	public List<TableColumn> getChildren () {
		return get( "children" );
	}

}
