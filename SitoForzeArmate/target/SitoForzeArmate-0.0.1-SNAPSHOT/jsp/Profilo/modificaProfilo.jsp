<%@ page import="com.example.sitoforzaarmata.model.mo.Utente.Utente" %>
<%@ page import="java.io.*" %>
<%@ page import="com.example.sitoforzaarmata.model.mo.Utente.Amministratore" %>
<%@ page import="com.example.sitoforzaarmata.model.mo.Utente.UtenteRegistrato" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    UtenteRegistrato user = (UtenteRegistrato) request.getAttribute("UserSelezionato");
    Amministratore admin = (Amministratore) request.getAttribute("AdminSelezionato");
%>
<html>
<head>
    <title>
        <%if(user != null){%>
            Modifica profilo utente:
        <%}
        else if(admin != null){%>
            Modifica profilo Amministratore:
        <%}%>
    </title>
    <style>
        .testo{
            width: 100%;
            height: 75%;
            font-family: Arial;
            white-space: pre;
        }
    </style>
</head>
<body>
<form action="Dispatcher" method="post" enctype="multipart/form-data">
    <section>
        <label for="Nome"> Nome: </label>
        <input id="Nome" type="text" <%if(user != null){%> value="<%=user.getNome()%>" <%}
                                        else if(admin != null){%> value="<%=admin.getNome()%>" <%}%> readonly>
        <br>

        <label for="Cognome"> Cognome: </label>
        <input id="Cognome" type="text" <%if(user != null){%> value="<%=user.getCognome()%>"<%}
                                        else if(admin != null){%> value="<%=admin.getCognome()%>" <%}%> readonly>
        <br>

        <label for="CF"> CF: </label>
        <input id="CF" type="text" <%if(user != null){%> value="<%=user.getCF()%>"<%}
                                        else if(admin != null){%> value="<%=admin.getCF()%>" <%}%> readonly>
        <br>

        <%if(user != null){%>
            <label for="Matricola"> Matricola: </label>
            <input id="Matricola" type="text" value="<%=user.getMatricola()%>" readonly>
        <%}
        else if(admin != null){%>
            <label for="IdAdministrator"> Id Amministratore: </label>
            <input id="IdAdministrator" type="text" value="<%=admin.getIdAdministrator()%>" readonly>
        <%}%>
        <br>
    </section>
    <section>
        <label for="Mail"> Mail: </label>
        <input id="Mail" type="email"
            <%if(user != null){%> value="<%=user.getMail()%>" <%}
            else if(admin != null){%> value="<%=admin.getMail()%>" <%}%> readonly>

        <label for="userPassword"> Password: </label>
        <input id="userPassword" name="userPassword" type="password" required
            <%if(user != null){%> value="<%=user.getPassword()%>" <%}
               else if(admin != null){%> value="<%=admin.getPassword()%>" <%}%>><br>

    </section>

    <label for="userTelefono">Telefono: </label>
    <input type="text" name="userTelefono" id="userTelefono" required
        <%if(user != null){%> value="<%=user.getTelefono()%>" <%}
           else if(admin != null){%> value="<%=admin.getTelefono()%>" <%}%>><br>

    <%if(user != null){%>
        <label for="userIndirizzo">Indirizzo: </label>
        <input id="userIndirizzo" name="userIndirizzo" value="<%=user.getIndirizzo()%>" type="text" required><br>

        <label for="userIBAN"> IBAN: </label>
        <input id="userIBAN" name="userIBAN" value="<%=user.getIBAN()%>" type="text" required><br>

        <section>
            <label for="userFoto"> Seleziona la foto da caricare: </label>
            <input type="file" id="userFoto" name="userFoto"><br>

            <label for="userDocumenti"> Seleziona i documenti da caricare: </label>
            <input type="file" id="userDocumenti" name="userDocumenti"><br>
        </section>

        <label for="Newsletter"> Newsletter: </label>
        <input type="checkbox" id="Newsletter" name="Newsletter" <%if(user.getIscrittoNewsletter()){%> checked <%}%>>
        <br>
    <%}%>
    <input type="hidden" name="userMail"
        <%if(user != null){%> value="<%=user.getMail()%>" <%}
           else if(admin != null){%> value="<%=admin.getMail()%>" <%}%>>

    <input type="hidden" name="controllerAction" value="Profilo.modificaProfilo"/>

    <input type="submit" value="Salva Modifiche">

</form>

<form action="Dispatcher" method="post">
    <input type="hidden" name="controllerAction" value="Profilo.view"/>
    <input type="submit" value="Annulla">
</form>

</body>
</html>
