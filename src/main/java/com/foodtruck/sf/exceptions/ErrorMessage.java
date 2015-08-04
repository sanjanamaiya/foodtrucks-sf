package com.foodtruck.sf.exceptions;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The error message which is displayed when
 * a rest end point fails
 * 
 * @author sanjana
 *
 */

@XmlRootElement
public class ErrorMessage {
	
	/** contains the same HTTP Status code returned by the server */
	int status;
	
	/** application specific error code */
	int code;
	
	/** message describing the error*/
	String message;
		
	/** link point to page where the error message is documented */
	String link;
	
	/** extra information that might useful for developers/users */
	String detailedMessage;	

	public int getStatus() 
	{
		return status;
	}

	public void setStatus(int status) 
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

	public String getMessage() 
	{
		return message;
	}

	public void setMessage(String message) 
	{
		this.message = message;
	}

	public String getDetailedMessage() 
	{
		return detailedMessage;
	}

	public void setDetailedMessage(String detailedMessage) 
	{
		this.detailedMessage = detailedMessage;
	}

	public String getLink() 
	{
		return link;
	}

	public void setLink(String link) 
	{
		this.link = link;
	}
	
	public ErrorMessage(TruckServiceException ex)
	{
		this.code = ex.getCode();
		this.detailedMessage = ex.getDetailedMessage();
		this.link = ex.getLink();
		this.message = ex.getMessage();
		this.status = ex.getStatus();
	}
	
	public ErrorMessage(NotFoundException ex)
	{
		this.status = Response.Status.NOT_FOUND.getStatusCode();
		this.message = ex.getMessage();
		this.link = "https://jersey.java.net/apidocs/2.9/jersey/javax/ws/rs/NotFoundException.html";		
	}

	public ErrorMessage() {}
}
