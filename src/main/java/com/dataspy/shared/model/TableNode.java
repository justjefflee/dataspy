package com.dataspy.shared.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class TableNode extends BaseTreeModel {

	public TableNode() {
		set("type", "table" );
	}

	public TableNode(String name) {
		set("name", name);
		set("type", "table" );
	}

	public String getName() {
		return (String) get("name");
	}

	public String toString() {
		return getName();
	}
}
