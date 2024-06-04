<%@ page import="com.progetto.sitoforzearmate.model.mo.Notizie.Avviso" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@include file="../../../static/html_daIncludere/Header.inc"%>
    <%
        List<Avviso> avvisoList = new ArrayList<>();
        if(loggedOn) avvisoList.addAll((List<Avviso>) request.getAttribute("Avvisi"));

        List<UtenteRegistrato> listaUtenti = new ArrayList<>();
        if(loggedAdminOn) listaUtenti.addAll((List<UtenteRegistrato>) request.getAttribute("listaUtenti"));

        String menuActiveLink = "Avvisi";
    %>

    <style>

        div.divForm{
            background-color: #fff;
            padding: 1rem;
            border-radius: 0.5rem;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
        }
        div.divForm:hover{
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.16);
        }

        form.formAvviso{
            float: left;
            width: 80%;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        span.anteprima{
            width: 250px;
            border: none;
            color: darkgrey;
        }

        a.newsletter{
            grid-column: 1 / -1;
            cursor: pointer;
            background-color: #3182ce;
            color: honeydew;
            text-decoration: none;
            display: block;
            margin-bottom: 1rem;
            text-align: center;
            font-size: 15px;
        }

        /* Form amministratore */

    </style>
</head>
<body>

<!-- Navbar -->
<%@include file="../../../../../static/html_daIncludere/navbar.inc"%>
<main>
<a href="/viewBachecaNewsletter" class="newsletter">Newsletter</a>

<section class="pagina avvisi">
    <% if(loggedAdminOn){%>
    <div style="display: grid; grid-template-columns: 1fr; gap: 1rem; height: 80%">

        <form name="newAvviso" action="/inviaAvviso" method="post" style="width: 60%; height: 100%; margin: 0 auto; padding: 15px; background-color: #f3f4f6;">

            <div>
                <p style="font-weight: bold;">A:</p>

                <div style="display: flex;">
                    <label for="Tutti" style="display: flex; align-items: center; margin-right: 15px;">
                        <input type="radio" id="Tutti" name="Scelta" value="Tutti" onclick="toggleSection()">
                        <span>Tutti</span>
                    </label>

                    <label for="Ruolo" style="display: flex; align-items: center; margin-right: 15px;">
                        <input type="radio" id="Ruolo" name="Scelta" value="Ruolo" onclick="toggleRuoloSection()">
                        <span>Ruolo</span>
                    </label>

                    <label for="Utente" style="display: flex; align-items: center;">
                        <input type="radio" id="Utente" name="Scelta" value="Utente" onclick="toggleUtenteSection()">
                        <span>Utente</span>
                    </label>
                </div>

                <section id="ruoloSection" style="display: none; margin-top: 15px;">
                    <div style="margin-bottom: 5px;">
                        <label for="Ufficiale" style="display: block; margin: 0 5px;">
                            <input type="checkbox" id="Ufficiale" name="RuoloSelezionato" value="Ufficiale">
                            <span>Ufficiale</span>
                        </label>
                    </div>

                    <div style="margin-bottom: 5px;">
                        <label for="Sottufficiale" style="display: block; margin: 0 5px;">
                            <input type="checkbox" id="Sottufficiale" name="RuoloSelezionato" value="Sottufficiale">
                            <span>Sottufficiale</span>
                        </label>
                    </div>

                    <div>
                        <label for="Graduato" style="display: block; margin: 0 5px;">
                            <input type="checkbox" id="Graduato" name="RuoloSelezionato" value="Graduato">
                            <span>Graduato</span>
                        </label>
                    </div>
                </section>

                <section id="utenteSection" style="display: none; margin-top: 15px; flex-wrap: wrap">
                    <% for(int i=0; i<listaUtenti.size(); i++){ %>
                    <div style="margin-bottom: 5px;">
                        <label for="Matricola" style="display: block; margin: 0 5px;">
                            <input type="checkbox" id="Matricola" name="Matricola" value="<%=listaUtenti.get(i).getMatricola()%>">
                            <span><%=listaUtenti.get(i).getNome()%> <%=listaUtenti.get(i).getCognome()%></span>
                        </label>
                    </div>
                    <% } %>
                </section>
            </div>

            <label for="Oggetto" style="font-weight: bold;">Oggetto:</label>
            <input type="text" id="Oggetto" name="Oggetto" maxlength="45" style="width: 100%; border: 1px solid #ccc; padding: 5px; border-radius: 4px;">

            <label for="Testo" style="font-weight: bold;">Testo dell'avviso:</label>
            <textarea name="Testo" id="Testo" required style="width: 100%; border: 1px solid #ccc; padding: 5px; border-radius: 4px; resize: vertical; flex-grow: 1; max-height: 50%; height: 100%"></textarea>

            <input type="submit" value="Invia Nuovo Avviso" onclick="selezionato()" style="margin-top: 15px; padding: 8px 15px; background-color: #3490dc; color: #fff; border: none; border-radius: 4px; cursor: pointer;">
        </form>
    </div>
    <%}%>
    <div style="display: grid; grid-template-columns: 1fr; gap: 1rem;">
        <%for(int i=0; i<avvisoList.size(); i++){%>
        <div class="divForm">
            <form class="formAvviso" name="avvisoView<%=avvisoList.get(i).getID()%>" action="/viewAvviso" method="post">

                <input type="hidden" name="avvisoId" value="<%=avvisoList.get(i).getID()%>">
                <!-- type hidden: usato per inviare parametri con la form specificandone il nome -->

                <input type="submit" value="<%=avvisoList.get(i).getID()%>: <%=avvisoList.get(i).getOggetto()%>"
                       style="display: inline-block;background-color:#fff;  color: black; border: none; padding: 0.5rem 1rem; border-radius: 0.25rem; cursor: pointer; font-weight: bold;">

                    <%
                        try{
                            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(avvisoList.get(i).getRiferimentoTesto().toString()), "UTF-8"));
                            int CaratteriLetti = 0;
                            String line = br.readLine();
                            while((line != null) && (CaratteriLetti < 200)){%>
                                <span class="anteprima">
                                    <%=line%>
                                    <%
                                        CaratteriLetti += line.length();
                                        line = br.readLine();
                                    %>
                                </span>
                            <%}
                        }
                        catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    %>


            </form>
            <form name="avvisoDelete<%=avvisoList.get(i).getID()%>" action="/deleteAvviso" method="post" style="float:right">

                <input type="hidden" name="avvisoId" value="<%=avvisoList.get(i).getID()%>">

                <input type="submit" value="Elimina"
                       style="display: inline-block; background-color: #e53e3e; color: #fff; padding: 0.5rem 1rem; border-radius: 0.25rem; cursor: pointer; font-weight: bold;">
            </form>
        </div>
        <%}%>
    </div>
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
            ruoloSection.style.display = "flex";
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
            utenteSection.style.display = "flex";
            ruoloSection.style.display = "none";

            UfficialeCheck.checked = false;
            SottufficialeCheck.checked = false;
            Graduato.checked = false;
        } else {
            utenteSection.style.display = "none";
        }
    }

</script>
</main>
<%@include file="../../../../../static/html_daIncludere/footer.inc"%>
</body>
</html>
