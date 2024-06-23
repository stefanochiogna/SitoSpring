<!DOCTYPE html>

<% String applicationMessage = (String) request.getAttribute("applicationMessage"); %>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Page</title>

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

<!--
box-shadow([orizzontale] [verticale] [sfocatura] [spessore] [colore])
spessore di -1px: l'ombra si estende leggermente all'interno dell'elemento
-->

<body style="background-color: #f3f4f6;">
<div style="min-height: 100vh; display: flex; align-items: center; justify-content: center;">
    <div style="max-width: 24rem; width: 100%; padding: 1.5rem; background-color: #fff; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06); border-radius: 0.375rem;">
        <h2 style="font-size: 1.875rem; font-weight: 600; margin-bottom: 1.5rem;">Login</h2>
        <form name="logonForm" method="post", action="/loginUser">
            <div style="margin-bottom: 1rem;">
                <label for="Email" style="display: block; font-weight: 600;">E-mail</label>
                <input type="text" id="Email" name="Email" style="margin-top: 0.25rem; padding: 0.5rem; width: 100%; border: 1px solid #e2e8f0; border-radius: 0.375rem; outline: none; transition: border-color 0.2s ease-in-out;"
                       maxlength="40" required>
            </div>
            <div style="margin-bottom: 1rem;">
                <label for="Password" style="display: block; font-weight: 600;">Password</label>
                <input type="password" id="Password" name="Password" style="margin-top: 0.25rem; padding: 0.5rem; width: 100%; border: 1px solid #e2e8f0; border-radius: 0.375rem; outline: none; transition: border-color 0.2s ease-in-out;"
                       maxlength="40" required>
            </div>

            <button type="submit" id="login-button" style="width: 100%; padding: 0.5rem 1rem; background-color: #3b82f6; color: #fff; font-weight: 600; border-radius: 0.375rem; outline: none; cursor: pointer; transition: background-color 0.2s ease-in-out;">Ok</button>

         </form>
        <%if(applicationMessage != null){%>
        <p style="margin-top: 1rem; color: #e53e3e;"><%=applicationMessage%></p>
        <%}%>
        <div style="margin-top: 1.5rem;">
            <p>
                Se non hai ancora un account: <a href="/viewRegistrazione" style="color: #3b82f6; font-weight: 600;">Registrati</a>
            </p>
            <p>
                <%// Invece che Dispatche?.... mettere solo link, es /homepage per andare alla homepage %>
                Se invece sei un <a href="/viewLoginAdmin" style="color: #3b82f6; font-weight: 600;">Amministratore</a>
            </p>

            <br>
            <a href="/homepage" style="width: 100%; padding: 0.5rem 1rem; background-color: #3b82f6; color: #fff; font-weight: 600; border-radius: 0.375rem; outline: none; cursor: pointer; transition: background-color 0.2s ease-in-out;">Indietro</a>

        </div>
    </div>
</div>
</body>

</html>
