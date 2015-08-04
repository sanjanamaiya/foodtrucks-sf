package com.foodtruck.sf.db;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location 
{
	private boolean needsRecoding;
	private double latitude;
	private double longitude;
	
	@JsonProperty("needs_recoding")
	public boolean isNeedsRecoding() 
	{
		return needsRecoding;
	}
	
	@JsonProperty("needs_recoding")
	public void setNeedsRecoding(boolean needsRecoding) 
	{
		this.needsRecoding = needsRecoding;
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
}
