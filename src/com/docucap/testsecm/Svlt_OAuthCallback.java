package com.docucap.testsecm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Svlt_OAuthCallback
 */
@WebServlet("/OAuthCallback")
public class Svlt_OAuthCallback extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Svlt_OAuthCallback() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGetOrPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGetOrPost(request, response);
	}
	
	private void doGetOrPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuilder sb = new StringBuilder();
		String method = request.getMethod();
		appendToBuilder(sb, "method", method);
		appendToBuilder(sb, "Headers", "");
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			String value = request.getHeader(headerName);
			appendToBuilder(sb, "  " + headerName, value);
		}
		appendToBuilder(sb, "Parameters", "");
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String value = request.getParameter(paramName);
			appendToBuilder(sb, "  " + paramName, value);
		}
		String contentType = request.getContentType();
		appendToBuilder(sb, "contentType", contentType);
		BufferedReader reader = request.getReader();
		StringBuilder bodyBuilder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null)
			bodyBuilder.append(line).append('\n');
		appendToBuilder(sb, "body", bodyBuilder.toString());
		File outFile = new File("/tmp/callbackReport.txt");
		Writer fileWriter = new FileWriter(outFile);
		fileWriter.write(sb.toString());
		fileWriter.close();
	}

	private void appendToBuilder(StringBuilder sb, String name, String value) {
		sb.append(name).append(": ").append(value).append('\n');
	}

}
