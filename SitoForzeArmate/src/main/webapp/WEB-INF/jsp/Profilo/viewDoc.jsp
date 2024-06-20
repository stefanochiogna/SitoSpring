<%@ page import="com.progetto.sitoforzearmate.model.mo.Bando" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.Base64" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../../html_daIncludere/Header.inc"%>
<%
    Bando bando = (Bando) request.getAttribute("BandoSelezionato");

    UtenteRegistrato UtenteSelezionato = (UtenteRegistrato) request.getAttribute("UtenteSelezionato");

%>
<html>
<head>
    <title> Documenti <%=UtenteSelezionato.getMatricola()%></title>
</head>
<body>
    <div class="container">
        <h1 class="titolo">
            <%=UtenteSelezionato.getNome()%> <%=UtenteSelezionato.getCognome()%> <%=UtenteSelezionato.getMatricola()%>
        </h1>

        <div>
            <%String Immagine64 = Base64.getEncoder().encodeToString(UtenteSelezionato.getFotoByte());%>
            <img src="data:image/jpg;base64, <%=Immagine64%>" alt="Immagine Profilo Selezionato" style="width: 80px; height: 80px; border-radius: 50%;">
        </div>

        <h2>
            CF: <%=UtenteSelezionato.getCF()%> <br>
            Sesso: <%=UtenteSelezionato.getSesso()%><br>
            Data di Nascita: <%=UtenteSelezionato.getDataNascita().toStringSQL()%> <br>

            <%if(UtenteSelezionato.getRuolo() != null){%>
            <p><%=UtenteSelezionato.getRuolo()%> presso <%=UtenteSelezionato.getLocazioneServizio()%></p>
            <%}
            else{%>
            <p> Utente non in servizio </p>
            <%}%>
            <br>
            Matricola: <%=UtenteSelezionato.getMatricola()%>
        </h2>

        <div>

            <%String Documento64 = Base64.getEncoder().encodeToString(UtenteSelezionato.getDocumentoByte());%>
            <img src="data:image/jpg;base64, <%=Documento64%>" alt="Documento Utente Selezionato">

        </div>

    </div>

    <form name="bandoView<%=bando%>" action="/viewBando" method="post" style="margin-bottom: 0; width: 100%; display: flex; align-items: center; justify-content: center;">

        <input type="hidden" name="bandoId" value="<%=bando.getId()%>">
        <!-- type hidden: usato per inviare parametri con la form specificandone il nome -->

        <input type="submit" value="Torna Indietro" style="color: #2f6a9c; font-weight: bold; cursor: pointer; border: none; width: 70%; height: 25px; background: transparent;">
    </form>
</body>
</html>
