package com.zenmarket.model;

/**
 * Totali aggregati di tutti gli articoli nella sessione corrente.
 */
public final class TotaliSessione {

    private final double totaleCostoAcquistoEur;
    private final double totaleIvaEur;
    private final double totaleTariffaZenmarketEur;
    private final double totaleCommissioneDepositoEur;
    private final double totaleSpedizioneEur;
    private final double totaleStoccaggioEur;
    private final double totaleFotoEur;
    private final double totaleCostiEur;
    private final double totaleBaseDoganaEur;
    private final double totaleNettoVenditaEur;
    private final double totaleGuadagnoNettoEur;
    private final int articoliConVendita;
    private final int articoliTotali;

    private TotaliSessione(Builder b) {
        this.totaleCostoAcquistoEur      = b.totaleCostoAcquistoEur;
        this.totaleIvaEur                = b.totaleIvaEur;
        this.totaleTariffaZenmarketEur   = b.totaleTariffaZenmarketEur;
        this.totaleCommissioneDepositoEur= b.totaleCommissioneDepositoEur;
        this.totaleSpedizioneEur         = b.totaleSpedizioneEur;
        this.totaleStoccaggioEur         = b.totaleStoccaggioEur;
        this.totaleFotoEur               = b.totaleFotoEur;
        this.totaleCostiEur              = b.totaleCostiEur;
        this.totaleBaseDoganaEur         = b.totaleBaseDoganaEur;
        this.totaleNettoVenditaEur       = b.totaleNettoVenditaEur;
        this.totaleGuadagnoNettoEur      = b.totaleGuadagnoNettoEur;
        this.articoliConVendita          = b.articoliConVendita;
        this.articoliTotali              = b.articoliTotali;
    }

    public double getTotaleCostoAcquistoEur()       { return totaleCostoAcquistoEur; }
    public double getTotaleIvaEur()                  { return totaleIvaEur; }
    public double getTotaleTariffaZenmarketEur()     { return totaleTariffaZenmarketEur; }
    public double getTotaleCommissioneDepositoEur()  { return totaleCommissioneDepositoEur; }
    public double getTotaleSpedizioneEur()           { return totaleSpedizioneEur; }
    public double getTotaleStoccaggioEur()           { return totaleStoccaggioEur; }
    public double getTotaleFotoEur()                 { return totaleFotoEur; }
    public double getTotaleCostiEur()                { return totaleCostiEur; }
    public double getTotaleBaseDoganaEur()           { return totaleBaseDoganaEur; }
    public double getTotaleNettoVenditaEur()         { return totaleNettoVenditaEur; }
    public double getTotaleGuadagnoNettoEur()        { return totaleGuadagnoNettoEur; }
    public int getArticoliConVendita()               { return articoliConVendita; }
    public int getArticoliTotali()                   { return articoliTotali; }

    public static final class Builder {
        private double totaleCostoAcquistoEur;
        private double totaleIvaEur;
        private double totaleTariffaZenmarketEur;
        private double totaleCommissioneDepositoEur;
        private double totaleSpedizioneEur;
        private double totaleStoccaggioEur;
        private double totaleFotoEur;
        private double totaleCostiEur;
        private double totaleBaseDoganaEur;
        private double totaleNettoVenditaEur;
        private double totaleGuadagnoNettoEur;
        private int articoliConVendita;
        private int articoliTotali;

        public Builder costoAcquisto(double v)     { this.totaleCostoAcquistoEur = v; return this; }
        public Builder iva(double v)               { this.totaleIvaEur = v; return this; }
        public Builder zenmarket(double v)         { this.totaleTariffaZenmarketEur = v; return this; }
        public Builder deposito(double v)          { this.totaleCommissioneDepositoEur = v; return this; }
        public Builder spedizione(double v)        { this.totaleSpedizioneEur = v; return this; }
        public Builder stoccaggio(double v)        { this.totaleStoccaggioEur = v; return this; }
        public Builder foto(double v)              { this.totaleFotoEur = v; return this; }
        public Builder costiTotali(double v)       { this.totaleCostiEur = v; return this; }
        public Builder baseDogana(double v)        { this.totaleBaseDoganaEur = v; return this; }
        public Builder nettoVendita(double v)      { this.totaleNettoVenditaEur = v; return this; }
        public Builder guadagno(double v)          { this.totaleGuadagnoNettoEur = v; return this; }
        public Builder articoliConVendita(int v)   { this.articoliConVendita = v; return this; }
        public Builder articoliTotali(int v)       { this.articoliTotali = v; return this; }

        public TotaliSessione build()              { return new TotaliSessione(this); }
    }
}
