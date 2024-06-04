<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String applicationMessage = (String) request.getAttribute("applicationMessage"); %>
<html>
<head>
    <title>Login Amiminitratore</title>
    <style>
        body {
            background-color: #f3f4f6;
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh; /* Impedisce che il div superi l'altezza dello schermo */
            margin: 0; /* Rimuove il margine di default del body */
        }

        .container {
            max-width: 28rem;
            background-color: #ffffff;
            padding: 1.25rem;
            border: 1px solid #e2e8f0;
            border-radius: 0.25rem;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
        }
        form {
            margin: 0;
        }
    </style>
</head>
<body class="min-h-screen flex items-center justify-center">
<div class="container">
    <h1 style="font-size: 1.5rem; font-weight: bold; margin-bottom: 1rem;">Login Amministratore</h1>
    <form name="logonFormAmministratore" action="/loginAdmin" method="post">
        <div style="margin-bottom: 1rem;">
            <label for="IdAdmin" style="font-weight: bold; color: #4a5568;">IdAmministratore</label>
            <input type="text" id="IdAdmin" name="IdAdministrator" maxlength="40" required
                   style="width: 100%; padding: 0.5rem; border: 1px solid #e2e8f0; border-radius: 0.25rem; font-size: 14px;">
        </div>
        <div style="margin-bottom: 1rem;">
            <label for="Password" style="font-weight: bold; color: #4a5568;">Password</label>
            <input type="password" id="Password" name="Password" maxlength="40" required
                   style="width: 100%; padding: 0.5rem; border: 1px solid #e2e8f0; border-radius: 0.25rem; font-size: 14px;">
        </div>
        <input type="submit" value="Ok"
               style="width: 100%; padding: 0.75rem 1rem; background-color: #4299e1; color: #ffffff; border: none; border-radius: 0.25rem; cursor: pointer; font-size: 16px;">
    </form>
    <%if(applicationMessage != null){%>
    <p style="color: #4a5568;"><%=applicationMessage%></p>
    <%}%>
    <br>
    <a href="/viewLogin"
       style="display: block; color: #4299e1; text-decoration: none; font-size: 14px;">Indietro</a>
</div>
</body>

</html>
