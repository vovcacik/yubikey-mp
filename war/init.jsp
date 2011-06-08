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
<%  if (!isInitialized) {
	    %><title>Yubikey Master Password server - initialization</title><%
	} else {
	    %><title></title><%
	}
%>
</head>

<body onLoad="document.forms[0].elements[0].focus();">
<%  if (!isInitialized && auth != null && auth.verify()) {
    	final YubikeyPref pref = YubikeyPref.createInstance(auth.getStaticPart(), 1);
    	if (YubikeyServer.put(pref)) {
    		%>Initialization completed. New admin is: "<%= auth.getStaticPart() %>"<br /><br /><%
    	} else {
        	%>Initialization could not be completed.<br /><br /><%
    	}

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