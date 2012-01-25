package com.dataspy.client.mvc;

import com.dataspy.client.AppEvents;
import com.dataspy.shared.model.Folder;
import com.dataspy.shared.model.Table;
import com.dataspy.shared.model.TableNode;
import com.extjs.gxt.ui.client.data.ModelData;
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

public class NavigationPanel extends ContentPanel {
    private TreeStore<ModelData> store;
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
	
	private Folder getTreeModel () {
   		Folder root = new Folder( Util.getDatabase().getName() );
   		Folder tables = new Folder( "Tables" );
   		root.add( tables );
   		if (Util.getDatabase() != null) {
   			for (Table table : Util.getDatabase().getTableMap().values()) {
   				TableNode tableNode = new TableNode( table.getName() );
   				tables.add( tableNode );
   			}
   		} else {
   			System.out.println( "database is null" );
   		}
   		return root;
	}
	
	private TreePanel createTree() {
		Folder model = getTreeModel();
		store = new TreeStore<ModelData>();
		store.add( model, true );
		
	    final TreePanel<ModelData> tree = new TreePanel<ModelData>(store);  
	    tree.setStateful(true);  
	    tree.setDisplayProperty("name");  
	    // statefull components need a defined id  
	    tree.setId("statefullasynctreepanel");  

		tree.addListener(Events.OnDoubleClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
          		ModelData m =  tree.getSelectionModel().getSelectedItem();
               	if ("table".equals(m.get( "type" )))
               		Dispatcher.forwardEvent( AppEvents.OpenTable, m.get("name") );
   			}
       	});
	     
		return tree;
	}
}

