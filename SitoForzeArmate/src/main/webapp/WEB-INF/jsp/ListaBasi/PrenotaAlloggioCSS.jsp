<%@ page import="com.progetto.sitoforzearmate.model.mo.Base.Base" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String locazione = (String) request.getAttribute("locazioneAlloggio");
%>
<html>
<head>
    <title>Prenota alloggio:</title>
</head>
<body style="background-color: #F3F4F6;">
<h1 style="display: flex; justify-content: center;"> Prenota alloggio presso <%=locazione%>: </h1>
<form action="/confermaIscrizione" method="post" style="max-width: 28rem; margin: auto; background-color: #fff; padding: 1.5rem; border-radius: 0.5rem; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);">

    <input type="hidden" name="locazioneAlloggio" value="<%=locazione%>">

    <label for="DataArrivo" style="font-weight: 500; color: #4a5568; margin: 0.5rem 0;"> Data di arrivo: </label>
    <input id="DataArrivo" name="DataArrivo" type="date" required style="width: 100%; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 0.25rem; outline: none;"><br>

    <label for="NumeroPersone" style="font-weight: 500; color: #4a5568; margin: 0.5rem 0;"> Numero persone: </label>
    <select id="NumeroPersone" name="NumeroPersone" required style="width: 100%; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 0.25rem; outline: none;">
        <option value="1"> 1 </option>
        <option value="2"> 2 </option>
        <option value="3"> 3 </option>
        <option value="4"> 4 </option>

    </select>
    <br>

    <label for="NumeroNotti" style="font-weight: 500; color: #4a5568; margin: 0.5rem 0;"> Numero notti: </label>
    <select id="NumeroNotti" name="NumeroNotti" required style="width: 100%; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 0.25rem; outline: none;">
        <option value="1"> 1 </option>
        <option value="2"> 2 </option>
        <option value="3"> 3 </option>
        <option value="4"> 4 </option>
        <option value="5"> 5 </option>
        <option value="6"> 6 </option>
        <option value="7"> 7 </option>
    </select>
    <br>

    <input type="submit" value="Conferma" style="background-color: #4299e1; color: #fff; padding: 0.75rem 1rem; border-radius: 0.25rem; cursor: pointer; margin-top: 1rem; transition: background-color 0.2s ease-in-out; width: 100%;" onmouseover="this.style.backgroundColor='#3182ce'" onmouseout="this.style.backgroundColor='#4299e1'">

</form>

<script>
    var dataOggi = new Date().toISOString().split("T")[0];
    document.getElementById("DataArrivo").setAttribute("min", dataOggi);
</script>

<form name="alloggioAnnulla<%=locazione%>" action="/viewBase" method="post" style="display: flex; justify-content: center;">

    <input type="hidden" name="luogoBase" value="<%=locazione%>">

    <input type="submit" value="Annulla" style="background-color: #9CA3AF; color: #fff; padding: 0.75rem 1rem; border-radius: 0.25rem; cursor: pointer; margin-top: 1rem; transition: background-color 0.2s ease-in-out; width: 10%;" onmouseover="this.style.backgroundColor='#6B7280'" onmouseout="this.style.backgroundColor='#9CA3AF'">

</form>

</body>
</html>
