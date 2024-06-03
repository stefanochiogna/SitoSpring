<%@ page import="com.example.sitoforzaarmata.model.mo.Notizie.Avviso" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="com.example.sitoforzaarmata.model.mo.Notizie.Newsletter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../../../../../static/html_daIncludere/Header.inc"%>
<%
    Newsletter newsletter = (Newsletter) request.getAttribute("NewsletterSelezionata");
%>
<html>
<head>
    <title><%=newsletter.getOggetto()%></title>
</head>
<body>
<section>
    <h1>
        <%=newsletter.getOggetto()%>
    </h1>

    <%
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(newsletter.getRiferimentoTesto().toString()), "UTF-8"));

            String line = br.readLine();
            while(line != null){%>
            <p>
                <%=line%>
                <%line = br.readLine();%>
            </p>
            <%}
            }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    %>
    <form name="newsletterAnnulla<%=newsletter.getID()%>" action="Dispatcher" method="post">

        <input type="hidden" name="controllerAction" value="BachecaNewsletter.view"/>

        <input type="submit" value="Indietro">

    </form>


</section>

</body>
</html>
