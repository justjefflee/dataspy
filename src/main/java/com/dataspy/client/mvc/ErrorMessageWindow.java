package com.dataspy.client.mvc;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.dataspy.shared.model.DataSpyException;

public class ErrorMessageWindow extends Window {
	private TextArea textArea;

	public ErrorMessageWindow () {
		setSize(550, 400);
		setPlain(true);
		setModal(true);
		setHeading("Error Message");
		setLayout(new FitLayout());
		
		add( createForm() );
		
		addButton(new Button("Ok",
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						hide();
					}
				}));
	}
	
	public void setThrowable (Throwable t) {
		/*
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement e : t.getStackTrace()) {
			sb.append( e.toString() + "\n" );
		}
		textArea.setValue( sb.toString() );
		*/
		if (t instanceof DataSpyException)
			textArea.setValue( ((DataSpyException)t).getDetailMessage() );
		else
			textArea.setValue( t.toString() );
	}
	
	private FormPanel createForm () {
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		fp.setScrollMode( Scroll.AUTO );
		fp.setAutoWidth( true );
		fp.setAutoHeight( true );
		fp.setStyleAttribute( "background-color", "#ffffff" );
		
		textArea = new TextArea();
		textArea.setFieldLabel("Detail");
		//textArea.setHeight( 200 );
		
		add( textArea );
		
		return fp;
	}
}
