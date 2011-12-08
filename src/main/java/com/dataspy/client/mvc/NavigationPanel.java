package com.dataspy.client.mvc;

import java.util.List;

import com.dataspy.client.AppEvents;
import com.dataspy.client.DataSpy;
import com.dataspy.client.DataSpyServiceAsync;
import com.dataspy.shared.model.FileModel;
import com.dataspy.shared.model.FolderModel;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class NavigationPanel extends ContentPanel {
	private DataSpyServiceAsync dataSpyService = (DataSpyServiceAsync) Registry.get(DataSpy.DATASPY_SERVICE);
    private TreeStore<FileModel> store;
	private TreePanel tree;

	public NavigationPanel () {
		setHeading( "Navigation" );
		setLayout( new FitLayout() );
		
		tree = createTree();
		add( tree );
	}
	
	private TreePanel createTree() {
		// data proxy  
	    RpcProxy<List<FileModel>> proxy = new RpcProxy<List<FileModel>>() {  
	        @Override  
	        protected void load(Object loadConfig, AsyncCallback<List<FileModel>> callback) {  
	        	dataSpyService.getFolderChildren((FileModel) loadConfig, callback);  
	        }  
	    };  
	   
	    // tree loader  
	    BaseTreeLoader loader = new BaseTreeLoader<FileModel>(proxy) {  
	    	@Override  
	        public boolean hasChildren(FileModel parent) {  
	    		return parent instanceof FolderModel;  
	        }  
	    };  
	   
	    store = new TreeStore<FileModel>(loader);
	    final TreePanel<FileModel> tree = new TreePanel<FileModel>(store);  
	    tree.setStateful(true);  
	    tree.setDisplayProperty("name");  
	    // statefull components need a defined id  
	    tree.setId("statefullasynctreepanel");  

        /*
		tree.setIconProvider(new ModelIconProvider<FileModel>() {
			public AbstractImagePrototype getIcon(FileModel model) {
				if (!(model instanceof FolderModel)) {
					String ext = model.getName().substring( model.getName().lastIndexOf(".") + 1);

                	if ("backlog".equals(model.getType()))
						return IconHelper.createPath("images/Cart.gif");
                	if ("release".equals(model.getType()))
						return IconHelper.createPath("images/application.png");
                	if ("report".equals(model.getType()))
						return IconHelper.createPath("images/Chart1.gif");
				} else if (model instanceof ProjectFolderModel) {
					return IconHelper.createPath("images/Computer.gif");
				}
				return null;
			}
		});
        */
		tree.addListener(Events.OnDoubleClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
          		FileModel fm =  tree.getSelectionModel().getSelectedItem();
               	if ("table".equals(fm.getType()))
               		Dispatcher.forwardEvent( AppEvents.OpenTable, fm.getName() );
   			}
       	});
	     
		return tree;
	}
}

