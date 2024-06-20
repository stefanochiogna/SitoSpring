<%@ page import="com.example.sitoforzaarmata.model.mo.Bando" %>
<%@ page import="java.io.*" %>
<%@ page import="com.example.sitoforzaarmata.model.mo.Utente.Amministratore" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.sitoforzaarmata.model.mo.Base.Base" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Bando bando = (Bando) request.getAttribute("BandoSelezionato");
    List<Base> listaBasi = new ArrayList<>();
    listaBasi.addAll((List<Base>) request.getAttribute("ListaBasi"));
%>
<html>
<head>
    <% if(bando != null){ %>
        <title>Modifica Bando</title>
    <style>
        .testo{
            width: 100%;
            height: 75%;
            font-family: Arial;
            white-space: pre;
        }
    </style>
    <%}
    else{%>
        <title>Inserimento Bando</title>
    <%}%>
</head>
<body>
    <form action="Dispatcher" method="post" enctype="multipart/form-data">
        <label for="oggettoBando">Oggetto: </label>
        <input type="text" <% if(bando != null){ %> value="<%=bando.getOggetto() %>" <%}%> name="oggettoBando" id="oggettoBando" required><br>

        <label for="numMaxIscritti"> Numero Massimo Iscritti: </label>
        <input id="numMaxIscritti" min="0" name="numMaxIscritti" <% if(bando != null){ %> value="<%=bando.getMaxNumPartecipanti()%>" <%}%> type="number" required><br>

        <label for="DataScadenza"> Data scadenza: </label>
        <input id="DataScadenza" name="DataScadenza" <% if(bando != null){ %> value="<%=bando.getDataSQL(bando.getDataScadenzaIscrizione())%>" <%}%> type="date" required><br>

        <label for="DataBando"> Data Bando: </label>
        <input id="DataBando" name="DataBando" <% if(bando != null){ %> value="<%=bando.getDataSQL(bando.getData())%>" <%}%> type="date" required><br>


        <label for="base"> Locazione bando: </label>
        <select id="base" name="base">
            <%for(int i=0; i<listaBasi.size(); i++){%>
                <option value="<%=listaBasi.get(i).getLocazione()%>" <% if(bando != null){
                        if(bando.getLocazione().equals(listaBasi.get(i).getLocazione())){%>
                            selected
                        <%}%>
                    <%}%>>
                    <%=listaBasi.get(i).getLocazione()%>
                </option>
            <%}%>
        </select><br>

        <% if(bando != null){ %>
            <label for="testoBando">Testo: </label>
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
            <label for="insBando"> Selezionare il bando che si vuole caricare: </label>
            <input type="file" id="insBando" name="insBando" required><br>
        <%}%>

        <% if(bando != null){ %>
            <input type="hidden" name="bandoId" value="<%=bando.getId()%>">
            <input type="hidden" name="controllerAction" value="Calendario.modificaBando"/>

            <input type="submit" value="Salva Modifiche">
        <%}
        else{%>
            <input type="hidden" name="controllerAction" value="Calendario.inserisciBando"/>

            <input type="submit" value="Crea nuovo Bando">
        <%}%>
    </form>

    <form action="Dispatcher" method="post">
        <input type="hidden" name="controllerAction" value="Calendario.view"/>
        <input type="submit" value="Annulla">
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
