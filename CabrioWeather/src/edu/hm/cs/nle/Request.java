package edu.hm.cs.nle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

/**
 * Sends request (http, api) and saves response.
 * 
 * @author Long Matthias Yan & Nhu-Huy Le
 *
 */
public class Request {
	
	public static Gson gson = new Gson ();
	protected String header;
	protected String body;

	public String getHeader () {
		return header;
	}

	public String getBody () {
		return body;
	}
	
	public Request () {}
	
	/**
	 * Sends request.
	 * 
	 * @param hostname			Target hostname or ip.
	 * @param request			request (header?)
	 * @param port				Target port.
	 * @param timeOut			Time til close socket.
	 * @throws IOException
	 */
	protected void sendRequest (final String hostname,final String request, final int port, final int timeOut) throws IOException {
		
		StringBuffer header = new StringBuffer ();
		StringBuffer body = new StringBuffer ();
		String line;
	
		try (Socket s = new Socket (hostname, port);
				InputStream in = s.getInputStream ();
				BufferedReader br = new BufferedReader (new InputStreamReader(in));
				OutputStream out = s.getOutputStream ();			
				PrintWriter pw = new PrintWriter (new OutputStreamWriter(out))) {
			s.setSoTimeout (timeOut);
			System.out.println ("Connection established: "+s.getRemoteSocketAddress());
			pw.println (request);
			pw.flush ();
			for (line = br.readLine (); !line.isEmpty (); line = br.readLine ())
				header.append (line + "\n");		
			for (line = br.readLine (); line != null; line = br.readLine ())
				body.append (line + "\n");
		}	
			this.header = header.toString ();
			this.body = body.toString ();
	}
}
