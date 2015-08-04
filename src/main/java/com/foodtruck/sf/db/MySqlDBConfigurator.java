package com.foodtruck.sf.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MySqlDBConfigurator
{
	private static final String HOST_PATH = "http://data.sfgov.org/resource/rqzj-sfat.json?Status=APPROVED";
	private static final String TEMP_JSON_FILE = "foodTrucks.json";
	
	
	private static void downloadJson()
	{
		ReadableByteChannel rbc = null;
		FileOutputStream fos = null;
		try
		{
			URL website = new URL(HOST_PATH);
			rbc = Channels.newChannel(website.openStream());
			fos = new FileOutputStream(TEMP_JSON_FILE);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
		catch (IOException e)
		{
			if (rbc != null)
			{
				try 
				{
					rbc.close();
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
			if (fos != null)
			{
				try 
				{
					fos.close();
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
		}
	}
	
	private static void writeJsonToDB(String propFile)
	{
		ObjectMapper mapper = new ObjectMapper();
		try 
		{
			ExtendedTruck[] trucks = mapper.readValue(new File(TEMP_JSON_FILE), ExtendedTruck[].class);
			ExtendedTruckDao dao = new ExtendedTruckDao(propFile);
			for (ExtendedTruck truck : trucks)
			{
				try 
				{
					dao.addTruck(truck);
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	public static void main(String args[])
	{
		downloadJson();
		writeJsonToDB(args[0]); // pass db prop file
	}

}
