package com.foodtruck.sf.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.foodtruck.sf.db.Truck;

/**
 * 
 * A matrix representation of the map of San Francisco city.
 * 
 * Each cell in the matrix represents approximately a
 * region of size GRANULARITY_IN_METERS x GRANULARITY_IN_METERS.
 * 
 * This makes searching in small regions very efficient.
 */

public class MapMatrix 
{
	// We do not do projections, since all the points are within
	// a rectangle 12KM wide and 11KM long. 
	
	// Projections can be used to improve accuracy, though the code 
	// complexity entailed by them might not be worth the gain in 
	// accuracy.
	
	// Each point in our matrix will represent approximately 
	// a square of 100 meters by 100 meters.
	private final double GRANULARITY_IN_METERS = 100;  
	
	// The distance in degrees represented by each row
	private final double latStep;
	
	// The distance in degrees represented by each column
	private final double longStep;
	
	// Minimum latitude value from our food truck list
	private final double minLat;
	
	// Maximum latitude value from the food truck list
	private final double maxLat;
	
	// (minLat + maxLat)/2
	private final double midLat;
	
	// Minimum longitude value from our food truck list
	private final double minLong;
	
	// Maximum longitude value from our food truck list
	private final double maxLong;
	
	// (minLong + maxLong)/2
	private final double midLong;
	
	// Number of columns in the map matrix
	private final int numCols;
	
	// Number of rows in the map matrix
	private final int numRows;
	
	// maxLat - minLat
	private final double widthInDegrees;
	
	// widthInDegrees converted to meters
	private final double widthInMetres;
	
	// maxLong -  minLong
	private final double lenghtInDegrees;
	
	// lenghtInDegrees converted to meters
	private final double lengthInMetres; 
	
	// Since we are dealing with latitude longitude coordinates,
	// due to the shape of the earth, the width distance will decrease
	// from south to north between the same pair of longitudes.
	// Though this is small since size of san francisco is minute
	// compared to the earth, we still correct for this.
	private final double worstCaseWidthErrorInMatrix;
	
	/*
	 * Matrix where each point approximately represents a square
	 * GRANULARITY_IN_METERS x GRANULARITY_IN_METERS on the ground. This
	 * approximation comes from the fact that each of these squares represent a
	 * fixed longitude and latitude in width and length, which would result in a
	 * constant height, but not width, due to the shape of the earth.
	 */
	private TrucksInACell[][] matrix;
	
	
	/**
	 * Initialize the map matrix. Calculate the number of
	 * rows and columns required, and populate the matrix 
	 * with Truck Ids
	 * 
	 * @param allTrucks
	 */
	public MapMatrix(HashMap<Integer, Truck> allTrucks) 
	{
		// Initialize the boundaries.
		double tempMinLat = 180;
		double tempMaxLat = -180;
		double tempMinLong = 180;
		double tempMaxLong = -180;
		for (Truck t : allTrucks.values()) 
		{
			tempMinLat = Math.min(t.getLatitude(), tempMinLat);
			tempMaxLat = Math.max(t.getLatitude(), tempMaxLat);
			
			tempMinLong = Math.min(t.getLongitude(), tempMinLong);
			tempMaxLong = Math.max(t.getLongitude(), tempMaxLong);
		}
		minLat = tempMinLat;
		// this has been done to ensure that the right index is found for trucks on the edges of the map
		maxLat = tempMaxLat + (tempMaxLat - minLat)/1000;   
		minLong = tempMinLong;
		maxLong = tempMaxLong + (tempMaxLong - minLong)/1000;
		midLat = (minLat + maxLat)/2;
		midLong = (minLong + maxLong)/2;
		
		widthInDegrees = maxLong - minLong;
		widthInMetres =	GeoUtil.GetDistance(midLat, minLong, midLat, maxLong);
		numCols = (int)(widthInMetres / GRANULARITY_IN_METERS) + 1;
		longStep = widthInDegrees / numCols;
				
		lenghtInDegrees = maxLat -  minLat;
		lengthInMetres = GeoUtil.GetDistance(minLat, midLong, maxLat, midLong);
		numRows = (int)(lengthInMetres / GRANULARITY_IN_METERS) + 1;
		latStep = lenghtInDegrees / numRows;
				
		matrix = new TrucksInACell[numRows][numCols];
		
		for (Truck t : allTrucks.values()) 
		{
			ProcessTruck(t);
		}
		
		// Worst case width error is the difference in the width of at south most
		// point and north most point.
		worstCaseWidthErrorInMatrix = Math.abs(
				GeoUtil.GetDistance(minLat, minLong, minLat, maxLong) -
				GeoUtil.GetDistance(maxLat, minLong, maxLat, maxLong));
	}


