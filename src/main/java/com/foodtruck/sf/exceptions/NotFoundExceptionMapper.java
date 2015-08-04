package com.foodtruck.sf.exceptions;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * The Exception mapper for NotFoundException. This exception cannot be encompassed by our
 * custom TruckServiceException, since it is thrown when an end point does not exist
 * 
 * @author sanjana
 *
 */

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> 
{
	@Override
	public Response toResponse(NotFoundException ex) 
	{
		return Response.status(ex.getResponse().getStatus())
				.entity(new ErrorMessage(ex))
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
}
