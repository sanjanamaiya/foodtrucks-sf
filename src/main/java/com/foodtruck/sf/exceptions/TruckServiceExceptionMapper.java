package com.foodtruck.sf.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TruckServiceExceptionMapper implements
		ExceptionMapper<TruckServiceException> 
{
	@Override
	public Response toResponse(TruckServiceException ex) 
	{
		System.out.println("I'm here");
		return Response.status(ex.getStatus()).entity(new ErrorMessage(ex))
				.type(MediaType.APPLICATION_JSON).build();
	}
}
