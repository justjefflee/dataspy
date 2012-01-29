package com.dataspy.client.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dataspy.client.AppEvents;
import com.dataspy.shared.model.Database;
import com.dataspy.shared.model.Folder;
import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Table;
import com.dataspy.shared.model.TableColumn;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeGridEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel.CellSelection;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TablePanel extends Window {
   	private FormBinding formBinding;
   	private TreeStore<ModelData> treeStore = new TreeStore<ModelData>();
   	private Table table;
	private TreeGrid<ModelData> tree;
	private TextArea textArea;
	private Database database;

	public TablePanel (Database database, final Table table) {
		try {
		this.database = database;
		this.table = table;
		//setLayout(new RowLayout(Orientation.HORIZONTAL)); 
		setLayout( new FitLayout() );
		setHeaderVisible( false );
		//setHeading( table.getName() );
		
		ToolBar toolbar = new ToolBar();
		Button clearButton = new Button( "Clear" );
		clearButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				treeStore.removeAll();
			}
		});
		toolbar.add( clearButton );
	    
		Button expandButton = new Button( "Expand" );
		expandButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				tree.expandAll();
			}
		});
		toolbar.add( expandButton );
		
		Button collapseButton = new Button( "Collapse" );
		collapseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				tree.collapseAll();
			}
		});
		toolbar.add( collapseButton );
		
		Button executeButton = new Button( "Execute" );
		toolbar.add( executeButton );
		
		setTopComponent( toolbar );
		ContentPanel cp = new ContentPanel();
		
		//gridDesign( cp );
		treeGridDesign( cp );
    	
		final TabPanel tabPanel = new TabPanel();
		final TabItem resultTabItem = new TabItem( "Result" );
		resultTabItem.setLayout( new FitLayout() );
		resultTabItem.add( cp );
		tabPanel.add( resultTabItem );
		
		final TabItem sqlTabItem = new TabItem( "SQL" );
		sqlTabItem.setLayout( new FitLayout() );
		textArea = new TextArea();
		sqlTabItem.add( textArea );
		tabPanel.add( sqlTabItem );
		
		add( tabPanel );
    	setSize( 800, 600 );
    	
		executeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				executeSql();
				tabPanel.setSelection( resultTabItem );
			}
		});
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void treeGridDesign (ContentPanel cp) {
		cp.setLayout( new FitLayout() );
		cp.setHeaderVisible( false );
    	cp.add( createTreeGrid() );
    	//cp.add( createFormPanel() );
	}
	
	/*
	private void gridDesign (ContentPanel cp) {
		//cp.setLayout( new FitLayout() );
		BorderLayout layout = new BorderLayout();
		cp.setLayout( layout );
		cp.setHeaderVisible( false );
    	
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 250);
		westData.setSplit(true);
		westData.setCollapsible(true);
		westData.setMargins(new Margins(0, 2, 0, 0));
		
    	cp.add( createGrid( table ), westData );
		
		//cp.setScrollMode( Scroll.AUTO );
		
        FormPanel fp = createFormPanel();  
        fp.setLabelWidth( 120 );
        formBinding = new FormBinding( fp, true );  
        formBinding.setStore( store );
    	
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0));
    	cp.add( fp, centerData );
	}
	*/
	
	public void executeSql () {
		Util.getDataSpyService().execute( database.getName(), textArea.getValue(), new AsyncCallback<List<RowData>>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(List<RowData> data) {
				try {
				treeStore.insert( toFolder( data ), 0, true );
				tree.setExpanded( treeStore.getRootItems().get(0), true, true );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void addData (final TableColumn parentColumn, final ModelData parent, String cellData) {
		System.out.println( "addData " + parentColumn.getTable().getName() + " " + parentColumn.getName() + " " + cellData );
		Util.getDataSpyService().getData( database.getName(), parentColumn.getTable().getName(), parentColumn.getName(), parentColumn.getType(), cellData, new AsyncCallback<List<RowData>>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(List<RowData> data) {
				treeStore.insert( parent, toFolder( parentColumn.getTable(), data ), 0, true );
				tree.setExpanded( parent, true, true );
			}
		});
	}

	public void addData (List<RowData> data) {
    	//store.add( data );
    	final ModelData m = toFolder( table, data );
    	treeStore.add( m, true );
		
		new DelayedTask( new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				tree.setExpanded( m, true, true );
			}
		}).delay( 100 );
	}
	
	private Folder toFolder (List<RowData> data) {
    	Folder f = new Folder();
    	int i = 0;
    	RowData metaData = data.get(0);
    	for (String key : metaData.getPropertyNames()) {
    		System.out.println( "toFolder " + key + " " + metaData.get(key) );
    		f.set( key, metaData.get( key ) );
    		i++;
    		if (i == 20)
    			break;
    	}
    	data.remove( 0 );
    	for (RowData rd : data) {
    		Folder c = new Folder();
    		c.set( "metaData", metaData );
    		f.add( c );
    		for (i = 0; metaData.get( "c"+i ) != null; i++) {
   				String key = (String) metaData.get("c"+i);
   				key = key.replace( ".", " " );
   				Object o = rd.get( key );
   				//System.out.println( "toFolder " + key + " " + o );
   				c.set( "c"+i, o );
    			if (i == 20)
    				break;
    		}
    	}
    	return f;
	}
	
	private Folder toFolder (Table table, List<RowData> data) {
    	Folder f = new Folder();
    	int i = 0;
    	for (TableColumn column : table.getColumns()) {
   			f.set( "c"+i, (i == 0 ? table.getName()+"." : "") + column.getName() );
    		i++;
    		if (i == 20)
    			break;
    	}
    	for (RowData rd : data) {
    		Folder c = new Folder();
    		c.set( "Table", table );
    		f.add( c );
    		i = 0;
    		for (TableColumn column : table.getColumns()) {
   				c.set( "c"+i, rd.get( column.getName() ) );
    			i++;
    			if (i == 20)
    				break;
    		}
    	}
    	return f;
	}
	
	private FormPanel createFormPanel () {
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible( false );
		fp.setScrollMode( Scroll.AUTO );
		fp.setWidth( 400 );
		
		for (TableColumn column : table.getColumns()) {
			TextField<String> tf = new TextField<String>();
			tf.setName( column.getName() );
			tf.setFieldLabel( column.getName() );
			fp.add( tf );
		}
		
		return fp;
	}
	
	private TreeGrid<ModelData> createTreeGrid () {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		for (int i = 0; i < 20; i++) {
			ColumnConfig cc = new ColumnConfig( "c"+i, "Column "+i, 140 );
			if (i == 0) {
				cc.setRenderer(new TreeGridCellRenderer<ModelData>());  
				cc.setWidth( 220 );
			}
			columns.add( cc );
		}
		ColumnModel cm = new ColumnModel(columns);
		tree = new TreeGrid<ModelData>( treeStore, cm );
		tree.setHideHeaders( true );
		final CellSelectionModel<ModelData> sm = new CellSelectionModel<ModelData>();
		tree.setSelectionModel( sm );
		
		tree.addListener(Events.CellDoubleClick, new Listener<BaseEvent>(){
            public void handleEvent(BaseEvent be) {
            	try {
                TreeGridEvent ge = (TreeGridEvent)be;
                CellSelection cs = sm.getSelectCell();
    			
           		ModelData rowData = ge.getModel();
       			String cellData = rowData.get( "c"+cs.cell );
       			
                List<TableColumn> columns = new ArrayList<TableColumn>();
                
           		//ModelData rowData = treeStore.getAt( cs.row );
           		Table t = (Table) rowData.get("Table");
           		if (t != null) {
           			TableColumn column = t.getColumns().get( cs.cell );
           			System.out.println( "double click " + cs.row + " " + cs.cell + " " + t.getName() );
           			System.out.println( "cell selected: " + cs.row + " " + cs.cell + " value: " +  cellData + " parents: " + column.getParents().size() );
                
           			columns.addAll( column.getParents() );
           			columns.addAll( column.getChildren() );
           		} else {
           			RowData metaData = (RowData) rowData.get( "metaData" );
           			String key = metaData.get( "c"+cs.cell );
           			String[] s = key.split( "\\." );
           			String tableName = s[0];
           			String columnName = s[1];
           			System.out.println( "double click: " + tableName + " " + columnName );
           			t = database.getTableMap().get( tableName );
           			for (TableColumn column : t.getColumns()) {
           				if (column.getName().equals( columnName )) {
           					columns.addAll( column.getParents() );
           					columns.addAll( column.getChildren() );
           					break;
           				}
           			}
           		}
                
                if (columns.size() > 0) {
                	String key = table.getName()+cs.row+""+cs.cell;
                	//rowData.set( column.getName()+"clicked", "" );
                	
        			Record rec = treeStore.getRecord( rowData );
        			rec.beginEdit();
        			//rec.set( column.getName(), cellData );
        			rec.set( key, "" );
        			rec.endEdit();
        			rec.commit(true);

        			for (TableColumn c : columns) {
        				Map<String,Object> data = new HashMap<String,Object>();
                		data.put( "parent", c );
                		data.put( "data", cellData );
                		addData( c, rowData, cellData );
        			}
                }
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
        });
		return tree;
	}
	
	/*
	private Grid<RowData> createGrid (final Table table) {
		GridCellRenderer<RowData> renderer = new GridCellRenderer<RowData>() {
			@Override
			public Object render(RowData model, String property, com.extjs.gxt.ui.client.widget.grid.ColumnData config,
					int rowIndex, int colIndex, ListStore<RowData> listStore, Grid<RowData> grid) {
				RowData rowData = listStore.getAt( rowIndex );
				TableColumn column = table.getColumns().get( colIndex );
				String key = table.getName()+rowIndex+""+colIndex;
               	//System.out.println( "check key: " + key );
               	if (rowData.get( key ) == null)
					config.style = "";
				else
					config.style = "background-color: #EDE275;";
				return rowData.get( column.getName() );
			}  
		};  

		final List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		ColumnConfig colConfig;
		
		for (TableColumn column : table.getColumns()) {
			colConfig = new ColumnConfig( column.getName(), column.getName(), 80);  
			colConfig.setRenderer( renderer );
			columns.add( colConfig );
		}
		
		final ColumnModel columnModel = new ColumnModel(columns);
		
		final Grid<RowData> grid = new Grid<RowData>( store, columnModel );
		grid.setBorders(true);
		grid.setLoadMask( true );
		grid.setStripeRows( true );
		final CellSelectionModel<RowData> sm = new CellSelectionModel<RowData>();
		grid.setSelectionModel( sm );
		
		grid.addListener(Events.CellDoubleClick, new Listener<BaseEvent>(){
            public void handleEvent(BaseEvent be) {
    			System.out.println( "double click" );
                GridEvent ge = (GridEvent)be;
                CellSelection cs = sm.getSelectCell();
          		TableColumn column = table.getColumns().get( cs.cell );
           		RowData rowData = store.getAt( cs.row );
           		String cellData = rowData.get( column.getName() );
                System.out.println( "cell selected: " + cs.row + " " + cs.cell + " value: " +  cellData + " parents: " + column.getParents().size() );
                List<TableColumn> parents = column.getParents();
                if (parents.size() > 0) {
                	String key = table.getName()+cs.row+""+cs.cell;
                	//System.out.println( "add key: " + key );
                	rowData.set( column.getName()+"clicked", "" );
                	
        			Record rec = store.getRecord( rowData );
        			rec.beginEdit();
        			rec.set( column.getName(), cellData );
        			rec.set( key, "" );
        			rec.endEdit();
        			rec.commit(true);

                	Map<String,Object> data = new HashMap<String,Object>();
                	data.put( "parent", parents.get(0) );
                	data.put( "data", cellData );
           			Dispatcher.forwardEvent( AppEvents.OpenParentFKData, data );
                }
      			formBinding.bind( rowData );
            }
        });

		grid.addListener(Events.CellClick, new Listener<BaseEvent>(){
            public void handleEvent(BaseEvent be) {
                CellSelection cs = sm.getSelectCell();
          		TableColumn column = table.getColumns().get( cs.cell );
           		RowData rowData = store.getAt( cs.row );
      			formBinding.bind( rowData );
           	}  
        });  
        
		return grid;
	}
	*/
}
