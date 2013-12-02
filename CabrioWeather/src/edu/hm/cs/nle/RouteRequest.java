package edu.hm.cs.nle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

/**
 * Saves and parses response of api.googlemaps.com.
 * 
 * @author Nhu-Huy Le & Long Matthias Yan
 *
 */
public class RouteRequest extends Request {
	
	private Direction direction;
	private static final String HOSTNAME = "maps.googleapis.com";
	
	public RouteRequest (String origin, String destination) throws IOException {
		this (origin, destination, "", "", "");
	}
	
	public RouteRequest (String origin, String destination, String waypoint1) throws IOException {
		this (origin, destination, waypoint1, "", "");
	}
	
	public RouteRequest (String origin, String destination, String waypoint1, String waypoint2) throws IOException {
		this (origin, destination, waypoint1, waypoint2, "");
	}
	
	/**
	 * Constructor for a route request.
	 * 
	 * @param origin			Origin address (google geocoded).
	 * @param destination		Destination address (google geocoded).
	 * @param waypoint1			First Waypoint address (google geocoded). 
	 * @param waypoint2			Second Waypoint address (google geocoded).
	 * @param waypoint3			Third Waypoint address (google geocoded).
	 * @throws IOException
	 */
	public RouteRequest (String origin, String destination, String waypoint1, String waypoint2, String waypoint3) throws IOException {
		
//		http request for api	
		String request = "GET /maps/api/directions/json"
				+ "?origin=" + origin
				+ "&destination=" + destination
				+ "&waypoints=" + waypoint1
					+ "|" + waypoint2
					+ "|" + waypoint3
				+ "&sensor=false"
				+ "&units=metric"
				+ " HTTP/1.0\n\n";
		
		sendRequest (HOSTNAME, request, 80, 10000);
		Writer log = new FileWriter ("response.txt");
		log.write (header.toString ()
				+ "\n"
				+ body.toString ());
		log.close ();
		if (header != null) System.out.println ("Route response received.");
	}
	
	/**
	 * Serializes the String stream.
	 * 
	 * @param stringReader
	 */
	public void serialize(StringReader stringReader) {
		
		direction = gson.fromJson (stringReader, Direction.class);
		
		if (direction != null) System.out.println ("Directions serialized.");
		else System.out.println ("Directions NOT serialized.");
	}

	public Direction getDirection () {
		return direction;
	}	
}

/**
 * Specific API response structure. (JSON)
 *
 */
class Direction {
	String status;
	Route[] routes;
}

class Route {
	String copyrights;
	Leg[] legs;
}

class Leg {
	Step[] steps;
}

class Step {
	Distance distance;
	Location start_location;
	Location end_location;
}

class Distance {
	double value;
}

class Location {
	double lat;
	double lng;
}