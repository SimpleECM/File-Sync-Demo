package com.docucap.testsecm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Utilities {
	
	private Utilities() {
		//Never called.
		//Defined here so class can't be instantiated.
	}
	
	public static Map<String,String> getParamMapFromBody(HttpServletRequest request) throws IOException {
		int nChars = request.getContentLength();
		InputStream inStream = request.getInputStream();
		byte[] contentBytes = new byte[nChars];
		inStream.read(contentBytes);
		String content = new String(contentBytes);
		System.out.println("content: " + content);
		Map<String,String> paramMap = new HashMap<String,String>();
		String[] defs = content.split("&");
		for (String def : defs) {
			String[] nameAndVal = def.split("=");
			String name = nameAndVal[0];
			String val = URLDecoder.decode(nameAndVal[1], "UTF-8") ;
			System.out.println("name: " + name + "; val: " + val);
			paramMap.put(name, val);
		}
		return paramMap;
	}
	
	public static List<SimpleECMAccount> getUsersOfAppWithAuthToken(String appAuthToken) throws ServletException {
		String jsonArrayStr = SimpleECMCalls.callPortal("/users", appAuthToken);
		return SimpleECMAccount.fromJSonArrayString(jsonArrayStr);
	}

	public static String insertPopupRegistrationCallbackIntoOAuthJs(String oauthJs) throws IOException {
		StringBuffer amended = new StringBuffer();
		BufferedReader reader = new BufferedReader(new StringReader(oauthJs));
		String line;
		boolean found = false;
		while ((line = reader.readLine()) != null) {
			if (found)
				amended.append(line).append('\n');
			else {
				if (line.indexOf("if (wnd)") < 0) {
					amended.append(line).append('\n');
				} else {
					reader.readLine();  //discarding the "wnd.focus();" line
					amended.append("                if (wnd) {\n");
					amended.append("                    if (wnd.opener && wnd.opener.registerOAuthPopup)\n");
					amended.append("                        wnd.opener.registerOAuthPopup(wnd);\n");
					amended.append("                    wnd.focus();\n");
					amended.append("                }\n");
					found = true;
				}
			}
		}
		return amended.toString();
	}
	
	public static String convertStringToBase64(String str) {
		return DatatypeConverter.printBase64Binary(str.getBytes());
	}

	public static String parseBase64ToString(String base64) {
		return new String(DatatypeConverter.parseBase64Binary(base64));
	}

	public static String getLogFileName() {
		File logDir = getLogDir();
		File logFile = new File(logDir, "dirLog.txt");
		return logFile.getPath();
	
	}
	
	private static File getLogDir() {
		File parentDir = new File("/tmp");
		File logDir = new File(parentDir, "dirLog");
		if (!logDir.exists()) {
			logDir.mkdir();
		}
		return logDir;
	}
	
	public static String longDescripOfError(Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append("Encountered Exception of class: " + e.getClass().getName()).append("\n");
		sb.append("  Message: " + e.getMessage()).append("\n");
		sb.append("  Stack trace:\n").append("\n");
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		e.printStackTrace(writer);
		sb.append(sw.toString()).append("\n");
		return sb.toString();
	}

	public static Document createDomDoc(String xmlString) throws JDOMException, IOException {
		return createDomDoc(xmlString.getBytes());
	}

	public static Document createDomDoc(byte[] bytes) throws JDOMException, IOException {
		InputStream inStream = new ByteArrayInputStream(bytes);
		return createDomDoc(inStream);
	}

	public static Document createDomDoc(InputStream inStream) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		return builder.build(inStream);
	}
}
