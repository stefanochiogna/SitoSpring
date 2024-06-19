package com.progetto.sitoforzearmate.services.configuration;

import java.io.File;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.logging.Level;

import com.progetto.sitoforzearmate.model.dao.DAOFactory;

public class Configuration {

    /* Database Configruation */
    public static final String DAO_IMPL=DAOFactory.MYSQLJDBCIMPL;
    public static String getDIRECTORY_FILE(){
        String relativePath = ".." + File.separator +"raccolta_file"+ File.separator;
        String fullPath = Paths.get(relativePath).toAbsolutePath().toString() + File.separator;
        return fullPath;
    }
    public static final String DATABASE_DRIVER="com.mysql.cj.jdbc.Driver";
    public static final String SERVER_TIMEZONE=Calendar.getInstance().getTimeZone().getID();
    public static final String
            DATABASE_URL="jdbc:mysql://localhost/forze_armate?user=root&password=DataBase1203!&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone="+SERVER_TIMEZONE;
            // DATABASE_URL = "jdbc:mysql://" + System.getenv("DB_HOST") + [":" + System.getenv("DB_PORT")] "/" + System.getenv("DB_NAME") + "?user=" + System.getenv("DB_USER") +
            //                  + "&password=" + System.getenv("DB_PASS") + "..."
            // DataBase1203!
    /* Session Configuration */
    public static final String COOKIE_IMPL=DAOFactory.COOKIEIMPL;

    /* Logger Configuration */
    public static final String GLOBAL_LOGGER_NAME="sitoforzaarmata";
    public static final String GLOBAL_LOGGER_FILE="/Users/stefa/Desktop/Sito_SistemiWeb/Log/forzaarmata_log.%g.%u.txt";
    public static final Level GLOBAL_LOGGER_LEVEL=Level.ALL;

}
