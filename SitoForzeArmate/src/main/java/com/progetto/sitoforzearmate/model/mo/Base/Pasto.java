package com.progetto.sitoforzearmate.model.mo.Base;

import com.progetto.sitoforzearmate.model.dao.Data;

public class Pasto {

    /* Attributi */
    private String Id;
    private String Matricola;
    private String Locazione;
    private String Turno;
    private Data data_prenotazione = new Data(0,0,0);
    
    /* Metodi */
    public void setId(String id) { this.Id = id; }
    public void setMatricola(String matricola) { this.Matricola=matricola; }
    public void setLocazione(String locazione) { this.Locazione=locazione; }
    public void setTurno (String turno) { this.Turno=turno; }
    public void setData_prenotazione (Data data) { this.data_prenotazione=data; }
    public void setData_prenotazioneString(String data){
        String[] campi = data.split("-");
        this.data_prenotazione.setAnno(Integer.parseInt(campi[0]));
        this.data_prenotazione.setMese(Integer.parseInt(campi[1]));
        this.data_prenotazione.setGiorno(Integer.parseInt(campi[2]));
    }
    public String getMatricola() { return Matricola; }
    public String getLocazione() { return Locazione; }
    public String getTurno() { return Turno; }
    public Data getData_prenotazione() { return data_prenotazione; }

    public String getId(){
        return Turno + data_prenotazione.toStringSQL();
    }
}
