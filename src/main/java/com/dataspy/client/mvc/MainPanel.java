package com.dataspy.client.mvc;

import java.util.List;

import com.dataspy.shared.model.Database;
import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Table;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends TabPanel {

	public MainPanel() {
		TabItem tabItem = new TabItem( "Welcome" );
		tabItem.setId( "Welcome" );
		tabItem.setClosable( true );
		tabItem.setLayout(new FitLayout());
		
		ContentPanel cp = new ContentPanel();
		cp.setUrl("welcome.html");
		
		tabItem.add( (Widget) cp );
		add( tabItem );
		setSelection( tabItem );
    }

	public void addData (Database database, Table table, List<RowData> data) {
		TabItem tabItem = findItem( database.getName() + " - " + table.getName(), false );
		if (tabItem != null) {
			TablePanel tp = (TablePanel) tabItem.getWidget( 0 );
			tp.addData( data );
		}
	}
	
	static int tablePanelCount = 1;
	public void openQuery (Database database) {
		try {
			System.out.println( "MainPanel openTable: " + database.getName() );
		
			TablePanel tp = new TablePanel( database, null );
			/*
			tp.setClosable( true );
			tp.setCollapsible( true );
			tp.setContainer( getElement() );
			tp.setConstrain( true );
			tp.addWindowListener(
					new WindowListener () {
						public void windowHide(WindowEvent we) {
						}
					});
					*/
		
			TabItem tabItem = new TabItem( database.getName() + " " + tablePanelCount++ );
			tabItem.setId( database.getName() );
			tabItem.setClosable( true );
			tabItem.setLayout(new FitLayout());
			tabItem.add( (Widget) tp );
			add( tabItem );
			setSelection( tabItem );
	    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void openTable (Database database, Table table) {
		try {
			System.out.println( "MainPanel openTable: " + table.getName() );
		
			TablePanel tp = new TablePanel( database, table );
			/*
			tp.setClosable( true );
			tp.setCollapsible( true );
			tp.setContainer( getElement() );
			tp.setConstrain( true );
			tp.addWindowListener(
					new WindowListener () {
						public void windowHide(WindowEvent we) {
						}
					});
					*/
		
			TabItem tabItem = new TabItem( database.getName() + " - " + table.getName() );
			tabItem.setId( database.getName() + " - " + table.getName() );
			tabItem.setClosable( true );
			tabItem.setLayout(new FitLayout());
			tabItem.add( (Widget) tp );
			//tabItem.setToolTip( tabName );
			add( tabItem );
			setSelection( tabItem );
	    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
