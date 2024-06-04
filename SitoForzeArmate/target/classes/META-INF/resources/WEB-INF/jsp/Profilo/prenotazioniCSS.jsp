<%@ page import="com.progetto.sitoforzearmate.model.mo.Base.PostoLetto" %>
<%@ page import="java.util.List" %>
<%@ page import="com.progetto.sitoforzearmate.model.mo.Base.Pasto" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.progetto.sitoforzearmate.model.mo.Utente.UtenteRegistrato" %>
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
    <title>Prenotazione Utente</title>
    <style>
        a.indietro {
            color: white;
            text-decoration: underline;
            display: block;
            background: #3b82f6;
            cursor: pointer;
            text-align: center;
            width: 5%;
        }
    </style>
</head>
<body>
<div>
    <section style="box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)">
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

    <section style="box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)">
        <h1> Pasti prenotati: </h1>
        <ul>
            <%if(pastiList != null){
                for(int i=0; i<pastiList.size(); i++){%>
            <li>
                Prenotazione presso: <%=pastiList.get(i).getLocazione()%><br>
                Data: <%=pastiList.get(i).getData_prenotazione().toStringSQL()%> per turno <%=pastiList.get(i).getTurno()%> <br>
                Id della prenotazione: <%=pastiList.get(i).getId()%>
            </li>
                <%}%>
            <%}%>
        </ul>
    </section>
</div>
<a href="/viewProfilo" class="indietro"> Indietro </a>
</body>
</html>
