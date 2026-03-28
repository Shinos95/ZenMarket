package com.zenmarket.ui;

import com.zenmarket.model.Articolo;
import com.zenmarket.model.Marketplace;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialogo per aggiungere o modificare un articolo.
 * Usato sia per l'inserimento nuovo che per la modifica di una riga esistente.
 */
public class ArticoloDialog extends JDialog {

    private boolean confermato = false;

    private final JTextField campoNome = new JTextField(24);
    private final JComboBox<Marketplace> campoMarketplace = new JComboBox<>(Marketplace.values());
    private final JSpinner campoQta = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final JSpinner campoPeso = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 50000.0, 100.0));
    private final JFormattedTextField campoCosto = new JFormattedTextField();
    private final JFormattedTextField campoVendita = new JFormattedTextField();
    private final JSpinner campoGiorniStoccaggio = new JSpinner(new SpinnerNumberModel(0, 0, 365, 1));
    private final JCheckBox campoFoto = new JCheckBox("Richiedi foto articolo (+500 ¥)");
    private final JTextArea campoNote = new JTextArea(3, 24);

    public ArticoloDialog(Frame owner, String titolo, Articolo articoloDaModificare) {
        super(owner, titolo, true);
        buildUI();
        if (articoloDaModificare != null) {
            popolaCampi(articoloDaModificare);
        }
        pack();
        setMinimumSize(new Dimension(460, 0));
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(16, 20, 8, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Nome prodotto
        aggiungiRiga(panel, gbc, row++, "Nome prodotto *", campoNome);

        // Marketplace
        aggiungiRiga(panel, gbc, row++, "Marketplace", campoMarketplace);

        // Info tariffa dinamica
        JLabel labelTariffa = new JLabel();
        labelTariffa.setForeground(new Color(130, 90, 200));
        labelTariffa.setFont(labelTariffa.getFont().deriveFont(Font.ITALIC, 11f));
        campoMarketplace.addActionListener(e -> {
            Marketplace m = (Marketplace) campoMarketplace.getSelectedItem();
            if (m != null) {
                labelTariffa.setText("  → Commissione ZenMarket: " + m.getTariffaJpy() + " ¥");
            }
        });
        campoMarketplace.setSelectedIndex(0);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(labelTariffa, gbc);

        // Quantità
        aggiungiRiga(panel, gbc, row++, "N° articoli (pezzi)", campoQta);

        // Peso
        campoPeso.setEditor(new JSpinner.NumberEditor(campoPeso, "#.# g"));
        aggiungiRiga(panel, gbc, row++, "Peso totale pacco (g)", campoPeso);

        // Costo acquisto
        campoCosto.setColumns(10);
        campoCosto.setValue(0.0);
        aggiungiRiga(panel, gbc, row++, "Costo acquisto (€) *", campoCosto);

        JLabel notaCosto = new JLabel("  Prezzo pagato al venditore, già convertito in €");
        notaCosto.setForeground(Color.GRAY);
        notaCosto.setFont(notaCosto.getFont().deriveFont(10f));
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(notaCosto, gbc);

        // Netto vendita
        campoVendita.setColumns(10);
        campoVendita.setValue(0.0);
        aggiungiRiga(panel, gbc, row++, "Netto vendita previsto (€)", campoVendita);

        JLabel notaVendita = new JLabel("  Lascia 0 se non ancora determinato");
        notaVendita.setForeground(Color.GRAY);
        notaVendita.setFont(notaVendita.getFont().deriveFont(10f));
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(notaVendita, gbc);

        // Stoccaggio extra
        aggiungiRiga(panel, gbc, row++, "Giorni stoccaggio extra (>60 giorni free)", campoGiorniStoccaggio);

        // Foto
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(campoFoto, gbc);

        // Note
        campoNote.setLineWrap(true);
        campoNote.setWrapStyleWord(true);
        campoNote.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        aggiungiRiga(panel, gbc, row++, "Note", new JScrollPane(campoNote));

        // Pulsanti
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnOk = new JButton("Aggiungi / Salva");
        btnOk.setBackground(new Color(70, 130, 180));
        btnOk.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Annulla");

        btnOk.addActionListener(e -> {
            if (valida()) {
                confermato = true;
                dispose();
            }
        });
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnCancel);
        btnPanel.add(btnOk);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnOk);
    }

    private void aggiungiRiga(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label + ": "), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comp, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }

    private boolean valida() {
        if (campoNome.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Inserisci il nome del prodotto.", "Errore", JOptionPane.WARNING_MESSAGE);
            campoNome.requestFocus();
            return false;
        }
        try {
            double costo = Double.parseDouble(campoCosto.getText().replace(',', '.').trim());
            if (costo <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Inserisci un costo di acquisto valido (>0).", "Errore", JOptionPane.WARNING_MESSAGE);
            campoCosto.requestFocus();
            return false;
        }
        return true;
    }

    private void popolaCampi(Articolo a) {
        campoNome.setText(a.getNome());
        campoMarketplace.setSelectedItem(a.getMarketplace());
        campoQta.setValue(a.getQuantita());
        campoPeso.setValue(a.getPesoTotaleGrammi());
        campoCosto.setValue(a.getCostoAcquistoEur());
        campoVendita.setValue(a.getNettoVenditaEur());
        campoGiorniStoccaggio.setValue(a.getGiorniStoccaggio());
        campoFoto.setSelected(a.isFotoRichiesta());
        campoNote.setText(a.getNote() != null ? a.getNote() : "");
    }

    /** Restituisce un nuovo Articolo con i valori inseriti. Chiamare solo se {@link #isConfermato()} == true */
    public Articolo getArticolo() {
        Articolo a = new Articolo();
        a.setNome(campoNome.getText().trim());
        a.setMarketplace((Marketplace) campoMarketplace.getSelectedItem());
        a.setQuantita((Integer) campoQta.getValue());
        a.setPesoTotaleGrammi(((Number) campoPeso.getValue()).doubleValue());
        try {
            a.setCostoAcquistoEur(Double.parseDouble(campoCosto.getText().replace(',', '.').trim()));
        } catch (NumberFormatException ex) { a.setCostoAcquistoEur(0); }
        try {
            a.setNettoVenditaEur(Double.parseDouble(campoVendita.getText().replace(',', '.').trim()));
        } catch (NumberFormatException ex) { a.setNettoVenditaEur(0); }
        a.setGiorniStoccaggio((Integer) campoGiorniStoccaggio.getValue());
        a.setFotoRichiesta(campoFoto.isSelected());
        a.setNote(campoNote.getText().trim());
        return a;
    }

    public boolean isConfermato() { return confermato; }
}
