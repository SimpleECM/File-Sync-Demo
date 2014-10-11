package com.docucap.testsecm;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AddUser")
public class Svlt_AddUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Svlt_AddUser() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,String> paramMap = Utilities.getParamMapFromBody(request);
		String company = paramMap.get("company");
        SimpleECMAccount account = SimpleECMAccount.stdAcctForProperties(paramMap);
        String creationBodyJson = account.toJsonForCreation();
        String authCode = "";
        Logging.trace(company);
        if (company.equals("truckingcompanya"))
        	authCode = Config.COMPANY_A_AUTH_CODE;
        else authCode = Config.COMPANY_B_AUTH_CODE;
        String responseJson = SimpleECMCalls.callPortal("/users", authCode, creationBodyJson);
		response.setContentType("application/json");
		response.getWriter().write(responseJson);
	}
}
