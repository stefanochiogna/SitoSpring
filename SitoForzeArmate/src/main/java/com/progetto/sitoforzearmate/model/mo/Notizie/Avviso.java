package com.progetto.sitoforzearmate.model.mo.Notizie;

import java.util.ArrayList;
import java.util.List;

public class Avviso extends Notizie{
    private List<String> MatricolaDestinatario = new ArrayList<>();

    /* Matricola */
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
