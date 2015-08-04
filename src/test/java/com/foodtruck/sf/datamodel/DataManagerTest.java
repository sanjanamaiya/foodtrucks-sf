package com.foodtruck.sf.datamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.foodtruck.sf.db.Truck;
import com.foodtruck.sf.db.TruckDAO;
import com.foodtruck.sf.exceptions.DataModelException;
import com.foodtruck.sf.exceptions.TruckServiceException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TruckDAO.class, DataManager.class})
public class DataManagerTest 
{
	private Truck createTruck(Integer truckId, double lat, double lng, String foodItems)	
	{
		Truck result = new Truck();
		result.setTruckId(truckId);
		result.setLatitude(lat);
		result.setLongitude(lng);
		result.setFoodItems(foodItems);
		return result;
	}
	
	/**
	 * test the method getAllTrucks in DataManager.
	 * @throws DataModelException
	 * @throws Exception
	 * @throws TruckServiceException
	 */
	@Test
	public void testGetAllTrucks() throws DataModelException, Exception, TruckServiceException 
	{
		ArrayList<Truck> testTruckArrayList = new ArrayList<Truck>();
		testTruckArrayList.add(createTruck(1, 45.66777D, -122.3435D, null));
		testTruckArrayList.add(createTruck(2, 46.66777D, -123.3435D, null));		
		
		TruckDAO mockTruckDAO = PowerMockito.mock(TruckDAO.class);
		PowerMockito.whenNew(TruckDAO.class).withNoArguments().thenReturn(mockTruckDAO);
		PowerMockito.when(mockTruckDAO.getTrucks()).thenReturn(testTruckArrayList);
		
		DataManager dm = DataManager.getInstance();
		dm.reload();
		ArrayList<Truck> result = dm.getAllTrucks();
		assertEquals(2, result.size());
		assertEquals(1, result.get(0).getTruckId());
		assertEquals(2, result.get(1).getTruckId());
	}

	/**
	 * Should throw TruckServiceException when trucks are not loaded
	 * @throws Exception 
	 * @throws DataModelException 
	 * @throws TruckServiceException 
	 * 
	 */
	@Test
	public void shouldThrowExceptionWhenNoTrucksLoaded() throws Exception, DataModelException, TruckServiceException 
	{
		boolean caughtException = false;
		ArrayList<Truck> testTruckArrayList = new ArrayList<Truck>();
		TruckDAO mockTruckDAO = PowerMockito.mock(TruckDAO.class);
		PowerMockito.whenNew(TruckDAO.class).withNoArguments().thenReturn(mockTruckDAO);
		PowerMockito.when(mockTruckDAO.getTrucks()).thenReturn(testTruckArrayList); // empty list
		
		DataManager dm = DataManager.getInstance();
		dm.reload();
		try
		{
			dm.getAllTrucks();
		}
		catch (TruckServiceException e)
		{
			if (e.getCode() == TruckServiceException.NO_ITEMS_FOUND_ERROR)
				caughtException = true;
		}
		
		assertEquals(true, caughtException);
	}
	
	/**
	 * Test the method getTrucksInRange
	 * @throws DataModelException
	 * @throws TruckServiceException
	 * @throws Exception
	 */
	@Test
	public void testGetTrucksInRange() throws DataModelException, TruckServiceException, Exception 
	{
		ArrayList<Truck> testTruckArrayList = new ArrayList<Truck>();
		testTruckArrayList.add(createTruck(1, 45.66777D, -122.3435D, null));
		testTruckArrayList.add(createTruck(2, 46.66777D, -123.3435D, null));		
		
		TruckDAO mockTruckDAO = PowerMockito.mock(TruckDAO.class);
		PowerMockito.whenNew(TruckDAO.class).withNoArguments().thenReturn(mockTruckDAO);
		PowerMockito.when(mockTruckDAO.getTrucks()).thenReturn(testTruckArrayList);
		
		ArrayList<Integer> mockMapMatrixOutput = new ArrayList<Integer>();
		mockMapMatrixOutput.add(2);
		
		MapMatrix mockMapMatrix = PowerMockito.mock(MapMatrix.class);
		PowerMockito.whenNew(MapMatrix.class).withAnyArguments().thenReturn(mockMapMatrix);
		PowerMockito.when(mockMapMatrix.getAllTrucksNearLatLong(
				Mockito.anyDouble(),
				Mockito.anyDouble(),	
				Mockito.anyDouble(), 
				Mockito.anyMapOf(Integer.class, Truck.class))).thenReturn(mockMapMatrixOutput);
		
		DataManager dm = DataManager.getInstance();
		dm.reload();
		ArrayList<Truck> result = dm.getTrucksInRange(45.5d, -122.5d, 1000.05d);
		assertEquals(1, result.size());
		assertEquals(2, result.get(0).getTruckId());
	}

