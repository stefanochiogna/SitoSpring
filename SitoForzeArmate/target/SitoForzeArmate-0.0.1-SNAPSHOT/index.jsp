<%@page session="false"%>
<!-- page session="false": indica che non verrà mantenuto lo stato tra chiamate http successive -->
<%String contextPath=request.getContextPath();%>
<!-- contextPath(): recupera il percorso directory -->

<!DOCTYPE HTML>
<html lang="it-IT">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="refresh" content="0; url=/homepage">
       <!-- refresh indica il numero di secondi da attendere prima che la pagina venga ridirezionata ad un'altra
            dopo 0 secondi viene quindi caricata la pagina seguita da url= -->

    <script type="text/javascript">
        // text/javascript: specifica codice scritto in linguaggio javascript

        function onLoadHandler() {
            window.location.href = "/homepage";
            // permette di caricare la pagina fornita
            // si va a chiamare il dispatcher che poi fornisce la view adeguata
        }
        window.addEventListener("load", onLoadHandler);
        // imposta onLoadHandler come listener di eventi di tipo load
    </script>
    <title>Page Redirection</title>
</head>
<body>
    If you are not redirected automatically, follow the <a href='/homepage'>link</a>
</body>
</html>
