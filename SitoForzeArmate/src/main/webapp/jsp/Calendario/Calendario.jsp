<%@ page import="com.example.sitoforzaarmata.model.mo.Bando" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<Bando> bandoList = new ArrayList<>();
    bandoList.addAll((List<Bando>) request.getAttribute("Bandi"));

    List<String> dateList = new ArrayList<>();
    dateList.addAll((List<String>) request.getAttribute("Date"));

    String menuActiveLink = "Calendario";
%>
<html>
<head>
    <%@include file="/html_daIncludere/Header.inc"%>
</head>
<body>
    <!-- Navbar -->
    <%@include file="../../html_daIncludere/navbar.inc"%>

    <% if(loggedAdminOn){%>
        <form name="newBando" action="Dispatcher" method="post">
            <input type="hidden" name="loggedAdmin" value="<%=loggedAdmin%>">

            <input type="hidden" name="controllerAction" value="Calendario.modificaBandoView"/>

            <input type="submit" value="Nuovo Bando">
        </form>
    <%}%>

    <%for(int k=0; k<dateList.size(); k++){%>
    <ul>
        <%for(int i=0; i<bandoList.size(); i++){%>
            <div>
                <%if(dateList.get(k).equals(bandoList.get(i).getDataSQL(bandoList.get(i).getData()))){%>
                <h1><%=dateList.get(i)%></h1>
                <li>
                    <form name="bandoView<%=bandoList.get(i).getId()%>" action="Dispatcher" method="post">
                        <!--
                            name: permette di identificare il form in maniera univoca
                            action: url dello user agent a cui viene sottomessa la form
                            method: metodo http con cui vengono passati i parametri
                        -->

                        <input type="hidden" name="bandoId" value="<%=bandoList.get(i).getId()%>">
                        <!-- type hidden: usato per inviare parametri con la form specificandone il nome -->

                        <input type="hidden" name="controllerAction" value="Calendario.viewBando"/>

                        <input type="submit" value="<%=bandoList.get(i).getId()%>: <%=bandoList.get(i).getOggetto()%>">
                    </form>

                    <% if(loggedAdminOn){%>
                        <form name="bandoDelete<%=bandoList.get(i).getId()%>" action="Dispatcher" method="post">

                            <input type="hidden" name="bandoId" value="<%=bandoList.get(i).getId()%>">

                            <input type="hidden" name="controllerAction" value="Calendario.deleteBando"/>

                            <input type="submit" value="Elimina" >

                        </form>

                        <form name="bandoModify<%=bandoList.get(i).getId()%>" action="Dispatcher" method="post">

                            <input type="hidden" name="bandoId" value="<%=bandoList.get(i).getId()%>">

                            <input type="hidden" name="controllerAction" value="Calendario.modificaBandoView"/>

                            <input type="submit" value="Modifica" >

                        </form>
                    <%}%>
                </li>
                <%}%>
            </div>
        <%}%>
    </ul>
    <%}%>
</body>
</html>
