<%@ page import="com.example.sitoforzaarmata.model.mo.Notizie.Newsletter" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<Newsletter> newsletterList = new ArrayList<>();
    newsletterList.addAll((List<Newsletter>) request.getAttribute("Newsletter"));

    String menuActiveLink = "Newsletter";
%>

<html>
<head>
    <%@include file="/html_daIncludere/Header.inc"%>
</head>
<body>
<!-- Navbar -->
<%@include file="../../../../../static/html_daIncludere/navbar.inc"%>

<section class="pagina newsletter">
    <ul>
        <a href="Dispatcher?controllerAction=BachecaAvviso.view"> Avvisi </a>

        <% if(loggedAdminOn){%>
        <form name="newNewsletter" action="Dispatcher" method="post">

            <label for="Oggetto"> Oggetto: </label>
            <input type="text" id="Oggetto" name="Oggetto" maxlength="45">

            <label for="Testo"> Testo dell'avviso: </label>
            <textarea name="Testo" id="Testo" required></textarea>

            <input type="hidden" name="controllerAction" value="BachecaNewsletter.inviaNewsletter"/>

            <input type="submit" value="Invia Nuova Newsletter">
        </form>
        <%}%>
        <%for(int i=0; i<newsletterList.size(); i++){%>
        <li>
            <form name="newsletterView<%=newsletterList.get(i).getID()%>" action="Dispatcher" method="post">

                <input type="hidden" name="newsletterId" value="<%=newsletterList.get(i).getID()%>">

                <input type="hidden" name="controllerAction" value="BachecaNewsletter.viewNewsletter"/>

                <input type="submit" value="<%=newsletterList.get(i).getID()%>: <%=newsletterList.get(i).getOggetto()%>">
            </form>

            <form name="newsletterDelete<%=newsletterList.get(i).getID()%>" action="Dispatcher" method="post">

                <input type="hidden" name="newsletterId" value="<%=newsletterList.get(i).getID()%>">

                <input type="hidden" name="controllerAction" value="BachecaNewsletter.deleteNewsletter"/>

                <input type="submit" value="Elimina" >

            </form>

        </li>
        <%}%>
    </ul>
</section>

</html>
