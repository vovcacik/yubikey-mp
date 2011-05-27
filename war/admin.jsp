<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Yubikey Master Password</title>
</head>
<body>
<%
	final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	final Query query = new Query("Prefs");
	query.addFilter("initialized", Query.FilterOperator.EQUAL, "true");
	List<Entity> prefs = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1));
	
	if (prefs.isEmpty()){
	    Entity init = new Entity("Prefs");
		init.setProperty("initialized", "true");
		
		Entity admin = new Entity("Prefs");
		admin.setProperty("admin", "");
		
	    Entity clientID = new Entity("Prefs");
		clientID.setProperty("clientid", "");
		
		Entity secret = new Entity("Secrets");
		secret.setProperty("user", "");
		secret.setProperty("pid", "");
		secret.setProperty("secret", "");
		
		datastore.put(init);
		datastore.put(admin);
		datastore.put(clientID);
		datastore.put(secret);
%>initialization successful<%
	}
%>
</body>
</html>