	@Test
	public void testGetTrucksInRangeReturnsEmpty() throws DataModelException, TruckServiceException, Exception 
	{
		ArrayList<Truck> testTruckArrayList = new ArrayList<Truck>();
		testTruckArrayList.add(createTruck(1, 45.66777D, -122.3435D, null));
		testTruckArrayList.add(createTruck(2, 46.66777D, -123.3435D, null));		
		
		TruckDAO mockTruckDAO = PowerMockito.mock(TruckDAO.class);
		PowerMockito.whenNew(TruckDAO.class).withNoArguments().thenReturn(mockTruckDAO);
		PowerMockito.when(mockTruckDAO.getTrucks()).thenReturn(testTruckArrayList);
		
		ArrayList<Integer> mockMapMatrixOutput = new ArrayList<Integer>();
		
		MapMatrix mockMapMatrix = PowerMockito.mock(MapMatrix.class);
		PowerMockito.whenNew(MapMatrix.class).withAnyArguments().thenReturn(mockMapMatrix);
		PowerMockito.when(mockMapMatrix.getAllTrucksNearLatLong(
				Mockito.anyDouble(),
				Mockito.anyDouble(),	
				Mockito.anyDouble(), 
				Mockito.anyMapOf(Integer.class, Truck.class))).thenReturn(mockMapMatrixOutput);
		
		DataManager dm = DataManager.getInstance();
		dm.reload();
		ArrayList<Truck> result = dm.getTrucksInRange(45.5d, -122.5d, 1000.05d);
		assertEquals(0, result.size());
	}
	
	/**
	 * Test the method getAllFoodTruckItems
	 * @throws DataModelException
	 * @throws TruckServiceException
	 * @throws Exception
	 */
	@Test
	public void testGetAllFoodItems() throws TruckServiceException, DataModelException, Exception 
	{
		// Arrange
		ArrayList<Truck> testTruckArrayList = new ArrayList<Truck>();
		testTruckArrayList.add(createTruck(1, 45.66777D, -122.3435D, "Soup: bread: sandwich: burgers"));
		testTruckArrayList.add(createTruck(2, 46.66777D, -123.3435D, "Ice cream: candy: Gelato: cotton candy"));		
		
		TruckDAO mockTruckDAO = PowerMockito.mock(TruckDAO.class);
		PowerMockito.whenNew(TruckDAO.class).withNoArguments().thenReturn(mockTruckDAO);
		PowerMockito.when(mockTruckDAO.getTrucks()).thenReturn(testTruckArrayList);
		MapMatrix mockMapMatrix = PowerMockito.mock(MapMatrix.class);
		PowerMockito.whenNew(MapMatrix.class).withAnyArguments().thenReturn(mockMapMatrix);
		
		// Act
		DataManager dm = DataManager.getInstance();
		dm.reload();
		ArrayList<String> result = dm.getAllFoodTruckItems();
		
		// Assert
		assertEquals(8, result.size());
		assertEquals(true, result.contains("soup"));
		assertEquals(true, result.contains("cotton candy"));
	}
	
	/**
	 * Test the method gelFoodItemsForTruck
	 * @throws DataModelException
	 * @throws TruckServiceException
	 * @throws Exception
	 */
	@Test
	public void testGetFoodItemsForTruck() throws TruckServiceException, DataModelException, Exception 
	{
		// Arrange
		ArrayList<Truck> testTruckArrayList = new ArrayList<Truck>();
		testTruckArrayList.add(createTruck(1, 45.66777D, -122.3435D, "Soup: bread: sandwich: burgers"));
		testTruckArrayList.add(createTruck(2, 46.66777D, -123.3435D, "Ice cream: candy: Gelato: cotton candy"));		
		
		TruckDAO mockTruckDAO = PowerMockito.mock(TruckDAO.class);
		PowerMockito.whenNew(TruckDAO.class).withNoArguments().thenReturn(mockTruckDAO);
		PowerMockito.when(mockTruckDAO.getTrucks()).thenReturn(testTruckArrayList);
		MapMatrix mockMapMatrix = PowerMockito.mock(MapMatrix.class);
		PowerMockito.whenNew(MapMatrix.class).withAnyArguments().thenReturn(mockMapMatrix);
		
		// Act
		DataManager dm = DataManager.getInstance();
		dm.reload();
		String result = dm.getFoodItemsForTruck(1);
		
		// Assert
		assertEquals("Soup: bread: sandwich: burgers", result);
	}
}
