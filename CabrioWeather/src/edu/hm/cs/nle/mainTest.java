package edu.hm.cs.nle;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

public class mainTest {
	
	public static void main(String[] args) throws IOException {
		
		PrintStream out = System.out;
		RouteRequest rr = new RouteRequest("sydney",
											"canberra"
											);
		out.println(isRoute(rr));
	}
	
	private static boolean isLocation(double lat, double lng) throws IOException {
		
		boolean result = false;
//		api acceptable weathercodes
		int[] code = {701, 800, 801, 802, 803, 804, 950, 951, 952, 953, 954, 955, 956};
		WeatherRequest wr = new WeatherRequest(lat, lng);
		
		wr.serialize(new StringReader(wr.getBody()));
		
		if(!(wr.getForecast().main.temp - 273.15 < 7))	
			for(int i: code)
				for(Weather k: wr.getForecast().weather)
					if(i == k.id) {
						result = true;
						break;
					}
		return result;
	}
	
	private static boolean isRoute(RouteRequest routeRequest) throws IOException {

//		in meters
		final int RANGE = 50000;
		double distanceSum = 0;
		
		double lastCheck = RANGE;
		double lastSum = 0;
		double lastLat = 0;
		double lastLng = 0;
		
		routeRequest.serialize(new StringReader(routeRequest.getBody()));
		
		if(routeRequest.getDirection() != null)
			
			lastLat = routeRequest.getDirection().routes[0].legs[0].steps[0].start_location.lat;
			lastLng = routeRequest.getDirection().routes[0].legs[0].steps[0].start_location.lng;
			
			for(Route r: routeRequest.getDirection().routes) 
				for(Leg l: r.legs)
					for(Step s: l.steps) {

						distanceSum += s.distance.value;
						
						System.out.println("------------------ \nNew Step initialized: " + distanceSum);
						
//						jump: distance between last checked step and last steps-sum
						for(double jump = lastCheck - lastSum; distanceSum >= lastCheck; lastCheck += RANGE) {
							
//							on the step-route: factor of the route to get the testing location
							double factor = jump / (distanceSum - lastSum);
//							from the last starting location
							double curLat = factor * (s.end_location.lat - lastLat) + lastLat;
							double curLng = factor * (s.end_location.lng - lastLng) + lastLng;
							
							System.out.println("Current Distance Sum: " + distanceSum + "\n"
												+ "Factor on the Route: " + factor + "\n"
												+ "Current Latitude: " + curLat + "\n" 
												+ "current Longitude: " + curLng + "\n"
												+ "last Checked Range-Point: " + lastCheck + "\n"
												+ "Getting Weather Information...");
							
							if(!isLocation(curLat, curLng)) {
								System.out.println("Status: Close !");
								return false;
							}
							else System.out.println("Status: Open !");
							jump += RANGE;
						}
						
						lastSum = distanceSum;
						lastLat = s.end_location.lat;
						lastLng = s.end_location.lng;
												
					}
		return true;
	}
}
