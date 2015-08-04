/**
 * 
 */
package com.foodtruck.sf.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.foodtruck.sf.db.Truck;
import com.foodtruck.sf.db.TruckDAO;
import com.foodtruck.sf.exceptions.DataModelException;
import com.foodtruck.sf.exceptions.TruckServiceException;
import com.foodtruck.sf.util.TruckServiceConstants;

/**
 * 
 * The class which acts as interface between the rest endpoints and
 * the database. The class holds the trucks in memory using a map
 * matrix for fast lookups of geo distance between 2 points
 * 
 * @author Sanjana Maiya
 *
 */
public class DataManager 
{
	
	private static DataManager dm;
	private MapMatrix mapMatrix;
	private FoodItemsHelper foodItems;
	private HashMap<Integer, Truck> allTrucks = new HashMap<Integer, Truck>();
	// Protects allTrucks and mapMatrix.
	private final ReadWriteLock readWriteLock; 
	
	/**
	 * DataManager is a singleton, use GetInstance to obtain the
	 * singleton instance.
	 * 
	 * @return
	 * @throws TruckServiceException
	 */
	public static DataManager getInstance() throws TruckServiceException
	{
		if (dm == null)
		{
			dm = new DataManager();
		}
		return dm;
	}

	private DataManager() throws TruckServiceException 
	{
		readWriteLock = new ReentrantReadWriteLock();
		// Reload the first time, so no requests are served during the first load time.
		reload();
		// Set up scheduler.
		//Timer time = new Timer(); 
		//DataReloadSchedule st = new DataReloadSchedule(); 
		//time.schedule(st, TruckServiceConstants.DATA_RELOAD_INTERVAL, TruckServiceConstants.DATA_RELOAD_INTERVAL); 
	}
		
	/**
	 * Reload the data manager.
	 * This creates a second set of data, which is later swapped
	 * with the original data once the loading of data is done.
	 * 
	 * This is done this way since data structures here do not support
	 * incremental updates and deletes. This increases the RAM requirement
	 * of the server since we have to store two copies of the data for a short 
	 * while, a potential optimization would be to
	 * support incremental updates and deletes in the data structures so
	 * this can be avoided.
	 * 
	 * @throws TruckServiceException
	 */
	public void reload() throws TruckServiceException 
	{
		TruckDAO truckDAO = new TruckDAO();
		HashMap<Integer, Truck> tempAllTrucks = new HashMap<Integer, Truck>();
		MapMatrix tempMapMatrix = null;
		FoodItemsHelper fITemp = new FoodItemsHelper(); 
		try 
		{
			for (Truck truck : truckDAO.getTrucks()) 
			{
				if (!tempAllTrucks.containsKey(truck.getTruckId())) 
				{
					tempAllTrucks.put(truck.getTruckId(), truck);
				}
				
				if (truck.getFoodItems() != null)
				{
					fITemp.addFoodItem(truck.getFoodItems());
				}
			}
			
			// Now add all trucks to MapMatrix for fast geo-retrieval.
			tempMapMatrix = new MapMatrix(tempAllTrucks);
		} 
		catch (DataModelException e) 
		{
			e.printStackTrace();
			throw new TruckServiceException(
					TruckServiceException.INITIALIZATION_ERROR, 
					TruckServiceException.INITIALIZATION_ERROR, 
					"Failed to initialize foodTruck service", 
					"InnerException : " + e.getMessage(), 
					TruckServiceConstants.ERROR_LINK);
		}

		readWriteLock.writeLock().lock();
		allTrucks = tempAllTrucks;
		mapMatrix = tempMapMatrix;
		foodItems = fITemp;
		readWriteLock.writeLock().unlock();
	}
	
	
	//////////////////////////////////////////////////////////////////
	//                                                              //
	//      Methods which serve the rest resource end points        // 
	//                                                              //
	//////////////////////////////////////////////////////////////////
	
