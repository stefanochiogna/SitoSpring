<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String applicationMessage = (String) request.getAttribute("applicationMessage"); %>
<html>
<head>
    <title>Login Amministratore</title>
</head>
<body>
    <form name="logonFormAmministratore" action="Dispatcher" method="post">

        <label for="IdAdmin">IdAmministratore</label>
        <input type="text" id="IdAdmin"  name="IdAdministrator" maxlength="40" required><br>

        <label for="Password">Password</label>
        <input type="password" id="Password" name="Password" maxlength="40" required><br>

        <input type="hidden" name="controllerAction" value="Login.loginAmministratore"/>
        <input type="submit" value="Ok">
    </form>
    <%if(applicationMessage != null){%>
        <p><%=applicationMessage%></p>
    <%}%>
    <br>
    <a href="Dispatcher?controllerAction=Login.view">Indietro</a>
</body>
</html>
