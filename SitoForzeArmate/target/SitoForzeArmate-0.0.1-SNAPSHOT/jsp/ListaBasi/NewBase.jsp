<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Inserimento Base</title>
</head>
<body>
<form action="Dispatcher" method="post" enctype="multipart/form-data">

  <section>
    <label for="Foto"> Foto della Base: </label>
    <input type="file" id="Foto" name="Foto" required> <br>

    <label for="Email"> Email: </label>
    <input id="Email" name="Email" type="email" maxlength="45" required><br>

    <label for="Telefono"> Telefono: </label>
    <input id="Telefono" name="Telefono" type="tel" pattern="[0-9]{10}" maxlength="10" required><br>

  </section>
  <section>
    <label for="Locazione"> Locazione nuova base</label>
    <input id="Locazione" name="Locazione" type="text" maxlength="20" required>
    <br>

    <label for="Provincia"> Provincia: </label>
    <input id="Provincia" name="Provincia" type="text" maxlength="20" required><br>

    <label for="CAP"> CAP: </label>
    <input id="CAP" name="CAP" type="text" pattern="[0-9]{5}" maxlength="5" required><br>

    <label for="Via"> Via: </label>
    <input id="Via" name="Via" type="text" maxlength="45" required><br>

    <label for="Latitudine"> Latitudine: </label>
    <input id="Latitudine" name="Latitudine" type="text" required><br>

    <label for="Longitudine"> Longitudine: </label>
    <input id="Longitudine" name="Longitudine" type="text" required><br>
  </section>

  <input type="hidden" name="controllerAction" value="ListaBasi.newBase">
  <input type="submit" value="Conferma">
</form>
</body>
</html>
