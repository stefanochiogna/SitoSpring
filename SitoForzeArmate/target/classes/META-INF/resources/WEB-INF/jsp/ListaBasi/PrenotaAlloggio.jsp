<%@ page import="com.example.sitoforzaarmata.model.mo.Base.Base" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String locazione = (String) request.getAttribute("locazioneAlloggio");
%>
<html>
<head>
    <title>Prenota alloggio:</title>
</head>
<body>
<form action="Dispatcher" method="post">

    <input type="hidden" name="locazioneAlloggio" value="<%=locazione%>">

    <label for="DataArrivo"> Data di arrivo: </label>
    <input id="DataArrivo" name="DataArrivo" type="date" required><br>

    <label for="NumeroPersone"> Numero persone: </label>
    <select id="NumeroPersone" name="NumeroPersone" required>
        <option value="1"> 1 </option>
        <option value="2"> 2 </option>
        <option value="3"> 3 </option>
        <option value="4"> 4 </option>

    </select>
    <br>

    <label for="NumeroNotti"> Numero notti: </label>
    <select id="NumeroNotti" name="NumeroNotti" required>
        <option value="1"> 1 </option>
        <option value="2"> 2 </option>
        <option value="3"> 3 </option>
        <option value="4"> 4 </option>
        <option value="5"> 5 </option>
        <option value="6"> 6 </option>
        <option value="7"> 7 </option>
    </select>
    <br>

    <input type="hidden" name="controllerAction" value="PrenotaAlloggio.conferma">
    <input type="submit" value="Conferma">

</form>

<script>
    var dataOggi = new Date().toISOString().split("T")[0];
    document.getElementById("DataArrivo").setAttribute("min", dataOggi);
</script>

<form name="alloggioAnnulla<%=locazione%>" action="Dispatcher" method="post">

    <input type="hidden" name="luogoBase" value="<%=locazione%>">

    <input type="hidden" name="controllerAction" value="ListaBasi.viewBase"/>

    <input type="submit" value="Annulla">

</form>

</body>
</html>
