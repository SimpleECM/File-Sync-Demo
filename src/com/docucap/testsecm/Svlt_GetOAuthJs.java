package com.docucap.testsecm;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetOAuthJs")
public class Svlt_GetOAuthJs extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Svlt_GetOAuthJs() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,String> paramMap = Utilities.getParamMapFromBody(request);
		String email = paramMap.get("email");
		String service = paramMap.get("service");
		String authToken = SimpleECMCalls.getAuthTokenOfUserWithEmail(email);
		if (authToken == null) {
			throw new ServletException("unable to obtain 'Access Code' for user with email: " + email
					);
		} else {
			String oauthJs = SimpleECMCalls.getOAuthJavaScriptForUserWithAuthToken(authToken, service);
			String amendedOAuthJs;
			try {
				amendedOAuthJs = Utilities.insertPopupRegistrationCallbackIntoOAuthJs(oauthJs);
			} catch (IOException e) {
				e.printStackTrace();
				amendedOAuthJs = oauthJs;
			}
			response.setContentType("text/plain");
			response.getWriter().write(amendedOAuthJs);
		}
	}

}
