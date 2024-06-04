<%@ page import="com.progetto.sitoforzearmate.model.mo.Notizie.Notizie" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String menuActiveLink = "Home";
%>

<html>
<head>
  <%@include file="../../static/html_daIncludere/Header.inc"%>
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
      z-index: -2;
    }
    div.immagine div.container{
      position: relative;
      height: 900px;
      place-items: center;
      margin: 0;
      padding: 0;
      text-align: center;
      width: 100%;
      background: url('https://www.aeronautica.difesa.it/wp-content/uploads/2022/12/brand_AM_2022_NEGATIVO-1024x341-1.png') no-repeat center center;
      opacity: 50%;
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
    section.bloccoArt{
      float: left;
      width: 23%;
      margin: 25px 1%;
    }


    section.bloccoArt button.containerArticolo{
      display: flex;
      justify-content: center;
      align-items: center;
      height: 170px;
      background-image: url("https://accademiaconcorsimilitari.it/wp-content/uploads/2017/05/AERONAUTICA-300x300.jpg");
      /* background-image: url("https://www.osservatorelibero.it/wp-content/uploads/2020/11/Douhet-Rigel-2020-Allievi-6-524x350.jpg"); */
      background-repeat: no-repeat;
      background-position: center;
      background-size: 65%;
      background-color: white;
      width: 100%;
    }
    section.bloccoArt button.containerArticolo:hover{
      border-color: #63b3ed;
    }

    section.bloccoArt button.containerArticolo input[type="submit"].articoli{
      text-align: center;
      background-color: rgba(240, 240, 240, 0.72);
      color: #2F2F2F;
      border: none;
      font-size: 20px;
      cursor: pointer;
    }

    .modifica{
      float: bottom;
    }

  </style>
</head>
<body>
<!-- Navbar -->
<%@include file="../../static/html_daIncludere/navbar.inc"%>
<main>
<div class="immagine">
  <div class="container"></div>
  <h1 class="titolo">Aeronautica Militare</h1>
</div>
<section>
  <% for (int i = 1; i <= 4; i++) { %>
  <% String notizia = "notizia" + i; %>
  <% Notizie notizie = (Notizie) request.getAttribute(notizia); %>
  <section class="bloccoArt">
      <form name="Articolo<%=notizia%>" class="Articolo" action="/viewArt" method="post">
        <input type="hidden" name="Id" value="<%=notizie.getID()%>">

        <button class="containerArticolo">
          <input class="articoli" type="submit" value="<%=notizie.getOggetto()%>">
        </button>
      </form>

    <% if (loggedAdminOn) { %>
    <div class="containerAdmin">
      <form name="Sostituire<%=notizia%>" action="/modifyArticolo" class="modifica mt-4"
            method="post" enctype="multipart/form-data" style="float: bottom;">

        <label for="Oggetto" style="display: block; font-weight: bold; color: #4a5568;">Oggetto: </label>
        <input type="text" id="Oggetto" name="Oggetto"
               style="margin-top: 0.25rem; display: block; width: 100%; padding: 0.25rem 0.5rem; border-radius: 0.25rem; border: 1px solid #cbd5e0;"
               required><br>

        <label for="Testo" style="display: block; font-weight: bold; color: #4a5568;">Testo del nuovo articolo: </label>
        <input type="file" id="Testo" name="Testo"
               style="margin-top: 0.25rem; display: block; width: 100%; padding: 0.25rem 0.5rem; border-radius: 0.25rem; border: 1px solid #cbd5e0;"
               required><br>

        <input type="hidden" name="IdAdministrator"
               value="<%=loggedAdmin.getIdAdministrator()%>">

        <input type="hidden" name="Id" value="<%=notizie.getID()%>">
        <input type="submit"
               style="margin-top: 1rem; display: block; width: 100%; background-color: #4299e1; color: #ffffff; font-weight: bold; padding: 0.5rem 1rem; border-radius: 0.25rem; border: none; cursor: pointer;"
               value="modifica">
      </form>
    </div>
    <% } %>
  </section>
  <% } %>
</section>
</main>
<%@include file="../../static/html_daIncludere/footer.inc"%>

</body>
</html>
