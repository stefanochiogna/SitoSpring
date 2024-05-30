<%@ page import="com.example.sitoforzaarmata.model.mo.Base.Base" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% List<Base> basi = (List<Base>) request.getAttribute("listaBasi"); %>
<html>
<head>
    <%@include file="../html_daIncludere/Header.inc"%>
  <style>
    .hidden{
      display: none;
    }
  </style>
</head>
<body>
  <form action="Dispatcher" method="post" enctype="multipart/form-data">

    <section>
      <label for="Nome"> Nome: </label>
      <input type="text" id="Nome" name="Nome" maxlength="20" required>
      <br/>

      <label for="Cognome"> Cognome: </label>
      <input type="text" id="Cognome" name="Cognome" maxlength="20" required>
      <br/>

      <label for="Sesso"> Sesso: </label>
      <select id="Sesso" name="Sesso" required>
        <option value="M"> Uomo </option>
        <option value="F"> Donna </option>
      </select><br/>

      <label for="DataNascita">Data di nascita: </label>
      <input id="DataNascita" name="DataNascita" type="date">

      <script>
        var dataOggi = new Date().toISOString().split("T")[0];
        var dataMin = new Date(dataOggi.getFullYear() - 18, dataOggi.getMonth(), dataOggi.getDate());
        document.getElementById("DataNascita").setAttribute("min", dataMin);
      </script>

      <label for="CF"> Codice Fiscale: </label>
      <input type="text" id="CF" name="CF" pattern=".{16}" maxlength="16">
      <br>
    </section>

    <section>
      <label for="Indirizzo"> Indirizzo: </label>
      <input type="text" id="Indirizzo" name="Indirizzo" maxlength="45">
      <br>

      <label for="Telefono"> Telefono: </label>
      <input type="tel" id="Telefono" name="Telefono" pattern="[0-9]{10}" maxlength="10">
      <br>

      <label for="Email"> Email: </label>
      <input type="email" id="Email" name="Email" maxlength="45" required>
      <br>

      <label for="Password"> Password: </label>
      <input type="password" id="Password" name="Password" maxlength="45" required>
      <br>
    </section>

    <label for="IBAN"> IBAN: </label>
    <input type="text" id="IBAN" name="IBAN" pattern=".{27}" maxlength="27" required>
    <br>

    <section>
      <label for="Ruolo"> Ruolo: </label>
      <select id="Ruolo" name="Ruolo">
        <option value=""> - </option>
        <option value="Ufficiale"> Ufficiale </option>
        <option value="Sottufficiale"> Sottufficiale </option>
        <option value="Graduato"> Graduato </option>
      </select>
      <br>

      <label for="LocazioneServizio" id="LabelLocazioneServizio" class="hidden"> Locazione Servizio: </label>
      <select id="LocazioneServizio" name="LocazioneServizio" class="hidden">
        <%for(int i=0; i<basi.size(); i++){%>
          <option value="<%=basi.get(i).getLocazione()%>"> base aerea di <%=basi.get(i).getLocazione()%></option>
        <%}%>
      </select>
      <br>

      <script>
        var RuoloSelect = document.getElementById("Ruolo")

        RuoloSelect.addEventListener("change", function(){
          var RuoloValue = RuoloSelect.value;
          var Locazione = document.getElementById("LocazioneServizio");
          var LabelLocazione = document.getElementById("LabelLocazioneServizio");

          if(RuoloValue !== ""){
            Locazione.classList.remove('hidden');
            LabelLocazione.classList.remove('hidden');
          }
          else{
            Locazione.classList.add('hidden');
            LabelLocazione.classList.add('hidden');
          }
        });
      </script>
    </section>

    <section>
      <label for="Foto"> Foto: </label>
      <input type="file" id="Foto" name="Foto">
      <br>

      <label for="Documento"> Documento identificativo: </label>
      <input type="file" id="Documento" name="Documento">
      <br>
    </section>

    <label for="Newsletter"> Newsletter: </label>
    <input type="checkbox" id="Newsletter" name="Newsletter">
    <br>

    <input type="hidden" name="controllerAction" value="Login.Registrazione"/>
    <input type="submit" value="Ok">

  </form>

  <a href="Dispatcher?controllerAction=Login.view">Indietro</a>
</body>
</html>
