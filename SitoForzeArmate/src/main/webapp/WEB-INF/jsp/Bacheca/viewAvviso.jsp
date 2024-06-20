<%@ page import="com.progetto.sitoforzearmate.model.mo.Notizie.Avviso" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../../../html_daIncludere/Header.inc"%>
<%
    Avviso avviso = (Avviso) request.getAttribute("AvvisoSelezionato");
%>
<html>
<head>
    <title><%=avviso.getOggetto()%></title>

</head>
<body>
    <section>
        <h1>
            <%=avviso.getOggetto()%>
        </h1>

        <%
            try{
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(avviso.getRiferimentoTesto().toString()), "UTF-8"));

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
        <form name="avvisoAnnulla<%=avviso.getID()%>" action="Dispatcher" method="post">

            <input type="hidden" name="controllerAction" value="BachecaAvviso.view"/>

            <input type="submit" value="Indietro">

        </form>


    </section>

</body>
</html>
