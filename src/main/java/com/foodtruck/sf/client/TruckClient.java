package com.foodtruck.sf.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

/**
 * This class is a very basic implementation of the web client. 
 * The testing of the resource endpoints is done through unit tests.
 * 
 * @author sanjana
 *
 */
public class TruckClient {

	public static void main(String[] args) 
	{
		testGetTrucks();

	}

	private static void testGetTrucks() 
	{
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		 
		WebTarget webTarget = client.target("http://localhost:8080/foodtruckservice/v1/sf/trucks");
		 
		Invocation.Builder invocationBuilder =
				webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder.header("some-header", "true");
		 
		Response response = invocationBuilder.get();
		System.out.println(response.getStatus());
		System.out.println(response.readEntity(String.class));
	}
}
