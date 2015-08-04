package com.foodtruck.sf.service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.wordnik.swagger.jaxrs.config.BeanConfig;

public class SwaggerBootstrap extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("1.0.0");
		beanConfig.setBasePath("http://ec2-52-27-9-65.us-west-2.compute.amazonaws.com:8080/foodtruckservice/v1");
		beanConfig.setResourcePackage("com.foodtruck.sf.service");
		beanConfig.setScan(true);
		beanConfig.setTitle("San Francisco Food Trucks service");
		beanConfig.setDescription("This is a food truck server for the "
				+ "city of San Francisco. Search for food trucks"
				+ "near you by specifying your location");
		beanConfig.setLicense("Apache 2.0");
		beanConfig.setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html");
	}
}
