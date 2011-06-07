<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.yubico.yubikeymp.*"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%
	// Atributes declaration
	boolean isAdmin = false;
    String admin = YubikeyUtil.getAdminName();

	// Parameters
	final YubikeyOTP auth = YubikeyOTP.createInstance(request.getParameter("auth"));
	isAdmin = YubikeyUtil.isAdminsOTP(auth);
	String kk = request.getParameter("kk");

%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<%  if (isAdmin) {
	    %><title>Yubikey Master Password server - key to the kingdom</title><%
	} else {
	    %><title></title><%
	}
%>
</head>

<body>
<%  if (isAdmin && !KingdomKey.isSet() && kk != null) {
    	if (KingdomKey.setKey(kk)) {
    	    %>Key to the kingdom set successfully.<%
    	} else {
    	    %>Key to the kingdom cannot be set.<%
    	}
    	kk = null;
	}
%>
</body>
</html>