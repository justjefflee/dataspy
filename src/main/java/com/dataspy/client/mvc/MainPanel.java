package com.dataspy.client.mvc;

import com.dataspy.shared.model.Table;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

public class MainPanel extends ContentPanel {

	public MainPanel() {
		setHeading( "Data Spy");
		setLayout( new FlowLayout() );
		setScrollMode( Scroll.AUTO );
    }

	public void openTable (Table table) {
		try {
			System.out.println( "MainPanel openTable: " + table.getName() );
		
			TablePanel tp = new TablePanel( table );
	    	add( tp );
	    	decorate( tp );
	    	layout();
	    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void decorate (ContentPanel cp) {
    	Draggable d = new Draggable(cp, cp.getHeader());  
    	d.setContainer(this);  
    	d.setUseProxy(false);  
    	Resizable r = new Resizable( cp );
    	r.setDynamic( true );
	}

}
