package com.dataspy.client.mvc;

import java.util.ArrayList;
import java.util.List;

import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Table;
import com.dataspy.shared.model.TableColumn;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel.CellSelection;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class TablePanel extends ContentPanel {

	public TablePanel (Table table) {
		setLayout( new FitLayout() );
		setHeading( table.getName() );
		
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
    	setSize( 500, 150 );
	}
	
	private Grid<RowData> createGrid (final Table table) {
    	final ListStore<RowData> store = new ListStore<RowData>();
    	store.add( table.getData() );
    	
		final List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		ColumnConfig colConfig;
		
		for (TableColumn column : table.getColumns()) {
			colConfig = new ColumnConfig( column.getName(), column.getName(), 80);  
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
                System.out.println( "cell selected: " + cs.row + " " + cs.cell + " value: " +  rowData.get( column.getName() ) );
            }
        });

		//grid.setHeight( 140 );
		//grid.setWidth( 500 );
		return grid;
	}
}
