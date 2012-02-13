package com.dataspy.client.mvc;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class GetParamsWindow extends Window {
	private List<String> params;
	private List<TextField> tfs;

	public GetParamsWindow (List<String> params) {
		this.params = params;
		setSize(550, 200);
		setPlain(true);
		setModal(true);
		setHeading("Enter Parameters");
		setLayout(new FitLayout());
		
		final FormPanel fp = createForm();
		
		add( fp );
		
		addButton(new Button("Cancel",
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						hide();
					}
				}));
		addButton(new Button("Execute",
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						if (fp.isValid()) {
							List<String> data = new ArrayList<String>();
							for (TextField tf : tfs) {
								data.add( (String) tf.getValue() );
							}
							execute( data );
							hide();
						}
					}
				}));
	}
	
	public void execute (List<String> data) {
	}
	
	private FormPanel createForm () {
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		fp.setScrollMode( Scroll.AUTO );
		fp.setAutoWidth( true );
		fp.setAutoHeight( true );
		fp.setStyleAttribute( "background-color", "#ffffff" );
		
		tfs = new ArrayList<TextField>();
		for (String param : params) {
			TextField tf = new TextField();
			tf.setFieldLabel( param.substring(1) );
			tf.setAllowBlank( false );
			fp.add( tf );
			tfs.add( tf );
		}
		return fp;
	}
}
