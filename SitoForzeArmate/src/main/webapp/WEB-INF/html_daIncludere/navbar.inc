<header>

    <nav>
        <div class="topnav" id="myTopnav">
            <a href="/homepage" id="Home" <%=menuActiveLink.equals("Home")?"class=\"active\"":""%>>Home</a>
            <a href="/viewCalendario" id="Calendario" <%=menuActiveLink.equals("Calendario")?"class=\"active\"":""%>>Calendario Concorsi e Bandi</a>
            <%if (loggedOn || loggedAdminOn) {%>
                <a href="/viewBachecaAvviso" id="Bacheca" <%=menuActiveLink.equals("Avvisi")?"class=\"active\"":""%>>Bacheca Avvisi</a>
                <a href="/viewListaBasi" id="ListaBasi" <%=menuActiveLink.equals("Basi")?"class=\"active\"":""%>>Basi Militari Attive</a>
                <a href="/viewProfilo" id="Profilo" <%=menuActiveLink.equals("Profilo")?"class=\"active\"":""%>>Profilo</a>
                <a class="login" href="/logout" id="logout-button"><span class="glyphicon glyphicon-log-out"></span> Log out</a>
            <%}%>
            <%if (!loggedOn && !loggedAdminOn) {%>
               <a class="login" href="/viewLogin" id="Login"><span class="glyphicon glyphicon-log-in"></span> Log in</a>
            <%}%>
        </div>
    </nav>
</header>
