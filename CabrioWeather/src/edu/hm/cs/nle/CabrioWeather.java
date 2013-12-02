package edu.hm.cs.nle;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CabrioWeather
 * 
 * @author Long Matthias Yan & Nhu-Huy Le
 */
@WebServlet("/CabrioWeather")
public class CabrioWeather extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CabrioWeather() {
        super();
    }

	/**
	 * Weather information for cabrio car every 50 kilometres.
	 * Response to maximal five location requests.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet (final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		
		final PrintWriter writer = response.getWriter ();
		final StringBuffer[] location = new StringBuffer[5];
		final RouteRequest routeRequest;

		getFormular (request, location);
		routeRequest = new RouteRequest (location[0].toString (), 
											location[1].toString (),
											location[2].toString (),
											location[3].toString (),
											location[4].toString ());
		writeResponse (writer, location, isRoute(routeRequest, 50000));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
	
	/**
	 * Parses the formular data to location StringBuffer array. 
	 * 
	 * @param request			
	 * @param location			Information will be stored here.
	 */
	private void getFormular (HttpServletRequest request, StringBuffer[] location) {
		
		String tmp = "";
		
//		corresponds to html formular field names
		String[] current = { "origin",
								"destination",
								"waypointA",
								"waypointB",
								"waypointC" };
		
		for (int i = 0; i < 5; i++) {
			location[i] = new StringBuffer ();
			for (int j = 0; j < 5; j++) {
				tmp = request.getParameter (current[i] + j);
				if (!tmp.equals ("")) location[i].append (tmp + ',');		
			}
			System.out.println (location[i]);
		}
	}
	
	/**
	 * Html response to writer stream.
	 * 
	 * @param writer
	 * @param location
	 * @param openCabrio
	 */
	private void writeResponse (final PrintWriter writer, final StringBuffer[] location, final boolean openCabrio) {
		
		final String sunImg = "http://oi44.tinypic.com/24fl2r7.jpg";
		final String rainImg = "http://oi40.tinypic.com/2lmwg92.jpg";

		writer.println ("<html>");
		writer.println ("<head><title>CabrioWeather</title></head>");
		writer.println ("<body>");
		for (StringBuffer sb: location)
			writer.println ("<h1>" + sb + "</h1>");		
		if (openCabrio)
			writer.println ("<img src =" + sunImg + "></img>");
		else writer.println ("<img src =" + rainImg +  "></img>");
		writer.println ("<body>");
		writer.println ("</html>");
		writer.close ();	
	}
	
	/**
	 * Cabrio Weather for a specific location.
	 * 
	 * @param lat				Latitude.
	 * @param lng				Longitude.
	 * @return					true: Open cabrio possible.
	 * 							false: Close cabrio.
	 * @throws IOException
	 */
	private static boolean isLocation (final double lat, final double lng) throws IOException {
		
//		api acceptable weathercodes (openweather)
		final int[] code = {701, 800, 801, 802, 803, 804, 950, 951, 952, 953, 954, 955, 956};
		final WeatherRequest wr = new WeatherRequest (lat, lng);
		boolean result = false;
		
		wr.serialize (new StringReader(wr.getBody()));
		if (! (wr.getForecast ().main.temp - 273.15 < 7))	
			for (int i: code)
				for (Weather k: wr.getForecast ().weather)
					if (i == k.id) {
						result = true;
						break;
					}
		return result;
	}
	
	/**
	 * Cabrio Weather for a whole route.
	 * 
	 * @param routeRequest			Route stored with essential informations.
	 * @param range					Testing every distance in metres.
	 * @return						true: Open cabrio possible.
	 * 								false: Close cabrio.					
	 * @throws IOException
	 */
	private static boolean isRoute (RouteRequest routeRequest, final int range) throws IOException {
		
		double curLat = 0;
		double curLng = 0;
		double lastLat = 0;
		double lastLng = 0;
		double factor = 0;
		
//		in meters
		double distanceSum = 0;
		double lastSum = 0;
		double lastCheck = range;
		double jump = 0;

		routeRequest.serialize(new StringReader(routeRequest.getBody()));		
		if(routeRequest.getDirection() != null)
			
//			sets starting location
			lastLat = routeRequest.getDirection().routes[0].legs[0].steps[0].start_location.lat;
			lastLng = routeRequest.getDirection().routes[0].legs[0].steps[0].start_location.lng;
			for(Route r: routeRequest.getDirection().routes)
				for(Leg l: r.legs)
					for(Step s: l.steps) {
						distanceSum += s.distance.value;
						System.out.println ("New Step initialized: " + distanceSum);
						
//						jump: distance between last checked step and last steps-sum
						for (jump = lastCheck - lastSum; distanceSum >= lastCheck; lastCheck += range) {
							
//							on the step-route: factor of the route to get the testing location
							factor = jump / (distanceSum - lastSum);
							
//							from the last starting location
							curLat = factor * (s.end_location.lat - lastLat) + lastLat;
							curLng = factor * (s.end_location.lng - lastLng) + lastLng;			
							System.out.println ("Current distance sum: " + distanceSum + "\n"
												+ "Factor on the route: " + factor + "\n"
												+ "Current latitude: " + curLat + "\n" 
												+ "Current longitude: " + curLng + "\n"
												+ "Last checked Range-Point: " + lastCheck + "\n"
												+ "Getting weather information...");
							if (!isLocation (curLat, curLng)) {
								System.out.println ("Status: Close Cabrio !");
								return false;
							}
							else System.out.println ("Status: Open Cabrio !");
							jump += range;
						}
						lastSum = distanceSum;
						lastLat = s.end_location.lat;
						lastLng = s.end_location.lng;
					}
		return true;
	}
}
