<%@ page import="com.example.sitoforzaarmata.model.mo.Notizie.Avviso" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@include file="/html_daIncludere/Header.inc"%>
    <%
        List<Avviso> avvisoList = new ArrayList<>();
        if(loggedOn) avvisoList.addAll((List<Avviso>) request.getAttribute("Avvisi"));

        List<UtenteRegistrato> listaUtenti = new ArrayList<>();
        if(loggedAdminOn) listaUtenti.addAll((List<UtenteRegistrato>) request.getAttribute("listaUtenti"));

        String menuActiveLink = "Avvisi";
    %>
</head>
<body>
    <!-- Navbar -->
    <%@include file="../../html_daIncludere/navbar.inc"%>

    <section class="pagina avvisi">
        <ul>
            <a href="Dispatcher?controllerAction=BachecaNewsletter.view"> Newsletter </a>

            <% if(loggedAdminOn){%>

            <form name="newAvviso" action="Dispatcher" method="post">

                <section style="padding: 15px; float: right">
                    <p>A: </p>

                    <label for="Tutti"> Tutti: </label>
                    <input type="radio" id="Tutti" name="Scelta" value="Tutti" onclick="toggleSection()">
                    <br>

                    <label for="Ruolo"> Ruolo: </label>
                    <input type="radio" id="Ruolo" name="Scelta" value="Ruolo" onclick="toggleRuoloSection()">
                    <section id="ruoloSection" style="display: none">
                        <label for="Ufficiale"> Ufficiale: </label>
                        <input type="checkbox" id="Ufficiale" name="RuoloSelezionato" value="Ufficiale"><br>

                        <label for="Sottufficiale"> Sottufficiale: </label>
                        <input type="checkbox" id="Sottufficiale" name="RuoloSelezionato" value="Sottufficiale"><br>

                        <label for="Graduato"> Graduato: </label>
                        <input type="checkbox" id="Graduato" name="RuoloSelezionato" value="Graduato"><br>
                    </section>

                    <label for="Utente"> Utente: </label>
                    <input type="radio" id="Utente" name="Scelta" value="Utente" onclick="toggleUtenteSection()">
                    <section id="utenteSection" style="display: none">
                        <%for(int i=0; i<listaUtenti.size(); i++){%>
                            <label for="Matricola"> <%=listaUtenti.get(i).getNome()%> <%=listaUtenti.get(i).getCognome()%>: </label>
                            <input type="checkbox" id="Matricola" name="Matricola" value="<%=listaUtenti.get(i).getMatricola()%>"><br>
                        <%}%>
                    </section>

                </section>

                <label for="Oggetto"> Oggetto: </label>
                <input type="text" id="Oggetto" name="Oggetto" maxlength="45">

                <label for="Testo"> Testo dell'avviso: </label>
                <textarea name="Testo" id="Testo" required></textarea>

                <input type="hidden" name="controllerAction" value="BachecaAvviso.inviaAvviso"/>

                <input type="submit" value="Invia Nuovo Avviso" onclick="selezionato()">
            </form>
            <%}%>

            <%for(int i=0; i<avvisoList.size(); i++){%>
            <li>
                <form name="avvisoView<%=avvisoList.get(i).getID()%>" action="Dispatcher" method="post">
                    <!--
                        name: permette di identificare il form in maniera univoca
                        action: url dello user agent a cui viene sottomessa la form
                        method: metodo http con cui vengono passati i parametri
                    -->

                    <input type="hidden" name="avvisoId" value="<%=avvisoList.get(i).getID()%>">
                    <!-- type hidden: usato per inviare parametri con la form specificandone il nome -->

                    <input type="hidden" name="controllerAction" value="BachecaAvviso.viewAvviso"/>

                    <input type="submit" value="<%=avvisoList.get(i).getID()%>: <%=avvisoList.get(i).getOggetto()%>">
                </form>

                <form name="avvisoDelete<%=avvisoList.get(i).getID()%>" action="Dispatcher" method="post">

                    <input type="hidden" name="avvisoId" value="<%=avvisoList.get(i).getID()%>">

                    <input type="hidden" name="controllerAction" value="BachecaAvviso.deleteAvviso"/>

                    <input type="submit" value="Elimina" >

                </form>

            </li>
            <%}%>
        </ul>
    </section>

    <script>

        function selezionato() {
            const tuttiChecked = document.getElementById('Tutti').checked;
            const ruoloChecked = document.getElementById('Ruolo').checked;
            const utenteChecked = document.getElementById('Utente').checked;

            if (!(tuttiChecked || ruoloChecked || utenteChecked)) {
                alert('Seleziona almeno un\'opzione prima di inviare l\'avviso.');
            }
        }

        function toggleSection(){
            var TuttiCheck = document.getElementById("Tutti");
            var ruoloSection = document.getElementById("ruoloSection");
            var utenteSection = document.getElementById("utenteSection");

            var UfficialeCheck = document.getElementById("Ufficiale");
            var SottufficialeCheck = document.getElementById("Sottufficiale");
            var Graduato = document.getElementById("Graduato");

            var Matricola = document.getElementById("Matricola");

            if (TuttiCheck.checked) {
                ruoloSection.style.display = "none";
                utenteSection.style.display = "none";

                UfficialeCheck.checked = false;
                SottufficialeCheck.checked = false;
                Graduato.checked = false;

                Matricola.checked = false;
            }
        }

        function toggleRuoloSection() {
            var ruoloCheckbox = document.getElementById("Ruolo");
            var ruoloSection = document.getElementById("ruoloSection");
            var utenteSection = document.getElementById("utenteSection");

            var Matricola = document.getElementById("Matricola");

            if (ruoloCheckbox.checked) {
                ruoloSection.style.display = "block";
                utenteSection.style.display = "none";

                Matricola.checked = false;
            } else {
                ruoloSection.style.display = "none";
            }
        }

        function toggleUtenteSection() {
            var utenteCheckbox = document.getElementById("Utente");
            var utenteSection = document.getElementById("utenteSection");
            var ruoloSection = document.getElementById("ruoloSection");

            var UfficialeCheck = document.getElementById("Ufficiale");
            var SottufficialeCheck = document.getElementById("Sottufficiale");
            var Graduato = document.getElementById("Graduato");

            if (utenteCheckbox.checked) {
                utenteSection.style.display = "block";
                ruoloSection.style.display = "none";

                UfficialeCheck.checked = false;
                SottufficialeCheck.checked = false;
                Graduato.checked = false;
            } else {
                utenteSection.style.display = "none";
            }
        }

    </script>


</html>
