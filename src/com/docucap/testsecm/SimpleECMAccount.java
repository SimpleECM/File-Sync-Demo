package com.docucap.testsecm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class SimpleECMAccount {
	private static final Map<String, JsonType> nameToTypeMap = new HashMap<String, JsonType>();
	private static final Map<String, NotificationMethod> stringToNotificationMethodMap = new HashMap<String, NotificationMethod>();
	private static final Map<String, Role> stringToRoleMap = new HashMap<String, Role>();
	private static final boolean PRINT_PROCESSING_NOTICES = false;
	private static final List<String> stdAuthorizedRoutes = new ArrayList<String>();
	private static final Map<NotificationMethod, String> MethodToStringMap = new HashMap<NotificationMethod, String>();
	private static final Map<String, NotificationMethod> StringToMethodMap = new HashMap<String, NotificationMethod>();

	private String _id;
	private String publisher_id;
	private String authentication_token;
	private String first_name;
	private String last_name;
	private String company_name;
	private String email;
	private String phone_number;
	private NotificationMethod notification_method;
	@SuppressWarnings("unused")
	private Role role;
	@SuppressWarnings("unused")
	private String current_sign_in_at;
	@SuppressWarnings("unused")
	private String current_sign_in_ip;
	@SuppressWarnings("unused")
	private String last_sign_in_at;
	@SuppressWarnings("unused")
	private String last_sign_in_ip;
	@SuppressWarnings("unused")
	private int sign_in_count;
	@SuppressWarnings("unused")
	private String logo_url;
	@SuppressWarnings("unused")
	private String logo_file_name;
	private List<String> authorized_routes;
	@SuppressWarnings("unused")
	private List<String> tag_ids;
	@SuppressWarnings("unused")
	private Keychain keychain;
	private String password;
	
	static {
		nameToTypeMap.put("_id", JsonType.STRING);
		nameToTypeMap.put("publisher_id", JsonType.STRING);
		nameToTypeMap.put("authentication_token", JsonType.STRING);
		nameToTypeMap.put("first_name", JsonType.STRING);
		nameToTypeMap.put("last_name", JsonType.STRING);
		nameToTypeMap.put("company_name", JsonType.STRING);
		nameToTypeMap.put("email", JsonType.STRING);
		nameToTypeMap.put("phone_number", JsonType.STRING);
		nameToTypeMap.put("notification_method", JsonType.STRING);
		nameToTypeMap.put("role", JsonType.STRING);
		nameToTypeMap.put("current_sign_in_ip", JsonType.STRING);
		nameToTypeMap.put("current_sign_in_at", JsonType.STRING);
		nameToTypeMap.put("last_sign_in_ip", JsonType.STRING);
		nameToTypeMap.put("last_sign_in_at", JsonType.STRING);
		nameToTypeMap.put("sign_in_count", JsonType.INT);
		nameToTypeMap.put("logo_url", JsonType.STRING);
		nameToTypeMap.put("logo_file_name", JsonType.STRING);
		nameToTypeMap.put("authorized_routes", JsonType.ARRAY);
		nameToTypeMap.put("tag_ids", JsonType.ARRAY);
		nameToTypeMap.put("keychain", JsonType.OBJECT);

		stringToNotificationMethodMap.put("email", NotificationMethod.EMAIL);
		stringToNotificationMethodMap.put("sms", NotificationMethod.SMS);

		stringToRoleMap.put("consumer", Role.CONSUMER);
		
		stdAuthorizedRoutes.add("GET/oauth/csrf_token");
		stdAuthorizedRoutes.add("GET/users/provider_accounts");
		stdAuthorizedRoutes.add("GET/users/oauth/app/keysets/:provider/javascript");
		stdAuthorizedRoutes.add("GET/roles");
		stdAuthorizedRoutes.add("GET/api_docs");
		stdAuthorizedRoutes.add("POST/oauth/exchange_for_token");
		stdAuthorizedRoutes.add("POST/users/authenticate");
		stdAuthorizedRoutes.add("DELETE/users/oauth/app/domains/:domain");
		
		stdAuthorizedRoutes.add("GET/documents/box/files/:file_id");
		stdAuthorizedRoutes.add("GET/documents/box/folders/:folder_id/items");
		stdAuthorizedRoutes.add("POST/documents/box/files");
		stdAuthorizedRoutes.add("POST/documents/box/folders");
		stdAuthorizedRoutes.add("DELETE/documents/box/files/:file_id");
		stdAuthorizedRoutes.add("DELETE/documents/box/folders/:folder_id");
		
		stdAuthorizedRoutes.add("GET/documents/dropbox/files/:file_id");
		stdAuthorizedRoutes.add("GET/documents/dropbox/folders/:folder_id/items");
		stdAuthorizedRoutes.add("POST/documents/dropbox/files");
		stdAuthorizedRoutes.add("POST/documents/dropbox/folders");
		stdAuthorizedRoutes.add("DELETE/documents/dropbox/files/:file_id");
		stdAuthorizedRoutes.add("DELETE/documents/dropbox/folders/:folder_id");
		
		stdAuthorizedRoutes.add("GET/documents/google/files/:file_id");
		stdAuthorizedRoutes.add("GET/documents/google/folders/:folder_id/items");
		stdAuthorizedRoutes.add("POST/documents/google/files");
		stdAuthorizedRoutes.add("POST/documents/google/folders");
		stdAuthorizedRoutes.add("DELETE/documents/google/files/:file_id");
		stdAuthorizedRoutes.add("DELETE/documents/google/folders/:folder_id");
	}
	
	private enum JsonType {
		STRING, INT, BOOLEAN, ARRAY, OBJECT, DOUBLE, LONG
	}
	
	private enum NotificationMethod {
		
		EMAIL, SMS;
		
		static {
			MethodToStringMap.put(EMAIL, "email");
			MethodToStringMap.put(SMS, "sms");
			StringToMethodMap.put("email", EMAIL);
			StringToMethodMap.put("sms", SMS);
		}
		
	    public String toJsonString() {
	    	return MethodToStringMap.get(this);
	    }

		public static NotificationMethod forString(String string) {
			return StringToMethodMap.get(string);
		}
	}
	
	private enum Role {
		CONSUMER
	}

	public static SimpleECMAccount stdAcctForProperties(Map<String, String> paramMap) {
		SimpleECMAccount inst = new SimpleECMAccount();
		inst.company_name = paramMap.get("companyName");
		inst.email = paramMap.get("email");
		inst.password = paramMap.get("password");
		inst.phone_number = paramMap.get("phoneNumber");
		inst.notification_method = NotificationMethod.forString(paramMap.get("notificationMethod"));
		inst.first_name = paramMap.get("firstName");
		inst.last_name = paramMap.get("lastName");
		inst.authorized_routes = stdAuthorizedRoutes;
		return inst;
	}

	public static List<SimpleECMAccount> fromJSonArrayString(String jsonArrayStr) {
		Gson gson = new Gson();
		SimpleECMAccount[] acctRecs = gson.fromJson(jsonArrayStr, SimpleECMAccount[].class);
		List<SimpleECMAccount> accounts = new ArrayList<SimpleECMAccount>();
		for (SimpleECMAccount rec : acctRecs) {
			accounts.add(rec);
		}
		return accounts;
	}

	public static SimpleECMAccount fromJson(JSONObject jsonObj) {
		SimpleECMAccount inst = new SimpleECMAccount();
		JSONArray names = jsonObj.names();
		int nNames = names.length();
		for (int nameI = 0; nameI < nNames; nameI++) {
			try {
				String name = names.getString(nameI);
				inst.populateField(name, jsonObj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return inst;
	}
	
	public static SimpleECMAccount forAdmin() {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("firstName", "Xavier");
		paramMap.put("lastName", "Thore");
		paramMap.put("companyName", "Trimble");
		paramMap.put("email", "xavier.thore@trimbletl.com");
		paramMap.put("password", "");
		paramMap.put("phoneNumber", "");
		paramMap.put("notificationMethod", "Email");
		SimpleECMAccount inst = stdAcctForProperties(paramMap);
		inst._id = "532739504433ab4b9600000f";
		inst.authentication_token = Config.TRIMBLE_AUTH_CODE;
		inst.publisher_id = "unknownPublisherId";
		return inst;
	}

	private SimpleECMAccount() {
		//private constructor
	}

	public String toFullJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public String toJsonForCreation() {
		Gson gson = new Gson();
		return gson.toJson(AcctRecForCreation.fromAccount(this));
	}
	
	private static class AcctRecForCreation {
		@SuppressWarnings("unused")
		private List<String> authorized_routes;
		@SuppressWarnings("unused")
		private String company_name;
		@SuppressWarnings("unused")
		private String email;
		@SuppressWarnings("unused")
		private String first_name;
		@SuppressWarnings("unused")
		private String last_name;
		@SuppressWarnings("unused")
		private String notification_method;
		@SuppressWarnings("unused")
		private String phone_number;
		@SuppressWarnings("unused")
		private String password;
		
		private static AcctRecForCreation fromAccount(SimpleECMAccount acct) {
			AcctRecForCreation inst = new AcctRecForCreation();
			inst.authorized_routes = acct.authorized_routes;
			inst.company_name = acct.company_name;
			inst.email = acct.email;
			inst.first_name = acct.first_name;
			inst.last_name = acct.last_name;
			inst.notification_method = acct.notification_method.toJsonString();
			inst.phone_number = acct.phone_number;
			inst.password = acct.password;
			return inst;
		}
	}

	public String getId() {
		return _id;
	}
	
	public String getEmail() {
		return email;
	}

	public String getAuthenticationToken() {
		return authentication_token;
	}

	public Collection<String> getAuthorizedRoutes() {
		return this.authorized_routes;
	}

	public String getPublisherId() {
		return publisher_id;
	}
	
	private void populateField(String name, JSONObject jsonObj) throws JSONException {
		if (unexpectedName(name)) {
			// Throw exception, because we've never heard of this field.
			throw new IllegalStateException("Don't yet know how to handle JSON field: " + name);
		}
		Object valObj = jsonObj.get(name);
		if (isStringField(name)) {
			String val = jsonObj.getString(name);
			setField(name, val);
		} else if (isIntField(name)) {
			int val = jsonObj.getInt(name);
			setField(name, val);
		} else if (isBooleanField(name)) {
			boolean val = jsonObj.getBoolean(name);
			setField(name, val);
		} else if (isArrayField(name)) {
			JSONArray val = jsonObj.getJSONArray(name);
			setField(name, val);
		} else if (isObjectField(name)) {
			JSONObject val = jsonObj.getJSONObject(name);
			setField(name, val);
		} else if (isDoubleField(name)) {
			double val = jsonObj.getDouble(name);
			setField(name, val);
		} else if (isLongField(name)) {
			long val = jsonObj.getLong(name);
			setField(name, val);
		}
		if (PRINT_PROCESSING_NOTICES)
			System.out.println("name: " + name + "; valObj: " + valObj);
	}

	private void setField(String name, String val) {
		if ("_id".equals(name))
			this._id = val;
		else if ("publisher_id".equals(name))
			this.publisher_id = val;
		else if ("authentication_token".equals(name))
			this.authentication_token = val;
		else if ("first_name".equals(name))
			this.first_name = val;
		else if ("last_name".equals(name))
			this.last_name = val;
		else if ("company_name".equals(name))
			this.company_name = val;
		else if ("email".equals(name))
			this.email = val;
		else if ("phone_number".equals(name))
			this.phone_number = val;
		else if ("notification_method".equals(name)) {
			if (!stringToNotificationMethodMap.containsKey(val)) {
				// Throw exception, because we've never heard of this role.
				throw new IllegalStateException("Don't yet know how to handle notification_method: " + val);
			}
			this.notification_method = stringToNotificationMethodMap.get(val);
		}
		else if ("role".equals(name)) {
			if (!stringToRoleMap.containsKey(val)) {
				// Throw exception, because we've never heard of this role.
				throw new IllegalStateException("Don't yet know how to handle role: " + val);
			}
			this.role = stringToRoleMap.get(val);
			
		}
		else if ("current_sign_in_ip".equals(name))
			this.current_sign_in_ip = val;
		else if ("current_sign_in_at".equals(name))
			this.current_sign_in_at = val;
		else if ("last_sign_in_at".equals(name))
			this.last_sign_in_at = val;
		else if ("last_sign_in_ip".equals(name))
			this.last_sign_in_ip = val;
		else if ("logo_url".equals(name))
			this.logo_url = val;
		else if ("logo_file_name".equals(name))
			this.logo_file_name = val;
		else {
			// Throw exception, because we've never heard of this field.
			throw new IllegalStateException("Don't yet know how to handle JSON field: " + name);
		}
	}

	private void setField(String name, int val) {
		if ("sign_in_count".equals(name)) {
			this.sign_in_count = val;
		} else {
			// Throw exception, because this is the only *jsonObject* fields in this object.
			throw new NoSuchFieldError("Didn't expect field '" + name + "' with type: int");
		}
	}

	private void setField(String name, boolean val) {
		// Throw exception, because we have no *boolean* fields in this object.
		throw new NoSuchFieldError("Didn't expect field '" + name + "' with type: boolean");
	}

	private void setField(String name, JSONArray val) throws JSONException {
		if ("tag_ids".equals(name)) {
			this.tag_ids = readStringArray(val);
		} else if ("authorized_routes".equals(name)) {
			this.authorized_routes = readStringArray(val);
		} else {
			// Throw exception, because those are the only *array* fields in this object.
			throw new NoSuchFieldError("Didn't expect field '" + name + "' with type: JSONArray");
		}
	}

	private void setField(String name, JSONObject val) {
		if ("keychain".equals(name)) {
			this.keychain = Keychain.fromJsonObject(val);
		} else {
			// Throw exception, because this is the only *jsonObject* fields in this object.
			throw new NoSuchFieldError("Didn't expect field '" + name + "' with type: JSONObject");
		}
	}

	private void setField(String name, double val) {
		// Throw exception, because we have no *double* fields in this object.
		throw new NoSuchFieldError("Didn't expect field '" + name + "' with type: double");
	}

	private void setField(String name, long val) {
		// Throw exception, because we have no *long* fields in this object.
		throw new NoSuchFieldError("Didn't expect field '" + name + "' with type: long");
	}

	private boolean unexpectedName(String name) {
		return !nameToTypeMap.containsKey(name);
	}

	private boolean isStringField(String name) {
		return nameToTypeMap.get(name) == JsonType.STRING;
	}

	private boolean isIntField(String name) {
		return nameToTypeMap.get(name) == JsonType.INT;
	}

	private boolean isBooleanField(String name) {
		return nameToTypeMap.get(name) == JsonType.BOOLEAN;
	}

	private boolean isArrayField(String name) {
		return nameToTypeMap.get(name) == JsonType.ARRAY;
	}

	private boolean isObjectField(String name) {
		return nameToTypeMap.get(name) == JsonType.OBJECT;
	}

	private boolean isDoubleField(String name) {
		return nameToTypeMap.get(name) == JsonType.DOUBLE;
	}

	private boolean isLongField(String name) {
		return nameToTypeMap.get(name) == JsonType.LONG;
	}

	private List<String> readStringArray(JSONArray val) throws JSONException {
		List<String> strings = new ArrayList<String>();
		int nStrings = val.length();
		for (int i = 0; i < nStrings; i++) {
			String string = val.getString(i);
			strings.add(string);
		}
		return strings;
	}

	public void addRoutesForService(String service) {
		List<String> newRoutes = new ArrayList<String>();
		newRoutes.add("GET/documents/" + service + "/files/:file_id");
		newRoutes.add("GET/documents/" + service + "/folders/:folder_id/items");
		newRoutes.add("POST/documents/" + service);
		newRoutes.add("POST/documents/" + service + "/files");
		newRoutes.add("POST/documents/" + service + "/folders");
		newRoutes.add("DELETE/documents/" + service + "/files/:file_id");
		newRoutes.add("DELETE/documents/" + service + "/folders/:folder_id");
		for (String route : newRoutes) {
			if (!authorized_routes.contains(route))
				authorized_routes.add(route);
		}
	}

	public void addRoutesForEFax() {
		String efaxRoute = "POST/efax/send";
		if (!authorized_routes.contains(efaxRoute))
			authorized_routes.add(efaxRoute);
	}
	
	private static class Keychain
	{
		//TODO fill out this class some, when we know more about the object
		
		@SuppressWarnings("unused")
		private JSONObject myObj;

		public static Keychain fromJsonObject(JSONObject val) {
			Keychain inst = new Keychain();
			inst.myObj = val;
			return inst;
		}
		
		private Keychain() {
			//private constructor
		}
	}

}
