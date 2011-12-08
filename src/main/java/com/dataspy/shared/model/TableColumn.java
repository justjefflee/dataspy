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
	
	public String getParent() {
		return get( "parent" );
	}
	public void setParent(String parent) {
		set( "parent", parent );
	}

}
