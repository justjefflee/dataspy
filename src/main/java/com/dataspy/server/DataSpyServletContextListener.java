package com.dataspy.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DataSpyServletContextListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		DataSpyServiceImpl.initDatabase();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
