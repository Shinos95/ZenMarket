package com.zenmarket.service;

import com.zenmarket.config.AppConfig;
import com.zenmarket.model.Articolo;
import com.zenmarket.model.RisultatoCalcolo;
import com.zenmarket.model.TotaliSessione;

import java.util.List;

/**
 * Servizio di calcolo puro: nessuna dipendenza dalla UI.
 * Tutta la logica economica è qui, testabile in isolamento.
 *
 * Logica basata su:
 *   - https://zenmarket.jp/it/fees.aspx
 *   - https://zenmarket.jp/it/payment.aspx
 *   - File Excel originale dell'utente
 */
public class CalcolatoreService {

    /**
     * Calcola tutti i costi e il guadagno per un singolo articolo.
     *
     * @param articolo    l'articolo da calcolare
     * @param cambioJpyEur tasso di cambio corrente JPY → EUR
     * @return un oggetto immutabile con tutti i valori calcolati
     */
    public RisultatoCalcolo calcola(Articolo articolo, double cambioJpyEur) {
        // ── 1. IVA italiana sul costo di acquisto ──────────────────────────
        double ivaEur = articolo.getCostoAcquistoEur() * AppConfig.IVA_ITALIANA;

        // ── 2. Tariffa servizio ZenMarket ─────────────────────────────────
        //   La tariffa è PER ARTICOLO (non per lotto): se compri 3 pezzi
        //   paghi 3 × tariffa. Fonte: "per articolo, indipendentemente dal
        //   numero di pezzi identici acquistati" → in realtà ZenMarket conta
        //   ogni riga/ordine come UN articolo, non ogni pezzo fisico.
        //   Il file Excel usa una tariffa fissa per riga → manteniamo lo stesso.
        double tariffaZenmarketEur = articolo.getMarketplace().getTariffaJpy() * cambioJpyEur;

        // ── 3. Spedizione EMS stimata ──────────────────────────────────────
        double pesoGrammi = articolo.getPesoTotaleGrammi();
        boolean pesoSottoMinimo = pesoGrammi < AppConfig.PESO_MINIMO_EMS_G;
        double spedizioneEur = pesoSottoMinimo ? 0.0 : calcolaSpedizione(pesoGrammi);

        // ── 4. Stoccaggio extra (giorni oltre il 60° gratuito) ─────────────
        double stoccaggioEur = 0.0;
        if (articolo.getGiorniStoccaggio() > 0) {
            stoccaggioEur = articolo.getGiorniStoccaggio()
                    * AppConfig.COSTO_STOCCAGGIO_GIORNALIERO_JPY
                    * cambioJpyEur;
        }

        // ── 5. Foto articolo (opzionale) ───────────────────────────────────
        double fotoEur = 0.0;
        if (articolo.isFotoRichiesta()) {
            fotoEur = AppConfig.SERVIZIO_FOTO_PER_ARTICOLO_JPY * cambioJpyEur;
        }

        // ── 6. Commissione deposito fondi (3,5%) ───────────────────────────
        //   Si applica sul totale che devi depositare su ZenMarket:
        //   costo articolo + tariffa ZenMarket + spedizione domestica Giappone
        //   (quest'ultima non è modellata qui, usiamo i costi noti)
        //   Il file Excel originale usava 5% come "costi deposito" calcolato
        //   sulla somma dei costi → manteniamo entrambe le logiche.
        //   La pagina pagamenti indica 3,5% sulla ricarica effettuata.
        //   Usiamo la logica del file Excel (5% sui sub-totali) per compatibilità
        //   con i dati storici, configurabile in AppConfig.
        double baseDeposito = articolo.getCostoAcquistoEur() + ivaEur
                + tariffaZenmarketEur + spedizioneEur;
        double commissioneDepositoEur = baseDeposito * AppConfig.COMMISSIONE_DEPOSITO;

        // ── 7. Totale costi ────────────────────────────────────────────────
        double totaleCostiEur = articolo.getCostoAcquistoEur()
                + ivaEur
                + tariffaZenmarketEur
                + spedizioneEur
                + commissioneDepositoEur
                + stoccaggioEur
                + fotoEur;

        // ── 8. Base doganale ───────────────────────────────────────────────
        //   Valore dichiarato = costo acquisto + IVA + spedizione
        //   Se supera 150€ → potenziali dazi
        double baseDoganaEur = articolo.getCostoAcquistoEur() + ivaEur + spedizioneEur;
        boolean superaSoglia = baseDoganaEur > AppConfig.SOGLIA_DOGANALE_EUR;

        // ── 9. Guadagno netto ─────────────────────────────────────────────
        double guadagnoEur = 0.0;
        if (articolo.getNettoVenditaEur() > 0) {
            guadagnoEur = articolo.getNettoVenditaEur() - totaleCostiEur;
        }

        // ── 10. Costo per singolo pezzo ───────────────────────────────────
        double costoPerPezzoEur = articolo.getQuantita() > 0
                ? totaleCostiEur / articolo.getQuantita()
                : totaleCostiEur;

        return new RisultatoCalcolo.Builder()
                .articolo(articolo)
                .cambioJpyEur(cambioJpyEur)
                .ivaEur(ivaEur)
                .tariffaZenmarketEur(tariffaZenmarketEur)
                .commissioneDepositoEur(commissioneDepositoEur)
                .spedizioneStimataEur(spedizioneEur)
                .stoccaggioEur(stoccaggioEur)
                .fotoEur(fotoEur)
                .totaleCostiEur(totaleCostiEur)
                .baseDoganaEur(baseDoganaEur)
                .superaSogliaDogana(superaSoglia)
                .guadagnoNettoEur(guadagnoEur)
                .costoPerPezzoEur(costoPerPezzoEur)
                .pesoSottoMinimo(pesoSottoMinimo)
                .build();
    }

