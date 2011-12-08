package com.dataspy.client.mvc;

import com.dataspy.client.AppEvents;
import com.dataspy.shared.model.Table;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.RootPanel;

public class AppView extends View {
	private Viewport viewport;
	private BorderLayout layout;
	private MainPanel mainPanel;
	
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
		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 250);
		westData.setSplit(true);
		westData.setCollapsible(true);
		westData.setMargins(new Margins(0, 2, 0, 0));

		viewport.add( new NavigationPanel(), westData);

		mainPanel = new MainPanel();
		viewport.add( mainPanel, centerData);
		RootPanel.get().add( viewport );
	}
	
	public void openTable (Table table) {
		mainPanel.openTable( table );
	}

	protected void handleEvent(AppEvent event) {
		if (event.getType() == AppEvents.Init) {
			initUI();
		}
	}

}
