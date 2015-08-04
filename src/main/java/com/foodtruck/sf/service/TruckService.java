package com.foodtruck.sf.service;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.foodtruck.sf.datamodel.DataManager;
import com.foodtruck.sf.db.Truck;
import com.foodtruck.sf.exceptions.TruckServiceException;
import com.foodtruck.sf.util.TruckServiceConstants;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * The resource class for food truck service
 * The url for this service is /foodtruckservice/v1/sf
 * 
 * @author sanjana
 *
 */

@Path("sf")
@Api(value="sf", description="San Francisco food truck service")
public class TruckService 
{

	/**
	 * Get all food trucks that are listed. This 
	 * does *not* return trucks with expired permits
	 * URL : /foodtruckservice/sf/trucks
	 * 
	 * @return
	 * @throws TruckServiceException 
	 */
	@Path("/trucks")
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation(value = "Get all food trucks sorted by increasing distance from location", 
	notes = "Returns all food trucks listed in the SF area",
	response = Truck.class, 
	responseContainer = "List")
	
	public ArrayList<Truck> getTrucks() throws TruckServiceException
	{
		DataManager dm = DataManager.getInstance();
		return dm.getAllTrucks();
	}
	
	
	/**
	 * Get food trucks which are around the given latitude
	 * and longitude, within the range specified
	 * int in meters
	 * URL : /foodtruckservice/v1/sf/trucks/inrange?lat=12.3&long=-122.3&distance=1.0
	 * 
	 * @param latitude
	 * @param longitude
	 * @param range : in miles
	 * @return
	 * @throws TruckServiceException 
	 */
	@GET
	@Path("/trucks/inrange")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@ApiOperation(value = "Get trucks in a given range",
    	notes = "Specify latitude, longitude and range in miles", 
	 response = Truck.class, 
	 responseContainer = "List")
	
	public ArrayList<Truck> getTrucksInRange(
			 @ApiParam(value = "latitude of location", required = true) @QueryParam("lat") double latitude,
			 @ApiParam(value = "longitude of location", required = true) @QueryParam("long") double longitude,
			 @ApiParam(value = "Distance in miles", required = true) @QueryParam("distance") double range) 
					throws TruckServiceException
	{
		
		DataManager dm = DataManager.getInstance();
		return dm.getTrucksInRange(latitude, longitude, range * TruckServiceConstants.MILE_TO_METERS);
	}
	
	
	/**
	 * Get all food items served by all trucks
	 * URL: /foodtruckservice/v1/sf/fooditems
	 * @return
	 * @throws TruckServiceException
	 */
	@GET
	@Path("/fooditems")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get all food items",
    	notes = "Get list of all food items served by SF food trucks", 
    	response = String.class, 
    	responseContainer = "List")
	
	public ArrayList<String> getFoodTruckItems() throws TruckServiceException
	{
		DataManager dm = DataManager.getInstance();
		return dm.getAllFoodTruckItems();
	}
	
	
	/**
	 * Get food items served by a particular truck
	 * URL : /foodtruckservice/v1/sf/fooditems/truck/34
	 * @param id
	 * @return
	 * @throws TruckServiceException
	 */
	@GET
	@Path("/fooditems/truck/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value = "Get all food items for a particular truck",
    	notes = "Specify the truck Id", 
    	response = String.class)
	
	public String getFoodItemsForTruck(
			@ApiParam(value = "Id of truck", required = true) @PathParam("id") int id) 
			throws TruckServiceException
	{
		DataManager dm = DataManager.getInstance();
		return dm.getFoodItemsForTruck(id);
	}
	
	
	/**
	 * Get food items available within a given range
	 * URL : 
	 * @param latitude
	 * @param longitude
	 * @param range in miles
	 * @return
	 * @throws TruckServiceException
	 */
	@GET
	@Path("/fooditems/inrange")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Get all food items in a given range",
    	notes = "Get list of all food items served by SF food trucks in a given range", 
    	response = String.class, 
    	responseContainer = "List")
	
	public ArrayList<String> getFoodItemsInRange(
			@ApiParam(value = "latitude of location", required = true) @QueryParam("lat") double latitude,
			@ApiParam(value = "longitude of location", required = true) @QueryParam("long") double longitude, 
			@ApiParam(value = "distance in miles", required = true) @QueryParam("distance") double range) 
			throws TruckServiceException
	{
		DataManager dm = DataManager.getInstance();
		return dm.getFoodItemsInRange(latitude, longitude, range * TruckServiceConstants.MILE_TO_METERS);
	}
}
