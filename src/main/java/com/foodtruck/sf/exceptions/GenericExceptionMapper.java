package com.foodtruck.sf.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.foodtruck.sf.util.TruckServiceConstants;

/**
 * Custom message for unchecked exceptions
 * 
 * @author sanjana
 *
 */

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> 
{
	@Override
	public Response toResponse(Throwable ex) 
	{
		
		ErrorMessage errorMessage = new ErrorMessage();		
		setHttpStatus(ex, errorMessage);
		errorMessage.setCode(TruckServiceConstants.GENERIC_APP_ERROR_CODE);
		errorMessage.setMessage(ex.getMessage());
		StringWriter errorStackTrace = new StringWriter();
		ex.printStackTrace(new PrintWriter(errorStackTrace));
		errorMessage.setDetailedMessage(errorStackTrace.toString());
		errorMessage.setLink(TruckServiceConstants.ERROR_LINK);
				
		return Response.status(errorMessage.getStatus())
				.entity(errorMessage)
				.type(MediaType.APPLICATION_JSON)
				.build();	
	}

	private void setHttpStatus(Throwable ex, ErrorMessage errorMessage) 
	{
		if(ex instanceof WebApplicationException ) 
		{  
			errorMessage.setStatus(((WebApplicationException)ex).getResponse().getStatus());
		}
		else 
		{
			// Server error 500 :(
			errorMessage.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()); 
		}
	}
}
