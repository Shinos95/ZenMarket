package com.zenmarket.model;

/**
 * Rappresenta un articolo acquistato tramite ZenMarket.
 * Contiene solo i dati grezzi inseriti dall'utente.
 * Tutti i calcoli derivati sono in {@link com.zenmarket.service.CalcolatoreService}.
 */
public class Articolo {

    private String nome;
    private Marketplace marketplace;
    private int quantita;           // numero di pezzi identici nello stesso ordine
    private double pesoTotaleGrammi;
    private double costoAcquistoEur; // prezzo pagato al venditore (già convertito in €)
    private double nettoVenditaEur;  // prezzo a cui si prevede di rivendere in Italia (0 = non specificato)
    private int giorniStoccaggio;    // giorni oltre i 60 gratuiti (default 0)
    private boolean fotoRichiesta;   // se hai richiesto il servizio foto articolo
    private String note;

    // ─── Costruttori ───────────────────────────────────────────────────────

    public Articolo() {
        this.marketplace = Marketplace.MERCARI_PRIVATI;
        this.quantita = 1;
        this.giorniStoccaggio = 0;
        this.fotoRichiesta = false;
    }

    public Articolo(String nome, Marketplace marketplace, int quantita,
                    double pesoTotaleGrammi, double costoAcquistoEur, double nettoVenditaEur) {
        this();
        this.nome = nome;
        this.marketplace = marketplace;
        this.quantita = quantita;
        this.pesoTotaleGrammi = pesoTotaleGrammi;
        this.costoAcquistoEur = costoAcquistoEur;
        this.nettoVenditaEur = nettoVenditaEur;
    }

    // ─── Getters & Setters ─────────────────────────────────────────────────

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Marketplace getMarketplace() { return marketplace; }
    public void setMarketplace(Marketplace marketplace) { this.marketplace = marketplace; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = Math.max(1, quantita); }

    public double getPesoTotaleGrammi() { return pesoTotaleGrammi; }
    public void setPesoTotaleGrammi(double pesoTotaleGrammi) { this.pesoTotaleGrammi = pesoTotaleGrammi; }

    public double getCostoAcquistoEur() { return costoAcquistoEur; }
    public void setCostoAcquistoEur(double costoAcquistoEur) { this.costoAcquistoEur = costoAcquistoEur; }

    public double getNettoVenditaEur() { return nettoVenditaEur; }
    public void setNettoVenditaEur(double nettoVenditaEur) { this.nettoVenditaEur = nettoVenditaEur; }

    public int getGiorniStoccaggio() { return giorniStoccaggio; }
    public void setGiorniStoccaggio(int giorniStoccaggio) { this.giorniStoccaggio = Math.max(0, giorniStoccaggio); }

    public boolean isFotoRichiesta() { return fotoRichiesta; }
    public void setFotoRichiesta(boolean fotoRichiesta) { this.fotoRichiesta = fotoRichiesta; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return nome != null ? nome : "(articolo senza nome)";
    }
}
