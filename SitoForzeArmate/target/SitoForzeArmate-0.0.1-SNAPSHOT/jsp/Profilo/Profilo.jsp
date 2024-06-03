<%@ page import="java.util.Base64" %>
<%@ page import="com.example.sitoforzaarmata.model.mo.Utente.Badge" %>
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
<body>
    <!-- Navbar -->
    <%@include file="../../html_daIncludere/navbar.inc"%>

    <%if(loggedOn){%>
        <section style="float:right">
            <%String Immagine64 = Base64.getEncoder().encodeToString(user.getFotoByte());%>
            <img src="data:image/jpg;base64, <%=Immagine64%>" alt="Immagine Profilo Utente Registrato"/>  <!-- inserisci immagine di profilo -->

            <section style="float: bottom">
                <%if((user.getRuolo() != null) && (badgeUtente != null)){%>
                    <button class="showBadge" onclick="mostraBadge()">Badge</button>
                    <div class="badge" id="badgeDiv" style="display: none">
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
                <div>
                    <a href="Dispatcher?controllerAction=Profilo.prenotazioniView">Visualizza prenotazioni</a>
                </div>
            </section>
        </section>
    <%}%>
    <h1 class="Utente">
        <%if(loggedOn){%> <%=user.getNome()%>&nbsp<%=user.getCognome()%> <%}
        else{%> <%=admin.getNome()%>&nbsp<%=admin.getCognome()%> <%}%>
    </h1><br>
    <div>
        <h1 class="Info"> Informazioni Personali </h1>
        <h2>
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
        <h1 class="Info"> Informazioni: </h1>
        <h2>
            Email: <%if(loggedOn){%> <%=user.getMail()%> <%} else{%> <%=admin.getMail()%> <%}%><br>
            Telefono: <%if(loggedOn){%> <%=user.getTelefono()%> <%} else{%> <%=admin.getTelefono()%> <%}%><br>
        </h2>
    </div>
    <%if(loggedOn){%>
        <%if(user.getRuolo() != null){%>
            <a href="Dispatcher?controllerAction=Rubrica.view"> Rubrica </a>
        <%}%>

        <form name="profiloModifica" action="Dispatcher" method="post">

            <input type="hidden" name="controllerAction" value="Profilo.modificaProfiloView"/>

            <input type="submit" value="Modifica" >
        </form>

    <%}%>

</body>
</html>
