<%@ page import="com.example.sitoforzaarmata.model.mo.Utente.UtenteRegistrato" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
  UtenteRegistrato loggedUser = (UtenteRegistrato) request.getAttribute("loggedUser");

  String InizialeSelezionata = (String) request.getAttribute("inizialeSelezionata");
  List<String> iniziali = (List<String>) request.getAttribute("ListaIniziali");
  List<UtenteRegistrato> contatti = (List<UtenteRegistrato>) request.getAttribute("Contatti");

  System.out.println("Dimensione contatti: " + contatti.size());
%>
<html>
<head>
  <title>Rubrica</title>

  <style>
    body {
      font-family: Arial, sans-serif;
      padding: 2rem;
      background-color: #f3f4f6;
    }
    #pageTitle h1 {
      font-size: 24px;
      font-weight: bold;
    }
    #initialSelector {
      display: flex;
      justify-content: space-between;
      margin-top: 1rem;
      font-size: 18px;
      font-weight: bold;
    }
    #initialSelector button,
    #initialSelector .selectedInitial {
      padding: 0.5rem;
      cursor: pointer;
    }
    #contacts {
      margin-top: 2rem;
      display: flex;
      flex-wrap: wrap;
    }
    .containerContatti{
      margin: 0px 25px;
      padding: 10px;
      background-color: #ffffff;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
    }
    .NomeCognome {
      font-weight: bold;
    }
    .phone,
    .email {
      display: block;
    }
    .clearfix::after {
      content: "";
      display: table;
      clear: both;
    }
    button {
      border-radius: 10%;
      color: white;
      background: #3b82f6;
      text-decoration: none;
    }
    button:hover {
      background-color: coral;
      text-decoration: underline;
    }
  </style>

  <script>
    function changeInitial(inital) {
      document.changeInitialForm.inizialeSelezionata.value = inital;
      document.changeInitialForm.submit();
    }
  </script>
</head>
<body>
  <section id="pageTitle">
    <h1>Contatti: <%=InizialeSelezionata%></h1>
  </section>
  <nav id="initialSelector">
    <%if (InizialeSelezionata.equals("*")) { %>
      <span class="selectedInitial">*</span>
    <%}
    else {%>
      <button class="initial" onclick="changeInitial('*')">*</button>
    <%}%>
    <%for (int i = 0; i < iniziali.size(); i++) {
      if (iniziali.get(i).equals(InizialeSelezionata)) {%>
        <span class="selectedInitial"><%=iniziali.get(i)%></span>
      <%}
      else {%>
        <button class="initial" onclick="changeInitial('<%=iniziali.get(i)%>')"><%=iniziali.get(i)%></button>
      <%}%>
    <%}%>
  </nav>

  <section id="contacts" class="clearfix">
    <%for (int i = 0; i < contatti.size(); i++) {%>
      <div class="containerContatti">
        <span class="NomeCognome"><%=contatti.get(i).getCognome()%> <%=contatti.get(i).getNome()%></span>
        <span class="phone"><%= contatti.get(i).getTelefono()%></span>
        <span class="email"><%= contatti.get(i).getMail()%></span>
        <span><%=contatti.get(i).getRuolo()%> in servizio presso: <%=contatti.get(i).getLocazioneServizio()%></span>
        <br/>
      </div>
    <%}%>
  </section>

  <a href="Dispatcher?controllerAction=Profilo.view" style="margin: 20px 0px; color: white; text-decoration: underline; display: block; background: #3b82f6; cursor: pointer; text-align: center; width: 5%;"> Indietro </a>

  <form name="changeInitialForm" method="post" action="Dispatcher">
    <input type="hidden" name="inizialeSelezionata"/>
    <input type="hidden" name="controllerAction" value="Rubrica.view"/>
  </form>
</body>
</html>
