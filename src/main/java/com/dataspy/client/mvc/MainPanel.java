package com.dataspy.client.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dataspy.shared.model.RowData;
import com.dataspy.shared.model.Table;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class MainPanel extends ContentPanel {
	private Map<String,TablePanel> tablePanelMap = new HashMap<String,TablePanel>();

	public MainPanel() {
		setHeading( "Workspace");
		setLayout( new FlowLayout() );
		setScrollMode( Scroll.AUTO );
		setContextMenu( createPanelContextMenu() );
    }

	public void addData (String tableName, List<RowData> data) {
		TablePanel tp = tablePanelMap.get( tableName );
		tp.addData( data );
	}
	
	public void openTable (Table table) {
		try {
			System.out.println( "MainPanel openTable: " + table.getName() );
		
			TablePanel tp = tablePanelMap.get( table.getName() );
			
			if (tp == null) {
				System.out.println( "MainPanel create new window" );
				tp = new TablePanel( table );
				tp.setClosable( true );
				tp.setCollapsible( true );
				tp.setContainer( getElement() );
				tp.setConstrain( true );
				tp.addWindowListener(
						new WindowListener () {
							public void windowHide(WindowEvent we) {
								tablePanelMap.remove( we.getWindow().getHeading() );
							}
						});
				tablePanelMap.put( table.getName(), tp );
	    		add( tp );
	    		layout();
			}
			tp.show();
	    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Menu createPanelContextMenu () {
  		final Menu menu = new Menu();  
  		final MenuItem tileItem = new MenuItem( "Tile" );
  		final MenuItem gridItem = new MenuItem( "Grid" );
  		final MenuItem closeAllItem = new MenuItem( "Close All" );
  		menu.add( tileItem );
  		menu.add( gridItem );
  		menu.add( closeAllItem );
		tileItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				int i = 0;
				for (TablePanel tp : tablePanelMap.values()) {
					tp.setPosition( 30 * (i+1), 30 * (i+1) );
					i++;
				}
			}
		});
		gridItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				int width = getWidth();
				int height = getHeight();
				int count = tablePanelMap.size();
				int rows = (count+1) / 2;
				width = width / 2;
				height = height / rows;
				System.out.println( "rows " + rows + ", height " + height );
				
				int i = 0;
				for (TablePanel tp : tablePanelMap.values()) {
					int row = i / 2;
					System.out.println( "i " + i + ", row " + row );
					System.out.println( "width " + ((i % 2) * width) + " height " + (row*height) );
					tp.setPosition( (i % 2) * width, row*height );
					tp.setSize( width, height );
					i++;
				}
			}
		});
  		return menu;
	}


}
