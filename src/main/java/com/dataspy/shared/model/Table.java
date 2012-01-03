package com.dataspy.shared.model;

import java.util.ArrayList;
import java.util.List;

public class Table implements java.io.Serializable {
	private List<TableColumn> columns = new ArrayList<TableColumn>();
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addTableColumn (TableColumn column) {
		columns.add( column );
	}
	public List<TableColumn> getColumns () {
		return columns;
	}
	public void setColumns (List<TableColumn> columns) {
		this.columns = columns;
	}
}
