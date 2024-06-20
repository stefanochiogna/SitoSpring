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
      <a class="initial" href="javascript:changeInitial('*');">*</a>
    <%}%>
    <%for (int i = 0; i < iniziali.size(); i++) {
      if (iniziali.get(i).equals(InizialeSelezionata)) {%>
        <span class="selectedInitial"><%=iniziali.get(i)%></span>
      <%}
      else {%>
        <a class="initial" href="javascript:changeInitial('<%=iniziali.get(i)%>');"><%=iniziali.get(i)%></a>
      <%}%>
    <%}%>
  </nav>

  <section id="contacts" class="clearfix">
      <%for (int i = 0; i < contatti.size(); i++) {%>
        <span class="NomeCognome"><%=contatti.get(i).getCognome()%> <%=contatti.get(i).getNome()%></span>
        <span class="phone"><%= contatti.get(i).getTelefono()%></span>
        <span class="email"><%= contatti.get(i).getMail()%></span>
        <span>In servizio presso: <%=contatti.get(i).getLocazioneServizio()%></span>
        <br/>
    <%}%>
  </section>

  <a href="Dispatcher?controllerAction=Profilo.view"> Indietro </a>

  <form name="changeInitialForm" method="post" action="Dispatcher">
    <input type="hidden" name="inizialeSelezionata"/>
    <input type="hidden" name="controllerAction" value="Rubrica.view"/>
  </form>
</body>
</html>
