package com.dataspy.shared.model;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;

public class TableIndex extends BeanModel {
    
	public String getName() {
		return get( "name" );
	}
	public void setName(String name) {
		set( "name", name );
	}
	public Boolean isUnique() {
		return get("isUnique");
	}
	public void setUnique(Boolean isUnique) {
		set( "isUnique", isUnique );
	}
	public Boolean isPrimary() {
		return get("isPrimary");
	}
	public void setPrimary(Boolean isPrimary) {
		set( "isPrimary", isPrimary);
	}
	public List<TableColumn> getColumns() {
		return get("columns");
	}
	public void setColumns(List<TableColumn> columns) {
		set("columns", columns);
	}
	public List<Boolean> getColumnsAscending() {
		return get("columnsAscending");
	}
	public void setColumnsAscending(List<Boolean> columnsAscending) {
		set( "columnsAscending", columnsAscending );
	}

}
