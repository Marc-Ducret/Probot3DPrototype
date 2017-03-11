package net.slimevoid.probot.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HttpThread {
	
	public static void httpGet(final String page) {
		httpGet(page, null);
	}
	
	public static void httpGet(final String page, final HttpGetProccess processor) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String p = page.replaceAll(" ", "%20");
					System.out.println("GET Querry on: "+"http://"+WEBSITE_URL+p);
					URL source = new URL("http://"+WEBSITE_URL+p);
					HttpURLConnection conn = (HttpURLConnection) source.openConnection();
					conn.connect();
					byte[] buf = new byte[256];

					DataInputStream in = new DataInputStream(new BufferedInputStream(conn.getInputStream()));

					String result = "";
					int  read;
					while((read = in.read(buf)) != -1) {
						for(int i = 0; i < read;i++) {
							result += (char)buf[i];
						}
					}
					if(processor != null) {
						processor.process(result);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static void httpPost(final String page, final String...params) {
		if(params.length % 2 != 0) {
			throw new IllegalArgumentException("Illegal amount of params");
		}
		new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							String urlParameters = "";
							for(int i = 0; i < params.length / 2; i ++) {
								String name  = params[i * 2 + 0];
								String value = params[i * 2 + 1];
								if(i > 0) urlParameters += "&";
								urlParameters += name+"="+URLEncoder.encode(value, "UTF-8");
							}
							String p = page.replaceAll(" ", "%20");
							String request = "http://"+WEBSITE_URL+p;
							System.out.println("POST Querry on: "+request+" with params "+urlParameters);
							URL url = new URL(request); 
							HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
							connection.setDoOutput(true);
							connection.setDoInput(true);
							connection.setInstanceFollowRedirects(false);
							connection.setRequestMethod("POST"); 
							connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
							connection.setRequestProperty("charset", "utf-8");
							connection.setRequestProperty("Content-Length", "" + urlParameters.getBytes().length);
							connection.setUseCaches (false);
							
							DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
							wr.writeBytes(urlParameters);
							wr.flush();
							BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
							String line;
							while ((line = reader.readLine()) != null) {
							    System.out.println(line);
							}
							wr.close();
							connection.disconnect();
						} catch (Exception e) {
							System.err.println("POST failed");
							e.printStackTrace();
						}
					}
		}).start();
	}
	
	public static abstract class HttpGetProccess {
		public abstract void process(String result);
	}
	
	private static final String WEBSITE_URL = "slimevoid.net/games/";
}
