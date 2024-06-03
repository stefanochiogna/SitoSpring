<%@ page import="com.example.sitoforzaarmata.model.mo.Notizie.Notizie" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String menuActiveLink = "Home";
%>

<html>
<head>
    <%@include file="/html_daIncludere/Header.inc"%>
    <style>

        /* SFONDO */
        div.immagine{
            position: relative;
            /* filter: brightness(50%); */
            height: 900px;
            place-items: center;
            margin: 0;
            padding: 0;
            text-align: center;
            width: 100%;
            background: url('https://www.concorsiaeronautica.it/wp-content/uploads/2020/05/come-entrare-in-aeronautica.jpg') no-repeat center center;
            background-size: cover;
            z-index: -1;
        }
        div.immagine h1.titolo{
            margin: 0;
            /* opacity: 75%; */
            padding: 0px 15px;
            background-color: rgba(15, 15, 15, 0.42);
            position: absolute;
            top: 50%;
            left: 50%;
            font-size: 75px;
            transform: translate(-50%, -50%);
            color: white;
        }

        /* ARTICOLI */
        .bloccoArt{
            float: left;
            width: 23%;
            margin: 25px 1%;
        }
        .articoli{
            display: block;
            width: 100%;
            height: 15%;
            background-color: white;
            color: black;
        }

        .articoli:hover{
            transform: translateY(-10px);
        }
        .modifica{
            float: bottom;
        }
    </style>

</head>
<body>
    <!-- Navbar -->
    <%@include file="../html_daIncludere/navbar.inc"%>

    <div class="immagine">
        <h1 class="titolo">Aeronautica Militare</h1>
    </div>

    <section>
        <%for(int i=1; i<=4; i++){%>
            <% String notizia = "notizia" + i; %>
            <% Notizie notizie = (Notizie) request.getAttribute(notizia); %>
            <section class="bloccoArt">
                <form name="Articolo<%=notizia%>" class="Articolo" action="Dispatcher" method="post">
                    <input type="hidden" name="controllerAction" value="PaginaIniziale.viewArt">
                    <input type="hidden" name="Id" value="<%=notizie.getID()%>">
                    <input class="articoli" type="submit" value="<%=notizie.getOggetto()%>">
                </form>

                <%if(loggedAdminOn){%>
                    <form name="Sostituire<%=notizia%>" action="Dispatcher" class="modifica" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="controllerAction" value="PaginaIniziale.sostituisciArticolo">

                        <label for="Oggetto">Oggetto: </label>
                        <input type="text" id="Oggetto" name="Oggetto"><br>

                        <label for="Testo"> Testo del nuovo articolo: </label>
                        <input type="file" id="Testo" name="Testo"><br>

                        <input type="hidden" name="IdAdministrator" value="<%=loggedAdmin.getIdAdministrator()%>">

                        <input type="hidden" name="Id" value="<%=notizie.getID()%>">
                        <input type="submit" value="modifica">
                    </form>
                <%}%>
            </section>
        <%}%>

    </section>

    <%@include file="../html_daIncludere/footer.inc"%>
</body>
</html>
