<%@ page import="com.example.sitoforzaarmata.model.mo.Notizie.Newsletter" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<Newsletter> newsletterList = new ArrayList<>();
    newsletterList.addAll((List<Newsletter>) request.getAttribute("Newsletter"));

    String menuActiveLink = "Avvisi";
%>

<html>
<head>
    <%@include file="/html_daIncludere/Header.inc"%>

    <style>
        div.divForm{
            background-color: #fff;
            padding: 1rem;
            border-radius: 0.5rem;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
        }

        div.divForm:hover{
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.16)
        }

        form.formNewsletter{
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

        a.Avvisi{
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
    </style>

</head>
<body>
<!-- Navbar -->
<%@include file="../../html_daIncludere/navbar.inc"%>
<main>
<a href="Dispatcher?controllerAction=BachecaAvviso.view" class="Avvisi"> Avvisi </a>

<section class="pagina newsletter">

    <% if(loggedAdminOn){%>
    <div style="display: grid; grid-template-columns: 1fr; gap: 1rem; height: 80%">
        <form name="newNewsletter" action="Dispatcher" method="post" style="width: 60%; height: 100%; margin: 0 auto; padding: 15px; background-color: #f3f4f6;">

            <label for="Oggetto" style="font-weight: bold;">Oggetto:</label>
            <input type="text" id="Oggetto" name="Oggetto" maxlength="45" style="width: 100%; border: 1px solid #ccc; padding: 5px; border-radius: 4px;">

            <label for="Testo" style="font-weight: bold;">Testo dell'avviso:</label>
            <textarea name="Testo" id="Testo" required style="width: 100%; border: 1px solid #ccc; padding: 5px; border-radius: 4px; resize: vertical; flex-grow: 1; max-height: 50%; height: 100%"></textarea>

            <input type="hidden" name="controllerAction" value="BachecaNewsletter.inviaNewsletter"/>

            <input type="submit" value="Invia Nuova Newsletter" style="margin-top: 15px; padding: 8px 15px; background-color: #3490dc; color: #fff; border: none; border-radius: 4px; cursor: pointer;">
        </form>
    </div>
    <%}%>
    <div style="display: grid; grid-template-columns: 1fr; gap: 1rem;">
        <%for(int i=0; i<newsletterList.size(); i++){%>
        <div class="divForm">
            <form class="formNewsletter" name="newsletterView<%=newsletterList.get(i).getID()%>" action="Dispatcher" method="post">

                <input type="hidden" name="newsletterId" value="<%=newsletterList.get(i).getID()%>">

                <input type="hidden" name="controllerAction" value="BachecaNewsletter.viewNewsletter"/>

                <input type="submit" value="<%=newsletterList.get(i).getID()%>: <%=newsletterList.get(i).getOggetto()%>"
                       style="display: inline-block;background-color:#fff;  color: black; border: none; padding: 0.5rem 1rem; border-radius: 0.25rem; cursor: pointer; font-weight: bold;">

                <%
                    try{
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(newsletterList.get(i).getRiferimentoTesto().toString()), "UTF-8"));
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

            <form name="newsletterDelete<%=newsletterList.get(i).getID()%>" action="Dispatcher" method="post" style="float:right">

                <input type="hidden" name="newsletterId" value="<%=newsletterList.get(i).getID()%>">

                <input type="hidden" name="controllerAction" value="BachecaNewsletter.deleteNewsletter"/>

                <input type="submit" value="Elimina"
                       style="display: inline-block; background-color: #e53e3e; color: #fff; padding: 0.5rem 1rem; border-radius: 0.25rem; cursor: pointer; font-weight: bold;">

            </form>

        </div>
        <%}%>
    </div>
</section>
</main>
<%@include file="../../html_daIncludere/footer.inc"%>
</html>
