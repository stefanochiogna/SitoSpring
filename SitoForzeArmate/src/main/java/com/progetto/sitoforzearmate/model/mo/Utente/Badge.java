package com.progetto.sitoforzearmate.model.mo.Utente;

/* Badge: viene creato al momento della creazione dell'istanza di Utente registrato */
public class Badge {
    private String Id;
    private Long OreLavorative;

    public void setId(String Id){
        this.Id = Id;
    }
    public void setOreLavorative(Long OreLavorative){
        this.OreLavorative = OreLavorative;
    }
    public String getId(){
        return Id;
    }
    public Long getOreLavorative(){
        return OreLavorative;
    }
    public void AggiungiOre(Long Ore){
        OreLavorative = OreLavorative + Ore;
    }
}
