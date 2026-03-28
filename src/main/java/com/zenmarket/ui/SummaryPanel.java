package com.zenmarket.ui;

import com.zenmarket.model.TotaliSessione;
import com.zenmarket.util.FormatUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Pannello di riepilogo con le card dei totali in cima alla finestra.
 */
public class SummaryPanel extends JPanel {

    private final JLabel lblTotaleCosti    = makeValueLabel(new Color(220, 80, 50));
    private final JLabel lblNettoVendita   = makeValueLabel(new Color(200, 160, 30));
    private final JLabel lblGuadagno       = makeValueLabel(new Color(0, 180, 100));
    private final JLabel lblBaseDogana     = makeValueLabel(new Color(130, 130, 180));
    private final JLabel lblNArticoli      = makeValueLabel(new Color(100, 160, 220));

    public SummaryPanel() {
        setLayout(new GridLayout(1, 5, 10, 0));
        setBorder(new EmptyBorder(0, 0, 12, 0));

        add(creaCard("TOTALE COSTI", lblTotaleCosti, new Color(220, 80, 50)));
        add(creaCard("NETTO VENDITA", lblNettoVendita, new Color(200, 160, 30)));
        add(creaCard("GUADAGNO NETTO", lblGuadagno, new Color(0, 180, 100)));
        add(creaCard("BASE DOGANALE", lblBaseDogana, new Color(130, 130, 180)));
        add(creaCard("N° ARTICOLI", lblNArticoli, new Color(100, 160, 220)));
    }

    private JPanel creaCard(String titolo, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, accentColor),
                new EmptyBorder(10, 14, 12, 14)
        ));
        card.setBackground(UIManager.getColor("Panel.background"));

        JLabel lblTitolo = new JLabel(titolo);
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 9f));
        lblTitolo.setForeground(UIManager.getColor("Label.disabledForeground"));

        card.add(lblTitolo, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private static JLabel makeValueLabel(Color color) {
        JLabel l = new JLabel("—");
        l.setFont(l.getFont().deriveFont(Font.BOLD, 20f));
        l.setForeground(color);
        return l;
    }

    public void aggiorna(TotaliSessione t) {
        lblTotaleCosti.setText(FormatUtils.eur(t.getTotaleCostiEur()));
        lblNettoVendita.setText(t.getArticoliConVendita() > 0
                ? FormatUtils.eur(t.getTotaleNettoVenditaEur()) : "—");

        double guad = t.getTotaleGuadagnoNettoEur();
        lblGuadagno.setText(t.getArticoliConVendita() > 0
                ? FormatUtils.guadagno(guad) : "—");
        lblGuadagno.setForeground(guad >= 0 ? new Color(0, 180, 100) : new Color(210, 50, 80));

        lblBaseDogana.setText(FormatUtils.eur(t.getTotaleBaseDoganaEur()));
        lblNArticoli.setText(String.valueOf(t.getArticoliTotali()));
    }

    public void reset() {
        lblTotaleCosti.setText("—");
        lblNettoVendita.setText("—");
        lblGuadagno.setText("—");
        lblBaseDogana.setText("—");
        lblNArticoli.setText("0");
    }
}
