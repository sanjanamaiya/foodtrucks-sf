package com.foodtruck.sf.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySqlConnectionFactory 
{
	
	private static MySqlConnectionFactory instance = new MySqlConnectionFactory();
    private String url = "jdbc:mysql://mydbinstance.cuvkf7bgzguk.us-west-2.rds.amazonaws.com:3306/FoodTruckDB";
    private String user = "testuser";  // the real user and password are set through a property file
    private String password = "blah";
    private String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    
	//private constructor
    private MySqlConnectionFactory() 
    {
        try 
        {
            Class.forName(DRIVER_CLASS);
        } 
        catch (ClassNotFoundException e) 
        {
            e.printStackTrace();
        }
    }
    
    private Connection createConnection(String propFile) throws SQLException  
    {
    	Properties prop = new Properties();
    	BufferedReader in = null;
        Connection connection = null;
        try 
        {
        	if (propFile != null)
        	{
        		try 
        		{
        			in = new BufferedReader(new FileReader(propFile));
        			if (in != null)
        			{
        				prop.load(in);
    					url = prop.getProperty("db.url");
    					user = prop.getProperty("db.user");
    			        password = prop.getProperty("db.passwd");
        			}
				} 
        		catch (IOException e) 
        		{
					// do nothing, will load default props
				}
        	}
        	
            connection = DriverManager.getConnection(url, user, password);
        } 
        catch (SQLException e) 
        {
            throw e;
        }
        finally
        {
        	if (in != null)
        	{
        		try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
        return connection;
    }   
     
    public static Connection getConnection(String propFile) throws SQLException 
    {
        return instance.createConnection(propFile);
    }
    
    public static Connection getConnection() throws SQLException 
    {
        return instance.createConnection(null);
    }

}
