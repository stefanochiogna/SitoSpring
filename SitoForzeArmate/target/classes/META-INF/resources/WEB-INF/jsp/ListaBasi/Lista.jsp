<%@ page import="com.progetto.sitoforzearmate.model.mo.Base.Base" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<Base> baseList = new ArrayList<>();
    baseList.addAll((List<Base>) request.getAttribute("Basi"));

    String menuActiveLink = "Basi";
%>

<html>
<head>
    <%@include file="../../../static/html_daIncludere/Header.inc"%>
    <style>
        #cercaBase {
            width: 100%; /* Full-width */
            font-size: 16px; /* Increase font-size */
            padding: 12px 20px 12px 40px; /* Add some padding */
            border: 1px solid #ddd; /* Add a grey border */
            margin-bottom: 12px; /* Add some space below the input */
        }

        .container{
            position: relative;
            display:flex;
            width:100%;
        }
        .paginaAvvisi{
            width: 66%;
        }
        .paginaMappa{
            width: 34%;
            height: 500px
        }
    </style>

    <script>
        function Ricerca() {
            var input, filter, ul, li, p, i, txtValue;
            input = document.getElementById('cercaBase');
            filter = input.value.toUpperCase();
            ul = document.getElementById("ListaBasi");
            li = ul.getElementsByTagName('li');

            // viene effettuato un ciclo su tutti gli elementi nel tag <li>
            // e nascosti quelli che non corrispondono alla ricerca
            for (i = 0; i < li.length; i++) {
                p = li[i].getElementsByTagName("p")[0];
                    /*
                        Element.getElementsByTagName restituisce una collezione di elementi
                        facendoo getElementsByTagName[0] si va a prendere solo il primo
                     */
                txtValue = p.textContent || p.innerText;
                    /*
                        textContent: restituisce l'intero testo contenuto nel tag p
                        innterText: restituisce il testo visibile
                     */

                if (txtValue.toUpperCase().indexOf(filter) > -1) {
                    li[i].style.display = "";
                    // se l'elemento cercato è presente nel testo del tag p
                    //      allora non si modifica lo style
                } else {
                    li[i].style.display = "none";
                    // se invece non è presente non si mostra l'elemento li[i]
                }
            }
        }
    </script>

</head>
<body>
<!-- Navbar -->
<%@include file="../../../static/html_daIncludere/navbar.inc"%>
<main>
    <% if(loggedAdminOn){%>
        <a href="/registraBase"> Registra Nuova Base </a>
    <%}%>

    <div class="container">
        <section class="paginaAvvisi">
            <input type="text" id="cercaBase" onkeyup="Ricerca()" placeholder="Search for names..">

            <ul id="ListaBasi">
                <%for(int i=0; i<baseList.size(); i++){%>
                <li>
                    <p> Base militare di <%=baseList.get(i).getLocazione()%> </p>
                    <form name="baseView<%=baseList.get(i).getLocazione()%>" action="/viewBase" method="post">

                        <input type="hidden" name="luogoBase" value="<%=baseList.get(i).getLocazione()%>">

                        <input type="submit" value="Seleziona">
                    </form>

                    <% if(loggedAdminOn){%>
                    <form name="baseDelete<%=baseList.get(i).getLocazione()%>" action="/deleteBase" method="post">

                        <input type="hidden" name="luogoBase" value="<%=baseList.get(i).getLocazione()%>">

                        <input type="submit" value="Elimina">

                    </form>
                    <%}%>

                </li>
                <%}%>
            </ul>
        </section>
        <section class="paginaMappa">
            <jsp:include page="Mappa.jsp" />
        </section>
    </div>
</main>
</body>
<%@include file="../../../static/html_daIncludere/footer.inc"%>
</html>
