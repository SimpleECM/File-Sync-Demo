package com.docucap.testsecm;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetFiles")
public class Svlt_GetFiles extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Svlt_GetFiles() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,String> paramMap = Utilities.getParamMapFromBody(request);
		String jsonResponse = askPortalToListServiceFolder(paramMap);
		response.setContentType("application/json");
		response.getWriter().write(jsonResponse);
	}

	private String askPortalToListServiceFolder(Map<String, String> paramMap) throws ServletException {
		String email = paramMap.get("email");
		String userAuthCode = SimpleECMCalls.getAuthTokenOfUserWithEmail(email);
		String service = paramMap.get("service");
		String folderId = paramMap.get("folderId");
		String remoteAccount = paramMap.get("remoteAccount");
		String url = Config.SERVICE_URL + "/documents/" + service + "/folders/" + folderId + "/items?username=" + remoteAccount;
		return SimpleECMCalls.callPortal(url, userAuthCode);
	}

}
