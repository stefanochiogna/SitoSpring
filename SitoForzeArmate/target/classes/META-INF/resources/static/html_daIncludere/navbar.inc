<header>

    <nav>
        <div class="topnav" id="myTopnav">
            <a href="Dispatcher?controllerAction=PaginaIniziale.view" id="Home" <%=menuActiveLink.equals("Home")?"class=\"active\"":""%>>Home</a>
            <a href="Dispatcher?controllerAction=Calendario.view" id="Calendario" <%=menuActiveLink.equals("Calendario")?"class=\"active\"":""%>>Calendario Concorsi e Bandi</a>
            <%if (loggedOn || loggedAdminOn) {%>
                <a href="Dispatcher?controllerAction=BachecaAvviso.view" id="Bacheca" <%=menuActiveLink.equals("Avvisi")?"class=\"active\"":""%>>Bacheca Avvisi</a>
                <a href="Dispatcher?controllerAction=ListaBasi.view" <%=menuActiveLink.equals("Basi")?"class=\"active\"":""%>>Basi Militari Attive</a>
                <a href="Dispatcher?controllerAction=Profilo.view" <%=menuActiveLink.equals("Profilo")?"class=\"active\"":""%>>Profilo</a>
                <a class="login" href="Dispatcher?controllerAction=Login.logout"><span class="glyphicon glyphicon-log-out"></span> Log out</a>
            <%}%>
            <%if (!loggedOn && !loggedAdminOn) {%>
               <a class="login" href="Dispatcher?controllerAction=Login.view"><span class="glyphicon glyphicon-log-in"></span> Log in</a>
            <%}%>
        </div>
    </nav>

    <!--
    <nav class="navbar">
        <a href="Dispatcher?controllerAction=PaginaIniziale.view" class="Home" <%=menuActiveLink.equals("Home")?"class=\"active\"":""%>>Home</a>
        <input class="menu-btn" id="menu-btn" type="checkbox">
        <label for="menu-btn" class="menu-icon">
            <span class="nav-icon"></span>
        </label>
        <ul class="menu">
            <li>
                <a href="Dispatcher?controllerAction=Calendario.view" id="Calendario" <%=menuActiveLink.equals("Calendario")?"class=\"active\"":""%>>Calendario</a>
                <ul>
                    <li>
                        <a href="Dispatcher?controllerAction=Calendario.view"> Bandi Attivi </a>
                    </li>
                    <li>
                        <a href="#"> Bandi scaduti </a>
                    </li>
                </ul>
            </li>

            <%if(loggedOn){%>
            <li>
                <a href="Dispatcher?controllerAction=BachecaAvviso.view" id="Bacheca" <%=menuActiveLink.equals("Avvisi")?"class=\"active\"":""%>>Bacheca</a>
                <ul>
                    <li>
                        <a href="#"> Avvisi </a>
                    </li>
                    <li>
                        <a href="#"> Newsletter </a>
                    </li>
                </ul>
            </li>

            <li>
                <a href="Dispatcher?controllerAction=ListaBasi.view" <%=menuActiveLink.equals("Basi")?"class=\"active\"":""%>>Basi Militari Attive</a>
            </li>

            <%}%>
            <li class="login">
                <a class="login" href="#"><span class="glyphicon glyphicon-user"></span></a>
                <ul>
                    <%if(!loggedOn){%>
                        <li>
                            <a href="Dispatcher?controllerAction=Login.view">Login</a>
                        </li>
                        <li>
                            <a href="Dispatcher?controllerAction=Login.viewRegistrazione">Registrati</a>
                        </li>
                    <%}%>

                    <%if(loggedOn){%>
                    <li>
                        <a href="Dispatcher?controllerAction=Profilo.view">Profilo</a>
                    </li>
                    <li>
                        <a href="Dispatcher?controllerAction=Login.logout">Log out</a>
                    </li>
                    <%}%>
                </ul>
            </li>

        </ul>
    </nav>
 -->
</header>
