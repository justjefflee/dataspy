package com.dataspy.client.mvc;

import java.util.List;

import com.dataspy.client.AppEvents;
import com.dataspy.shared.model.Database;
import com.dataspy.shared.model.DatabaseConfig;
import com.dataspy.shared.model.Folder;
import com.dataspy.shared.model.Table;
import com.dataspy.shared.model.TableColumn;
import com.dataspy.shared.model.TableIndex;
import com.dataspy.shared.model.TableNode;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

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
		
		Button refreshButton = new Button( "Refresh" );
		refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				refresh();
			}
		});
		toolbar.add( refreshButton );
		setTopComponent( toolbar );

		tree = createTree();
		add( tree );
	}
	
	private void refresh () {
		final MessageBox box = MessageBox.wait("Progress", "Refreshing", "Refreshing...");  

		Util.getDataSpyService().getDatabases( new AsyncCallback<List<Database>>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
				box.close();
			}
			@Override
			public void onSuccess(List<Database> databases) {
				System.out.println( "got database " + databases.size() );
				box.close();
				Registry.register( "databases", databases );
				Folder model = getTreeModel();
				store.removeAll();
				store.add( model, true );
			}
		});
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
				Folder indexes = new Folder( "Indexes" );
				tableNode.add( indexes );
				for (TableIndex tableIndex : table.getIndexes()) {
					Folder index = new Folder( tableIndex.getName() );
					indexes.add( index );
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
		final MenuItem addDatabaseMenuItem = new MenuItem( "Add Database" );
		addDatabaseMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
           		Dispatcher.forwardEvent( AppEvents.AddDatabase );
			}
		});
		contextMenu.add( addDatabaseMenuItem );
		
		final MenuItem editDatabaseMenuItem = new MenuItem( "Edit Database" );
		editDatabaseMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
                ModelData m = tree.getSelectionModel().getSelectedItem();
                DatabaseConfig databaseConfig = ((Database)m.get( "database" )).getDatabaseConfig();
           		Dispatcher.forwardEvent( AppEvents.EditDatabase, new Object[] { databaseConfig } );
			}
		});
		contextMenu.add( editDatabaseMenuItem );
		
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
						if ("Databases".equals( m.get( "name" ))) {
							addDatabaseMenuItem.show();
							editDatabaseMenuItem.hide();
							newQueryMenuItem.hide();
						} else if (m.get( "database" ) != null) {
							addDatabaseMenuItem.hide();
							editDatabaseMenuItem.show();
							newQueryMenuItem.show();
						} else {
							addDatabaseMenuItem.hide();
							editDatabaseMenuItem.hide();
							newQueryMenuItem.hide();
						}
					}
		});

		tree.setIconProvider(new ModelIconProvider<ModelData>() {
			public AbstractImagePrototype getIcon(ModelData model) {
               	if ("Databases".equals( model.get("name")))
					return IconHelper.createPath("images/Disk.gif");
               	if ("Tables".equals( model.get("name")))
					return IconHelper.createPath("images/tables.gif");
               	if (model.get("table") != null && model.get("table") instanceof Table)
					return IconHelper.createPath("images/table.png");
               	if (model.get("database") != null && model.get("database") instanceof Database)
					return IconHelper.createPath("images/Datbase.gif");
				return null;
			}
		});

		
		tree.setContextMenu( contextMenu );
		return tree;
	}
}

