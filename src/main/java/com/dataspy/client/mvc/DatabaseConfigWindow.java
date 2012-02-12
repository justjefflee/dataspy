package com.dataspy.client.mvc;

import com.dataspy.client.AppEvents;
import com.dataspy.shared.model.DatabaseConfig;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DatabaseConfigWindow extends Window {
	private TextField<String> tfParams = new TextField<String>();
	private DatabaseConfig databaseConfig;

	public void setDatabaseConfig (DatabaseConfig databaseConfig) {
		this.databaseConfig = databaseConfig;
		tfParams.setValue( databaseConfig.getParams() );
	}
	
	public DatabaseConfigWindow () {
		setHeading("Database Configuration" );
		setWidth(620);
		setHeight(150);
		setResizable(false);
		setModal( true );
		setLayout(new FitLayout());

		add( createForm() );
		
		final Button btnCancel = new Button("Cancel");
		btnCancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
		
		final Button btnSave = new Button("Save");
		btnSave.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				save();
				hide();
			}
		});
		
		final Button btnDelete = new Button("Delete");
		btnDelete.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
		
		addButton( btnDelete );
		addButton( btnSave );
		addButton( btnCancel );
	}
	
	private void save () {
		Util.getDataSpyService().saveDatabaseParams( databaseConfig.getKey(), tfParams.getValue(), new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(Void v) {
			}
		});
	}
	
	private FormPanel createForm () {
		tfParams.setFieldLabel("Params");
		tfParams.setAllowBlank(false);
		tfParams.setAutoWidth( true );
		tfParams.getMessages().setBlankText("Parameters is required");
		tfParams.setMaxLength( 1024 );
		
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		fp.setScrollMode( Scroll.AUTO );
		fp.setAutoWidth( true );
		fp.setAutoHeight( true );
		fp.setStyleAttribute( "background-color", "#ffffff" );
		
		FormData formData = new FormData("100%");  
		
		LayoutContainer main = new LayoutContainer();  
		main.setLayout(new ColumnLayout());  
		
		LayoutContainer left = new LayoutContainer();  
		left.setStyleAttribute("paddingRight", "10px");  
		FormLayout layout = new FormLayout();  
		layout.setLabelWidth( 80 );
		layout.setLabelAlign(LabelAlign.LEFT);  
		left.setLayout(layout);  
		
		LayoutContainer leftsub = new LayoutContainer();  
		leftsub.setLayout(new ColumnLayout());  
		LayoutContainer leftsub1 = new LayoutContainer();  
		leftsub1.setStyleAttribute("paddingRight", "10px");  
		layout = new FormLayout();  
		layout.setLabelWidth( 80 );
		layout.setLabelAlign(LabelAlign.LEFT);  
		leftsub1.setLayout( layout );  
		
		LayoutContainer leftsub2 = new LayoutContainer();  
		leftsub2.setStyleAttribute("paddingRight", "10px");  
		layout = new FormLayout();  
		layout.setLabelWidth( 70 );
		layout.setLabelAlign(LabelAlign.LEFT);  
		leftsub2.setLayout( layout );  
		
		leftsub.add(leftsub1, new ColumnData(.7));  
		leftsub.add(leftsub2, new ColumnData(.3));  
		
		left.add(tfParams, formData);  
		
		left.add( leftsub );
		
		main.add(left, new ColumnData(1.0));  
		fp.add(main, new FormData("100%"));  
		
		return fp;
	}

}
