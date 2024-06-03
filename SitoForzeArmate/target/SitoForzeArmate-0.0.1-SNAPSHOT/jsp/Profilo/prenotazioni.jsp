<%@ page import="com.example.sitoforzaarmata.model.mo.Base.PostoLetto" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.sitoforzaarmata.model.mo.Base.Pasto" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.sitoforzaarmata.model.mo.Utente.UtenteRegistrato" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  List<Pasto> pastiList = new ArrayList<>();
  List<PostoLetto> alloggiList = new ArrayList<>();

  alloggiList.addAll((List<PostoLetto>) request.getAttribute("listaAlloggi"));
  pastiList.addAll((List<Pasto>) request.getAttribute("listaPasti"));

  boolean loggedOn = (Boolean) request.getAttribute("loggedOn");
  UtenteRegistrato loggedUser = (UtenteRegistrato) request.getAttribute("loggedUser");
%>

<html>
<head>
    <title>Prenotazioni Utente</title>
</head>
<body>
  <section>
    <h1> Alloggi prenotati: </h1>
    <ul>
      <%if(alloggiList != null){
        for(int i=0; i<alloggiList.size(); i++){%>
          <li>
            Prenotazione presso: <%=alloggiList.get(i).getLocazione()%><br>
            Data arrivo: <%=alloggiList.get(i).getData_arrivo().toStringSQL()%> per <%=alloggiList.get(i).getNum_notti()%> notti <br>
            Id della prenotazione: <%=alloggiList.get(i).getId()%>
          </li>
        <%}
      }%>
    </ul>
  </section>

  <section>
    <h1> Pasti prenotati: </h1>
    <ul>
      <%if(pastiList != null){
        for(int i=0; i<pastiList.size(); i++){%>
        <li>
          Prenotazione presso: <%=pastiList.get(i).getLocazione()%><br>
          Data: <%=pastiList.get(i).getData_prenotazione().toStringSQL()%> per turno <%=pastiList.get(i).getTurno()%> <br>
          Id della prenotazione: <%=pastiList.get(i).getId()%>
        </li>
        <%}
      }%>
    </ul>
  </section>

  <a href="Dispatcher?controllerAction=Profilo.view"> Indietro </a>
</body>
</html>
