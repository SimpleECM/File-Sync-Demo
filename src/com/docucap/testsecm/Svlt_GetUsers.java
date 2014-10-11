package com.docucap.testsecm;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetUsers")
public class Svlt_GetUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Svlt_GetUsers() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,String> paramMap = Utilities.getParamMapFromBody(request);
		String company = paramMap.get("company");
		String authCode = "";
		if (company.equals("truckingcompanya"))
			authCode = Config.COMPANY_A_AUTH_CODE;
		else authCode = Config.COMPANY_B_AUTH_CODE;
		String json = SimpleECMCalls.callPortal("/users", authCode);
		response.getWriter().write(json);
	}
}
