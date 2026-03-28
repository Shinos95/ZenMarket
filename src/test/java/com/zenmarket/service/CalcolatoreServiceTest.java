package com.zenmarket.service;

import com.zenmarket.config.AppConfig;
import com.zenmarket.model.Articolo;
import com.zenmarket.model.Marketplace;
import com.zenmarket.model.RisultatoCalcolo;
import com.zenmarket.model.TotaliSessione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalcolatoreServiceTest {

    private CalcolatoreService service;
    private static final double CAMBIO = 0.006; // 1 JPY = 0.006 EUR
    private static final double DELTA  = 0.001; // tolleranza double

    @BeforeEach
    void setUp() {
        service = new CalcolatoreService();
    }

    // ─── IVA ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("IVA = 22% del costo acquisto")
    void testIva() {
        Articolo a = articolo(100.0, 1000, Marketplace.MERCARI_PRIVATI, 1, 0);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        assertEquals(22.0, r.getIvaEur(), DELTA);
    }

    // ─── Tariffa ZenMarket ────────────────────────────────────────────────

    @Test
    @DisplayName("Tariffa Mercari privati = 800 JPY × cambio")
    void testTariffaMercariPrivati() {
        Articolo a = articolo(100, 1000, Marketplace.MERCARI_PRIVATI, 1, 0);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        double attesa = AppConfig.ZENMARKET_FEE_MERCARI_JPY * CAMBIO;
        assertEquals(attesa, r.getTariffaZenmarketEur(), DELTA);
    }

    @Test
    @DisplayName("Tariffa standard = 500 JPY × cambio")
    void testTariffaStandard() {
        Articolo a = articolo(100, 1000, Marketplace.AMAZON_JP, 1, 0);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        double attesa = AppConfig.ZENMARKET_FEE_STANDARD_JPY * CAMBIO;
        assertEquals(attesa, r.getTariffaZenmarketEur(), DELTA);
    }

    @Test
    @DisplayName("Tariffa ZenPlus = 300 JPY × cambio")
    void testTariffaZenPlus() {
        Articolo a = articolo(100, 1000, Marketplace.ZENPLUS, 1, 0);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        double attesa = AppConfig.ZENMARKET_FEE_SCONTATA_JPY * CAMBIO;
        assertEquals(attesa, r.getTariffaZenmarketEur(), DELTA);
    }

    // ─── Spedizione EMS ───────────────────────────────────────────────────

    @Test
    @DisplayName("Peso < 200g → pesoSottoMinimo = true, spedizione = 0")
    void testPesoSottoMinimo() {
        Articolo a = articolo(100, 150, Marketplace.MERCARI_PRIVATI, 1, 0);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        assertTrue(r.isPesoSottoMinimo());
        assertEquals(0.0, r.getSpedizioneStimataEur(), DELTA);
    }

    @Test
    @DisplayName("Peso 300g → fascia <499g = 10,30€")
    void testSpedizione300g() {
        double sped = service.calcolaSpedizione(300);
        assertEquals(10.30, sped, DELTA);
    }

    @Test
    @DisplayName("Peso 1000g → fascia <1199g = 18,00€")
    void testSpedizione1000g() {
        double sped = service.calcolaSpedizione(1000);
        assertEquals(18.00, sped, DELTA);
    }

    @Test
    @DisplayName("Peso 3000g (3kg) → fascia <3999g = 26,00€")
    void testSpedizione3000g() {
        double sped = service.calcolaSpedizione(3000);
        assertEquals(26.00, sped, DELTA);
    }

    @Test
    @DisplayName("Peso esattamente 499g → fascia <=499g = 10,30€")
    void testSpedizioneBordo499() {
        double sped = service.calcolaSpedizione(499);
        assertEquals(10.30, sped, DELTA);
    }

    @Test
    @DisplayName("Peso 500g → fascia <699g = 12,50€")
    void testSpedizione500g() {
        double sped = service.calcolaSpedizione(500);
        assertEquals(12.50, sped, DELTA);
    }

    // ─── Commissione deposito ─────────────────────────────────────────────

    @Test
    @DisplayName("Commissione deposito = 3,5% sulla base imponibile")
    void testCommissioneDeposito() {
        Articolo a = articolo(100, 1000, Marketplace.MERCARI_PRIVATI, 1, 0);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        // base = costo + IVA + zenmarket + spedizione
        double base = r.getArticolo().getCostoAcquistoEur()
                + r.getIvaEur()
                + r.getTariffaZenmarketEur()
                + r.getSpedizioneStimataEur();
        double attesa = base * AppConfig.COMMISSIONE_DEPOSITO;
        assertEquals(attesa, r.getCommissioneDepositoEur(), DELTA);
    }

    // ─── Soglia doganale ─────────────────────────────────────────────────

    @Test
    @DisplayName("Base dogana > 150€ → superaSogliaDogana = true")
    void testSogliaDoganaSuperata() {
        Articolo a = articolo(200, 3000, Marketplace.MERCARI_PRIVATI, 1, 0);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        assertTrue(r.isSuperaSogliaDogana());
    }

    @Test
    @DisplayName("Base dogana < 150€ → superaSogliaDogana = false")
    void testSogliaDoganaOk() {
        Articolo a = articolo(50, 500, Marketplace.MERCARI_PRIVATI, 1, 0);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        assertFalse(r.isSuperaSogliaDogana());
    }

    // ─── Guadagno ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Guadagno = netto vendita - totale costi")
    void testGuadagno() {
        Articolo a = articolo(100, 1000, Marketplace.MERCARI_PRIVATI, 1, 200);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        double atteso = 200.0 - r.getTotaleCostiEur();
        assertEquals(atteso, r.getGuadagnoNettoEur(), DELTA);
    }

    @Test
    @DisplayName("Guadagno = 0 se netto vendita non specificato")
    void testGuadagnoSenzaVendita() {
        Articolo a = articolo(100, 1000, Marketplace.MERCARI_PRIVATI, 1, 0);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        assertEquals(0.0, r.getGuadagnoNettoEur(), DELTA);
        assertFalse(r.hasVendita());
    }

    // ─── Costo per pezzo ─────────────────────────────────────────────────

    @Test
    @DisplayName("Costo/pz = totale costi / quantità")
    void testCostoPerPezzo() {
        Articolo a = articolo(120, 3000, Marketplace.MERCARI_PRIVATI, 4, 300);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        assertEquals(r.getTotaleCostiEur() / 4.0, r.getCostoPerPezzoEur(), DELTA);
    }

    // ─── Stoccaggio e foto ────────────────────────────────────────────────

    @Test
    @DisplayName("Stoccaggio extra 10 giorni = 10 × 50 JPY × cambio")
    void testStoccaggio() {
        Articolo a = articolo(100, 1000, Marketplace.MERCARI_PRIVATI, 1, 0);
        a.setGiorniStoccaggio(10);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        double atteso = 10 * AppConfig.COSTO_STOCCAGGIO_GIORNALIERO_JPY * CAMBIO;
        assertEquals(atteso, r.getStoccaggioEur(), DELTA);
    }

    @Test
    @DisplayName("Foto richiesta = 500 JPY × cambio")
    void testFoto() {
        Articolo a = articolo(100, 1000, Marketplace.MERCARI_PRIVATI, 1, 0);
        a.setFotoRichiesta(true);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        double atteso = AppConfig.SERVIZIO_FOTO_PER_ARTICOLO_JPY * CAMBIO;
        assertEquals(atteso, r.getFotoEur(), DELTA);
    }

    // ─── Totali sessione ──────────────────────────────────────────────────

    @Test
    @DisplayName("calcolaTotali somma correttamente i costi di più articoli")
    void testTotaliSessione() {
        Articolo a1 = articolo(80,  2000, Marketplace.MERCARI_PRIVATI, 3, 200);
        Articolo a2 = articolo(150, 4000, Marketplace.AMAZON_JP,       1, 350);
        RisultatoCalcolo r1 = service.calcola(a1, CAMBIO);
        RisultatoCalcolo r2 = service.calcola(a2, CAMBIO);

        TotaliSessione t = service.calcolaTotali(List.of(r1, r2));

        assertEquals(r1.getTotaleCostiEur() + r2.getTotaleCostiEur(),
                t.getTotaleCostiEur(), DELTA);
        assertEquals(2, t.getArticoliTotali());
        assertEquals(2, t.getArticoliConVendita());
    }

    @Test
    @DisplayName("Articolo senza vendita non conta nei totali di guadagno")
    void testTotaliSenzaVendita() {
        Articolo a1 = articolo(80,  2000, Marketplace.MERCARI_PRIVATI, 1, 200);
        Articolo a2 = articolo(50,  1500, Marketplace.MERCARI_PRIVATI, 1, 0);   // no vendita
        RisultatoCalcolo r1 = service.calcola(a1, CAMBIO);
        RisultatoCalcolo r2 = service.calcola(a2, CAMBIO);

        TotaliSessione t = service.calcolaTotali(List.of(r1, r2));
        assertEquals(1, t.getArticoliConVendita());
        assertEquals(r1.getGuadagnoNettoEur(), t.getTotaleGuadagnoNettoEur(), DELTA);
    }

    // ─── Dati Excel originali (regression test) ───────────────────────────

    @Test
    @DisplayName("Esempio dal file Excel: Ichiban Whitebeard 3 pz, 3500g, €81 → totale costi ragionevole")
    void testEsempioExcel() {
        Articolo a = articolo(81, 3500, Marketplace.MERCARI_PRIVATI, 3, 230);
        RisultatoCalcolo r = service.calcola(a, CAMBIO);
        // IVA: 81 × 0.22 = 17.82
        assertEquals(17.82, r.getIvaEur(), DELTA);
        // Spedizione 3500g → fascia <3999g = 26€
        assertEquals(26.0, r.getSpedizioneStimataEur(), DELTA);
        // Il totale costi deve essere < netto vendita (230€) per avere senso
        assertTrue(r.getTotaleCostiEur() < 230.0,
                "Totale costi atteso < €230, ottenuto: " + r.getTotaleCostiEur());
        // Costo per pezzo = totaleCosti / 3
        assertEquals(r.getTotaleCostiEur() / 3.0, r.getCostoPerPezzoEur(), DELTA);
    }

    // ─── Helper ───────────────────────────────────────────────────────────

    private Articolo articolo(double costo, double peso, Marketplace mkt, int qta, double vendita) {
        Articolo a = new Articolo();
        a.setCostoAcquistoEur(costo);
        a.setPesoTotaleGrammi(peso);
        a.setMarketplace(mkt);
        a.setQuantita(qta);
        a.setNettoVenditaEur(vendita);
        a.setNome("Test articolo");
        return a;
    }
}
