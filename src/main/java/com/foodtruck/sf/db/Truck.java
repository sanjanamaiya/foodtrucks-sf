package com.foodtruck.sf.db;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class Truck 
{
	private int truckId;
	private String facilityType;
	private String status;
	private String foodItems;
	private double latitude;
	private double longitude;
	private String applicant;
	private String address;
	private int locationId;
	
	public Truck()
	{
		
	}
	
	public Truck(int id, String facilityType, String status, double latitude, double longitude, 
			String applicant, String address, String items, int locationId)
	{
		this.truckId = id;
		this.facilityType = facilityType;
		this.status = status;
		this.latitude = latitude;
		this.longitude = longitude;
		this.applicant = applicant;
		this.address = address;
		foodItems = items;
		this.locationId = locationId;
	}
	
	public int getTruckId() 
	{
		return truckId;
	}
	
	public void setTruckId(int truckId) 
	{
		this.truckId = truckId;
	}
	
	@JsonProperty("facilitytype")
	public String getFacilityType() 
	{
		return facilityType;
	}
	
	@JsonProperty("facilitytype")
	public void setFacilityType(String facilityType) 
	{
		this.facilityType = facilityType;
	}
	
	public String getStatus() 
	{
		return status;
	}
	
	public void setStatus(String status) 
	{
		this.status = status;
	}
	
	@JsonProperty("fooditems")
	public String getFoodItems()
	{
		return foodItems;
	}
	
	@JsonProperty("fooditems")
	public void setFoodItems(String foodItems) 
	{
		this.foodItems = foodItems;
	}
	
	public double getLatitude() 
	{
		return latitude;
	}
	
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public void setLongitude(double longitude) 
	{
		this.longitude = longitude;
	}

	public String getApplicant() 
	{
		return applicant;
	}

	public void setApplicant(String applicant) 
	{
		this.applicant = applicant;
	}

	public String getAddress() 
	{
		return address;
	}

	public void setAddress(String address) 
	{
		this.address = address;
	}
	
	@JsonProperty("objectid")
	public int getLocationId() 
	{
		return locationId;
	}
	
	@JsonProperty("objectid")
	public void setLocationId(int locationId) 
	{
		this.locationId = locationId;
	}

}
