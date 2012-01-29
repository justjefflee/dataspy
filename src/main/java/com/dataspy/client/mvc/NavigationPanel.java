package com.dataspy.client.mvc;

import com.dataspy.client.AppEvents;
import com.dataspy.shared.model.Database;
import com.dataspy.shared.model.Folder;
import com.dataspy.shared.model.Table;
import com.dataspy.shared.model.TableColumn;
import com.dataspy.shared.model.TableNode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
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
   		Folder root = new Folder( "Databases" );
   		for (Database database : Util.getDatabases()) {
   			Folder dbroot = new Folder( database.getName() );
   			dbroot.set( "database", database );
   			root.add( dbroot );
   			Folder tables = new Folder( "Tables" );
   			tables.set( "database", database );
   			dbroot.add( tables );
			for (Table table : database.getTableMap().values()) {
				TableNode tableNode = new TableNode( database, table );
				tables.add( tableNode );
				Folder columns = new Folder( "Columns" );
				tableNode.add( columns );
				for (TableColumn tableColumn : table.getColumns()) {
					Folder column = new Folder( tableColumn.getName() + " (" + tableColumn.getType() + " " + tableColumn.getLength() + ")" );
					columns.add( column );
				}
			}
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
               		Dispatcher.forwardEvent( AppEvents.OpenTable, new Object[] { m.get("database"), m.get("table") } );
   			}
       	});
	     
		Menu contextMenu = new Menu();
		final MenuItem newQueryMenuItem = new MenuItem( "New Query" );
		newQueryMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                ModelData m = tree.getSelectionModel().getSelectedItem();
           		Dispatcher.forwardEvent( AppEvents.NewQuery, new Object[] { m.get("database") } );
			}
		});

		contextMenu.add( newQueryMenuItem );
		
		contextMenu.addListener( Events.BeforeShow,
				new Listener<MenuEvent>() {
					@Override
					public void handleEvent(MenuEvent be) {
						ModelData m = tree.getSelectionModel().getSelectedItem();
						if (m == null)
							return;
						if (m.get( "database" ) != null)
							newQueryMenuItem.show();
						else
							newQueryMenuItem.hide();
					}
		});


		
		tree.setContextMenu( contextMenu );
		return tree;
	}
}

