package com.zenmarket.config;

/**
 * Configurazione centralizzata dell'applicazione.
 * Modifica qui tutte le costanti senza toccare la logica di calcolo.
 *
 * Fonti: https://zenmarket.jp/it/fees.aspx  |  https://zenmarket.jp/it/payment.aspx
 */
public final class AppConfig {

    private AppConfig() {}

    // ─────────────────────────────────────────────
    //  TASSO DI CAMBIO DEFAULT
    // ─────────────────────────────────────────────
    /** Tasso di cambio JPY → EUR di default. Modificabile dall'utente a runtime. */
    public static final double DEFAULT_CAMBIO_JPY_EUR = 0.006;

    // ─────────────────────────────────────────────
    //  TARIFFE DI SERVIZIO ZENMARKET (in JPY)
    //  Fonte: https://zenmarket.jp/it/fees.aspx
    // ─────────────────────────────────────────────
    /** Commissione per acquisti Mercari (venditori privati) – dal 1° ottobre 2025 */
    public static final int ZENMARKET_FEE_MERCARI_JPY = 800;

    /** Commissione per Mercari Shops, Amazon JP, Rakuten, JDirectItems Auction e la maggior parte degli altri */
    public static final int ZENMARKET_FEE_STANDARD_JPY = 500;

    /** Commissione per negozi consigliati (ZenPlus, store partner) */
    public static final int ZENMARKET_FEE_SCONTATA_JPY = 300;

    // ─────────────────────────────────────────────
    //  COMMISSIONE DEPOSITO FONDI
    //  Fonte: https://zenmarket.jp/it/payment.aspx
    //  "commissione fissa del 3,5% per tutti i metodi tranne bonifico bancario"
    // ─────────────────────────────────────────────
    /** Commissione applicata al deposito di fondi (carta / PayPal) */
    public static final double COMMISSIONE_DEPOSITO = 0.035;

    /** Commissione bonifico bancario internazionale (SWIFT) in JPY – alternativa al 3,5% */
    public static final int COMMISSIONE_BONIFICO_JPY = 4500;

    // ─────────────────────────────────────────────
    //  IVA ITALIANA SU ACQUISTO
    // ─────────────────────────────────────────────
    /** IVA italiana applicata sul costo di acquisto (22%) */
    public static final double IVA_ITALIANA = 0.22;

    // ─────────────────────────────────────────────
    //  SOGLIA DOGANALE ITALIA (€)
    //  Pacchi con valore dichiarato > 150 € sono soggetti a dazi
    // ─────────────────────────────────────────────
    public static final double SOGLIA_DOGANALE_EUR = 150.0;

    // ─────────────────────────────────────────────
    //  TARIFFE EMS SPEDIZIONE INTERNAZIONALE (€)
    //  Tabella per fascia di peso (grammi) verso Italia
    //  Modifica i valori se le tariffe cambiano
    // ─────────────────────────────────────────────
    /** Ogni fascia: {peso_max_grammi, tariffa_eur} */
    public static final double[][] TARIFFE_EMS = {
        //  peso_max(g)   tariffa(€)
        {    499,          10.30 },
        {    699,          12.50 },
        {    899,          14.70 },
        {    999,          15.20 },
        {   1199,          18.00 },
        {   1399,          20.00 },
        {   1699,          23.50 },
        {   1999,          26.00 },
        {   2999,          22.50 },
        {   3999,          26.00 },
        {   5999,          33.50 },
        {   7999,          41.00 },
    };

    /** Peso minimo (g) per la spedizione EMS */
    public static final int PESO_MINIMO_EMS_G = 200;

    // ─────────────────────────────────────────────
    //  STOCCAGGIO OLTRE I 60 GIORNI GRATUITI
    //  Fonte: https://zenmarket.jp/it/fees.aspx
    // ─────────────────────────────────────────────
    public static final int GIORNI_STOCCAGGIO_GRATUITI = 60;
    public static final int COSTO_STOCCAGGIO_GIORNALIERO_JPY = 50;

    // ─────────────────────────────────────────────
    //  SERVIZI OPZIONALI (JPY)
    // ─────────────────────────────────────────────
    public static final int SERVIZIO_FOTO_PER_ARTICOLO_JPY = 500;
    public static final int PENALE_CANCELLAZIONE_JPY       = 1000;
    public static final int REIMBALLAGGIO_BASE_JPY         = 1000;
}
