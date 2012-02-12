package com.dataspy.shared.model;

import java.io.Serializable;

public class DataSpyException extends Exception implements Serializable {
	private String detailMessage = "";

	public DataSpyException () {
	}
	
	public DataSpyException (String msg) {
		super( msg );
	}
	
	public DataSpyException (Exception e) {
		super( e );
		StringBuilder sb = new StringBuilder( e.getMessage() + "\n" );
		
		if (e.getCause() != null) {
			for (StackTraceElement ste : e.getCause().getStackTrace()) {
				sb.append( ste.toString() + "\n" );
			}
			sb.append( "--------------\n" );
		}
		for (StackTraceElement ste : e.getStackTrace()) {
			sb.append( ste.toString() + "\n" );
		}
		detailMessage = sb.toString();
	}
	
	public String getDetailMessage () {
		return detailMessage;
	}
}
