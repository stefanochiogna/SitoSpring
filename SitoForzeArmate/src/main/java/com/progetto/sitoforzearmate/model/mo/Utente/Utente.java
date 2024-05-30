package com.progetto.sitoforzearmate.model.mo.Utente;

import com.example.sitoforzaarmata.model.dao.Data;

public class Utente {
    /* Variabili */
    private String Nome;
    private String Cognome;
    private String CF;
    private String mail;
    private String telefono;
    private String password;
    private Data dataNascita = new Data(0,0,0); 
    private String sesso;

    /* Metodi */
    public String getNome(){
        return Nome;
    }
    public void setNome(String Nome){
        this.Nome = Nome;
    }
    public String getCognome(){
        return Cognome;
    }
    public void setCognome(String Cognome){
        this.Cognome = Cognome;
    }
    public String getCF(){
        return CF;
    }
    public void setCF(String CF){
        this.CF = CF;
    }
    public String getMail(){
        return mail;
    }
    public void setMail(String mail){
        this.mail = mail;
    }
    public String getTelefono(){
        return telefono;
    }
    public void setTelefono(String telefono){
        this.telefono = telefono;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public Data getDataNascita() {
        return dataNascita;
    }
    public String getDataNascitaString(){
        return dataNascita.toStringSQL();
    }
    public void setDataNascitaString(String dataNascita){
        String[] campi = dataNascita.split("-");

        this.dataNascita.setAnno(Integer.parseInt(campi[0]));
        this.dataNascita.setMese(Integer.parseInt(campi[1]));
        this.dataNascita.setGiorno(Integer.parseInt(campi[2]));
    }
    public void setDataNascita(Data dataNascita) {
        this.dataNascita = dataNascita;
    }
    public String getSesso() {
        return sesso;
    }
    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

}
