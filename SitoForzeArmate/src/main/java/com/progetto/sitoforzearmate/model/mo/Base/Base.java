package com.progetto.sitoforzearmate.model.mo.Base;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class Base {

    /* Attributi */
    private String Locazione;
    private String mail;
    private String telefono;
    private String Provincia;
    private String CAP;
    private String via;
    private String IdAdministrator;
    private byte[] Foto;
    private double latitudine;
    private double longitudine;

    /* Metodi */
    public void setMail(String mail){
        this.mail = mail;
    }
    public void setTelefono(String telefono){
        this.telefono = telefono;
    }
    public void setIdAdministrator(String IdAdministrator){
        this.IdAdministrator = IdAdministrator;
    }
    public String getMail(){
        return mail;
    }
    public String getTelefono(){
        return telefono;
    }
    public String getIdAdministrator(){
        return IdAdministrator;
    }
    public void setLocazione(String locazione){
        this.Locazione = locazione;
    }
    public void setProvincia(String provincia){
        this.Provincia = provincia;
    }
    public void setCAP(String CAP){
        this.CAP = CAP;
    }
    public void setVia(String Via){
        this.via = Via;
    }
    public String getLocazione(){
        return Locazione;
    }
    public String getProvincia(){
        return Provincia;
    }
    public String getCAP(){
        return CAP;
    }
    public String getVia(){
        return via;
    }
    public void setLatitudine(double latitudine){
        this.latitudine = latitudine;
    }
    public void setLongitudine(double longitudine){
        this.longitudine = longitudine;
    }
    public double getLatitudine(){
        return latitudine;
    }
    public double getLongitudine(){
        return longitudine;
    }

    /* Gestione foto */
    public ByteArrayInputStream getFoto(){
        return new ByteArrayInputStream(Foto);
    }
    public byte[] getFotoByte(){
        return Foto;
    }
    public long getFotoDim(){
        return Foto.length;
    }
    public void setFoto(Blob Foto){
        try {
            this.Foto = Foto.getBytes(1, (int) Foto.length());
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    public void setFotoByte(byte[] Foto){
        this.Foto = Foto;
    }
}
