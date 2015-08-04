/**
 * 
 */
package com.foodtruck.sf.datamodel;

import java.util.ArrayList;
import java.util.HashMap;

import com.foodtruck.sf.db.Truck;

/**
 * Holds the food items list from all trucks. Future scope of the 
 * class is to index the food items so that searching trucks by
 * food items can be done efficiently
 *  
 * @author sanjana
 *
 */
public class FoodItemsHelper 
{
	private ArrayList<String> foodItems;
	
	public void addFoodItem(String item)
	{
		if (foodItems == null)
		{
			foodItems = new ArrayList<String>();
		}
		
		String[] itemArray = item.split(":");
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
	
	public ArrayList<String> getFoodItems()
	{
		return foodItems;
	}
	public void initWithAllTrucks(HashMap<Integer, Truck> allTrucks) 
	{
		
	}

}
