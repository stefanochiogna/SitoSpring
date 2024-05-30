package com.progetto.sitoforzearmate.model.mo.Utente;


import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UtenteRegistrato extends Utente{

    /* Attributi */
    private String Matricola;
    private String Ruolo;
    private Long Stipendio;
    private String IBAN;
    private byte[] Foto;
    private byte[] Documento;
    private String Indirizzo;
    private String Locazione_servizio;

    /* Relazione */
    boolean IscrittoNewsletter;
    private UtenteRegistrato[] contatto;
    private List<String> idBandi = new ArrayList<>();
    private List<String> idAvvisi = new ArrayList<>();

    /* Metodi */
    public void setMatricola(String Matricola){
        this.Matricola = Matricola;
    }
    public void setRuolo(String Ruolo){
        this.Ruolo = Ruolo;
    }
    public void setStipendio(Long Stipendio){
        this.Stipendio = Stipendio;
    }
    public void setIBAN(String IBAN){
        this.IBAN = IBAN;
    }
    public void setIndirizzo(String Indirizzo){
        this.Indirizzo = Indirizzo;
    }
    public void setLocazioneServizio(String Locazione_servizio) {
        this.Locazione_servizio = Locazione_servizio;
        }
    public String getMatricola(){
        return Matricola;
    }
    public String getRuolo(){
        return Ruolo;
    }
    public Long getStipendio(){
        return Stipendio;
    }
    public String getIBAN(){
        return IBAN;
    }
    public String getIndirizzo(){
        return Indirizzo;
    }
    public String getLocazioneServizio() {
        return Locazione_servizio;
    }

    /* Foto / documenti */
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

    public ByteArrayInputStream getDocumento(){
        return new ByteArrayInputStream(Documento);
    }
    public byte[] getDocumentoByte(){
        return Documento;
    }
    public long getDocumentoDim(){
        return Documento.length;
    }
    public void setDocumento(Blob Documento){
        try {
            this.Documento = Documento.getBytes(1, (int) Documento.length());
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    public void setDocumentoByte(byte[] Documento){
        this.Documento = Documento;
    }

    /* Gestione Contatti */
    public UtenteRegistrato[] getContatti(){
        return contatto;
    }
    public void setContatti(UtenteRegistrato[] contatto){
        this.contatto = contatto;
    }
    public UtenteRegistrato getContatto(int indice){
        return this.contatto[indice];
    }
    public void setContatto(UtenteRegistrato contatto, int indice){
        this.contatto[indice] = contatto;
    }

    /* Bandi */
    public void AddIdBando(String idBandi){
        this.idBandi.add(idBandi);
    }
    public void AddAllBando(List<String> idBandi){
        this.idBandi.addAll(idBandi);
    }
    public boolean trovaBando(String idBandi){
        for(int i=0; i<this.idBandi.size(); i++){
            if(this.idBandi.get(i).equals(idBandi))   return true;
        }
        return false;
    }
    public Integer getLunghezzaBando(){
        return this.idBandi.size();
    }
    public String getElementoBando(Integer i){
        return this.idBandi.get(i);
    }
    public void stampaBandi(){
        for(int i=0; i<this.idBandi.size(); i++)    System.out.println(idBandi.get(i));
    }
    public void deleteIdBando(String idBandi){
        this.idBandi.remove(idBandi);
    }

    /* Avvisi / Newsletter */
    public void AddIdAvviso(String idAvvisi){
        this.idAvvisi.add(idAvvisi);
    }
    public void AddAllAvviso(List<String> idAvvisi){
        this.idAvvisi.addAll(idAvvisi);
    }
    public boolean trovaAvviso(String idAvvisi){
        for(int i=0; i<this.idAvvisi.size(); i++){
            System.out.println(this.idAvvisi.get(i) + " " + idAvvisi);
            if(this.idAvvisi.get(i).equals(idAvvisi))   return true;
        }
        return false;
    }
    public Integer getLunghezzaAvviso(){
        return this.idAvvisi.size();
    }
    public String getElementoAvviso(Integer i){
        return this.idAvvisi.get(i);
    }
    public void stampaAvvisi(){
        for(int i=0; i<this.idAvvisi.size(); i++)    System.out.println(idAvvisi.get(i));
    }
    public void deleteIdAvviso(String idAvvisi){
        this.idAvvisi.remove(idAvvisi);
    }
    public boolean getIscrittoNewsletter() {
        return this.IscrittoNewsletter;
    }
    public Integer getIscrittoNewsletterInt() {
        if(this.IscrittoNewsletter) return 1;
        else return 0;
    }
    public void setIscrittoNewsletter(boolean iscrittoNewsletter) {
        this.IscrittoNewsletter = iscrittoNewsletter;
    }
    public void setIscrittoNewsletterInt(Integer IscrittoNewsletter) {
        if(IscrittoNewsletter == 1) this.IscrittoNewsletter = true;
        else this.IscrittoNewsletter = false;
    }
}
