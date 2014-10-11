package com.docucap.testsecm;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetServices")
public class Svlt_GetServices extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Svlt_GetServices() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,String> paramMap = Utilities.getParamMapFromBody(request);
		String email = paramMap.get("email");
		String json = getJsonForOAuthInfoForUserWithEmail(email);
		System.out.println("json:\n" + json);
		response.setContentType("application/json");
		response.getWriter().write(json);
	}

	private String getJsonForOAuthInfoForUserWithEmail(String email) throws ServletException {
		String authToken = SimpleECMCalls.getAuthTokenOfUserWithEmail(email);
		return SimpleECMCalls.callPortal("/users/provider_accounts", authToken);
	}
}
