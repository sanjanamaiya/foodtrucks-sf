package com.foodtruck.sf.db;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtendedTruck extends Truck 
{
	private int cnn;
	private String locationDescr;
	private String blockLot;
	private String block;
	private String lot;
	private String permit;
	private double X;
	private double Y;
	private String schedule;
	private Timestamp noISent;
	private Timestamp approved;
	private String received;
	private short priorPermit;
	private Timestamp expirationDate;
	private Location location;
	
	public int getCnn() 
	{
		return cnn;
	}
	
	public void setCnn(int cnn) 
	{
		this.cnn = cnn;
	}
	
	@JsonProperty("locationdescription")
	public String getLocationDescr() 
	{
		return locationDescr;
	}
	
	@JsonProperty("locationdescription")
	public void setLocationDescr(String locationDescr) 
	{
		this.locationDescr = locationDescr;
	}
	
	@JsonProperty("blocklot")
	public String getBlockLot()
	{
		return blockLot;
	}
	
	@JsonProperty("blocklot")
	public void setBlockLot(String blockLot)
	{
		this.blockLot = blockLot;
	}
	
	public String getBlock() 
	{
		return block;
	}
	
	public void setBlock(String block) 
	{
		this.block = block;
	}
	
	public String getLot() 
	{
		return lot;
	}
	
	public void setLot(String lot) 
	{
		this.lot = lot;
	}
	
	public String getPermit() 
	{
		return permit;
	}
	
	public void setPermit(String permit) 
	{
		this.permit = permit;
	}
	
	public double getX() 
	{
		return X;
	}
	
	public void setX(double x) 
	{
		X = x;
	}
	
	public double getY() 
	{
		return Y;
	}
	
	public void setY(double y) 
	{
		Y = y;
	}
	
	public String getSchedule() 
	{
		return schedule;
	}
	
	public void setSchedule(String schedule)
	{
		this.schedule = schedule;
	}
	
	public Timestamp getNoISent() 
	{
		return noISent;
	}
	
	public void setNoISent(Timestamp noISent) 
	{
		this.noISent = noISent;
	}
	
	public Timestamp getApproved() 
	{
		return approved;
	}
	
	public void setApproved(Timestamp approved) 
	{
		this.approved = approved;
	}
	
	public String getReceived() 
	{
		return received;
	}
	
	public void setReceived(String received)
	{
		this.received = received;
	}
	
	@JsonProperty("priorpermit")
	public short getPriorPermit() 
	{
		return priorPermit;
	}
	
	@JsonProperty("priorpermit")
	public void setPriorPermit(short priorPermit)
	{
		this.priorPermit = priorPermit;
	}
	
	@JsonProperty("expirationdate")
	public Timestamp getExpirationDate() 
	{
		return expirationDate;
	}
	
	@JsonProperty("expirationdate")
	public void setExpirationDate(Timestamp expirationDate)
	{
		this.expirationDate = expirationDate;
	}
	
	public Location getLocation() 
	{
		return location;
	}
	
	public void setLocation(Location location) 
	{
		this.location = location;
	}
}
