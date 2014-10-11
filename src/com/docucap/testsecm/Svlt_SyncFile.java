package com.docucap.testsecm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * Servlet implementation class Svlt_StoreToService
 */
@WebServlet("/SyncFile")
@MultipartConfig(location="/tmp")
public class Svlt_SyncFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Svlt_SyncFile() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,String> paramMap = Utilities.getParamMapFromBody(request);
		StoreParamsRec paramsRec = handleInput(paramMap);
		callStoreToService(paramsRec, response);
	}
	
	private StoreParamsRec handleInput(Map<String, String> paramMap) throws ServletException, IOException {
		StoreParamsRec paramsRec = new StoreParamsRec();
		String email = paramMap.get("email");
		String userAuthCode = SimpleECMCalls.getAuthTokenOfUserWithEmail(email);
		String service = paramMap.get("service");
		String fileId = paramMap.get("fileId");
		String fileName = paramMap.get("fileName");
		String remoteAccount = paramMap.get("remoteAccount");
		String syncTo = paramMap.get("companyName") + "@gmail.com";
		String url = "https://services.simpleecm.com/api/v0/secm/documents/" + service + "/files/" + fileId + "?username=" + remoteAccount;
		paramsRec.fileToStore = RetrieveFile(url, userAuthCode);
		paramsRec.service = "google";
		paramsRec.folder = email;
		paramsRec.remoteName = fileName;
		paramsRec.remoteAccount = syncTo;
	    return paramsRec;
	}
	
	private File RetrieveFile(String url, String userAuthCode){
    	File file = null;
    	
    	DefaultHttpClient httpclient = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet(url);
    	SimpleECMCalls.setHeaders(httpGet, userAuthCode);
        
    	try{
            HttpResponse httpResponse = httpclient.execute(httpGet);
    		int stateCode = httpResponse.getStatusLine().getStatusCode();
    		if(stateCode == HttpStatus.SC_OK){
    			
    			String filename = "";
    			Header[] headers = httpResponse.getAllHeaders();    			
    			for(Header h : headers){
    				if(h.getName().equals("Content-Disposition")){
    					filename = h.toString().split("=")[1].replace("\"", "");
    					break;
    				}
    			}
    			file = ConvertInputStreamToFile(httpResponse.getEntity().getContent(), filename);	
            }
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    	}
    	finally{
    		httpclient.getConnectionManager().shutdown();
    	}
    	
    	return file;
    }
	
    private File ConvertInputStreamToFile(InputStream instream, String filename){
    	File file = new File(filename);
    	
    	OutputStream outstream = null;
    	try {
    		outstream = new FileOutputStream(file);
    		byte[] tmp = new byte[4096];
    		int l;
    		while ((l = instream.read(tmp)) != -1) {
    			outstream.write(tmp, 0, l);
	    	}
    		outstream.flush();
    	} 
    	catch(Exception ex){
    		ex.printStackTrace();
    	}
    	finally {
    		try {
    			if (instream != null) {
    				instream.close();
        		}
        		if (outstream != null) {
        			outstream.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	return file;
    }

	private void callStoreToService(StoreParamsRec paramsRec, HttpServletResponse response) throws IOException, ServletException {
		String userAuthCode = "";
		if (paramsRec.remoteAccount.equals("truckingcompanya@gmail.com"))
			userAuthCode = Config.COMPANY_A_AUTH_CODE;
		else userAuthCode = Config.COMPANY_B_AUTH_CODE;
	    String url = Config.SERVICE_URL + "/documents/" + paramsRec.service + "/files";
	    HttpPost httpPost = new HttpPost(url);
	    SimpleECMCalls.setHeaders(httpPost, userAuthCode);
	    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create(); 
	    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	    entityBuilder.addPart("file", new FileBody(paramsRec.fileToStore));
	    entityBuilder.addTextBody("remote_path", paramsRec.folder);
	    entityBuilder.addTextBody("remote_filename", paramsRec.remoteName);
	    entityBuilder.addTextBody("username", paramsRec.remoteAccount);
	    httpPost.setEntity(entityBuilder.build());
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		HttpClient httpClient = clientBuilder.build();
	    HttpResponse serverResponse = httpClient.execute(httpPost);
	    HttpEntity serverResponseEntity = serverResponse.getEntity();
        int statusCode = serverResponse.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            String errorStr = EntityUtils.toString(serverResponseEntity);
            System.out.println("statusCode: " + statusCode + "; errorStr: " + errorStr);
            throw new ServletException(errorStr);
        } else {
            if (serverResponseEntity != null) {
                String responseStr = EntityUtils.toString(serverResponseEntity);
                ContentType contentType = ContentType.getOrDefault(serverResponseEntity);
                String mimeType = contentType.getMimeType();
                if ("application/json".equals(mimeType)) {
            		response.setContentType("application/json");
            		response.getWriter().write(responseStr);
                } else {
                	throw new ServletException("Response from server wasn't JSON, but: " + mimeType);
                }
            } else {
            	throw new ServletException("The call to the server didn't return a response entity");
            }
        }
	}
	
	private static class StoreParamsRec
	{
		String service;
		String folder;
		String remoteName;
		String remoteAccount;
		File fileToStore;
	}

}
