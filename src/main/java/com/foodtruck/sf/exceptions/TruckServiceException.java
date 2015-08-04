package com.foodtruck.sf.exceptions;

/**
 * 
 * Custom exception : holds information which will allow developers/users to 
 * debug errors thrown from the food truck service 
 * 
 * @author sanjana
 *
 */
public class TruckServiceException extends Throwable
{
	private static final long serialVersionUID = 1L;
	public static final int INITIALIZATION_ERROR = 5003;
	public static final int NO_ITEMS_FOUND_ERROR = 5004;
	
	/**
	 * the HTTP status of the response sent back to the client in case of an
	 * error. If null, a default value is returned
	 */
	Integer status;
	
	/** application specific error code */
	int code;
	
	/** link documenting the exception */	
	String link;
	
	/** detailed error description for developers*/
	String detailedMessage;
	
	/**
	 * 
	 * @param status
	 * @param code
	 * @param message
	 * @param developerMessage
	 * @param link
	 */
	public TruckServiceException(int status, int code, String message,
			String developerMessage, String link) 
	{
		super(message);
		this.status = status;
		this.code = code;
		this.detailedMessage = developerMessage;
		this.link = link;
	}
	
	public TruckServiceException()
	{
		
	}

	public Integer getStatus() 
	{
		return status;
	}

	public void setStatus(Integer status) 
	{
		this.status = status;
	}

	public int getCode() 
	{
		return code;
	}

	public void setCode(int code) 
	{
		this.code = code;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link) 
	{
		this.link = link;
	}

	public String getDetailedMessage() 
	{
		return detailedMessage;
	}

	public void setDetailedMessageMessage(String detailedMessage) 
	{
		this.detailedMessage = detailedMessage;
	}

}
