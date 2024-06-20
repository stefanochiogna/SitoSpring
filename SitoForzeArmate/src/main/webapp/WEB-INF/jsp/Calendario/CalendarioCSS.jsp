<%@ page import="com.progetto.sitoforzearmate.model.mo.Bando" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.LocalDate" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  List<Bando> bandoList = new ArrayList<>();
  bandoList.addAll((List<Bando>) request.getAttribute("Bandi"));

  List<String> dateList = new ArrayList<>();
  dateList.addAll((List<String>) request.getAttribute("Date"));

  String menuActiveLink = "Calendario";

  LocalDate oggi = LocalDate.now();
%>
<html>
<head>
  <%@include file="../../html_daIncludere/Header.inc"%>

  <style>
    h1.data{
      grid-column: 1 / -1;
      cursor: pointer;
      background-color: #3182ce;
      color: honeydew;
      text-decoration: none;
      display: block;
      margin-bottom: 1rem;
      text-align: center;
      font-size: 25px;
    }
  </style>
</head>
<body style="background-color: #f7fafc;">
<!-- Navbar -->
<%@include file="../../html_daIncludere/navbar.inc"%>

<main>
  <% if(loggedAdminOn){%>
  <form name="newBando" action="/modificaBandoView" method="post" style="margin-bottom: 0;">
    <input type="hidden" name="loggedAdmin" value="<%=loggedAdmin%>">
    <input type="submit" value="Nuovo Bando" style="background-color: #4299e1; color: #fff; padding: 0.5rem 1rem; border-radius: 0.25rem; cursor: pointer;">
  </form>
  <%}%>

  <%for(int k=0; k<dateList.size(); k++){%>
  <% int j = 0; %>
  <ul style="list-style-type: none;">
    <div style="box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.16); width: 97%;">
    <%for(int i=0; i<bandoList.size(); i++){%>
      <%if((loggedAdminOn) || (LocalDate.now().isBefore(LocalDate.parse(bandoList.get(i).getDataScadenzaIscrizione().toStringSQL()) ))){ %>

        <%if(dateList.get(k).equals(bandoList.get(i).getDataSQL(bandoList.get(i).getData()))){%>
        <% j++; %>
        <%if(j == 1){%> <h1 class="data"><%=dateList.get(k)%></h1> <%}%>

        <li style="display: flex; align-items: center;">
          <form name="bandoView<%=bandoList.get(i).getId()%>" action="/viewBando" method="post" style="margin-bottom: 0; width: 100%; display: flex; align-items: center; justify-content: center;">
            <!--
                name: permette di identificare il form in maniera univoca
                action: url dello user agent a cui viene sottomessa la form
                method: metodo http con cui vengono passati i parametri
            -->

            <input type="hidden" name="bandoId" value="<%=bandoList.get(i).getId()%>">
            <!-- type hidden: usato per inviare parametri con la form specificandone il nome -->

            <input type="submit" value="<%=bandoList.get(i).getId()%>: <%=bandoList.get(i).getLocazione()%> - <%=bandoList.get(i).getOggetto()%>" style="color: #2f6a9c; font-weight: bold; cursor: pointer; border: none; width: 70%; height: 25px; background: transparent;">
          </form>
        </li>

        <li style="display: flex; align-items: center; margin-top: 10px">
          <% if(loggedAdminOn){%>
          <form name="bandoDelete<%=bandoList.get(i).getId()%>" action="/deleteBando" method="post" style="margin-bottom: 0; margin-left: 1rem; padding: 0; display: flex;">

            <input type="hidden" name="bandoId" value="<%=bandoList.get(i).getId()%>">

            <input type="submit" value="Elimina" style="display: inline-block; background-color: #e53e3e; color: #fff; padding: 0.5rem 1rem; border-radius: 0.25rem; cursor: pointer; font-weight: bold;">

          </form>

          <form name="bandoModify<%=bandoList.get(i).getId()%>" action="/modificaBandoView" method="post" style="margin-bottom: 0; margin-left: 1rem; padding: 0; display: flex;">

            <input type="hidden" name="bandoId" value="<%=bandoList.get(i).getId()%>">

            <input type="submit" value="Modifica" style="padding: 8px 15px; background-color: #3490dc; color: #fff; border: none; border-radius: 4px; cursor: pointer;">

          </form>
          <%}%>
        <%}%>
      </li>
      <%}%>
    <%}%>
    </div>
  </ul>
  <%}%>
</main>
<%@include file="../../html_daIncludere/footer.inc"%>
</body>
</html>
