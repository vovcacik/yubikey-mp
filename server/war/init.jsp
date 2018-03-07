<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.yubico.yubikeymp.YubikeyServer"%>
<%@ page import="com.yubico.yubikeymp.YubikeyOTP"%>
<%@ page import="com.yubico.yubikeymp.YubikeyPref"%>
<%@ page import="com.yubico.yubikeymp.KingdomKey"%>
<%
	final YubikeyOTP auth = YubikeyOTP.createInstance(request.getParameter("auth"));
	final boolean isInitialized = YubikeyServer.isInitialized();

%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<title>Yubikey Master Password server - initialization</title>
</head>

<body onLoad="document.forms[0].elements[0].focus();">
<%  if (auth != null && auth.verify()) {
    	if (!isInitialized) {
	    	final YubikeyPref pref = YubikeyPref.createInstance(auth.getStaticPart(), 1);
	    	if (YubikeyServer.put(pref)) {
	    		%>Initialization completed. New admin is: "<%= auth.getStaticPart() %>"<%
		    	
	    		if (!KingdomKey.isSet()) {
		    		// Show form for setting up kingdom key
			        %><br /><br />
			        <form name="kk" action="/kk.jsp" method="post">
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
	    	} else {
	        	%>Initialization could not be completed.<%
	        	
	        	// Show initialize form
	        	%><br /><br />
	        	<form name="init" action="/init.jsp" method="post">
	    			<label>
	    				<span>Auth</span>
	    				<input type="text" name="auth" />
	    				<input type="submit" value="Submit" />
	    			</label>
	    		</form><%
	    	}
    	} else {
    	    %>Server is already initialized.<%
    	    response.setHeader("Refresh", "3; url=/");
    	}
	} else {
	 	// UNAUTHORIZED
	    response.sendError(401);
	}
%>
</body>
</html>