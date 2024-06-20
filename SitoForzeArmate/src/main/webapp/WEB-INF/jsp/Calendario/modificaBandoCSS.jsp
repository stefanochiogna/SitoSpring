<%@ page import="com.progetto.sitoforzearmate.model.mo.Bando" %>
<%@ page import="java.io.*" %>
<%@ page import="com.progetto.sitoforzearmate.model.mo.Utente.Amministratore" %>
<%@ page import="java.util.List" %>
<%@ page import="com.progetto.sitoforzearmate.model.mo.Base.Base" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Bando bando = (Bando) request.getAttribute("BandoSelezionato");
    List<Base> listaBasi = (List<Base>) request.getAttribute("ListaBasi");
%>
<html>
<head>
    <% if(bando != null){ %>
    <title style="font-weight: bold;">Modifica Bando</title>
    <style>
        .testo{
            width: 100%;
            height: 500px;
            font-family: Arial;
            white-space: pre-wrap;
            resize: vertical;
        }
    </style>
    <%}
    else{%>
    <title style="font-weight: bold;">Inserimento Bando</title>
    <%}%>
</head>
<body style="background-color: #F3F4F6;">
<form <% if(bando != null){%> action="/modificaBando" <%}else{%> action="/inserisciBando" <%}%> method="post" enctype="multipart/form-data" style="max-width: 65%; min-width: 725px; margin: auto; background-color: #fff; padding: 1.5rem; border-radius: 0.5rem; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);">
    <label for="oggettoBando" style="font-weight: bold;">Oggetto: </label>
    <input type="text" <% if(bando != null){ %> value="<%=bando.getOggetto() %>" <%}%> name="oggettoBando" id="oggettoBando" required style="width: 100%; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 0.25rem; outline: none; transition: border-color 0.2s ease-in-out;"><br>

    <label for="numMaxIscritti" style="font-weight: bold;"> Numero Massimo Iscritti: </label>
    <input id="numMaxIscritti" min="0" name="numMaxIscritti" <% if(bando != null){ %> value="<%=bando.getMaxNumPartecipanti()%>" <%}%> type="number" required style="width: 100%; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 0.25rem; outline: none; transition: border-color 0.2s ease-in-out;"><br>

    <label for="DataScadenza" style="font-weight: bold;"> Data scadenza: </label>
    <input id="DataScadenza" name="DataScadenza" <% if(bando != null){ %> value="<%=bando.getDataSQL(bando.getDataScadenzaIscrizione())%>" <%}%> type="date" required style="width: 100%; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 0.25rem; outline: none; transition: border-color 0.2s ease-in-out;"><br>

    <label for="DataBando" style="font-weight: bold;"> Data Bando: </label>
    <input id="DataBando" name="DataBando" <% if(bando != null){ %> value="<%=bando.getDataSQL(bando.getData())%>" <%}%> type="date" required style="width: 100%; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 0.25rem; outline: none; transition: border-color 0.2s ease-in-out;"><br>


    <label for="base" style="font-weight: bold;"> Locazione bando: </label>
    <select id="base" name="Locazione" style="width: 100%; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 0.25rem; outline: none; transition: border-color 0.2s ease-in-out;">
        <%for(int i=0; i<listaBasi.size(); i++){%>
        <option value="<%=listaBasi.get(i).getLocazione()%>" <% if(bando != null){ %>
                <%if(bando.getLocazione().equals(listaBasi.get(i).getLocazione())){%>selected<%}%><%}%>>
            <%=listaBasi.get(i).getLocazione()%>
        </option>
        <%}%>
    </select><br>

    <% if(bando != null){ %>
    <label for="testoBando" style="font-weight: bold;">Testo: </label>
    <textarea class="testo" id="testoBando" name="testoBando" required><%try{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(bando.getRiferimentoTesto().toString()), "UTF-8"));
        String line = br.readLine();
        while(line != null){%><%=line+'\n'%><%line = br.readLine();%><%}
        br.close();
    }
    catch (Exception e){
        throw new RuntimeException(e);
    }%></textarea>
    <%}
    else{%>
    <label for="insBando" style="font-weight: bold;"> Selezionare il bando che si vuole caricare: </label>
    <input type="file" id="insBando" name="insBando" required style="width: 100%; padding: 0.75rem; border: 1px solid #e2e8f0; border-radius: 0.25rem; outline: none; transition: border-color 0.2s ease-in-out;"><br>
    <%}%>

    <ul style="display: flex; padding: 0; list-style-type: none;">
        <li style="display: flex;">
        <% if(bando != null){ %>
        <input type="hidden" name="bandoId" value="<%=bando.getId()%>">

        <input type="submit" value="Salva Modifiche" style="margin-top: 15px; padding: 8px 15px; background-color: #3490dc; color: #fff; border: none; border-radius: 4px; cursor: pointer;">
        <%}
        else{%>

        <input type="submit" value="Crea nuovo Bando" style="margin-top: 15px; padding: 8px 15px; background-color: #3490dc; color: #fff; border: none; border-radius: 4px; cursor: pointer;">
        <%}%>
        </li>
        <li style="display: flex; margin-left: 72%;">
        <a href="/viewCalendario" style="margin-top: 15px; padding: 8px 15px; background-color: #9CA3AF; color: #fff; border: none; border-radius: 4px; cursor: pointer;"> Annulla </a>
        </li>
    </ul>
</form>

<script>
    var dataOggi = new Date().toISOString().split("T")[0];
    document.getElementById("DataScadenza").setAttribute("min", dataOggi);
    document.getElementById("DataBando").setAttribute("min", dataOggi);

    var dataBandoInput = document.getElementById("DataBando");
    var dataScadenzaInput = document.getElementById("DataScadenza");
    dataScadenzaInput.addEventListener("change", function (){
        var DataSelezionata = new Date(dataScadenzaInput.value);
        var DataBandoMin = new Date(DataSelezionata.getFullYear(), DataSelezionata.getMonth() + 1, DataSelezionata.getDate());

        dataBandoInput.setAttribute("min", DataBandoMin.toISOString().split("T")[0]);
    });
</script>

</body>
</html>
