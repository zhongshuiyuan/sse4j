package org.sse.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sse.http.util.GZipWriter;
import org.sse.http.util.JSONPWriter;
import org.sse.http.util.XmlParser;
import org.sse.ws.base.WSPointF;
import org.sse.ws.base.WSResult;
import org.sse.ws.base.WSRouter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Desc: xml=&gzip={true,false}
 * 
 * @author dux(duxionggis@126.com)
 * 
 */
public class Routing extends HttpServlet {
	private static final long serialVersionUID = 1L;
	org.sse.ws.Routing routing = new org.sse.ws.Routing();
	
	public Routing() {
		super();
	}

	public void destroy() {
		super.destroy();
	}
	
	//	<ws:webPlan>         
	//	  <arg0>            
	//	    <endPoint>
	//	      <x></x>
	//	      <y></y>
	//	    </endPoint>            
	//	    <key></key>            
	//	    <preference></preference>            
	//	    <startPoint>
	//	      <x></x>
	//	      <y></y>
	//	    </startPoint>            
	//	    <!--viaPoints>
	//	      <x></x>
	//	      <y></y>
	//	    </viaPoints-->
	//	  </arg0>
	//	</ws:webPlan>
	//
	//	<ws:plan>         
	//	  <arg0>            
	//	    <endPoint>
	//	      <x></x>
	//	      <y></y>
	//	    </endPoint>            
	//	    <key></key>            
	//	    <preference></preference>            
	//	    <startPoint>
	//	      <x></x>
	//	      <y></y>
	//	    </startPoint>            
	//	    <!--viaPoints>
	//	      <x></x>
	//	      <y></y>
	//	    </viaPoints-->
	//	  </arg0>
	//	</ws:plan>
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1 get parameters
		String xml = request.getParameter("xml");
		// System.out.println(xml);
		String callback = request.getParameter("callback");
		if (callback != null && !callback.isEmpty()) {
			JSONPWriter.write(this.excute(XmlParser.getDocument(xml)), response, callback);
			return;
		}
		
		String gzip = request.getParameter("gzip");
		boolean zip = (gzip == null ? true : gzip.equalsIgnoreCase("true"));
		try {
			// 2 write
			GZipWriter.write(this.excute(XmlParser.getDocument(xml)), response, zip);
		} catch (Exception ex) {
			WSResult result = new WSResult();
			result.setResultCode(0);
			result.setFaultString(ex.getMessage());
			GZipWriter.write(result, response, zip);
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private WSResult excute(Document doc) {
		if (doc == null)
			return null;
		String firstTag = doc.getDocumentElement().getTagName();
		NodeList list = doc.getDocumentElement().getFirstChild().getChildNodes();
		if (firstTag == null || list == null || list.getLength() == 0)
			return null;
		WSRouter router = new WSRouter();
		List<WSPointF> viaPts = new ArrayList<WSPointF>();
		String name = null;
		String val = null;
		for (int i = 0; i < list.getLength(); i++) {
			name = list.item(i).getNodeName();
			if (name.equalsIgnoreCase("endPoint")) {
				NodeList ends = list.item(i).getChildNodes();
				if (ends != null && ends.getLength() > 0) {
					WSPointF end = new WSPointF();
					for (int j = 0; j < ends.getLength(); j++) {
						name = ends.item(j).getNodeName();
						val = ends.item(j).getTextContent();
						if (!val.isEmpty())
							if (name.equalsIgnoreCase("x"))
								end.setX(Float.valueOf(val).floatValue());
							else if (name.equalsIgnoreCase("y"))
								end.setY(Float.valueOf(val).floatValue());
					}
					router.setEndPoint(end);
				}
			} else if (name.equalsIgnoreCase("key")) {
				val = list.item(i).getTextContent();
				if (!val.isEmpty())
					router.setKey(val);
			} else if (name.equalsIgnoreCase("preference")) {
				val = list.item(i).getTextContent();
				if (!val.isEmpty())
					router.setPreference(val);
			} else if (name.equalsIgnoreCase("startPoint")) {
				NodeList starts = list.item(i).getChildNodes();
				if (starts != null && starts.getLength() > 0) {
					WSPointF start = new WSPointF();
					for (int j = 0; j < starts.getLength(); j++) {
						name = starts.item(j).getNodeName();
						val = starts.item(j).getTextContent();
						if (!val.isEmpty())
							if (name.equalsIgnoreCase("x"))
								start.setX(Float.valueOf(val).floatValue());
							else if (name.equalsIgnoreCase("y"))
								start.setY(Float.valueOf(val).floatValue());
					}
					router.setStartPoint(start);
				}
			} else if (name.equalsIgnoreCase("viaPoints")) {
				NodeList vias = list.item(i).getChildNodes();
				if (vias != null && vias.getLength() > 0) {
					WSPointF via = new WSPointF();
					for (int j = 0; j < vias.getLength(); j++) {
						name = vias.item(j).getNodeName();
						val = vias.item(j).getTextContent();
						if (!val.isEmpty())
							if (name.equalsIgnoreCase("x"))
								via.setX(Float.valueOf(val).floatValue());
							else if (name.equalsIgnoreCase("y"))
								via.setY(Float.valueOf(val).floatValue());
					}
					if (!via.isEmpty())
						viaPts.add(via);
				}
			}
		}
		if (viaPts.size() > 0)
			router.setViaPoints(viaPts.toArray(new WSPointF[0]));
		if (firstTag.equalsIgnoreCase("ws:webPlan")) {
			return routing.webPlan(router);
		} else if (firstTag.equalsIgnoreCase("ws:plan")) {
			return routing.plan(router);
		}
		return null;
	}
}
