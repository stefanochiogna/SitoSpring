<%@ page import="com.example.sitoforzaarmata.model.mo.Base.Pasto" %>
<%@ page import="com.example.sitoforzaarmata.model.mo.Base.PostoLetto" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath=request.getContextPath();
    Pasto pasto = (Pasto) request.getAttribute("Pasto");
    PostoLetto alloggio = (PostoLetto) request.getAttribute("PostoLetto");
%>
<html>
<head>
    <%@include file="../../../../../static/html_daIncludere/Header.inc"%>
</head>
<body>

    <h1> La prenotazione è stata effettuata correttamente: </h1>
    <%if(pasto != null){%>
        <section>
            <p>
            Turno <%=pasto.getTurno()%> prenotato in mensa per la data <%=pasto.getData_prenotazione().toStringSQL()%> presso la base di <%=pasto.getLocazione()%><br>
            Identificativo del turno: <%=pasto.getId()%> <br>
            </p>
        </section>
    <%}
    else if(alloggio != null){%>
    <section>
        <p>
            Alloggerai presso la base di <%=alloggio.getLocazione()%><br>
            L'arrivo è previsto per la data <%=alloggio.getData_arrivo().toStringSQL()%> per <%=alloggio.getNum_notti()%> noti<br>
            <br>
            Mostra questo identificativo della prenotazione: <%=alloggio.getId()%>
        </p>
    </section>
    <%}%>

    <button id="Conferma"> Conferma </button>
    <script>
        document.getElementById("Conferma").addEventListener("click", onclick)
        function onclick(){
            window.location.href = "<%=contextPath%>/Dispatcher?controllerAction=ListaBasi.view";
        }
    </script>
</body>
</html>
