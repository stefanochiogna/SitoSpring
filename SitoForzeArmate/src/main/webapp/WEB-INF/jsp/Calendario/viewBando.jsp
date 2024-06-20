<%@ page import="com.example.sitoforzaarmata.model.mo.Bando" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../../../../../static/html_daIncludere/Header.inc"%>
<%
    Bando bando = (Bando) request.getAttribute("BandoSelezionato");
    boolean Iscritto = false;
    if (loggedOn) Iscritto = (boolean) request.getAttribute("Iscritto");
    String inAttesa = (String) request.getAttribute("inAttesa");   
%>
<html>
<head>
    <title><%=bando.getOggetto()%></title>
</head>
<body>
<section>
    <h1>
        <%=bando.getOggetto()%>
    </h1>

    <%
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(bando.getRiferimentoTesto().toString()), "UTF-8"));

            String line = br.readLine();
            while(line != null){%>
                <p>
                    <%=line%>
                    <%line = br.readLine();%>
                </p>
            <%}
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    %>


    <% if(loggedOn && !Iscritto && (bando.getNumPartecipanti() < bando.getMaxNumPartecipanti())){ %>
    <form name="bandoIscrizione<%=bando.getId()%>" action="Dispatcher" method="post">

        <input type="hidden" name="bandoId" value="<%=bando.getId()%>">

        <input type="hidden" name="Iscritto" value="True">

        <input type="hidden" name="controllerAction" value="Calendario.iscrizione"/>

        <input type="submit" value="Iscriviti">

    </form>
    <%}
    else if(bando.getNumPartecipanti() >= bando.getMaxNumPartecipanti()){%>
        <button type="button" disabled> Massimo numero di partecipanti raggiunto </button>>
    <%}%>
    <% if(loggedOn && Iscritto){ %>
    <form name="annullaIscrizione<%=bando.getId()%>" action="Dispatcher" method="post">

        <input type="hidden" name="bandoId" value="<%=bando.getId()%>">

        <input type="hidden" name="Iscritto" value="False">

        <input type="hidden" name="controllerAction" value="Calendario.annullaIscrizione"/>

        <input type="submit" value="Annulla Iscrizione">

    </form>
    <%}%>

    <form name="bandoAnnulla<%=bando.getId()%>" action="Dispatcher" method="post">

        <input type="hidden" name="controllerAction" value="Calendario.view"/>

        <input type="submit" value="Indietro">

    </form>

</section>
</body>
</html>
