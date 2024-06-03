<%@ page import="com.example.sitoforzaarmata.model.mo.Utente.UtenteRegistrato" %>
<%@ page import="com.example.sitoforzaarmata.model.mo.Utente.Amministratore" %>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Aeronautica Militare</title>

<%
    boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    UtenteRegistrato loggedUser = (UtenteRegistrato) request.getAttribute("loggedUser");

    boolean loggedAdminOn = (Boolean) request.getAttribute("loggedAdminOn");
    Amministratore loggedAdmin = (Amministratore) request.getAttribute("loggedAdmin");
%>

<link rel="shortcut icon" href="https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Coat_of_arms_of_the_Italian_Air_Force.svg/804px-Coat_of_arms_of_the_Italian_Air_Force.svg.png">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<!-- <link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbarCSS.css"> -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/WEB-INF/css/styleCSS.css">

