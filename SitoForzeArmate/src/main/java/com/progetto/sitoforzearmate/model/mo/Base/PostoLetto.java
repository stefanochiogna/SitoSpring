package com.progetto.sitoforzearmate.model.mo.Base;

import com.example.sitoforzaarmata.model.dao.Data;

public class PostoLetto {
    private String Matricola;
    private String Locazione;
    private String Id;
    private Data data_arrivo = new Data(0,0,0);
    private Integer num_persone;
    private Integer num_notti;

    /* SET */
    public void setMatricola(String matricola) { this.Matricola = matricola; }
    public void setLocazione(String locazione) { this.Locazione = locazione; }
    public void setData_arrivo(Data data) { this.data_arrivo = data; }
    public void setData_arrivoString(String data){
        String[] campi = data.split("-");
        this.data_arrivo.setAnno(Integer.parseInt(campi[0]));
        this.data_arrivo.setMese(Integer.parseInt(campi[1]));
        this.data_arrivo.setGiorno(Integer.parseInt(campi[2]));
    }
    public void setNum_persone(Integer persone) { this.num_persone = persone; }
    public void setNum_notti(Integer notti) { this.num_notti = notti; }
    public void setId(String Id){
        this.Id = Id;
    }

    /* GET */
    public String getMatricola() { return Matricola; }
    public String getLocazione() { return Locazione; }
    public Data getData_arrivo() { return data_arrivo; }
    public Integer getNum_persone() { return num_persone; }
    public Integer getNum_notti() { return  num_notti; }
    public String getId(){
        return Matricola + data_arrivo.toStringSQL();
    }
    public String getIdSet(){return this.Id; }
}
