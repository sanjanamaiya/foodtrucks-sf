package com.foodtruck.sf.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtruck.sf.datamodel.DataManager;
import com.foodtruck.sf.db.Truck;
import com.foodtruck.sf.exceptions.ErrorMessage;
import com.foodtruck.sf.exceptions.GenericExceptionMapper;
import com.foodtruck.sf.exceptions.TruckServiceException;
import com.foodtruck.sf.exceptions.TruckServiceExceptionMapper;
import com.foodtruck.sf.service.TruckService;
import com.foodtruck.sf.util.TruckServiceConstants;

/**
 * Unit tests for resource foodtruckservice.
 * 
 * Using junit, powermock and JerseyTest with grizzly container to test the
 * resource end points and response in case of errors
 * 
 * @author sanjana
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DataManager.class)
public class TruckServiceTest extends JerseyTest
{
	
	/****************************************************
	 * Configure all parameters required for setting up
	 * JerseyTest. Registering provider.packages which
	 * has been set up for this project's web.xml
	 * 
	 ****************************************************/
	@Override
    protected Application configure() 
	{
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(TruckService.class);
		classes.add(TruckServiceExceptionMapper.class);
		classes.add(GenericExceptionMapper.class);
        return new ResourceConfig(classes);
    }
	

	/************************************************************
	 * Check if a custom json response is displayed in case
	 * a checked exception is thrown during execution of 
	 * the rest API. For this test, TruckServiceExceptionMapper
	 * has been added to the list of resources in configure()
	 * 
	 ************************************************************/
	@Test
	public void shouldReturnCustomResponseForCheckedException() 
	{
		//Arrange
		DataManager mockDm = Mockito.mock(DataManager.class);
		PowerMockito.mockStatic(DataManager.class);
		try 
		{
			PowerMockito.when(DataManager.getInstance()).thenReturn(mockDm);
			PowerMockito.when(mockDm.getAllTrucks()).
					thenThrow(new TruckServiceException(
							1, 
							2, 
							"test message",
							"sql exception", 
							"http://myserver.com/details"));
		} 
		catch (TruckServiceException e1) 
		{
			e1.printStackTrace();
		}
		
		// Act
		Response response = null;
		try
		{
			 target("sf/trucks").request().get(String.class);
		}
		catch (WebApplicationException e)
		{
			response = e.getResponse();
		}
		
		//Assert
		ByteArrayInputStream in = (ByteArrayInputStream) response.getEntity();
		int n = in.available();
		byte[] bytes = new byte[n];
		in.read(bytes, 0, n);
		String res = new String(bytes, StandardCharsets.UTF_8);
		
		ObjectMapper mapper = new ObjectMapper();
		ErrorMessage message = null;
		try 
		{
			message = mapper.readValue(res,  ErrorMessage.class);
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}
		
		if (message != null)
		{
			assertEquals(1, message.getStatus());
			assertEquals(2, message.getCode());
			assertEquals("test message", message.getMessage());
			assertEquals("sql exception", message.getDetailedMessage());
			assertEquals("http://myserver.com/details", message.getLink());
		}
		else
		{
			fail("Message null, expected exeption not thrown");
		}
    }
	
	
	/***************************************************************
	 * Check if a custom json response is displayed in case
	 * an unchecked exception is thrown during execution of 
	 * the rest API. For this test, GenericExceptionMapper
	 * has been added to the list of resources in configure()
	 * 
	 * @throws TruckServiceException
	 ***************************************************************/
	@Test
	public void shouldReturnCustomResponseForUncheckedException() throws TruckServiceException 
	{
		//Arrange
		DataManager mockDm = Mockito.mock(DataManager.class);
		PowerMockito.mockStatic(DataManager.class);
		try 
		{
			// Throw any unchecked exception, ex: NullPointerException 
			PowerMockito.when(DataManager.getInstance()).thenReturn(mockDm);
			PowerMockito.when(mockDm.getAllTrucks()).
					thenThrow(new NullPointerException());
		} 
		catch (NullPointerException e1) 
		{
			e1.printStackTrace();
		}
		
		// Act
		Response response = null;
		try
		{
			 target("sf/trucks").request().get(String.class);
		}
		catch (WebApplicationException e)
		{
			response = e.getResponse();
		}
		
		// Assert
		ByteArrayInputStream in = (ByteArrayInputStream) response.getEntity();
		int available = in.available();
		byte[] bytes = new byte[available];
		in.read(bytes, 0, available);
		String res = new String(bytes, StandardCharsets.UTF_8);
		
		ObjectMapper mapper = new ObjectMapper();
		ErrorMessage message = null;
		try 
		{
			message = mapper.readValue(res,  ErrorMessage.class);
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}
		
		if (message != null)
		{
			// the rest of the parameters are exception dependent, these checks are
			// sufficient to determine that a custom response has been returned
			assertEquals(TruckServiceConstants.GENERIC_APP_ERROR_CODE, message.getCode());
			assertEquals(TruckServiceConstants.ERROR_LINK, message.getLink());
		}
		else
		{
			assertFalse(true);
		}
    }
	
	
	/********************************************************************
	 * 
	 * The web service call /v1/sf/fooditems should return all foodItems
	 * @throws TruckServiceException
	 *
	 ********************************************************************/
	@Test
    public void getFoodItemsShouldReturnAllItems() throws TruckServiceException 
	{
		// Arrange
		String expectedJsonResponse = "[\"Jerk chicken\",\"curry chicken\",\"curry goat\"]";
		ArrayList<String> foodItems = new ArrayList<String>();
		foodItems.add("Jerk chicken");
		foodItems.add("curry chicken");
		foodItems.add("curry goat");
		
		DataManager mockDm = Mockito.mock(DataManager.class);
		PowerMockito.mockStatic(DataManager.class);
		PowerMockito.when(DataManager.getInstance()).thenReturn(mockDm);
		PowerMockito.when(mockDm.getAllFoodTruckItems()).thenReturn(foodItems);

		// Act
        final String response = target("sf/fooditems").request().get(String.class);
        
        // Assert
        assertEquals(expectedJsonResponse, response);
    }
	
	
	/*******************************************************************
	 * 
	 * The web service call /v1/sf/fooditems should return all foodItems
	 * @throws TruckServiceException
	 * 
	 *******************************************************************/
	@Test
    public void getFoodItemsPerTruckShouldReturnFoodItemsForTruck() throws TruckServiceException 
	{
		// Arrange
		String expectedJsonResponse = "Jerk chicken: curry chicken : curry goat";
		String foodItems = "Jerk chicken: curry chicken : curry goat";
		DataManager mockDm = Mockito.mock(DataManager.class);
		PowerMockito.mockStatic(DataManager.class);
		PowerMockito.when(DataManager.getInstance()).thenReturn(mockDm);
		PowerMockito.when(mockDm.getFoodItemsForTruck(1)).thenReturn(foodItems);

		// Act
        final String response = target("sf/fooditems/truck/1").request().get(String.class);
        
        // Assert
        assertEquals(expectedJsonResponse, response);
    }
	
	
	/*******************************************************************
	 * 
	 * The web service call /v1/sf/fooditems/inrange should return all 
	 * foodItems within a given range of distance
	 * @throws TruckServiceException
	 * 
	 *******************************************************************/
	@Test
    public void getFoodItemsInRangeShouldReturnFoodItemsForTrucksInrange() throws TruckServiceException 
	{
		// Arrange
		String expectedJsonResponse = "[\"Jerk chicken\",\"curry goat\"]";
		ArrayList<String> food = new ArrayList<String>();
		food.add("Jerk chicken");
		food.add("curry goat");
		
		DataManager mockDm = Mockito.mock(DataManager.class);
		PowerMockito.mockStatic(DataManager.class);
		PowerMockito.when(DataManager.getInstance()).thenReturn(mockDm);
		PowerMockito.when(mockDm.getFoodItemsInRange(
				Matchers.eq(12.3), 
				Matchers.eq(-122.3), 
				Mockito.anyDouble())).thenReturn(food);

		// Act
        final String response = target("sf/fooditems/inrange").
        		queryParam("lat", "12.3").
        		queryParam("long", "-122.3").
        		queryParam("distance", "1.0").
        		request().get(String.class);
        
        // Assert
        assertEquals(expectedJsonResponse, response);
    }
	
	
	/****************************************************************
	 * 
	 * The web service call sf/trucks should return all trucks
	 * @throws TruckServiceException
	 * 
	 ****************************************************************/
	@Test
    public void getTrucksShouldReturnAllTrucks() throws TruckServiceException 
	{
		// Arrange
		String expectedJsonResponse = "[{\"truckId\":1,\"status\":\"Pending\",\"latitude"
				+ "\":36.119998931884766,\"longitude\":-130.4499969482422,\"applicant\":"
				+ "\"Jettison\",\"address\":\"46838 Wolfe Street\",\"facilitytype\":"
				+ "\"Trucks\",\"fooditems\":\"Jerk chicken: curry chicken : curry goat\""
				+ ",\"objectid\":12345},{\"truckId\":2,\"status\":\"Approved\",\"latitude\""
				+ ":34.119998931884766,\"longitude\":-122.44999694824219,\"applicant\":"
				+ "\"Jackson\",\"address\":\"34683 Green Aven\",\"facilitytype\":\"Trucks\","
				+ "\"fooditems\":null,\"objectid\":4567}]";
		
		ArrayList<Truck> trucks = new ArrayList<Truck>();
		String foodItems = "Jerk chicken: curry chicken : curry goat";
		trucks.add(new Truck(1, "Trucks", "Pending", 36.12F, -130.45F, 
				"Jettison", "46838 Wolfe Street", foodItems, 12345));
		trucks.add(new Truck(2, "Trucks", "Approved", 34.12F, -122.45F, 
				"Jackson", "34683 Green Aven", null, 4567));
		
		/**
		 * The DataManager is a singleton, use Powermock to get a mock dataManager 
		 * object. The entire datamodel layer of the code is mocked, and we 
		 * test only the rest endpoint code through a grizzly container
		 */
		
		DataManager mockDm = Mockito.mock(DataManager.class);
		PowerMockito.mockStatic(DataManager.class);
		PowerMockito.when(DataManager.getInstance()).thenReturn(mockDm);
		PowerMockito.when(mockDm.getAllTrucks()).thenReturn(trucks);

		// Act
        final String response = target("sf/trucks").request().get(String.class);
        
        // Assert
        assertEquals(expectedJsonResponse, response);
    }
	
	
	/************************************************************
	 * 
	 * The web service call /sf/trucks should return all trucks
	 * @throws TruckServiceException
	 *
	 ************************************************************/
	@Test
    public void getTrucksInRangeShouldReturnAllTrucksinRadius() throws TruckServiceException 
	{
		// Arrange
		String expectedJsonResponse = "[{\"truckId\":1,\"status\":\"Pending\",\"latitude"
				+ "\":36.119998931884766,\"longitude\":-130.4499969482422,\"applicant\":"
				+ "\"Jettison\",\"address\":\"46838 Wolfe Street\",\"facilitytype\":"
				+ "\"Trucks\",\"fooditems\":\"Jerk chicken: curry chicken : curry goat\""
				+ ",\"objectid\":12345},{\"truckId\":2,\"status\":\"Approved\",\"latitude\""
				+ ":34.119998931884766,\"longitude\":-122.44999694824219,\"applicant\":"
				+ "\"Jackson\",\"address\":\"34683 Green Aven\",\"facilitytype\":\"Trucks\","
				+ "\"fooditems\":null,\"objectid\":4567}]";
		
		ArrayList<Truck> trucks = new ArrayList<Truck>();
		String foodItems = "Jerk chicken: curry chicken : curry goat";
		trucks.add(new Truck(1, "Trucks", "Pending", 36.12F, -130.45F, 
				"Jettison", "46838 Wolfe Street", foodItems, 12345));
		trucks.add(new Truck(2, "Trucks", "Approved", 34.12F, -122.45F, 
				"Jackson", "34683 Green Aven", null, 4567));
		
		/**
		 * The DataManager is a singleton, use Powermock to get a mock dataManager 
		 * object. The entire datamodel layer of the code is mocked, and we 
		 * test only the rest endpoint code through a grizzly container
		 */
		
		DataManager mockDm = Mockito.mock(DataManager.class);
		PowerMockito.mockStatic(DataManager.class);
		PowerMockito.when(DataManager.getInstance()).thenReturn(mockDm);
		PowerMockito.when(mockDm.getTrucksInRange(
				Matchers.eq(12.3), 
				Matchers.eq(-122.3), 
				Mockito.anyDouble())).thenReturn(trucks);

		// Act
		final String response = target("sf/trucks/inrange")
				.queryParam("lat", "12.3")
				.queryParam("long", "-122.3")
				.queryParam("distance", "1.0")
				.request().get(String.class);
        
        // Assert
        assertEquals(expectedJsonResponse, response);
    }
}
