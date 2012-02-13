package com.dataspy.client.mvc;

import java.util.List;

import com.dataspy.client.AppEvents;
import com.dataspy.shared.model.Database;
import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Table;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;

public class TablePanel extends ContentPanel {
   	//private FormBinding formBinding;
   	//private TreeStore<ModelData> treeStore = new TreeStore<ModelData>();
   	//private Table table;
	//private TreeGrid<ModelData> tree;
	//private Database database;
	private SqlPanel sqlPanel;
	private ResultPanel resultPanel;
	private TabPanel tabPanel;
	private TabItem resultTabItem;

	public TablePanel (Database database, final Table table) {
		try {
		//this.database = database;
		//this.table = table;
		setLayout( new FitLayout() );
		setHeaderVisible( false );
		
		tabPanel = new TabPanel();
		
		TabItem sqlTabItem = new TabItem( "SQL" );
		sqlTabItem.setLayout( new FitLayout() );
		
		sqlPanel = new SqlPanel( this, database );
		sqlTabItem.add( sqlPanel );
		tabPanel.add( sqlTabItem );
		
		resultTabItem = new TabItem( "Result" );
		resultTabItem.setLayout( new FitLayout() );
		resultPanel = new ResultPanel( this, database, table );
		resultTabItem.add( resultPanel );
		tabPanel.add( resultTabItem );
		
		add( tabPanel );
		
		if (table != null)
			tabPanel.setSelection( resultTabItem );
		
		} catch (Exception e) {
			Dispatcher.forwardEvent( AppEvents.Error, e );
			e.printStackTrace();
		}
	}
	
	public void addData (List<RowData> data) {
		resultPanel.addData( data );
	}
	
	public void executeSql () {
		try {
			System.out.println( "TablePanel: executeSql" );
			resultPanel.executeSql();
			tabPanel.setSelection( resultTabItem );
		} catch (Exception e) {
			Dispatcher.forwardEvent( AppEvents.Error, e );
		}
	}
	
	public String getSql () {
		return sqlPanel.getSql();
	}
	
}
