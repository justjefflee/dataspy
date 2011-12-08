package com.dataspy.shared.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class FileModel extends BaseModelData {

	protected FileModel() {
	}

	public FileModel(String name, String path, String type) {
		setName(name);
		setPath(path);
		setType(type);
	}

	public void setName(String name) {
		set("name", name);
	}
	public String getName() {
		return get("name");
	}

	public void setPath(String path) {
		set("path", path);
	}

	public String getPath() {
		return get("path");
	}

	public void setType(String type) {
		set("type", type);
	}

	public String getType() {
		return get("type");
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FileModel) {
			FileModel mobj = (FileModel) obj;
			return getName().equals(mobj.getName())
					&& getPath().equals(mobj.getPath());
		}
		return super.equals(obj);
	}

	public String toString() {
		return getName() + " " + getPath();
	}

}
