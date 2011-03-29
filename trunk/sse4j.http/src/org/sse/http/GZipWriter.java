package org.sse.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.sse.ws.base.WSResult;

public class GZipWriter {
	private final static String charset = "UTF-8";
	private final static String contentTypeZip = "application/zip";

	//	<return>
	//    	<faultString></faultString>
	//    	<jsonString></jsonString>
	//    	<resultCode></resultCode>
	//  </return>
	public static void write(WSResult result, HttpServletResponse response)
			throws IOException {
		if (result == null)
			return;
		StringBuffer sb = new StringBuffer();
		sb.append("<return><faultString>").append(result.getFaultString());
		sb.append("</faultString><jsonString>").append(result.getJsonString());
		sb.append("</jsonString><resultCode>").append(result.getResultCode());
		sb.append("</resultCode></return>");

		response.setCharacterEncoding(charset);
		response.setHeader("Content-disposition", "attachment;filename="
				+ System.currentTimeMillis() + ".zip");
		response.setContentType(contentTypeZip);
		OutputStream ops = response.getOutputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(bos);
		gos.write(sb.toString().getBytes(charset));
		gos.finish();
		gos.close();

		response.setContentLength(bos.size());
		ops.write(bos.toByteArray());
		bos.flush();
		bos.close();
		ops.flush();
		ops.close();
	}
}
