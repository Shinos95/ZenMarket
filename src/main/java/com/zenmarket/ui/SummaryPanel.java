package com.zenmarket.ui;

import com.zenmarket.model.TotaliSessione;
import com.zenmarket.util.FormatUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Pannello di riepilogo con card in stile dark moderne.
 * Usa layout responsivo che si adatta alla larghezza della finestra.
 */
public class SummaryPanel extends JPanel {

    private record Card(JLabel value, JLabel subtitle) {}

    private final Card cardCosti    = makeCard(Theme.CARD_COST);
    private final Card cardVendita  = makeCard(Theme.CARD_REV);
    private final Card cardGuadagno = makeCard(Theme.CARD_PROFIT);
    private final Card cardDogana   = makeCard(Theme.CARD_DOGANA);
    private final Card cardArticoli = makeCard(Theme.CARD_COUNT);

    public SummaryPanel() {
        setLayout(new GridLayout(1, 5, 10, 0));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(0, 0, 14, 0));

        add(buildCard("TOTALE COSTI",    cardCosti,    Theme.CARD_COST));
        add(buildCard("NETTO VENDITA",   cardVendita,  Theme.CARD_REV));
        add(buildCard("GUADAGNO NETTO",  cardGuadagno, Theme.CARD_PROFIT));
        add(buildCard("BASE DOGANALE",   cardDogana,   Theme.CARD_DOGANA));
        add(buildCard("N° ARTICOLI",     cardArticoli, Theme.CARD_COUNT));
    }

    private JPanel buildCard(String titolo, Card card, Color accentColor) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.SURFACE);
        outer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, accentColor),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                new EmptyBorder(12, 16, 12, 16)
            )
        ));

        JLabel lblTitolo = Theme.tagLabel(titolo);
        lblTitolo.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel bottom = new JPanel(new BorderLayout(0, 2));
        bottom.setOpaque(false);
        bottom.add(card.value, BorderLayout.CENTER);
        bottom.add(card.subtitle, BorderLayout.SOUTH);

        outer.add(lblTitolo, BorderLayout.NORTH);
        outer.add(bottom, BorderLayout.CENTER);
        return outer;
    }

    private Card makeCard(Color color) {
        JLabel val = new JLabel("—");
        val.setFont(Theme.FONT_CARD_VAL);
        val.setForeground(color);

        JLabel sub = new JLabel(" ");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.MUTED);
        return new Card(val, sub);
    }

    public void aggiorna(TotaliSessione t) {
        cardCosti.value.setText(FormatUtils.eur(t.getTotaleCostiEur()));
        cardCosti.subtitle.setText(t.getArticoliTotali() + " articol" +
                (t.getArticoliTotali() == 1 ? "o" : "i"));

        if (t.getArticoliConVendita() > 0) {
            cardVendita.value.setText(FormatUtils.eur(t.getTotaleNettoVenditaEur()));
            cardVendita.subtitle.setText(t.getArticoliConVendita() + " con prezzo vendita");

            double guad = t.getTotaleGuadagnoNettoEur();
            cardGuadagno.value.setText(FormatUtils.guadagno(guad));
            cardGuadagno.value.setForeground(guad >= 0 ? Theme.GREEN : Theme.RED);
            cardGuadagno.subtitle.setText(guad >= 0 ? "Profitto" : "Perdita");
        } else {
            cardVendita.value.setText("—");
            cardVendita.subtitle.setText("nessun prezzo inserito");
            cardGuadagno.value.setText("—");
            cardGuadagno.value.setForeground(Theme.CARD_PROFIT);
            cardGuadagno.subtitle.setText(" ");
        }

        cardDogana.value.setText(FormatUtils.eur(t.getTotaleBaseDoganaEur()));
        cardDogana.subtitle.setText("soglia €150");

        cardArticoli.value.setText(String.valueOf(t.getArticoliTotali()));
        cardArticoli.subtitle.setText("in sessione");
    }

    public void reset() {
        for (Card c : new Card[]{cardCosti, cardVendita, cardGuadagno, cardDogana}) {
            c.value.setText("—");
            c.subtitle.setText(" ");
        }
        cardGuadagno.value.setForeground(Theme.CARD_PROFIT);
        cardArticoli.value.setText("0");
        cardArticoli.subtitle.setText("in sessione");
    }
}
