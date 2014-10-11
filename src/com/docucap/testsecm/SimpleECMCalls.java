package com.docucap.testsecm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SimpleECMCalls {

	private static final boolean USE_GSON_FOR_PARSING = true;

	public static String getAuthTokenOfUserWithEmail(String email) throws ServletException {
		SimpleECMAccount account = getUserWithEmail(email);
		if (account == null) {
			return null;
		} else {
			return account.getAuthenticationToken();
		}
	}

	public static SimpleECMAccount getUserWithEmail(String email) throws ServletException {
		List<SimpleECMAccount> accounts = getAllAccounts(ForAppOrAdmin.FOR_ADMIN, "");
		for (SimpleECMAccount account : accounts) {
			if (account.getEmail().equals(email))
				return account;
		}
		return null;
	}

	public static List<SimpleECMAccount> getChildrenOfAccountWithEmail(String parentEmail) throws ServletException {
		String parentId = getIdOfAccountWithEmail(parentEmail);
		List<SimpleECMAccount> accounts = getAllAccounts(ForAppOrAdmin.FOR_ADMIN, "");
		List<SimpleECMAccount> retVal = new ArrayList<SimpleECMAccount>();
		for (SimpleECMAccount account : accounts) {
			if (account.getPublisherId().equals(parentId))
				retVal.add(account);
		}
		return retVal;
	}

	public static String getIdOfUserWithEmail(String emailOfUser) throws ServletException {
		List<SimpleECMAccount> accounts = getAllAccounts(ForAppOrAdmin.FOR_ADMIN, "");
		for (SimpleECMAccount account : accounts) {
			if (account.getEmail().equals(emailOfUser))
				return account.getId();
		}
		return null;
	}

	public static String getIdOfAccountWithEmail(String emailOfUser) throws ServletException {
		List<SimpleECMAccount> accounts = getAllAccounts(ForAppOrAdmin.FOR_ADMIN, "");
		for (SimpleECMAccount account : accounts) {
			if (account.getEmail().equals(emailOfUser))
				return account.getId();
		}
		return null;
	}
	
	public static SimpleECMAccount getPublisherOfAccount(SimpleECMAccount childAccount) throws ServletException {
		String publisherId = childAccount.getPublisherId();
		List<SimpleECMAccount> accounts = getAllAccounts(ForAppOrAdmin.FOR_ADMIN, "");
		for (SimpleECMAccount account : accounts) {
			String accountId = account.getId();
			if (publisherId.equals(accountId))
				return account;
		}
		return null;
	}

	public static String getOAuthJavaScriptForUserWithAuthToken(String authToken, String service) throws ServletException {
		String uriPath = "/users/oauth/app/keysets/" + service + "/javascript";
		String responseStr = SimpleECMCalls.callPortal(uriPath, authToken);
    	JSONTokener tokener = new JSONTokener(responseStr);
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(tokener);
			String[] fieldNames = JSONObject.getNames(jsonObj);
			if (fieldNames.length != 1 || !"code".equals(fieldNames[0])) {
				throw new ServletException("Expected JSON object with single field 'code'");
			}
			String code = jsonObj.getString("code");
			return code;
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	static List<SimpleECMAccount> getAllAccounts(ForAppOrAdmin appOrAdmin, String company) throws ServletException {
		String responseStr;
		List<SimpleECMAccount> allAccounts = new ArrayList<SimpleECMAccount>();
		if (appOrAdmin == ForAppOrAdmin.FOR_ADMIN) {
			SimpleECMAccount adminAccount = getAdminAccount();
			allAccounts.add(adminAccount);
			responseStr = SimpleECMCalls.callPortal("/users?page=1&per_page=1000", Config.TRIMBLE_AUTH_CODE);
		} else if (company == "truckingcompanya"){
			responseStr = SimpleECMCalls.callPortal("/users?page=1&per_page=1000", Config.COMPANY_A_AUTH_CODE);
		} else {
			responseStr = SimpleECMCalls.callPortal("/users?page=1&per_page=1000", Config.COMPANY_B_AUTH_CODE);
		}
		List<SimpleECMAccount> foundAccounts = parsePortalAccountList(responseStr);
		allAccounts.addAll(foundAccounts);
		return allAccounts;
	}

	private static SimpleECMAccount getAdminAccount() {
		return SimpleECMAccount.forAdmin();
	}

	private static List<SimpleECMAccount> getAllAccounts(String company) throws ServletException {
		return getAllAccounts(ForAppOrAdmin.FOR_APP, company);
	}

	private static List<SimpleECMAccount> parsePortalAccountList(String portalStr) throws ServletException {
		if (USE_GSON_FOR_PARSING) {
			return SimpleECMAccount.fromJSonArrayString(portalStr);
		} else {
	    	List<SimpleECMAccount> accounts = new ArrayList<SimpleECMAccount>();
	    	JSONTokener tokener = new JSONTokener(portalStr);
	    	JSONArray finalResult;
			try {
				finalResult = new JSONArray(tokener);
		    	int len = finalResult.length();
		    	for (int i = 0; i < len; i++) {
		    		JSONObject jsonObj = finalResult.getJSONObject(i);
					SimpleECMAccount acct = SimpleECMAccount.fromJson(jsonObj);
					accounts.add(acct);
		    	}
		        return accounts;
			} catch (JSONException e) {
				e.printStackTrace();
				throw new ServletException(e);
			}
		}
	}
	
	public static String callPortal(String uriPath, String authCode) throws ServletException {
		String url = uriPath.startsWith("http") ? uriPath : Config.SERVICE_URL + uriPath;
        HttpGet httpRequest = new HttpGet(url);
		setHeaders(httpRequest, authCode);
        try {
            return executeRequest(httpRequest);
        } catch (Exception e) {
            e.printStackTrace();
        	throw new ServletException(e);
        } finally {
        	((HttpRequestBase)httpRequest).releaseConnection();
        }
	}

	public static String callPortal(String uriPath, String authCode, String bodyJson) throws ServletException {
		return callPortal(RequestType.POST, uriPath, authCode, bodyJson);
	}

	public static String callPortal(RequestType rqType, String uriPath, String authCode, String bodyJson) throws ServletException {
		String url = uriPath.startsWith("http") ? uriPath : Config.SERVICE_URL + uriPath;
		HttpEntityEnclosingRequestBase httpRequest = rqType == RequestType.POST ? new HttpPost(url) : new HttpPut(url);
		setHeaders(httpRequest, authCode, true);
        try {
            HttpEntity bodyEntity = new StringEntity(bodyJson);
            httpRequest.setEntity(bodyEntity);
            return executeRequest(httpRequest);
        } catch (Exception e) {
            e.printStackTrace();
        	throw new ServletException(e);
        } finally {
        	((HttpRequestBase)httpRequest).releaseConnection();
        }
	}

	public static void setHeaders(HttpUriRequest httpRequest, String authCode) {
		setHeaders(httpRequest, authCode, false);
	}

	public static void setHeaders(HttpUriRequest httpRequest, String authCode, boolean setContentType) {
        String authHeaderValue;
        String authCodeAndPwd = authCode + ":X";
        String b64 = Utils.convertStringToBase64(authCodeAndPwd);
        authHeaderValue = "Basic " + b64;
        httpRequest.setHeader("Authorization", authHeaderValue);
        httpRequest.setHeader("Accept", "application/json");
        if (setContentType)
            httpRequest.setHeader("Content-Type", "application/json");
	}

	private static String executeRequest(HttpUriRequest httpRequest) throws IOException, ServletException {
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		HttpClient httpClient = clientBuilder.build();
        HttpResponse usersResponse = httpClient.execute(httpRequest);
        final HttpEntity usersResponseEntity = usersResponse.getEntity();
        StatusLine statusLine = usersResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        String reasonPhrase = statusLine.getReasonPhrase();
        if (statusCode != HttpStatus.SC_OK) {
            String body = EntityUtils.toString(usersResponseEntity);
            System.out.println("statusCode: " + statusCode + "; reasonPhrase: " + reasonPhrase + "; body: " + body);
            throw new ServletException("reasonPhrase: " + reasonPhrase + "; body: " + body);
        } else {
            if (usersResponseEntity != null) {
                String responseStr = EntityUtils.toString(usersResponseEntity);
                ContentType contentType = ContentType.getOrDefault(usersResponseEntity);
                String mimeType = contentType.getMimeType();
                //System.out.println("responseStr: " + responseStr);
                if ("application/json".equals(mimeType)) {
                	return responseStr;
                } else {
                	throw new ServletException("Response from server wasn't JSON, but: " + mimeType);
                }
            } else {
            	throw new ServletException("The call to the server didn't return a response entity");
            }
        }
	}
	
	public static enum RequestType {
		GET, POST, PUT
	}
	
	static enum ForAppOrAdmin {
		FOR_ADMIN, FOR_APP
	}

}