    /**
     * Calcola i totali aggregati di una lista di risultati.
     */
    public TotaliSessione calcolaTotali(List<RisultatoCalcolo> risultati) {
        double costoAcq = 0, iva = 0, zen = 0, dep = 0, sped = 0,
               stocc = 0, foto = 0, totCosti = 0, dogana = 0,
               vendita = 0, guadagno = 0;
        int conVendita = 0;

        for (RisultatoCalcolo r : risultati) {
            costoAcq  += r.getArticolo().getCostoAcquistoEur();
            iva       += r.getIvaEur();
            zen       += r.getTariffaZenmarketEur();
            dep       += r.getCommissioneDepositoEur();
            sped      += r.getSpedizioneStimataEur();
            stocc     += r.getStoccaggioEur();
            foto      += r.getFotoEur();
            totCosti  += r.getTotaleCostiEur();
            dogana    += r.getBaseDoganaEur();
            if (r.hasVendita()) {
                vendita  += r.getArticolo().getNettoVenditaEur();
                guadagno += r.getGuadagnoNettoEur();
                conVendita++;
            }
        }

        return new TotaliSessione.Builder()
                .costoAcquisto(costoAcq)
                .iva(iva)
                .zenmarket(zen)
                .deposito(dep)
                .spedizione(sped)
                .stoccaggio(stocc)
                .foto(foto)
                .costiTotali(totCosti)
                .baseDogana(dogana)
                .nettoVendita(vendita)
                .guadagno(guadagno)
                .articoliConVendita(conVendita)
                .articoliTotali(risultati.size())
                .build();
    }

    /**
     * Determina la tariffa EMS in base al peso totale del pacco.
     * Tabella prezzi verso Italia.
     * Restituisce 0.0 se il peso supera il massimo tabulato (gestione manuale).
     */
    public double calcolaSpedizione(double pesoGrammi) {
        for (double[] fascia : AppConfig.TARIFFE_EMS) {
            if (pesoGrammi <= fascia[0]) {
                return fascia[1];
            }
        }
        // Oltre 8kg: restituisce l'ultima tariffa nota come stima minima
        return AppConfig.TARIFFE_EMS[AppConfig.TARIFFE_EMS.length - 1][1];
    }

    /**
     * Converte un importo da JPY a EUR usando il cambio fornito.
     */
    public double jpyToEur(double jpy, double cambio) {
        return jpy * cambio;
    }
}