	/**
	 * Place the truck in the matrix based on its 
	 * latitude and longitude
	 * 
	 * @param truck
	 */
	private void ProcessTruck(Truck truck) 
	{
		double relativeLat = truck.getLatitude() - minLat;
		double relativeLong = truck.getLongitude() - minLong;
		int row = (int)(relativeLat * numRows / lenghtInDegrees) ;
		int col = (int)(relativeLong * numCols / widthInDegrees);
		if (matrix[row][col] == null) 
		{
			matrix[row][col] = new TrucksInACell();
		}
		matrix[row][col].addTruck(truck.getTruckId());
	}
	

	/**
	 * 
	 * Gets all the trucks near the specified LatLong pair
	 * within the specified radius.
	 * 
	 * Internally, this searches for trucks inside a bounding square 
	 * box around the latlong using the matrix representation of the map.
	 * It later prunes the results with an accurate distance 
	 * check so as to keep the results precise and restrict to the 
	 * circular region rather than the square.
	 * 
	 * The complexity depends on the size of the region as well as
	 * the number of trucks found, but not on the total number of trucks
	 * in the map.
	 * 
	 * @param lat
	 * @param longitude
	 * @param radiusInMeters
	 * @param allTrucks
	 * @return
	 */
	public ArrayList<Integer> getAllTrucksNearLatLong(double lat,
			double longitude, double radiusInMeters,
			Map<Integer, Truck> allTrucks) 
	{		
		double relativeLat = lat - minLat;
		double relativeLong = longitude - minLong;
		// Latitude degrees can be inaccurate due to the shape of the earth, account for this by making
		// the coverage region bigger. Pruning takes care of accuracy of responses.
		double radiusInLatDegrees = radiusInMeters / lengthInMetres * lenghtInDegrees;
		double radiusInLongDegrees = (radiusInMeters + worstCaseWidthErrorInMatrix) / widthInMetres * widthInDegrees;
		
		int minRow = Math.max(0, (int)((relativeLat - radiusInLatDegrees) / latStep));
		int maxRow = Math.min(numRows - 1, 
							  (int)((relativeLat + radiusInLatDegrees) / latStep));
		
		int minColumn = Math.max(0 ,
								 (int)((relativeLong - radiusInLongDegrees) / longStep));
		int maxColumn = Math.min(numCols - 1 ,
								 (int)((relativeLong + radiusInLongDegrees) / longStep));
		
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = minRow; i <= maxRow; i++) {
			for (int j = minColumn; j <= maxColumn; j++) {
				if (matrix[i][j] != null)
				{
					result.addAll(matrix[i][j].getTrucks());
				}
			}
		}
		
		return PruneResult(result, lat, longitude, radiusInMeters, allTrucks);
	}
	
	/**
	 * What we need is the trucks within a given radius
	 * Prune the results obtained to eliminate any extra
	 * trucks that have been added due to our map matrix
	 * calculation. 
	 * 
	 * Also, return a sorted list of trucks in ascending 
	 * order of distance, with the closest trucks to a 
	 * given point listed first
	 * 
	 * @param potentials
	 * @param lat
	 * @param longitude
	 * @param radiusInMeters
	 * @param allTrucks
	 * @return
	 */
	protected ArrayList<Integer> PruneResult(
			Collection<Integer> potentials,
			double lat, 
			double longitude, 
			double radiusInMeters,
			Map<Integer, Truck> allTrucks)
	{
		TreeMap<Double, ArrayList<Integer>> sortedTrucks = new TreeMap<Double, ArrayList<Integer>>();
		
		for (Integer truckId : potentials) 
		{
			Truck tr = allTrucks.get(truckId);
			if (tr == null)
				continue;
			double distance = GeoUtil.GetDistance(lat, longitude, tr.getLatitude(), tr.getLongitude());
			if (distance <= radiusInMeters) 
			{
				if (sortedTrucks.containsKey(distance))
				{
					ArrayList<Integer> res = sortedTrucks.get(distance);
					res.add(truckId);
					sortedTrucks.put(distance, res);
				}
				else
				{
					ArrayList<Integer> res = new ArrayList<Integer>();
					res.add(truckId);
					sortedTrucks.put(distance, res);
				}
				//prunedResult.add(truckId);
			}
		}
		ArrayList<Integer> prunedResult = new ArrayList<Integer>();
		for (ArrayList<Integer> list : sortedTrucks.values())
		{
			for (Integer i : list)
			{
				prunedResult.add(i);
			}
		}
		return prunedResult;
	}
}
