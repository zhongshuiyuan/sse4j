package org.sse.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.sse.ws.base.WSResult;
import com.google.gson.Gson;

public class HttpTest {

	public static void main(String[] args) throws IOException {
		URL url = new URL("http://localhost:8080/sse4j/servlet/Searching");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Connection", "Keep-Alive");

		conn.connect();

		// send
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		String xml = "xml=<ws:poiInfo><arg0><id>200</id><key>110000</key></arg0></ws:poiInfo>";
		out.writeBytes(xml);
		out.flush();
		out.close();

		// return
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(conn.getInputStream())));
		String s;
		StringBuffer sb = new StringBuffer();
		while ((s = reader.readLine()) != null) {
			// System.out.println(s);
			sb.append(s.trim());
		}
		reader.close();

		conn.disconnect();

		// result
		WSResult result = new Gson().fromJson(sb.toString(), WSResult.class);
		System.out.println(result.getJsonString());
	}
}
