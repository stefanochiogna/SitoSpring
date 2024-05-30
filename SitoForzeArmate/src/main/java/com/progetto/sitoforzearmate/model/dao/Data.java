package com.progetto.sitoforzearmate.model.dao;

import java.time.LocalDate;

public class Data{
    private Integer Anno;
    private Integer Mese;
    private Integer Giorno;

    public Data(Integer Anno, Integer Mese, Integer Giorno){
        this.Anno = Anno;
        this.Mese = Mese;
        this.Giorno = Giorno;
    }
    public Data(String data){
        String[] campi = data.split("-");

        this.Anno = Integer.parseInt(campi[0]);
        this.Mese = Integer.parseInt(campi[1]);
        this.Giorno = Integer.parseInt(campi[2]);
    }

    public void setData(Data data){
        this.Anno = data.getAnno();
        this.Mese = data.getMese();
        this.Giorno = data.getGiorno();
    }
    public void setAnno(Integer anno) {
        this.Anno = anno;
    }
    public void setMese(Integer mese) {
        this.Mese = mese;
    }
    public void setGiorno(Integer giorno) {
        this.Giorno = giorno;
    }
    public Data getData(Integer Anno, Integer Mese, Integer Giorno){
        return new Data(Anno, Mese, Giorno);
    }
    public Integer getAnno(){
        return Anno;
    }
    public Integer getMese(){
        return Mese;
    }
    public Integer getGiorno(){
        return Giorno;
    }
    public String toStringSQL(){
        String Anno = this.Anno.toString();
        String Mese = this.Mese.toString();
        String Giorno = this.Giorno.toString();
        while(Anno.length() < 4 ) Anno = "0" + Anno;
        while(Mese.length() < 2 ) Mese = "0" + Mese;
        while(Giorno.length() < 2 ) Giorno = "0" + Giorno;
        return Anno + "-" + Mese + "-" + Giorno;
    }

    public static void incrementaGiorno(Data data){
        LocalDate date = LocalDate.of(data.getAnno(), data.getMese(), data.getGiorno());
        date = date.plusDays(1);

        data.setAnno(date.getYear());
        data.setMese(date.getMonthValue());
        data.setGiorno(date.getDayOfMonth());
    }
}