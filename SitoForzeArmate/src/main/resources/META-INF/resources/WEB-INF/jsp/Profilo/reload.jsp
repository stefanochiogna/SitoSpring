<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String contextPath=request.getContextPath();%>
<html>
<head>
  <script type="text/javascript">
    // text/javascript: specifica codice scritto in linguaggio javascript

    function onLoadHandler() {
      window.location.href = "<%=contextPath%>/Dispatcher?controllerAction=Profilo.view";
      // permette di caricare la pagina fornita
      // si va a chiamare il dispatcher che poi fornisce la view adeguata
    }
    window.addEventListener("load", onLoadHandler);
    // imposta onLoadHandler come listener di eventi di tipo load
  </script>
  <title>Page Redirection</title>
</head>
<body>
If you are not redirected automatically, follow the <a href='/viewProfilo'>link</a>
</body>
</html>