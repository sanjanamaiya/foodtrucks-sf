package com.foodtruck.sf.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.foodtruck.sf.exceptions.DataModelException;

public class TruckDAO 
{
	private Connection connection;
	private PreparedStatement statement;
	private String dbPropFile = "/opt/truckservice/db.properties";
	
	////////////////////////////////
	// db properties
	///////////////////////////////
	public static final String COLUMN_ID = "Id";
	public static final String COLUMN_FACILITY_TYPE = "FacilityType";
	public static final String COLUMN_STATUS = "FacilityType";
	public static final String COLUMN_LONGITUDE = "Longitude";
	public static final String COLUMN_LATITUDE = "Latitude";
	public static final String COLUMN_APPLICANT = "Applicant";
	public static final String COLUMN_ADDRESS = "Address";
	public static final String COLUMN_FOOD_ITEMS = "FoodItems";
	public static final String COLUMN_LOCATION_ID = "LocationId";
	
	
	public TruckDAO(String propFile)
	{
		this.dbPropFile = propFile;
	}
	
	public TruckDAO()
	{
		
	}
	
	public ArrayList<Truck> getTrucks() throws DataModelException 
	{
        String query = "SELECT * FROM FoodTruck WHERE Status = 'APPROVED' AND Location IS NOT NULL";
        ResultSet rs = null;
        Truck foodTruck = null;
        ArrayList<Truck> allFoodTrucks = new ArrayList<Truck>();
        
        try 
        {
            connection = MySqlConnectionFactory.getConnection(dbPropFile);
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                foodTruck = new Truck();
                foodTruck.setTruckId(rs.getInt(COLUMN_ID));
                foodTruck.setFacilityType(rs.getString(COLUMN_FACILITY_TYPE));
                foodTruck.setStatus(rs.getString(COLUMN_STATUS));
                foodTruck.setLatitude(rs.getDouble(COLUMN_LATITUDE));
                foodTruck.setLongitude(rs.getDouble(COLUMN_LONGITUDE));
                foodTruck.setApplicant(rs.getString(COLUMN_APPLICANT));
                foodTruck.setAddress(rs.getString(COLUMN_ADDRESS));
                foodTruck.setFoodItems(rs.getString(COLUMN_FOOD_ITEMS));
                foodTruck.setLocationId(rs.getInt(COLUMN_LOCATION_ID));
                allFoodTrucks.add(foodTruck);
            }
        }
        catch (SQLException e)
        {
        	throw new DataModelException("SQL error retrieving trucks from the database: " + e.getMessage());
        }
        finally 
        {
            if (connection != null)
            {
            	try 
            	{
					connection.close();
				} 
            	catch (SQLException e) 
            	{
					e.printStackTrace();
				}
            }
            if (statement != null)
            {
            	try 
            	{
					statement.close();
				} 
            	catch (SQLException e) 
            	{
					e.printStackTrace();
				}
            }
            if (rs != null)
            {
            	try 
            	{
					rs.close();
				} 
            	catch (SQLException e) 
            	{
					e.printStackTrace();
				}
            }
        }
        return allFoodTrucks;
    }
	
	
	///////////////////////////////////////////
	// Testing ...
	///////////////////////////////////////////
	public static void main(String[] args)
	{
		try 
		{
			ArrayList<Truck> trucks = new TruckDAO().getTrucks();
			for (Truck t : trucks)
			{
				System.out.println(t.getTruckId());
			}
		} 
		catch (DataModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
