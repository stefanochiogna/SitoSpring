<%@ page import="com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato" %>
<%@ page import="com.progetto.sitoforzearmate.model.mo.Utente.Amministratore" %>
<%@ page import="java.io.File" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.io.OutputStream" %>
<%@ page import="java.io.OutputStreamWriter" %>
<%@ page import="java.io.FileOutputStream" %>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Aeronautica Militare</title>

<%
    boolean loggedOn = false;
    UtenteRegistrato loggedUser = null;
    if(request.getAttribute("loggedOn") != null) {
        loggedOn = (Boolean) request.getAttribute("loggedOn");
        loggedUser = (UtenteRegistrato) request.getAttribute("loggedUser");
    }

    boolean loggedAdminOn = false;
    Amministratore loggedAdmin = null;
    if(request.getAttribute("loggedAdminOn") != null) {
        loggedAdminOn = (Boolean) request.getAttribute("loggedAdminOn");
        loggedAdmin = (Amministratore) request.getAttribute("loggedAdmin");
    }
%>

<link rel="shortcut icon" href="https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Coat_of_arms_of_the_Italian_Air_Force.svg/804px-Coat_of_arms_of_the_Italian_Air_Force.svg.png">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<!-- <link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbarCSS.css"> -->
<link rel="stylesheet" href="/css/styleCSS.css">



