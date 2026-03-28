package com.zenmarket.model;

/**
 * Risultato immutabile di un calcolo per un singolo {@link Articolo}.
 * Non contiene logica: è solo un contenitore di valori calcolati.
 */
public final class RisultatoCalcolo {

    // Input snapshot
    private final Articolo articolo;
    private final double cambioJpyEur;

    // Costi calcolati (tutti in EUR)
    private final double ivaEur;
    private final double tariffaZenmarketEur;   // tariffa servizio per articolo × quantità
    private final double commissioneDepositoEur; // 3,5% sul totale depositato
    private final double spedizioneStimataEur;   // EMS per fascia di peso, -1 se sotto peso minimo
    private final double stoccaggioEur;          // solo se giorni > 60
    private final double fotoEur;                // solo se richiesta
    private final double totaleCostiEur;

    // Valori doganali
    private final double baseDoganaEur;   // costo acq + IVA + spedizione (soglia 150€)
    private final boolean superaSogliaDogana;

    // Profitto
    private final double guadagnoNettoEur;   // nettoVendita - totaleCosti (0 se vendita non specificata)
    private final double costoPerPezzoEur;   // totaleCosti / quantità

    // Avvisi
    private final boolean pesoSottoMinimo;   // true se peso < 200g (spedizione non calcolabile)

    public RisultatoCalcolo(Builder b) {
        this.articolo = b.articolo;
        this.cambioJpyEur = b.cambioJpyEur;
        this.ivaEur = b.ivaEur;
        this.tariffaZenmarketEur = b.tariffaZenmarketEur;
        this.commissioneDepositoEur = b.commissioneDepositoEur;
        this.spedizioneStimataEur = b.spedizioneStimataEur;
        this.stoccaggioEur = b.stoccaggioEur;
        this.fotoEur = b.fotoEur;
        this.totaleCostiEur = b.totaleCostiEur;
        this.baseDoganaEur = b.baseDoganaEur;
        this.superaSogliaDogana = b.superaSogliaDogana;
        this.guadagnoNettoEur = b.guadagnoNettoEur;
        this.costoPerPezzoEur = b.costoPerPezzoEur;
        this.pesoSottoMinimo = b.pesoSottoMinimo;
    }

    // ─── Getters ───────────────────────────────────────────────────────────

    public Articolo getArticolo()              { return articolo; }
    public double getCambioJpyEur()            { return cambioJpyEur; }
    public double getIvaEur()                  { return ivaEur; }
    public double getTariffaZenmarketEur()     { return tariffaZenmarketEur; }
    public double getCommissioneDepositoEur()  { return commissioneDepositoEur; }
    public double getSpedizioneStimataEur()    { return spedizioneStimataEur; }
    public double getStoccaggioEur()           { return stoccaggioEur; }
    public double getFotoEur()                 { return fotoEur; }
    public double getTotaleCostiEur()          { return totaleCostiEur; }
    public double getBaseDoganaEur()           { return baseDoganaEur; }
    public boolean isSuperaSogliaDogana()      { return superaSogliaDogana; }
    public double getGuadagnoNettoEur()        { return guadagnoNettoEur; }
    public double getCostoPerPezzoEur()        { return costoPerPezzoEur; }
    public boolean isPesoSottoMinimo()         { return pesoSottoMinimo; }

    public boolean hasVendita() {
        return articolo.getNettoVenditaEur() > 0;
    }

    // ─── Builder ───────────────────────────────────────────────────────────

    public static final class Builder {
        private Articolo articolo;
        private double cambioJpyEur;
        private double ivaEur;
        private double tariffaZenmarketEur;
        private double commissioneDepositoEur;
        private double spedizioneStimataEur;
        private double stoccaggioEur;
        private double fotoEur;
        private double totaleCostiEur;
        private double baseDoganaEur;
        private boolean superaSogliaDogana;
        private double guadagnoNettoEur;
        private double costoPerPezzoEur;
        private boolean pesoSottoMinimo;

        public Builder articolo(Articolo a)                   { this.articolo = a; return this; }
        public Builder cambioJpyEur(double v)                 { this.cambioJpyEur = v; return this; }
        public Builder ivaEur(double v)                       { this.ivaEur = v; return this; }
        public Builder tariffaZenmarketEur(double v)          { this.tariffaZenmarketEur = v; return this; }
        public Builder commissioneDepositoEur(double v)       { this.commissioneDepositoEur = v; return this; }
        public Builder spedizioneStimataEur(double v)         { this.spedizioneStimataEur = v; return this; }
        public Builder stoccaggioEur(double v)                { this.stoccaggioEur = v; return this; }
        public Builder fotoEur(double v)                      { this.fotoEur = v; return this; }
        public Builder totaleCostiEur(double v)               { this.totaleCostiEur = v; return this; }
        public Builder baseDoganaEur(double v)                { this.baseDoganaEur = v; return this; }
        public Builder superaSogliaDogana(boolean v)          { this.superaSogliaDogana = v; return this; }
        public Builder guadagnoNettoEur(double v)             { this.guadagnoNettoEur = v; return this; }
        public Builder costoPerPezzoEur(double v)             { this.costoPerPezzoEur = v; return this; }
        public Builder pesoSottoMinimo(boolean v)             { this.pesoSottoMinimo = v; return this; }

        public RisultatoCalcolo build() { return new RisultatoCalcolo(this); }
    }
}
