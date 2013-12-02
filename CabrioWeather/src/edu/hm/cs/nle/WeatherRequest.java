package edu.hm.cs.nle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

/**
 * Saves and parses response of api.openweather.com.
 * 
 * @author Nhu-Huy Le & Long Matthias Yan
 *
 */
public class WeatherRequest extends Request {
	
	private Forecast forecast;
	final String hostname = "api.openweathermap.org";

	/**
	 * Constructor for a weather request.
	 * 
	 * @param lat				Latitude of position.
	 * @param lng				Longitude of position.
	 * @throws IOException
	 */
	public WeatherRequest (double lat, double lng) throws IOException {
		
		String request = "POST /data/2.5/weather"
				+ "?lat=" + lat
				+ "&lon=" + lng
				+ " HTTP/1.0\n\n";
		
		sendRequest (hostname, request, 80, 10000);
		
		Writer log = new FileWriter ("response2.txt");
		log.write (header.toString () + "\n" + body.toString ());
		log.close ();
		if (header != null) System.out.println ("Forecast response received.");
	}

	/**
	 * Serializes the String stream.
	 * 
	 * @param stringReader
	 */
	public void serialize (StringReader stringReader) {
		
		forecast = gson.fromJson (stringReader, Forecast.class);
		
		if (forecast != null) System.out.println ("Forecast serialized.");
		else System.out.println ("Forecast not serialized.");
	}
	
	public Forecast getForecast() {
		return forecast;
	}
}

/**
 * Specific API response structure. (JSON)
 *
 */
class Forecast {
	Main main;
	Weather[] weather;
}

class Main {
//	in Kelvin (Subtract 273.15 to convert to Celsius)
	double temp;
}

class Weather {
	int id;
}