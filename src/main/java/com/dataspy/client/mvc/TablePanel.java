package com.dataspy.client.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dataspy.client.AppEvents;
import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Table;
import com.dataspy.shared.model.TableColumn;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
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
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class TablePanel extends Window {
   	private ListStore<RowData> store = new ListStore<RowData>();
   	private FormBinding formBinding;

	public TablePanel (final Table table) {
		//setLayout(new RowLayout(Orientation.HORIZONTAL)); 
		setLayout( new FitLayout() );
		setHeading( table.getName() );
		
		ToolBar toolbar = new ToolBar();
		Button clearButton = new Button( "Clear" );
		clearButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				store.removeAll();
			}
		});
		toolbar.add( clearButton );
	    
		setTopComponent( toolbar );
		ContentPanel cp = new ContentPanel();
		//cp.setLayout( new FitLayout() );
		cp.setLayout(new RowLayout(Orientation.HORIZONTAL)); 
		cp.setHeaderVisible( false );
    	final Grid<RowData> grid = createGrid( table );
    	cp.add( grid, new com.extjs.gxt.ui.client.widget.layout.RowData( .5, 1 ));
		//cp.setScrollMode( Scroll.AUTO );
		
        FormPanel fp = createFormPanel( table );  
        formBinding = new FormBinding( fp, true );  
        formBinding.setStore( store );
    	cp.add( fp, new com.extjs.gxt.ui.client.widget.layout.RowData( .5, 1 ));
    	
		TabPanel tabPanel = new TabPanel();
		TabItem tabItem = new TabItem( "Result" );
		tabItem.setLayout( new FitLayout() );
		tabItem.add( cp );
		tabPanel.add( tabItem );
		
		tabItem = new TabItem( "SQL" );
		tabItem.setLayout( new FitLayout() );
		tabItem.add( new TextArea() );
		tabPanel.add( tabItem );
		
		add( tabPanel );
    	setSize( 500, 250 );
	}
	
	public void addData (List<RowData> data) {
    	store.add( data );
	}
	
	private FormPanel createFormPanel (final Table table) {
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible( false );
		fp.setScrollMode( Scroll.AUTO );
		
		for (TableColumn column : table.getColumns()) {
			TextField<String> tf = new TextField<String>();
			tf.setName( column.getName() );
			tf.setFieldLabel( column.getName() );
			fp.add( tf );
		}
		
		return fp;
	}
	
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
                System.out.println( "cell selected: " + cs.row + " " + cs.cell + " value: " +  cellData );
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
                	//data.put( "columnName", column.getParentColumn() );
                	//data.put( "columnType", column.getParentType() );
                	data.put( "data", cellData );
                	//System.out.println( "parent: " + column.getParentTable() + "." + column.getParentColumn() );
           			Dispatcher.forwardEvent( AppEvents.OpenParentFKData, parents.get(0) );
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
        
		//grid.setHeight( 140 );
		//grid.setWidth( 500 );
		return grid;
	}
}
