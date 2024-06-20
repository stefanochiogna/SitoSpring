<%@ page import="com.progetto.sitoforzearmate.model.mo.Base.Base" %>
<%@ page import="java.util.Base64" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../../html_daIncludere/Header.inc"%>
<%
    Base base = (Base) request.getAttribute("BaseSelezionata");
%>
<%String Immagine64 = Base64.getEncoder().encodeToString(base.getFotoByte());%>
<html>
<head>
    <style>
        div.immagine {
            position: relative;
            height: 400px;
            place-items: center;
            margin: 0;
            padding: 0;
            text-align: center;
            width: 100%;
            background-image: url('data:image/jpg;base64, <%=Immagine64%>');
            background-position: center;
            background-repeat: no-repeat;
            background-size: contain;
        }

        div.immagine::before {
            content: "";
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-image: url('data:image/jpg;base64, <%=Immagine64%>');
            background-position: center;
            background-repeat: no-repeat;
            background-size: cover;
            opacity: 0.5;
            z-index: -1;
        }

        div.immagine h1.titoloBase{
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

    </style>
</head>
<body style="display: flow-root;">
    <div class="immagine"><h1 class="titoloBase"> Base di <%=base.getLocazione()%></h1> </div>

    <% if(loggedOn){ %>
    <section>
        <ul style="list-style-type: none; display:flex">
            <li style="display: flex; align-items: center; margin-top: 10px; margin-left: 1%; margin-right: 1%; width: 48%">
                <form name="prenotaPasto<%=base.getLocazione()%>" action="/viewPrenotaPasto" method="post" style="margin: 0px; padding: 0px; width: 100%">

                    <input type="hidden" name="locazioneBase" value="<%=base.getLocazione()%>">

                    <input type="submit" value="Prenota Pasto" style="border: none; width: 100%; background-color: rgba(112, 172, 226, 0.7); color: #fff; padding: 8px 15px; border-radius: 4px; cursor: pointer;  font-weight: bold;">
                </form>
            </li>
            <li style="margin-bottom: 0; margin-left: 1%; margin-right: 1%; padding: 0; display: flex; margin-top: 10px; width: 48%">
                <form name="prenotaAlloggio<%=base.getLocazione()%>" action="/viewPrenotaAlloggi" method="post" style="margin: 0px; padding: 0px; width: 100%">

                    <input type="hidden" name="locazioneBase" value="<%=base.getLocazione()%>">

                    <input type="submit" value="Prenota Alloggio" style="width: 100%; padding: 8px 15px; background-color: rgba(112, 172, 226, 0.7); color: #fff; border: none; border-radius: 4px; cursor: pointer;">

                </form>

            </li>
        </ul>
    </section>
    <%}%>

    <section>

        <div>
            <h1 style="font-size: 25px"> Indicazioni: </h1>
            <p> <%=base.getVia()%>  <%=base.getLocazione()%>, <%=base.getCAP()%> <%=base.getProvincia()%></p>
        </div>

        <p style="color: blueviolet; display: block;">
            Se hai bisogno di ulteriori informazioni, contatta la segreteria: <br>
            E-mail: <%= base.getMail() %> <br>
            Telefono: <%= base.getTelefono() %> <br>
        </p>
        <br>

    </section>

    <a href="/viewListaBasi" class="indietro"> Indietro </a>

</body>
</html>
