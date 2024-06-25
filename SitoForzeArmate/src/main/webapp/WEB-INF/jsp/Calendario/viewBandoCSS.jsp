<%@ page import="com.progetto.sitoforzearmate.model.mo.Bando" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.io.*" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="com.progetto.sitoforzearmate.services.configuration.Configuration" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../../html_daIncludere/Header.inc"%>
<%
  Bando bando = (Bando) request.getAttribute("BandoSelezionato");
  boolean Iscritto = false;
  boolean maxIscrittiRaggiunto = (boolean) request.getAttribute("maxIscrittiRaggiunto");

  if (loggedOn) Iscritto = (boolean) request.getAttribute("Iscritto");
  String inAttesa = (String) request.getAttribute("inAttesa");

  List<UtenteRegistrato> partecipanti = new ArrayList<>();
  if(loggedAdminOn){
    partecipanti.addAll( (List<UtenteRegistrato>) request.getAttribute("partecipanti") );
  }

  System.out.println("loggedOn: " + loggedOn);
  System.out.println("iscritto: " + Iscritto);

%>
<html>
<head>
  <title><%=bando.getOggetto()%></title>

  <style>
    div.container{
      margin: 25px;
      padding: 1.5rem;
      background-color: #fff;
      border-radius: 0.5rem;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
      width: 96.5%;
    }
    div.container h1.titolo{
      font-size: 2rem;
      font-weight: bold;
      margin-bottom: 1rem;
      text-align: center;
      background: #343B66;
      color: white;
      display: block;
    }
    div.container p.testo{
      margin-bottom: 1rem;
      display: block;
    }
    div.container a.indietro{
      color: white;
      text-decoration: underline;
      display: block;
      background: #3b82f6;
      cursor: pointer;
      text-align: center;
      width: 5%;
    }
    div.container form.tasto{
      color: black;
      text-decoration: underline;
      display: block;
      background: white;
      cursor: pointer;
      text-align: center;
      width: 5%;
    }
  </style>

</head>
<body style="background-color: #f7fafc;">
<div class="container">
  <h1 class="titolo">
    <%=bando.getOggetto()%>
  </h1>

  <%
    try{
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Configuration.getPATH(bando.getRiferimentoTesto().toString())), "UTF-8"));

      String line = br.readLine();
      while(line != null){%>
  <p class="testo">
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
  <form name="bandoIscrizione<%=bando.getId()%>" action="/iscrizione" method="post" class="tasto">

    <input type="hidden" name="bandoId" value="<%=bando.getId()%>">

    <input type="hidden" name="Iscritto" value="True">

    <input type="hidden" name="inAttesa" value="True">

    <input type="submit" value="Iscriviti: <%=bando.getNumPartecipanti()%>/<%=bando.getMaxNumPartecipanti()%>">

  </form>
  <%}
  else if(maxIscrittiRaggiunto && loggedOn){%>
  <button type="button" disabled style="margin: 10px 0px"> Massimo numero di partecipanti raggiunto </button>
  <%}%>
  <% if(loggedOn && Iscritto && !(inAttesa.equalsIgnoreCase("rifiutato"))){ %>
  <form name="annullaIscrizione<%=bando.getId()%>" action="/annullaIscrizione" method="post" class="tasto">

    <input type="hidden" name="bandoId" value="<%=bando.getId()%>">

    <input type="hidden" name="Iscritto" value="False">
    
    <input type="submit" value="Annulla Iscrizione">

  </form>

  <button type="button" disabled>

    <%= inAttesa %>

  </button>

  <%}%>

  <a href="/viewCalendario" class="indietro"> Indietro </a>
</div>


<%if(loggedAdminOn && (LocalDate.now().isAfter(LocalDate.parse(bando.getDataScadenzaIscrizione().toStringSQL()) ))){%>
  <div>
    <% for(int i = 0; i < bando.getNumPartecipanti(); i++) {%>
    <% if(bando.getEsito(partecipanti.get(i).getMatricola()).equalsIgnoreCase("in attesa")){%>
      <p>
        <%= partecipanti.get(i).getNome() %> <%= partecipanti.get(i).getCognome() %> - <%= partecipanti.get(i).getMatricola() %>
      </p>
      <p>
      <form name="visualizzaDoc<%=partecipanti.get(i).getMatricola()%>" action="/viewDoc" method="post" class="tasto">

        <input type="hidden" name="UtenteSelezionato" value="<%=partecipanti.get(i).getMatricola()%>">

        <input type="hidden" name="bandoId" value="<%=bando.getId()%>">

        <input type="submit" value = "Visualizza documenti">
      </form>

      <form name="rifiutaPartecipante<%=partecipanti.get(i).getMatricola()%>" action="/esitoPartecipante" method="post" style="margin-bottom: 0; margin-left: 1rem; padding: 0; display: flex;">

        <input type="hidden" name="UtenteSelezionato" value="<%=partecipanti.get(i).getMatricola()%>">

        <input type="hidden" name="bandoId" value="<%=bando.getId()%>">

        <input type="hidden" name="inAttesa" value="rifiutato">

        <input type="submit" value="Rifiuta" style="display: inline-block; background-color: #e53e3e; color: #fff; padding: 0.5rem 1rem; border-radius: 0.25rem; cursor: pointer; font-weight: bold;">

      </form>
      <form name="accettaPartecipante<%=partecipanti.get(i).getMatricola()%>" action="/esitoPartecipante" method="post" style="margin-bottom: 0; margin-left: 1rem; padding: 0; display: flex;">

        <input type="hidden" name="UtenteSelezionato" value="<%=partecipanti.get(i).getMatricola()%>">

        <input type="hidden" name="inAttesa" value="accettato">

        <input type="hidden" name="bandoId" value="<%=bando.getId()%>">

        <input type="submit" value="Accetta" style="display: inline-block; background-color: #3182ce; color: #fff; padding: 0.5rem 1rem; border-radius: 0.25rem; cursor: pointer; font-weight: bold;">

      </form>
    </p>
    <%}%>

    <%}%>
  </div>
<%}%>


</body>
</html>
