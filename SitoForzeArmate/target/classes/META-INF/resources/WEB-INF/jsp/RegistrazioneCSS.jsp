<%@ page import="com.example.sitoforzaarmata.model.mo.Base.Base" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% List<Base> basi = (List<Base>) request.getAttribute("listaBasi"); %>
<html>
<head>
  <%@include file="../../../../static/html_daIncludere/Header.inc"%>
  <style>
    .hidden{
      display: none;
    }
       /* Stili per il form */
     .form-container {
       margin: 0 250px;
       padding: 1.5rem;
       background-color: #fff;
       border-radius: 0.5rem;
       box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
     }

    .form-field {
      margin-bottom: 1rem;
    }

    .form-label {
      font-weight: 500;
      color: #4a5568;
    }

    .form-input {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #e2e8f0;
      border-radius: 0.25rem;
      outline: none;
      transition: border-color 0.2s ease-in-out;
    }

    .form-input:focus {
      border-color: #63b3ed;
    }

    .form-select {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #e2e8f0;
      border-radius: 0.25rem;
      outline: none;
      transition: border-color 0.2s ease-in-out;
    }

    .form-select:focus {
      border-color: #63b3ed;
    }

    .form-button {
      background-color: #4299e1;
      color: #fff;
      padding: 0.75rem 1rem;
      border-radius: 0.25rem;
      cursor: pointer;
    }

    .form-button:hover {
      background-color: #3182ce;
    }

    .form-link {
      color: #4299e1;
      text-decoration: underline;
    }

    .form-flex-container {
      display: flex;
      justify-content: space-between;
    }

    .form-field-left {
      flex: 1;
      margin-right: 1rem;
    }

    .form-field-right {
      flex: 1;
      margin-left: 1rem;
    }

  </style>
</head>
<body class="bg-gray-100">
<form action="Dispatcher" method="post" enctype="multipart/form-data" class="form-container">

  <div class="form-flex-container">
    <div class="form-field form-field-left">
      <label for="Nome" class="form-label"> Nome: </label>
    <input type="text" id="Nome" name="Nome" maxlength="20" required class="form-input">

      <label for="Cognome" class="form-label"> Cognome: </label>
      <input type="text" id="Cognome" name="Cognome" maxlength="20" required class="form-input">

    <label for="Sesso" class="form-label"> Sesso: </label>
    <select id="Sesso" name="Sesso" required class="form-select">
      <option value="M"> M </option>
      <option value="F"> F </option>
    </select>

    <label for="DataNascita" class="form-label">Data di nascita: </label>
    <input id="DataNascita" name="DataNascita" type="date" class="form-input">

  <script>
    var dataOggi = new Date().toISOString().split("T")[0];
    var dataMin = new Date(dataOggi.getFullYear() - 18, dataOggi.getMonth(), dataOggi.getDate());
    document.getElementById("DataNascita").setAttribute("min", dataMin);
  </script>

  <label for="CF" class="form-field"> Codice Fiscale: </label>
  <input type="text" id="CF" name="CF" pattern=".{16}" maxlength="16" class="form-input">
  <br>

    <label for="Indirizzo" class="form-label"> Indirizzo: </label>
    <input type="text" id="Indirizzo" name="Indirizzo" maxlength="45" class="form-input">
    <br>

    <label for="Telefono" class="form-label"> Telefono: </label>
    <input type="tel" id="Telefono" name="Telefono" pattern="[0-9]{10}" maxlength="10" class="form-input">
    <br>

    </div>

    <div class="form-field form-field-right">
    <label for="Email" class="form-label"> Email: </label>
    <input type="email" id="Email" name="Email" maxlength="45" required class="form-input">
    <br>

    <label for="Password" class="form-label"> Password: </label>
    <input type="password" id="Password" name="Password" maxlength="45" required class="form-input">
    <br>

  <label for="IBAN" class="form-label"> IBAN: </label>
  <input type="text" id="IBAN" name="IBAN" pattern=".{27}" maxlength="27" required class="form-input">
  <br>

    <label for="Ruolo" class="form-label"> Ruolo: </label>
    <select id="Ruolo" name="Ruolo" class="form-select">
      <option value=""> - </option>
      <option value="Ufficiale"> Ufficiale </option>
      <option value="Sottufficiale"> Sottufficiale </option>
      <option value="Graduato"> Graduato </option>
    </select>
    <br>

    <label for="LocazioneServizio" id="LabelLocazioneServizio" class="hidden" class="form-label"> Locazione Servizio: </label>
    <select id="LocazioneServizio" name="LocazioneServizio" class="hidden" class="form-select">
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

    <label for="Foto" class="form-label"> Foto: </label>
    <input type="file" id="Foto" name="Foto" class="form-input">
    <br>

    <label for="Documento" class="form-label"> Documento identificativo: </label>
    <input type="file" id="Documento" name="Documento" class="form-input">
    <br>

  <label for="Newsletter" class="form-label"> Newsletter: </label>
  <input type="checkbox" id="Newsletter" name="Newsletter" class="form-input">
  <br>

    </div>
  </div>

  <div style="float: bottom;">

    <a href="Dispatcher?controllerAction=Login.view" class="form-button" style="float: right">Indietro</a>

  <input type="hidden" name="controllerAction" value="Login.Registrazione">
  <input type="submit" value="Ok" class="form-button">

  </div>
</form>
</body>
</html>