	/**
	 * Gets all the trucks that are known.
	 * @return
	 * @throws TruckServiceException 
	 */
	public ArrayList<Truck> getAllTrucks() throws TruckServiceException 
	{
		readWriteLock.readLock().lock();
		ArrayList<Truck> truckList = new ArrayList<Truck>();
		try
		{
			if (allTrucks.size() == 0)
			{
				throw new TruckServiceException(
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						"No foodtrucks found", 
						"There probably has been an error loading the trucks into the data manager", 
						TruckServiceConstants.ERROR_LINK);
			}
			for (Integer id : allTrucks.keySet())
			{
				truckList.add(allTrucks.get(id));
			}
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
		return truckList;
	}
	
	/**
	 * Gets all the trucks that are within the specified radius 
	 * 
	 * @param latitude
	 * @param longitude
	 * @param range in meters
	 * @return
	 * @throws TruckServiceException
	 */
	public ArrayList<Truck> getTrucksInRange(
			double latitude, 
			double longitude, 
			double range) throws TruckServiceException 
	{
		readWriteLock.readLock().lock();
		ArrayList<Truck> truckList = new ArrayList<Truck>();
		try
		{
			if (allTrucks.size() == 0)
			{
				throw new TruckServiceException(
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						"No foodtrucks found", 
						"There probably has been an error loading the trucks into the data manager", 
						TruckServiceConstants.ERROR_LINK);
			}
			
			ArrayList<Integer> truckIds = mapMatrix.getAllTrucksNearLatLong(
					latitude, longitude, range, allTrucks);
			for (Integer id : truckIds)
			{
				truckList.add(allTrucks.get(id));
			}
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
		return truckList;
	}
	
	/**
	 * Get all food items available in all trucks
	 * @return
	 * @throws TruckServiceException
	 */
	public ArrayList<String> getAllFoodTruckItems() throws TruckServiceException
	{
		readWriteLock.readLock().lock();
		ArrayList<String> items = null;
		try
		{
			items = foodItems.getFoodItems();
			if (items == null)
			{
				throw new TruckServiceException(
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						"No food items found", 
						"There probably has been an error loading the food items into the data manager", 
						TruckServiceConstants.ERROR_LINK);
			}
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
		return items;
	}
	
	/**
	 * Get the food items listed by a particular truck
	 * @param truckId
	 * @return
	 * @throws TruckServiceException
	 */
	public String getFoodItemsForTruck(int truckId) throws TruckServiceException 
	{
		readWriteLock.readLock().lock();
		String foodList = "";
		try
		{
			if (allTrucks.size() == 0)
			{
				throw new TruckServiceException(
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						"The food truck does not exist", 
						"There probably has been an error loading the trucks into the data manager", 
						TruckServiceConstants.ERROR_LINK);
			}
			Truck truck = allTrucks.get(truckId);
			if (truck == null)
			{
				throw new TruckServiceException(
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						"Food truck does not exist", 
						"There probably has been an error loading the trucks into the data manager, "
						+ "or an invalid truck id has been specified", 
						TruckServiceConstants.ERROR_LINK);
			}
			
			foodList = truck.getFoodItems();
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
		return foodList;
	}
	
	/**
	 * Get all food items available in food
	 * trucks within a given distance
	 * 
	 * @param latitude
	 * @param longitude
	 * @param range in meters
	 * @return
	 * @throws TruckServiceException
	 */
	public ArrayList<String> getFoodItemsInRange(
			double latitude, 
			double longitude, 
			double range) throws TruckServiceException
	{
		readWriteLock.readLock().lock();
		ArrayList<String> foodItems = new ArrayList<String>();
		try
		{
			if (allTrucks.size() == 0)
			{
				throw new TruckServiceException(
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						TruckServiceException.NO_ITEMS_FOUND_ERROR, 
						"No food trucks found", 
						"There probably has been an error loading the trucks into the data manager", 
						TruckServiceConstants.ERROR_LINK);
			}
			
			ArrayList<Integer> truckIds = mapMatrix.getAllTrucksNearLatLong(
					latitude, longitude, range, allTrucks);
			for (Integer id : truckIds)
			{
				Truck t = allTrucks.get(id);
				String items = t.getFoodItems();
				if (items != null)
				{
					String[] itemArray = items.split(":");
					if (itemArray != null && itemArray.length > 0)
					{
						for (String fI : itemArray)
						{
							String modifiedStr = fI.toLowerCase().trim();
							if (!foodItems.contains(modifiedStr))
								foodItems.add(modifiedStr);
						}
					}
				}
			}
		}
		finally
		{
			readWriteLock.readLock().unlock();
		}
		return foodItems;
	}

	//////////////////////////////////////////////
	//                Testing                   //
	//////////////////////////////////////////////
	public static void main(String[] args) 
			throws TruckServiceException 
	{
		DataManager m = DataManager.getInstance();
		
		ArrayList<Truck> trucks = m.getAllTrucks();
		for (Truck t : trucks)
		{
			double distance = GeoUtil.GetDistance(37.7377, -122.37655, t.getLatitude(), t.getLongitude());
			System.out.println("Truck: " + t.getTruckId() + ". Distance : " + distance);
			
		}
		ArrayList<Truck> truckss = m.getTrucksInRange(37.7377F, -122.37655F, 2000);
		System.out.println(truckss.size());
		
		ArrayList<String> trucksss = m.getFoodItemsInRange(37.7377F, -122.37655F, 2000);
		System.out.println(trucksss.size());
		
		double distance = GeoUtil.GetDistance(37.7377, -122.37655, 37.73213, -122.37330);
		System.out.println(distance);
		
		String items = m.getFoodItemsForTruck(1);
		System.out.println(items);
	}
}

	
