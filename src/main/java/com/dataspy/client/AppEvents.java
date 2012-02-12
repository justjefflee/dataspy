package com.dataspy.client;

import com.extjs.gxt.ui.client.event.EventType;

public class AppEvents {

   	public static final EventType Init = new EventType();

	public static final EventType Error = new EventType();
	
	public static final EventType AddDatabase = new EventType();
	public static final EventType EditDatabase = new EventType();
	
	public static final EventType NewQuery = new EventType();
	
	public static final EventType OpenTable = new EventType();
	
	public static final EventType OpenParentFKData = new EventType();

}
