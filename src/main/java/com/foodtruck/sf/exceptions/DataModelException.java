package com.foodtruck.sf.exceptions;

public class DataModelException extends Throwable
{
	private static final long serialVersionUID = 1L;
	
	public DataModelException(String message)
	{
		super(message);
	}
	
	public DataModelException(Throwable e)
	{
		super(e);
	}
	
	public DataModelException(String message, Throwable e)
	{
		super(message, e);
	}

}
