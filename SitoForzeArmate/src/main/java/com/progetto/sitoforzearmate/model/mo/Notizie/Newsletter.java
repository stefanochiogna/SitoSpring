package com.progetto.sitoforzearmate.model.mo.Notizie;

import java.util.ArrayList;
import java.util.List;

public class Newsletter extends Notizie{
    private List<String> MailDestinatario = new ArrayList<>();
    private List<String> MatricolaDestinatario = new ArrayList<>();

    /* Matricola */
    public List<String> getMailDestinatario(){
        return MailDestinatario;
    }
    public void addMailDestinatario(String MailDestinatario){
        this.MailDestinatario.add(MailDestinatario);
    }
    public void removeMailDestinatario(String MailDestinatario){
        this.MailDestinatario.remove(MailDestinatario);
    }

    /* public String getDestinatarioByIndex(int indice){
        return MailDestinatario.get(indice);
    } */
    public Integer getMailDestinatariLen(){
        return MailDestinatario.size();
    }

    public List<String> getMatricolaDestinatario(){
        return MatricolaDestinatario;
    }
    public void addMatricolaDestinatario(String MatricolaDestinatario){
        this.MatricolaDestinatario.add(MatricolaDestinatario);
    }
    public void removeMatricolaDestinatario(String MatricolaDestinatario){
        this.MatricolaDestinatario.remove(MatricolaDestinatario);
    }
    public String getDestinatarioByIndex(int indice){
        return MatricolaDestinatario.get(indice);
    }
    public Integer getDestinatariLen(){
        return MatricolaDestinatario.size();
    }
}
