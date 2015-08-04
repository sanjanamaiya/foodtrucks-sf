package com.foodtruck.sf.datamodel;

import java.util.TimerTask;

import com.foodtruck.sf.exceptions.TruckServiceException;

/**
 * Class which calls DataManager's reload every x hours
 * @author sanjana
 *
 */
public class DataReloadSchedule extends TimerTask
{
	@Override
	public void run() 
	{
		try 
		{
			DataManager.getInstance().reload();
		} 
		catch (TruckServiceException e) 
		{
		}
	}

}
