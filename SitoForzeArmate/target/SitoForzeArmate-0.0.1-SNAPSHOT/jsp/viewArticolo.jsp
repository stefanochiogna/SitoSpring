<%@ page import="com.example.sitoforzaarmata.model.mo.Notizie.Notizie" %>
<%@ page import="java.io.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Notizie notizia = (Notizie) request.getAttribute("NotiziaSelezionata");
%>
<html>
<head>
    <%@include file="../html_daIncludere/Header.inc"%>
</head>
<body>
    <h1>
        <%=notizia.getOggetto()%>
    </h1>

    <%
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(notizia.getRiferimentoTesto().toString()), "UTF-8"));

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
    <a href="Dispatcher?controllerAction=PaginaIniziale.view"> Indietro </a>
</body>
</html>
