<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Inserimento Base</title>
</head>
<body style="background-color: #F3F4F6;">
<h1 style="display: flex; justify-content: center;"> Inserimento base: </h1>
<div style="display: flex; align-items: center; justify-content: center;">
<form action="Dispatcher" method="post" enctype="multipart/form-data" style="max-width: 25rem; padding: 2rem; background-color: #FFF; border-radius: 0.5rem; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);">

    <section style="margin-bottom: 1rem;">
        <label for="Foto" style="color: #4B5563; font-weight: 600;"style="color: #4B5563; font-weight: 600;"> Foto della Base: </label>
        <input type="file" id="Foto" name="Foto" required style="width: 100%; border: 1px solid #D1D5DB; border-radius: 0.25rem; padding: 0.5rem 0.75rem; margin-top: 0.25rem;"> <br>
    </section>
    <section style="margin-bottom: 1rem;">
        <label for="Email" style="color: #4B5563; font-weight: 600;">Email:</label>
        <input id="Email" name="Email" type="email" maxlength="45" required style="width: 100%; border: 1px solid #D1D5DB; border-radius: 0.25rem; padding: 0.5rem 0.75rem; margin-top: 0.25rem;">
    </section>

    <section style="margin-bottom: 1rem;">
        <label for="Telefono" style="color: #4B5563; font-weight: 600;">Telefono:</label>
        <input id="Telefono" name="Telefono" type="tel" pattern="[0-9]{10}" maxlength="10" required style="width: 100%; border: 1px solid #D1D5DB; border-radius: 0.25rem; padding: 0.5rem 0.75rem; margin-top: 0.25rem;">
    </section>

    <section style="margin-bottom: 1rem;">
        <label for="Locazione" style="color: #4B5563; font-weight: 600;">Locazione nuova base:</label>
        <input id="Locazione" name="Locazione" type="text" maxlength="20" required style="width: 100%; border: 1px solid #D1D5DB; border-radius: 0.25rem; padding: 0.5rem 0.75rem; margin-top: 0.25rem;">
    </section>

    <section style="margin-bottom: 1rem;">
        <label for="Provincia" style="color: #4B5563; font-weight: 600;">Provincia:</label>
        <input id="Provincia" name="Provincia" type="text" maxlength="20" required style="width: 100%; border: 1px solid #D1D5DB; border-radius: 0.25rem; padding: 0.5rem 0.75rem; margin-top: 0.25rem;">
    </section>

    <section style="margin-bottom: 1rem;">
        <label for="CAP" style="color: #4B5563; font-weight: 600;">CAP:</label>
        <input id="CAP" name="CAP" type="text" pattern="[0-9]{5}" maxlength="5" required style="width: 100%; border: 1px solid #D1D5DB; border-radius: 0.25rem; padding: 0.5rem 0.75rem; margin-top: 0.25rem;">
    </section>

    <section style="margin-bottom: 1rem;">
        <label for="Via" style="color: #4B5563; font-weight: 600;">Via:</label>
        <input id="Via" name="Via" type="text" maxlength="45" required style="width: 100%; border: 1px solid #D1D5DB; border-radius: 0.25rem; padding: 0.5rem 0.75rem; margin-top: 0.25rem;">
    </section>

    <section style="margin-bottom: 1rem;">
        <label for="Latitudine" style="color: #4B5563; font-weight: 600;">Latitudine:</label>
        <input id="Latitudine" name="Latitudine" type="text" required style="width: 100%; border: 1px solid #D1D5DB; border-radius: 0.25rem; padding: 0.5rem 0.75rem; margin-top: 0.25rem;">
    </section>

    <section style="margin-bottom: 1rem;">
        <label for="Longitudine" style="color: #4B5563; font-weight: 600;">Longitudine:</label>
        <input id="Longitudine" name="Longitudine" type="text" required style="width: 100%; border: 1px solid #D1D5DB; border-radius: 0.25rem; padding: 0.5rem 0.75rem; margin-top: 0.25rem;">
    </section>

    <input type="hidden" name="controllerAction" value="ListaBasi.newBase">
    <input type="submit" value="Conferma" style="width: 100%; background-color: #3B82F6; color: #FFF; font-weight: 600; padding: 0.75rem 1rem; border-radius: 0.25rem; margin-top: 1rem; cursor: pointer;">
</form>
</div>

<form name="nuovaBaseAnnulla" action="Dispatcher" method="post" style="display: flex; justify-content: center;">

    <input type="hidden" name="controllerAction" value="ListaBasi.view"/>

    <input type="submit" value="Annulla" style="background-color: #9CA3AF; color: #fff; padding: 0.75rem 1rem; border-radius: 0.25rem; cursor: pointer; margin-top: 1rem; transition: background-color 0.2s ease-in-out; width: 10%;" onmouseover="this.style.backgroundColor='#6B7280'" onmouseout="this.style.backgroundColor='#9CA3AF'">
</form>
</body>
</html>
