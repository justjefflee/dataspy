package com.dataspy.client.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dataspy.client.AppEvents;
import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Table;
import com.dataspy.shared.model.TableColumn;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel.CellSelection;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class TablePanel extends Window {
   	private ListStore<RowData> store = new ListStore<RowData>();

	public TablePanel (Table table) {
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
		cp.setLayout( new FitLayout() );
		cp.setHeaderVisible( false );
    	cp.add( createGrid( table ));
		//cp.setScrollMode( Scroll.AUTO );
		
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
	
	private Grid<RowData> createGrid (final Table table) {
    	store.add( table.getData() );
    	
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
                if (column.getParentTable() != null) {
                	String key = table.getName()+cs.row+""+cs.cell;
                	//System.out.println( "add key: " + key );
                	rowData.set( column.getName()+"clicked", "" );
                	
        			Record rec = store.getRecord( rowData );
        			rec.beginEdit();
        			rec.set( column.getName(), cellData );
        			rec.set( key, "" );
        			rec.endEdit();
        			rec.commit(true);

                	Map<String,String> data = new HashMap<String,String>();
                	data.put( "tableName", column.getParentTable() );
                	data.put( "columnName", column.getParentColumn() );
                	data.put( "columnType", column.getParentType() );
                	data.put( "data", cellData );
                	System.out.println( "parent: " + column.getParentTable() + "." + column.getParentColumn() );
           			Dispatcher.forwardEvent( AppEvents.OpenParentFKData, data );
                }
            }
        });

		//grid.setHeight( 140 );
		//grid.setWidth( 500 );
		return grid;
	}
}
