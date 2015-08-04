package com.foodtruck.sf.datamodel;

import com.google.common.geometry.S2LatLng;

/**
 * A few geo utilities using the s2-geometry library.
 * https://code.google.com/p/s2-geometry-library-java/
 * 
 * The library is licensed under Apache License 2.0
 * 
 * 
 * @author sanjana
 *
 */
public class GeoUtil 
{

	public static double GetDistance(S2LatLng point1, S2LatLng point2) 
	{
		return point1.getEarthDistance(point2);
	}

	public static double GetDistance(double latitude1, double longitude1,
			double latitude2, double longitude2) 
	{
		S2LatLng point1 = S2LatLng.fromDegrees(latitude1, longitude1);
		S2LatLng point2 = S2LatLng.fromDegrees(latitude2, longitude2);
		return GetDistance(point1, point2);
	}
}
