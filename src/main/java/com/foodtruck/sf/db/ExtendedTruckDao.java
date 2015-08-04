package com.foodtruck.sf.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ExtendedTruckDao
{
	private Connection connection;
	private PreparedStatement statement;
	private String dbPropFile = "/opt/truckservice/db.properties";
	
	////////////////////////////////
	// db properties
	///////////////////////////////
	
	public final String COLUMN_APPROVED = "Approved";
	public final String COLUMN_BLOCKLOT = "BlockLot";
	public final String COLUMN_BLOCK = "Block";
	public final String COLUMN_LOT = "Lot";
	public final String COLUMN_CNN = "Cnn";
	public final String COLUMN_EXPIRATION_DATE = "ExpirationDate";
	public final String COLUMN_LOCATION_DESCR = "LocationDesc";
	public final String COLUMN_NOI_SENT = "NOISent";
	public final String COLUMN_PERMIT = "permit";
	public final String COLUMN_PRIOR_PERMIT = "PriorPermit";
	public final String COLUMN_RECEIVED = "Received";
	public final String COLUMN_SCHEDULE = "Schedule";
	public final String COLUMN_X = "X";
	public final String COLUMN_Y = "Y";
	public final String COLUMN_LOCATION = "Location";
	
	public ExtendedTruckDao(String propFile)
	{
		this.dbPropFile = propFile;
	}
	
	public ExtendedTruckDao()
	{
		
	}
	
	public ArrayList<ExtendedTruck> getTrucks() throws SQLException 
	{
        String query = "SELECT * FROM FoodTruck WHERE Status = 'APPROVED' AND Location IS NOT NULL";
        ResultSet rs = null;
        ExtendedTruck foodTruck = null;
        ArrayList<ExtendedTruck> allFoodTrucks = new ArrayList<ExtendedTruck>();
        
        try 
        {
            connection = MySqlConnectionFactory.getConnection(dbPropFile);
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                foodTruck = new ExtendedTruck();
                foodTruck.setTruckId(rs.getInt(TruckDAO.COLUMN_ID));
                foodTruck.setFacilityType(rs.getString(TruckDAO.COLUMN_FACILITY_TYPE));
                foodTruck.setStatus(rs.getString(TruckDAO.COLUMN_STATUS));
                foodTruck.setLatitude(rs.getDouble(TruckDAO.COLUMN_LATITUDE));
                foodTruck.setLongitude(rs.getDouble(TruckDAO.COLUMN_LONGITUDE));
                foodTruck.setApplicant(rs.getString(TruckDAO.COLUMN_APPLICANT));
                foodTruck.setAddress(rs.getString(TruckDAO.COLUMN_ADDRESS));
                
                foodTruck.setApproved(rs.getTimestamp(COLUMN_APPROVED));
                foodTruck.setBlock(rs.getString(COLUMN_BLOCK));
                foodTruck.setBlockLot(rs.getString(COLUMN_BLOCKLOT));
                foodTruck.setLot(rs.getString(COLUMN_LOT));
                foodTruck.setCnn(rs.getInt(COLUMN_CNN));
                foodTruck.setExpirationDate(rs.getTimestamp(COLUMN_EXPIRATION_DATE));
                foodTruck.setLocationDescr(rs.getString(COLUMN_LOCATION_DESCR));
                foodTruck.setLocationId(rs.getInt(TruckDAO.COLUMN_LOCATION_ID));
                foodTruck.setNoISent(rs.getTimestamp(COLUMN_NOI_SENT));
                foodTruck.setPermit(rs.getString(COLUMN_PERMIT));
                foodTruck.setPriorPermit(rs.getShort(COLUMN_PRIOR_PERMIT));
                foodTruck.setReceived(rs.getString(COLUMN_RECEIVED));
                foodTruck.setSchedule(rs.getString(COLUMN_SCHEDULE));
                foodTruck.setX(rs.getDouble(COLUMN_X));
                foodTruck.setY(rs.getDouble(COLUMN_Y));
                foodTruck.setFoodItems(rs.getString(TruckDAO.COLUMN_FOOD_ITEMS));
                String loc = rs.getString(COLUMN_LOCATION);
                loc.replace("(", "");
                loc.replace(")", "");
                String[] latlong = loc.split(",");
                
                Location location = new Location();
                location.setLatitude(Double.parseDouble(latlong[0].trim()));
                location.setLongitude(Double.parseDouble(latlong[1].trim()));
                foodTruck.setLocation(location);
                
                allFoodTrucks.add(foodTruck);
            }
        } 
        finally 
        {
            if (connection != null)
            {
            	connection.close();
            }
            if (statement != null)
            {
            	statement.close();
            }
            if (rs != null)
            {
            	rs.close();
            }
        }
        return allFoodTrucks;
    }
	
	public void addTruck(ExtendedTruck truck) throws SQLException 
	{
        String query = "INSERT INTO FoodTruck(LocationId, Applicant, "
        		+ "FacilityType, Cnn, LocationDesc, Address, BlockLot, "
        		+ "Block, Lot, Permit, Status, FoodItems, X, Y, Latitude, "
        		+ "Longitude, Schedule, NOISent, Approved, Received, "
        		+ "PriorPermit, ExpirationDate, Location) VALUES (";
        
        StringBuilder sb = new StringBuilder();
        sb.append(query);
        sb.append(truck.getLocationId() + ",");
        sb.append("\"" + truck.getApplicant() + "\"" + ",");
        sb.append("\"" + truck.getFacilityType() + "\"" + ",");
        sb.append(truck.getCnn() + ",");
        sb.append("\"" + truck.getLocationDescr() + "\"" + ",");
        sb.append("\"" + truck.getAddress() + "\"" + ",");
        sb.append("\"" + truck.getBlockLot() + "\"" + ",");
        sb.append("\"" + truck.getBlock() + "\"" + ",");
        sb.append("\"" + truck.getLot() + "\"" + ",");
        sb.append("\"" + truck.getPermit() + "\"" + ",");
        sb.append("\"" + truck.getStatus() + "\"" + ",");
        sb.append("\"" + truck.getFoodItems() + "\"" + ",");
        sb.append(truck.getX() + ",");
        sb.append(truck.getY() + ",");
        sb.append(truck.getLatitude() + ",");
        sb.append(truck.getLongitude() + ",");
        sb.append("\"" + truck.getSchedule() + "\"" + ",");
        if (truck.getNoISent() == null)
        {
        	sb.append("NULL,");
        }
        else
        {
        	sb.append("'" + truck.getNoISent() + "'" + ",");
        }
        sb.append("'" + truck.getApproved() + "'" + ",");
        sb.append("\"" + truck.getReceived() + "\"" + ",");
        sb.append(truck.getPriorPermit() + ",");
        sb.append("'" + truck.getExpirationDate() + "'" + ",");
        
        Location loc = truck.getLocation();
        if (loc != null)
        {
        	String locationString = "(" + loc.getLatitude() + "," + loc.getLongitude() + ")";
            sb.append("\"" + locationString + "\"");
            sb.append(");");
        }
        else
        {
        	sb.append("NULL");
        	sb.append(");");
        }
        
        
        try 
        {
            connection = MySqlConnectionFactory.getConnection(dbPropFile);
            statement = connection.prepareStatement(sb.toString());
            statement.executeUpdate();
        } 
        finally 
        {
            if (connection != null)
            {
            	connection.close();
            }
            if (statement != null)
            {
            	statement.close();
            }
        }
    }
	
	// /////////////////////////////////////////
	// Testing ...
	// /////////////////////////////////////////
	public static void main(String[] args) 
	{
		try 
		{
			ArrayList<ExtendedTruck> trucks = new ExtendedTruckDao().getTrucks();
			for (ExtendedTruck t : trucks) 
			{
				System.out.println(t.getTruckId());
			}
		} 
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
