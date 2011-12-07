package com.dataspy.client.mvc;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.RootPanel;
import com.dataspy.client.AppEvents;

public class AppView extends View {
	private Viewport viewport;
	private BorderLayout layout;
	
	public AppView(Controller controller) {
		super(controller);
	}

	protected void initialize() {
	}

	private void initUI() {
		viewport = new Viewport();
		layout = new BorderLayout();
		viewport.setLayout( layout );
	
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0));
		
		MainPanel mp = new MainPanel();
		viewport.add( mp, centerData);
		RootPanel.get().add( viewport );
	}

	protected void handleEvent(AppEvent event) {
		if (event.getType() == AppEvents.Init) {
			initUI();
		}
	}

}
