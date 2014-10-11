package com.docucap.testsecm;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetUser")
public class Svlt_GetUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Svlt_GetUser() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,String> paramMap = Utilities.getParamMapFromBody(request);
		String email = paramMap.get("email");
		SimpleECMAccount acct = SimpleECMCalls.getUserWithEmail(email);
		String json = acct.toFullJson();
		response.setContentType("application/json");
		response.getWriter().write(json);
	}
}
