package com.dataspy.client;

import java.util.List;

import com.dataspy.shared.model.FileModel;
import com.dataspy.shared.model.Table;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dataspy-service")
public interface DataSpyService extends RemoteService {

	public Table getTable (String tableName);
	
    public List<FileModel> getFolderChildren(FileModel fileModel);
	
}
