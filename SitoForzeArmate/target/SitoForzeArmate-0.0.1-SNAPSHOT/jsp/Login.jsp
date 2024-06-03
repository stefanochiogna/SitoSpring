<%@ page import="com.example.sitoforzaarmata.model.mo.Utente.UtenteRegistrato" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    //boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
    //UtenteRegistrato loggedUser = (UtenteRegistrato) request.getAttribute("loggedUser");
    String applicationMessage = (String) request.getAttribute("applicationMessage");
    //String menuActiveLink = "Login";
%>

<html>
<head>
    <title>Login</title>

    <script>
        function headerOnLoadHandler() {
            var EmailTextField = document.querySelector("#Email");
            var EmailTextFieldMsg = "La mail \xE8 obbligatoria.";
            var passwordTextField = document.querySelector("#Password");
            var passwordTextFieldMsg = "La password \xE8 obbligatoria.";

            if (EmailTextField != undefined && passwordTextField != undefined ) {
                EmailTextField.setCustomValidity(EmailTextFieldMsg);    // custom error message
                EmailTextField.addEventListener("change", function () {
                    this.setCustomValidity(this.validity.valueMissing ? EmailTextFieldMsg : "");
                });
                passwordTextField.setCustomValidity(passwordTextFieldMsg);
                passwordTextField.addEventListener("change", function () {
                    this.setCustomValidity(this.validity.valueMissing ? passwordTextFieldMsg : "");
                });
            }
        }
    </script>
</head>
<body>
    <form name="logonForm" action="Dispatcher" method="post">

        <label for="Email">E-mail</label>
        <input type="text" id="Email"  name="Email" maxlength="40" required><br>

        <label for="Password">Password</label>
        <input type="password" id="Password" name="Password" maxlength="40" required><br>

        <input type="hidden" name="controllerAction" value="Login.login"/>

        <input type="submit" value="Ok">
    </form>
    <%if(applicationMessage != null){%>
        <p><%=applicationMessage%></p>
    <%}%>
    <br>
    <p>
        Se non hai ancora un account: <a href="Dispatcher?controllerAction=Login.viewRegistrazione">Registrati</a>
    </p>
    <p>
        Se invece sei un <a href="Dispatcher?controllerAction=Login.viewAmministratore">Amministratore</a>
    </p>
</body>
</html>
