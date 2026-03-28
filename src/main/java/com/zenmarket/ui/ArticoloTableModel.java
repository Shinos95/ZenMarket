package com.zenmarket.ui;

import com.zenmarket.model.RisultatoCalcolo;
import com.zenmarket.util.FormatUtils;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel per la tabella principale degli articoli.
 * Separa la logica di presentazione dalla logica di calcolo.
 */
public class ArticoloTableModel extends AbstractTableModel {

    public enum Colonna {
        NOME("Prodotto"),
        MARKETPLACE("Marketplace"),
        QTA("N°"),
        PESO("Peso (g)"),
        COSTO_ACQ("Costo Acq."),
        IVA("IVA 22%"),
        ZENMARKET("ZenMarket"),
        SPEDIZIONE("Spedizione"),
        DEPOSITO("Deposito 3,5%"),
        STOCCAGGIO("Stoccaggio"),
        TOT_COSTI("Tot. Costi"),
        BASE_DOGANA("Base Dogana"),
        NETTO_VENDITA("Netto Vendita"),
        GUADAGNO("Guadagno"),
        COSTO_PZ("€/pz");

        private final String header;
        Colonna(String header) { this.header = header; }
        public String getHeader() { return header; }
    }

    private final List<RisultatoCalcolo> righe = new ArrayList<>();

    public void setRighe(List<RisultatoCalcolo> nuoveRighe) {
        righe.clear();
        righe.addAll(nuoveRighe);
        fireTableDataChanged();
    }

    public RisultatoCalcolo getRiga(int rowIndex) {
        return righe.get(rowIndex);
    }

    @Override public int getRowCount()    { return righe.size(); }
    @Override public int getColumnCount() { return Colonna.values().length; }

    @Override
    public String getColumnName(int col) {
        return Colonna.values()[col].getHeader();
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return switch (Colonna.values()[col]) {
            case QTA -> Integer.class;
            default  -> String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int col) {
        RisultatoCalcolo r = righe.get(row);
        return switch (Colonna.values()[col]) {
            case NOME         -> r.getArticolo().getNome();
            case MARKETPLACE  -> r.getArticolo().getMarketplace().getLabel();
            case QTA          -> r.getArticolo().getQuantita();
            case PESO         -> FormatUtils.grammi(r.getArticolo().getPesoTotaleGrammi());
            case COSTO_ACQ    -> FormatUtils.eur(r.getArticolo().getCostoAcquistoEur());
            case IVA          -> FormatUtils.eur(r.getIvaEur());
            case ZENMARKET    -> FormatUtils.eur(r.getTariffaZenmarketEur());
            case SPEDIZIONE   -> r.isPesoSottoMinimo() ? "⚠ N/D" : FormatUtils.eur(r.getSpedizioneStimataEur());
            case DEPOSITO     -> FormatUtils.eur(r.getCommissioneDepositoEur());
            case STOCCAGGIO   -> r.getStoccaggioEur() > 0 ? FormatUtils.eur(r.getStoccaggioEur()) : "—";
            case TOT_COSTI    -> FormatUtils.eur(r.getTotaleCostiEur());
            case BASE_DOGANA  -> FormatUtils.eur(r.getBaseDoganaEur()) + (r.isSuperaSogliaDogana() ? " ⚠" : "");
            case NETTO_VENDITA -> r.hasVendita() ? FormatUtils.eur(r.getArticolo().getNettoVenditaEur()) : "—";
            case GUADAGNO     -> r.hasVendita() ? FormatUtils.guadagno(r.getGuadagnoNettoEur()) : "—";
            case COSTO_PZ     -> FormatUtils.eur(r.getCostoPerPezzoEur());
        };
    }

    @Override public boolean isCellEditable(int row, int col) { return false; }
}
