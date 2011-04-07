package org.sse.http;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sse.ws.base.WSResult;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Locating extends HttpServlet {
	private static final long serialVersionUID = 1L;
	org.sse.ws.Locating locating = new org.sse.ws.Locating();

	public Locating() {
		super();
	}
	
	//	<ws:geocoding>
	//	  <arg0>
	//	    <address></address>
	//	    <key></key>
	//	  </arg0>
	//	</ws:geocoding>
	//
	//	<ws:reverseGeocoding>
	//	  <arg0>
	//	    <x></x>
	//	    <y></y>
	//	  </arg0>
	//	</ws:reverseGeocoding>
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 1 get parameters
		request.setCharacterEncoding(GZipWriter.charset);
		String locate = request.getParameter("xml");

		// 2 write
		GZipWriter.write(this.excute(XmlParser.getDocument(locate)), response);
	}
	
	private WSResult excute(Document doc) {
		if(doc==null)
			return null;
		String firstTag = doc.getDocumentElement().getTagName();
		NodeList list = doc.getDocumentElement().getFirstChild()
				.getChildNodes();
		if (firstTag == null || list == null || list.getLength() == 0)
			return null;
		
		return null;
	}
}
