package com.dataspy.shared.model;

import java.util.ArrayList;
import java.util.List;

public class Table implements java.io.Serializable {
	private List<TableColumn> columns = new ArrayList<TableColumn>();
	private List<TableIndex> indexes = new ArrayList<TableIndex>();
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
	public TableColumn getColumn (String columnName) {
		for (TableColumn c : getColumns()) {
			if (c.getName().equals( columnName ))
				return c;
		}
		return null;
	}
	public List<TableIndex> getIndexes () {
		return indexes;
	}
	public void setIndexes (List<TableIndex> indexes) {
		this.indexes = indexes;
	}
}
