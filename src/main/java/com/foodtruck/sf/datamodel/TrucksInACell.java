package com.foodtruck.sf.datamodel;

import java.util.ArrayList;

/**
 * Class to represent a list of trucks
 * in one cell.
 * 
 */
public class TrucksInACell 
{
	public TrucksInACell() 
	{
		trucks = new ArrayList<Integer>();
	}
	
	public void addTruck(Integer truckId) {
		trucks.add(truckId);
	}
 
	public ArrayList<Integer> getTrucks() {
		return trucks;
	}

	private ArrayList<Integer> trucks;
}
