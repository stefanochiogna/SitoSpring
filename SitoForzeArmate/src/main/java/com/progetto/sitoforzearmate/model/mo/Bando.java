package com.progetto.sitoforzearmate.model.mo;

import com.progetto.sitoforzearmate.model.dao.Data;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bando {
    /* Data */

    /* Attributi */
    private String Id;
    private String Oggetto;
    // private Integer NumPartecipanti;    // = dimensione lista utenti registrati
    private Integer MaxNumPartecipanti;
    private Path RiferimentoTesto;
    private String IdAdmin;
    private Data data = new Data(0,0,0);
    private Data dataScadenzaIscrizione = new Data(0,0,0);

    /* ATTRIBUTI RELAZIONI */
    private String Locazione;
    private List<String> Matricola = new ArrayList<>();

    // private List<String> Esito = new ArrayList<>();

    private HashMap<String, String> Esito = new HashMap<>();
    // TODO: Esito come coppia <String, String> = <Matricola, Esito>

    /* SET */
    public void setId(String Id){
        this.Id = Id;
    }
    public void setOggetto(String Oggetto){
        this.Oggetto = Oggetto;
    }
    public void setRiferimentoTesto(Path Riferimento){
        this.RiferimentoTesto = Riferimento;
    }
    public void setDataInt(Integer Anno, Integer Mese, Integer Giorno){
        data.setAnno(Anno);
        data.setMese(Mese);
        data.setGiorno(Giorno);
    }
    public void setData(Data data){
        data.setData(data);
    }
    public void setDataStringa(String data){
        String[] campi = data.split("-");

        this.data.setAnno(Integer.parseInt(campi[0]));
        this.data.setMese(Integer.parseInt(campi[1]));
        this.data.setGiorno(Integer.parseInt(campi[2]));
    }
    public void setDataScadenzaIscrizioneInt(Integer Anno, Integer Mese, Integer Giorno){
        dataScadenzaIscrizione.setAnno(Anno);
        dataScadenzaIscrizione.setMese(Mese);
        dataScadenzaIscrizione.setGiorno(Giorno);
    }
    public void setDataScadenzaIscrizioneStringa(String data){
        String[] campi = data.split("-");
        this.dataScadenzaIscrizione.setAnno(Integer.parseInt(campi[0]));
        this.dataScadenzaIscrizione.setMese(Integer.parseInt(campi[1]));
        this.dataScadenzaIscrizione.setGiorno(Integer.parseInt(campi[2]));
    }
    public void setDataScadenzaIscrizione(Data data){
        dataScadenzaIscrizione.setData(data);
    }
    public void setMaxNumPartecipanti(Integer maxNumPartecipanti){
        this.MaxNumPartecipanti = maxNumPartecipanti;
    }
    public void setLocazione(String Locazione){
        this.Locazione = Locazione;
    }
    public void setIdAdmin(String IdAdmin){
        this.IdAdmin = IdAdmin;
    }



    /* GET */
    public String getId(){
        return Id;
    }
    public String getOggetto(){
        return Oggetto;
    }
    public Integer getNumPartecipanti(){
        return Matricola.size();
    }
    public Integer getMaxNumPartecipanti(){
        return MaxNumPartecipanti;
    }
    public Data getData(){
        return data;
    }
    public String getDataSQL(Data data){
        String anno = data.getAnno().toString();
        while(anno.length() < 4) anno = "0" + anno;

        String mese = data.getMese().toString();
        while(mese.length() < 2) mese = "0" + mese;

        String giorno = data.getGiorno().toString();
        while(giorno.length() < 2) giorno = "0" + giorno;

        return anno + "-" + mese + "-" + giorno;
    }
    public Data getDataScadenzaIscrizione(){
        return dataScadenzaIscrizione;
    }
    public String getLocazione(){
        return Locazione;
    }
    public Path getRiferimentoTesto() {
        return RiferimentoTesto;
    }
    public String getIdAdmin(){
        return IdAdmin;
    }

    /* */
    public void AddEsito(String Esito, String Matricola){
        this.Esito.put(Matricola, Esito);
    }
    public void updateEsito(String Esito, String Matricola){
        this.Esito.replace(Matricola, Esito);
    }
    public String getEsito(String Matricola){
        return this.Esito.get(Matricola);
    }
    public Integer getEsitoLength(){
        return this.Esito.size();
    }
    public List<String> getEsitoKey(){
        List<String> listaMatri = new ArrayList<>();

        for(int i = 0; i < this.Esito.keySet().toArray().length; i++)
            listaMatri.add((String) this.Esito.keySet().toArray()[i]);

        return listaMatri;
    }

    private Integer getIndexMatricola(String Matricola){
        for(int i=0; i<this.Matricola.size(); i++){
            if(this.Matricola.get(i).equalsIgnoreCase(Matricola))   return i;
        }
        return -1;
    }

    /* SET e GET Matricola */
    public void AddMatricola(String Matricola){
        this.Matricola.add(Matricola);
    }
    public boolean trovaMatricola(String Matricola){
        for(int i=0; i<this.Matricola.size(); i++){
            if(this.Matricola.get(i).equalsIgnoreCase(Matricola))   return true;
        }
        return false;
    }
    public void deleteMatricola(String Matricola){
        this.Matricola.remove(Matricola);
    }

    public void updateTesto(String text){
        try {
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getRiferimentoTesto().toString()), StandardCharsets.UTF_8));
            br.write(text);
            br.close();
        }
        catch(IOException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
