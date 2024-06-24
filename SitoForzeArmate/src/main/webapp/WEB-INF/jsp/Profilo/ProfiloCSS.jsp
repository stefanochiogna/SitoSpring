<%@ page import="java.util.Base64" %>
<%@ page import="com.progetto.sitoforzearmate.model.mo.Utente.Badge" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    UtenteRegistrato user = (UtenteRegistrato) request.getAttribute("UtenteCorrente");
    Amministratore admin = (Amministratore) request.getAttribute("AdminCorrente");
    Badge badgeUtente = (Badge) request.getAttribute("Badge");
    String menuActiveLink = "Profilo";
%>

<html>
<head>
    <%@include file="../../html_daIncludere/Header.inc"%>

    <style>
        .Utente{
            font-family: Verdana;
            font-size: 20px;
        }
        .Info{
            font-size: 14px;
        }
    </style>
</head>
<body style="background-color: #f3f4f6;">

<!-- Navbar -->
<%@include file="../../html_daIncludere/navbar.inc"%>

<%if(loggedOn){%>
<div style="display: flex; justify-content: flex-end; padding: 1rem;">

    <div style="display: flex; flex-direction: column; margin-right: 25px;">
        <%if((user.getRuolo() != null) && (badgeUtente != null)){%>
        <button style="background-color: #3b82f6; color: white; font-weight: bold; padding: 0.5rem 1rem; border-radius: 0.375rem;" onclick="mostraBadge()">Badge</button>
        <div id="badgeDiv" style="display: none; margin-top: 1rem;">
            <p>
                Id Badge: <%=badgeUtente.getId()%><br>
                Ore di lavoro: <%=badgeUtente.getOreLavorative()%><br>
            </p>
        </div>
        <script>
            function mostraBadge(){
                var badgeDiv = document.getElementById("badgeDiv");
                if(badgeDiv.style.display == 'none'){
                    badgeDiv.style.display = 'block';
                }
                else{
                    badgeDiv.style.display = 'none';
                }
            }
        </script>
        <%}%>
        <div style="margin-top: 1rem;">
            <a href="/prenotazioniView" style="color: #3b82f6;">Visualizza prenotazioni</a>
        </div>
    </div>
    <%String Immagine64 = Base64.getEncoder().encodeToString(user.getFotoByte());%>
    <img src="data:image/jpg;base64, <%=Immagine64%>" alt="Immagine Profilo Utente Registrato" style="width: 80px; height: 80px; border-radius: 50%;">
</div>
<%}%>

<div style="max-width: 800px; margin: 0 auto; padding: 1rem; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);">
    <h1 style="font-size: 45px; font-weight: bold; margin-bottom: 1rem;">
        <%if(loggedOn){%> <%=user.getNome()%>&nbsp<%=user.getCognome()%> <%}
    else{%> <%=admin.getNome()%>&nbsp<%=admin.getCognome()%> <%}%>
    </h1>

    <div style="display: grid; grid-template-columns: 1fr; gap: 1rem;">
        <div>
            <h1 style="font-weight: bold; font-size: 28px;"> Informazioni Personali </h1>
            <h2 style="margin-top: 1rem; font-size: 20px;">
                CF: <%if(loggedOn){%> <%=user.getCF()%> <%} else{%> <%=admin.getCF()%> <%}%><br>
                Sesso: <%if(loggedOn){%> <%=user.getSesso()%> <%} else{%> <%=admin.getSesso()%> <%}%><br>
                Data di Nascita: <%if(loggedOn){%> <%=user.getDataNascita().toStringSQL()%> <%} else{%> <%=admin.getDataNascita().toStringSQL()%> <%}%><br>

                <%if(loggedOn){%>
                <%if(user.getRuolo() != null){%>
                <p><%=user.getRuolo()%> presso <%=user.getLocazioneServizio()%></p>
                <%}
                else{%>
                <p> Utente non in servizio </p>
                <%}%>
                <br>
                Matricola: <%=user.getMatricola()%>
                <%}
                else{%>
                IdAdministrator: <%=admin.getIdAdministrator()%>
                <%}%>
            </h2>
        </div>

        <div>
            <h1 style="font-weight: bold; font-size: 28px;"> Informazioni: </h1>
            <h2 style="margin-top: 1rem; font-size: 20px;">
                Email: <%if(loggedOn){%> <%=user.getMail()%> <%} else{%> <%=admin.getMail()%> <%}%><br>
                Telefono: <%if(loggedOn){%> <%=user.getTelefono()%> <%} else{%> <%=admin.getTelefono()%> <%}%><br>
            </h2>
        </div>
    </div>

    <%if(loggedOn){%>
    <%if(user.getRuolo() != null){%>
    <a href="/viewRubrica" style="display: inline-block; background-color: #3b82f6; color: white; font-weight: bold; padding: 0.5rem 1rem; border-radius: 0.375rem; text-decoration: none; margin-top: 1rem;"> Rubrica </a>
    <%}%>

    <form name="profiloModifica" action="/modificaProfiloView" method="post" style="margin-top: 1rem;">
        <input type="hidden" name="controllerAction" value="Profilo.modificaProfiloView">
        <input type="submit" value="Modifica" style="display: inline-block; background-color: #3b82f6; color: white; font-weight: bold; padding: 0.5rem 1rem; border-radius: 0.375rem; text-decoration: none;">
    </form>
    <%}%>
</div>

</body>

</body>
</html>
