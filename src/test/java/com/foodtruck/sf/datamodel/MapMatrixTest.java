package com.foodtruck.sf.datamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.foodtruck.sf.db.Truck;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GeoUtil.class})
public class MapMatrixTest 
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
	 * From the candidate list, prune the results and return those trucks
	 * are within the given radius. Also, the trucks should be returned 
	 * in ascending order of distance, with the closest trucks to a 
	 * given point listed first
	 */
	@Test
	public void shouldPruneResultsBasedOnDistanceAndReturnSortedList() 
	{
		// Arrange
		double testLat = 45.66000D;
		double testLon = 45.66000D;
		HashMap<Integer, Truck> testTruckList = new HashMap<Integer, Truck>();
		testTruckList.put(1, createTruck(1, 45.66777D, -122.3435D, null));
		testTruckList.put(4, createTruck(4, 46.66778D, -123.3435D, null));
		testTruckList.put(2, createTruck(2, 46.66779D, -123.3435D, null));
		testTruckList.put(3, createTruck(3, 46.66780D, -123.3435D, null));
		testTruckList.put(5, createTruck(5, 46.66781D, -123.3436D, null));
		
		ArrayList<Integer> potentials = new ArrayList<Integer>();
		potentials.add(1);
		potentials.add(2);
		potentials.add(3);
		potentials.add(4);
		potentials.add(5);
		
		PowerMockito.mockStatic(GeoUtil.class);
		PowerMockito.when(GeoUtil.GetDistance(testLat, testLon, 45.66777, -122.3435D)).thenReturn(101.0D);
		PowerMockito.when(GeoUtil.GetDistance(testLat, testLon, 46.66778D, -123.3435D)).thenReturn(99.9D);
		PowerMockito.when(GeoUtil.GetDistance(testLat, testLon, 46.66779D, -123.3435D)).thenReturn(100.0D);
		PowerMockito.when(GeoUtil.GetDistance(testLat, testLon, 46.66780D, -123.3435D)).thenReturn(150.0D);
		PowerMockito.when(GeoUtil.GetDistance(testLat, testLon, 46.66781D, -123.3436D)).thenReturn(40.0D);
		
		// Act
		MapMatrix mm = new MapMatrix(testTruckList);
		ArrayList<Integer> prunedRes = mm.PruneResult(potentials, testLat, testLon, 100.00D, testTruckList);
		
		//Assert
		assertEquals(3, prunedRes.size());
		assertEquals(5, (int) prunedRes.get(0));
		assertEquals(4, (int) prunedRes.get(1));
		assertEquals(2, (int) prunedRes.get(2));
	}
	
	/**
	 * Select boundary conditions and test the trucks retrieved from the
	 * center. This test defines a range which is very close to all
	 * the trucks, but the trucks are just outside the radius specified
	 */
	@Test
	public void shouldNotReturnTrucksOutsideSpecifiedRange() 
	{
		// Arrange
		// Distance between (midLat, midLong) and (minLat, minLong) : 7.504 km, 4.664 miles
		// Distance between (minLat, minLong) and (maxLat, maxLong) : 15.005 km, 9.326 miles
		double testLat = 37.7585593762; // midLat
		double testLon = -122.431686893;  // midLong
		HashMap<Integer, Truck> testTruckList = getBoundaryTrucks();
		
		// Act
		MapMatrix mm = new MapMatrix(testTruckList);
		ArrayList<Integer> prunedRes = mm.getAllTrucksNearLatLong(testLat, testLon, 7490.00, testTruckList);
		
		//Assert
		assertEquals(0, prunedRes.size());
	}
	
	/**
	 * Return trucks which are within the range specified. In this test, 
	 * the boundary trucks are outside the range
	 */
	@Test
	public void shouldReturnTrucksInSpecifiedRange() 
	{
		// Arrange
		// Distance between (midLat, midLong) and (minLat, minLong) : 7.504 km, 4.664 miles
		// Distance between (minLat, minLong) and (maxLat, maxLong) : 15.005 km, 9.326 miles
		double testLat = 37.7585593762; // midLat
		double testLon = -122.431686893;  // midLong
		HashMap<Integer, Truck> testTruckList = getBoundaryTrucks();
		testTruckList.put(5, createTruck(5, 37.7377432884455, -122.3900712081, null));
		
		// Act
		MapMatrix mm = new MapMatrix(testTruckList);
		ArrayList<Integer> prunedRes = mm.getAllTrucksNearLatLong(testLat, testLon, 7490.00, testTruckList);
		
		//Assert
		assertEquals(1, prunedRes.size());
		assertEquals(5, (int) prunedRes.get(0));
	}
	
	/**
	 * Specify range such that all trucks are returned
	 */
	@Test
	public void shouldReturnTrucksInSpecifiedRangeWithBoundaryConditions() 
	{
		// Arrange
		// Distance between (midLat, midLong) and (minLat, minLong) : 7.496 km, ~4.664 miles
		// Distance between (minLat, minLong) and (maxLat, maxLong) : ~15.005 km, ~9.326 miles
		double testLat = 37.7585593762; // midLat
		double testLon = -122.431686893;  // midLong
		HashMap<Integer, Truck> testTruckList = getBoundaryTrucks();
		testTruckList.put(5, createTruck(5, 37.7377432884455, -122.3900712081, null));
		
		// Act
		MapMatrix mm = new MapMatrix(testTruckList);
		ArrayList<Integer> prunedRes = mm.getAllTrucksNearLatLong(testLat, testLon, 7499.00, testTruckList);
		
		//Assert
		assertEquals(5, prunedRes.size());
	}
	
	/**
	 * The longitudes are not parallel, leading to differences in distances
	 * between points. This case tests whether the trucks are retrieved 
	 * taking this difference into account
	 */
	@Test
	public void shouldReturnTrucksInRangeConsideringLongDifferences() 
	{
		// Arrange
		// Distance between (midLat, midLong) and (minLat, minLong) : 7.496 km, ~4.664 miles
		// Distance between (minLat, minLong) and (maxLat, maxLong) : ~15.005 km, ~9.326 miles
		// Distance between midpoint and Truck 1/4 : 7496.69073502893
		// Distance between midpoint and Truck 2/3 : 7494.356981125472
		double testLat = 37.7585593762; // midLat
		double testLon = -122.431686893;  // midLong
		HashMap<Integer, Truck> testTruckList = getBoundaryTrucks();
		testTruckList.put(5, createTruck(5, 37.7377432884455, -122.3900712081, null));
		
		// Act
		MapMatrix mm = new MapMatrix(testTruckList);
		ArrayList<Integer> prunedRes = mm.getAllTrucksNearLatLong(testLat, testLon, 7495.00, testTruckList);
		
		//Assert
		assertEquals(3, prunedRes.size());
		assertEquals(true, prunedRes.contains(2));
		assertEquals(true, prunedRes.contains(3));
		assertEquals(true, prunedRes.contains(5));
	}
	
	/**
	 * Specify a point outside the map matrix/boundary trucks
	 * and test if trucks are retrieved correctly
	 */
	@Test
	public void shouldReturnTrucksInRangeWhenPointOutsideMapMatrix() 
	{
		// Arrange
		double testLat = 37.9085593762; // 
		double testLon = -122.501686893;  // 
		HashMap<Integer, Truck> testTruckList = getBoundaryTrucks();
		testTruckList.put(5, createTruck(5, 37.7377432884455, -122.3900712081, null));
		
		// Act
		MapMatrix mm = new MapMatrix(testTruckList);
		ArrayList<Integer> prunedRes = mm.getAllTrucksNearLatLong(testLat, testLon, 13000.00, testTruckList);
		
		//Assert
		assertEquals(1, prunedRes.size());
		assertEquals(true, prunedRes.contains(3));
	}

	private HashMap<Integer, Truck> getBoundaryTrucks() {
		HashMap<Integer, Truck> testTruckList = new HashMap<Integer, Truck>();
		testTruckList.put(1, createTruck(1, 37.7093754640014, -122.373302577485, null));  // min, min
		testTruckList.put(2, createTruck(2, 37.8077432884455, -122.373302577485, null));  // max, min
		testTruckList.put(3, createTruck(3, 37.8077432884455, -122.4900712081, null));    // max, max
		testTruckList.put(4, createTruck(4, 37.7093754640014, -122.4900712081, null));    // min, max
		return testTruckList;
	}

}
