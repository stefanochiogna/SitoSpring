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

  <style>
    div.container{
      margin: 25px;
      padding: 1.5rem;
      background-color: #fff;
      border-radius: 0.5rem;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
      width: 96.5%;
    }
    div.container h1.titolo{
      font-size: 2rem;
      font-weight: bold;
      margin-bottom: 1rem;
      text-align: center;
      background: #343B66;
      color: white;
      display: block;
    }
    div.container p.testo{
      margin-bottom: 1rem;
      display: block;
    }
    div.container a.indietro{
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
<body style="background-color: #f7fafc;">
<div class="container">
  <h1 class="titolo">
    <%=newsletter.getOggetto()%>
  </h1>

  <%
    try{
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(newsletter.getRiferimentoTesto().toString()), "UTF-8"));

      String line = br.readLine();
      while(line != null){%>
  <p class="testo">
    <%=line%>
    <%line = br.readLine();%>
  </p>
  <%}
  }
  catch (Exception e){
    throw new RuntimeException(e);
  }
  %>

  <a href="Dispatcher?controllerAction=BachecaNewsletter.view" class="indietro"> Indietro </a>


</div>

</body>
</html>
