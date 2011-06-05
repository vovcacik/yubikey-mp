<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.yubico.yubikeymp.*"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%
	// Atributes declaration
	boolean isInitialized = true;

	// Parameters
	final YubikeyOTP auth = YubikeyOTP.createInstance(request.getParameter("auth"));

	// Check if server need to initialize
	final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	final Query query = new Query("Prefs");
	Iterator<Entity> iterator = datastore.prepare(query).asIterator();
	isInitialized = iterator.hasNext();

%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<%  if (!isInitialized) {
	    %><title>Yubikey Master Password server - initialization</title><%
	} else {
	    %><title></title><%
	}
%>
</head>

<body onLoad="document.forms[0].elements[0].focus();">
<%  if (!isInitialized && auth != null && auth.verify()) {
    	Entity prefs = new Entity("Prefs");
    	prefs.setProperty("admin", auth.getStaticPart());
    	prefs.setProperty("clientID", 1);
    	datastore.put(prefs); //TODO <%= <%! how to use them?
    	%>Initialization completed. New admin is: "<%= auth.getStaticPart() %>"<br /><br /><%
    	
    	if (!KingdomKey.isSet()) {
    		// Show form for setting up kingdom key
	        %><form name="kk" action="/kk.jsp" method="post">
    			<label>
    				<span>Key to the kingdom</span>
 		   			<input type="text" name="kk" />
 		   		</label>
				<label>
					<span>Auth</span>
					<input type="text" name="auth" />
					<input type="submit" value="Submit" />
				</label>
			</form><%
    	}
	}
%>
</body>
</html>