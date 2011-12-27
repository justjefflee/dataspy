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
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class NavigationPanel extends ContentPanel {
	private DataSpyServiceAsync dataSpyService = (DataSpyServiceAsync) Registry.get(DataSpy.DATASPY_SERVICE);
    private TreeStore<FileModel> store;
	private TreePanel tree;

	public NavigationPanel () {
		setHeading( "Navigation" );
		setLayout( new FitLayout() );
		
		ToolBar toolbar = new ToolBar();
		Button expandButton = new Button( "Expand" );
		expandButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				tree.expandAll();
				//tree.collapseAll();
			}
		});
		
		toolbar.add( expandButton );
	    
		setTopComponent( toolbar );

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

