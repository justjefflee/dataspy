package com.dataspy.client.mvc;

import com.dataspy.client.AppEvents;
import com.dataspy.shared.model.Database;
import com.dataspy.shared.model.Sql;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SqlPanel extends ContentPanel {
	private TablePanel tablePanel;
	private TextArea textArea;
	private Database database;
	private SimpleComboBox<String> sqlCombo;

	public SqlPanel (final TablePanel tablePanel, final Database database) {
		this.tablePanel = tablePanel;
		this.database = database;
		setLayout( new FitLayout() );
		setHeaderVisible( false );
		
		ToolBar toolbar = new ToolBar();
		
		Button addButton = new Button( "Add" );
		addButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				add();
			}
		});
		toolbar.add( addButton );
		
		Button saveButton = new Button( "Save" );
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				save();
			}
		});
		toolbar.add( saveButton );
		
		Button removeButton = new Button( "Remove" );
		removeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				remove();
			}
		});
		toolbar.add( removeButton );
		
		Button formatButton = new Button( "Format" );
		formatButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				format();
			}
		});
		toolbar.add( formatButton );
		
		toolbar.add( new FillToolItem() );
		
		sqlCombo = new SimpleComboBox<String>();
		sqlCombo.setWidth( 350 );
		sqlCombo.setEditable( false );
		sqlCombo.setTriggerAction(TriggerAction.ALL);

		for (Sql sql : database.getDatabaseConfig().getSqls()) {
			sqlCombo.add( sql.getSql().replaceAll( "\\\r\\\n", " " ) );
		}
		sqlCombo.addSelectionChangedListener(
    		new SelectionChangedListener<SimpleComboValue<String>>() {
	    		@Override
    			public void selectionChanged(final SelectionChangedEvent<SimpleComboValue<String>> sce) {
	    			System.out.println( "SqlPanel: sce " + sqlCombo.getSelectedIndex() );
	    			textArea.setValue( database.getDatabaseConfig().getSqls().get( sqlCombo.getSelectedIndex() ).getSql() );
	    		}
    		});

		toolbar.add( sqlCombo );
		
		Button executeButton = new Button( "Execute" );
		toolbar.add( executeButton );
		
		executeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				tablePanel.executeSql();
			}
		});
		
		setTopComponent( toolbar );
		
		textArea = new TextArea();
		add( textArea );
	}
	
	public String getSql () {
		return textArea.getValue();
	}
	
	private void save () {
		String key = database.getDatabaseConfig().getSqls().get( sqlCombo.getSelectedIndex() ).getKey();
		Util.getDataSpyService().saveDatabaseSql( database.getDatabaseConfig().getKey(), key, getSql(), new AsyncCallback<Sql>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(Sql sql) {
				sqlCombo.removeAll();
				for (Sql s : database.getDatabaseConfig().getSqls()) {
					if (sql.getKey().equals( s.getKey() ))
						s.setSql( sql.getSql() );
					sqlCombo.add( s.getSql().replaceAll( "\\\r\\\n", " " ) );
				}
			}
		});
	}
	
	private void add () {
		final String sql = getSql();
		if (sql == null)
			return;
		Util.getDataSpyService().saveDatabaseSql( database.getDatabaseConfig().getKey(), null, sql, new AsyncCallback<Sql>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(Sql sql) {
				try {
				sqlCombo.add( sql.getSql() );
				database.getDatabaseConfig().addSql( sql );
				} catch (Exception e) {
					e.printStackTrace();
					Dispatcher.forwardEvent( AppEvents.Error, e );
				}
			}
		});
	}
	private void remove () {
		final String sql = getSql();
		final String key = database.getDatabaseConfig().getSqls().get( sqlCombo.getSelectedIndex() ).getKey();
		Util.getDataSpyService().removeDatabaseSql( database.getDatabaseConfig().getKey(), key, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(Void v) {
				try {
					sqlCombo.clearSelections();
					sqlCombo.remove( sql );
					database.getDatabaseConfig().removeSql( key );
					textArea.setValue( null );
				} catch (Exception e) {
					e.printStackTrace();
					Dispatcher.forwardEvent( AppEvents.Error, e );
				}
			}
		});
	}
	private void format () {
		final String sql = getSql();
		Util.getDataSpyService().formatSql( sql, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent( AppEvents.Error, caught );
			}
			@Override
			public void onSuccess(String formattedSql) {
				try {
					textArea.setValue( formattedSql );
				} catch (Exception e) {
					e.printStackTrace();
					Dispatcher.forwardEvent( AppEvents.Error, e );
				}
			}
		});
	}
}
