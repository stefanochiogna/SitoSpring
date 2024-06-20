<%@ page import="com.example.sitoforzaarmata.model.mo.Base.Base" %>
<%@ page import="com.example.sitoforzaarmata.model.mo.Base.Pasto" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String locazione = (String) request.getAttribute("locazionePasto");
%>
<html>
<head>
    <title>Prenota pasto:</title>
</head>
<body>
    <form action="Dispatcher" method="post">

        <input type="hidden" name="locazionePasto" value="<%=locazione%>">

        <label for="Turno"> Turno: </label>
        <select id="Turno" name="Turno" required>
            <option value="A"> Colazione </option>
            <option value="B"> Pranzo </option>
            <option value="C"> Cena </option>
        </select>
        <br>

        <label for="DataPrenotazione"> Data prenotazione: </label>
        <input id="DataPrenotazione" name="DataPrenotazione" type="date" min="" required><br>

        <input type="hidden" name="controllerAction" value="PrenotaPasto.conferma">
        <input type="submit" value="Conferma">

    </form>

    <script>
        var dataOggi = new Date().toISOString().split("T")[0];
        document.getElementById("DataPrenotazione").setAttribute("min", dataOggi);
    </script>

    <form name="pastoAnnulla<%=locazione%>" action="Dispatcher" method="post">

        <input type="hidden" name="luogoBase" value="<%=locazione%>">

        <input type="hidden" name="controllerAction" value="ListaBasi.viewBase"/>

        <input type="submit" value="Annulla">

    </form>

</body>
</html>
