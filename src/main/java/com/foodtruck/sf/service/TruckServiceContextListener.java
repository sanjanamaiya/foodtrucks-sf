package com.foodtruck.sf.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.foodtruck.sf.datamodel.DataManager;
import com.foodtruck.sf.exceptions.TruckServiceException;

public class TruckServiceContextListener implements ServletContextListener
{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) 
	{
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) 
	{
		System.out.println("Setting up data manager...");
		try 
		{
			// loads data into data manager from db
			DataManager.getInstance();
		} 
		catch (TruckServiceException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
