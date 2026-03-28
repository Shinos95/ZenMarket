package com.zenmarket.ui;

import com.zenmarket.config.AppConfig;
import com.zenmarket.model.Articolo;
import com.zenmarket.model.RisultatoCalcolo;
import com.zenmarket.model.TotaliSessione;
import com.zenmarket.service.CalcolatoreService;
import com.zenmarket.service.SessioneService;
import com.zenmarket.util.FormatUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Finestra principale dell'applicazione.
 * Coordina UI, servizi e aggiornamenti della tabella.
 */
public class MainFrame extends JFrame {

    private final CalcolatoreService calcolatore = new CalcolatoreService();
    private final SessioneService sessione = new SessioneService();

    private final ArticoloTableModel tableModel = new ArticoloTableModel();
    private final JTable tabella = new JTable(tableModel);
    private final SummaryPanel summaryPanel = new SummaryPanel();

    // Cambio JPY → EUR
    private final SpinnerNumberModel cambiModel =
            new SpinnerNumberModel(AppConfig.DEFAULT_CAMBIO_JPY_EUR, 0.001, 1.0, 0.0001);
    private final JSpinner spinCambio = new JSpinner(cambiModel);

    // Label info tariffa corrente
    private final JLabel lblTariffaInfo = new JLabel();

    public MainFrame() {
        super("Calcolatore Zenmarket × Mercari");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 700));
        buildUI();
        sessione.caricaEsempi();
        ricalcola();
        pack();
        setLocationRelativeTo(null);
    }

    // ─── Costruzione UI ────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(14, 16, 14, 16));

        root.add(buildToolbar(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
        buildMenu();
        configuraTabella();
    }

    private JPanel buildToolbar() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));

        // Titolo
        JLabel titolo = new JLabel("Calcolatore Zenmarket × Mercari");
        titolo.setFont(titolo.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(titolo, BorderLayout.WEST);

        // Cambio + pulsanti
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        // Cambio JPY
        right.add(new JLabel("¥ → € :"));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinCambio, "0.00000");
        spinCambio.setEditor(editor);
        spinCambio.setPreferredSize(new Dimension(100, 26));
        spinCambio.addChangeListener(e -> ricalcola());
        right.add(spinCambio);

        lblTariffaInfo.setFont(lblTariffaInfo.getFont().deriveFont(Font.ITALIC, 11f));
        lblTariffaInfo.setForeground(new Color(120, 120, 150));
        right.add(lblTariffaInfo);

        // Pulsanti azione
        JButton btnAggiungi = toolbar_btn("+ Aggiungi", new Color(60, 140, 60));
        btnAggiungi.addActionListener(e -> apriDialogAggiungi());

        JButton btnModifica = toolbar_btn("✎ Modifica", new Color(70, 130, 180));
        btnModifica.addActionListener(e -> modificaSelezionato());

        JButton btnRimuovi = toolbar_btn("✕ Rimuovi", new Color(200, 60, 60));
        btnRimuovi.addActionListener(e -> rimuoviSelezionato());

        JButton btnEsempi = toolbar_btn("⟳ Esempi", new Color(120, 80, 160));
        btnEsempi.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "Sostituire gli articoli attuali con i dati di esempio?",
                    "Carica esempi", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                sessione.caricaEsempi();
                ricalcola();
            }
        });

        right.add(Box.createHorizontalStrut(10));
        right.add(btnAggiungi);
        right.add(btnModifica);
        right.add(btnRimuovi);
        right.add(Box.createHorizontalStrut(4));
        right.add(btnEsempi);

        panel.add(right, BorderLayout.EAST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.add(new JSeparator(), BorderLayout.SOUTH);
        wrapper.setBorder(new EmptyBorder(0, 0, 10, 0));
        return wrapper;
    }

    private JButton toolbar_btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.add(summaryPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabella);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210)));
        panel.add(scroll, BorderLayout.CENTER);

        // Legenda
        panel.add(buildLegenda(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLegenda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        p.setBorder(BorderFactory.createTitledBorder("Note"));
        p.setBackground(new Color(248, 248, 252));

        String[] voci = {
            "⚠ Base Dogana — Se supera €150 potrebbero applicarsi dazi doganali",
            "⚠ N/D Spedizione — Peso < 200g: contattare ZenMarket",
            "Tariffa Mercari privati: 800 ¥/art. | Standard: 500 ¥/art. | ZenPlus: 300 ¥/art.",
            "Deposito fondi (carta/PayPal): 3,5% | Bonifico SWIFT: ~4.500 ¥"
        };
        for (String v : voci) {
            JLabel l = new JLabel(v);
            l.setFont(l.getFont().deriveFont(10f));
            l.setForeground(new Color(90, 90, 110));
            p.add(l);
        }
        return p;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lbl = new JLabel("Doppio click su una riga per modificarla  |  Dati fonte: zenmarket.jp/it/fees.aspx");
        lbl.setFont(lbl.getFont().deriveFont(10f));
        lbl.setForeground(Color.GRAY);
        p.add(lbl);
        return p;
    }

    private void buildMenu() {
        JMenuBar mb = new JMenuBar();

        JMenu mFile = new JMenu("File");
        JMenuItem mNuova = new JMenuItem("Nuova sessione");
        mNuova.addActionListener(e -> nuovaSessione());
        JMenuItem mApri = new JMenuItem("Apri…");
        mApri.addActionListener(e -> apriFile());
        JMenuItem mSalva = new JMenuItem("Salva…");
        mSalva.addActionListener(e -> salvaFile());
        JMenuItem mEsci = new JMenuItem("Esci");
        mEsci.addActionListener(e -> System.exit(0));
        mFile.add(mNuova); mFile.add(mApri); mFile.add(mSalva);
        mFile.addSeparator(); mFile.add(mEsci);

        JMenu mAiuto = new JMenu("Aiuto");
        JMenuItem mInfo = new JMenuItem("Tariffe ZenMarket");
        mInfo.addActionListener(e -> mostraInfoTariffe());
        mAiuto.add(mInfo);

        mb.add(mFile); mb.add(mAiuto);
        setJMenuBar(mb);
    }

    private void configuraTabella() {
        tabella.setRowHeight(24);
        tabella.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabella.getTableHeader().setReorderingAllowed(false);
        tabella.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabella.setShowGrid(true);
        tabella.setGridColor(new Color(220, 220, 230));

        // Doppio click → modifica
        tabella.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) modificaSelezionato();
            }
        });

        // Larghezze colonne
        int[] widths = { 160, 160, 40, 80, 80, 70, 80, 85, 85, 75, 90, 95, 95, 90, 75 };
        TableColumnModel cm = tabella.getColumnModel();
        for (int i = 0; i < Math.min(widths.length, cm.getColumnCount()); i++) {
            cm.getColumn(i).setPreferredWidth(widths[i]);
        }

        // Renderer colori
        tabella.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 247, 252));
                    String txt = value != null ? value.toString() : "";
                    ArticoloTableModel.Colonna colName = ArticoloTableModel.Colonna.values()[col];
                    switch (colName) {
                        case COSTO_ACQ, IVA -> c.setForeground(new Color(180, 60, 40));
                        case TOT_COSTI     -> { c.setForeground(new Color(180, 60, 40)); setFont(getFont().deriveFont(Font.BOLD)); }
                        case NETTO_VENDITA -> c.setForeground(new Color(160, 120, 0));
                        case GUADAGNO      -> {
                            if (txt.startsWith("+")) c.setForeground(new Color(0, 140, 80));
                            else if (txt.startsWith("-")) c.setForeground(new Color(200, 40, 40));
                            else c.setForeground(UIManager.getColor("Table.foreground"));
                            setFont(getFont().deriveFont(Font.BOLD));
                        }
                        case BASE_DOGANA   -> {
                            if (txt.contains("⚠")) c.setForeground(new Color(200, 100, 0));
                            else c.setForeground(new Color(100, 100, 150));
                        }
                        case SPEDIZIONE    -> {
                            if (txt.contains("N/D")) c.setForeground(new Color(200, 40, 40));
                            else c.setForeground(UIManager.getColor("Table.foreground"));
                        }
                        default -> c.setForeground(UIManager.getColor("Table.foreground"));
                    }
                }
                ((JLabel) c).setHorizontalAlignment(
                    col <= 1 ? SwingConstants.LEFT : SwingConstants.RIGHT);
                return c;
            }
        });
    }

    // ─── Logica di calcolo ─────────────────────────────────────────────────

    private void ricalcola() {
        double cambio = (double) spinCambio.getValue();
        List<RisultatoCalcolo> risultati = new ArrayList<>();
        for (Articolo a : sessione.getArticoli()) {
            risultati.add(calcolatore.calcola(a, cambio));
        }
        tableModel.setRighe(risultati);

        if (!risultati.isEmpty()) {
            TotaliSessione totali = calcolatore.calcolaTotali(risultati);
            summaryPanel.aggiorna(totali);
        } else {
            summaryPanel.reset();
        }

        // Aggiorna info cambio
        double zenMercari = AppConfig.ZENMARKET_FEE_MERCARI_JPY * cambio;
        lblTariffaInfo.setText(String.format(" (800¥ = %s | 500¥ = %s)",
                FormatUtils.eur(zenMercari),
                FormatUtils.eur(AppConfig.ZENMARKET_FEE_STANDARD_JPY * cambio)));
    }

    // ─── Azioni UI ─────────────────────────────────────────────────────────

    private void apriDialogAggiungi() {
        ArticoloDialog dialog = new ArticoloDialog(this, "Aggiungi articolo", null);
        dialog.setVisible(true);
        if (dialog.isConfermato()) {
            sessione.aggiungi(dialog.getArticolo());
            ricalcola();
        }
    }

    private void modificaSelezionato() {
        int idx = tabella.getSelectedRow();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Seleziona una riga da modificare.", "Nessuna selezione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Articolo corrente = sessione.get(idx);
        ArticoloDialog dialog = new ArticoloDialog(this, "Modifica articolo", corrente);
        dialog.setVisible(true);
        if (dialog.isConfermato()) {
            sessione.aggiorna(idx, dialog.getArticolo());
            ricalcola();
            tabella.setRowSelectionInterval(idx, idx);
        }
    }

    private void rimuoviSelezionato() {
        int idx = tabella.getSelectedRow();
        if (idx < 0) return;
        String nome = sessione.get(idx).getNome();
        int r = JOptionPane.showConfirmDialog(this,
                "Rimuovere \"" + nome + "\"?", "Conferma rimozione", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            sessione.rimuovi(idx);
            ricalcola();
        }
    }

    private void nuovaSessione() {
        int r = JOptionPane.showConfirmDialog(this,
                "Cancellare tutti gli articoli e iniziare una nuova sessione?",
                "Nuova sessione", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            sessione.svuota();
            ricalcola();
        }
    }

    private void apriFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON (*.json)", "json"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                sessione.carica(fc.getSelectedFile());
                ricalcola();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore apertura file:\n" + ex.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salvaFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON (*.json)", "json"));
        fc.setSelectedFile(new File("sessione_zenmarket.json"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().endsWith(".json")) f = new File(f.getAbsolutePath() + ".json");
            try {
                sessione.salva(f);
                JOptionPane.showMessageDialog(this, "Sessione salvata in:\n" + f.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore salvataggio:\n" + ex.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostraInfoTariffe() {
        String msg = """
                TARIFFE ZENMARKET (aggiornate 2025)
                ────────────────────────────────────────
                Mercari (venditori privati): 800 ¥/articolo
                Mercari Shops / Amazon JP / Rakuten: 500 ¥/articolo
                ZenPlus / Negozi consigliati: 300 ¥/articolo
                
                COMMISSIONE DEPOSITO FONDI
                ────────────────────────────────────────
                Carta di credito / PayPal: 3,5%
                Bonifico bancario SWIFT: ~4.500 ¥
                
                STOCCAGGIO
                ────────────────────────────────────────
                60 giorni GRATUITI
                Oltre: 50 ¥/giorno per articolo
                
                SOGLIA DOGANALE ITALIA
                ────────────────────────────────────────
                > €150 → possibili dazi doganali
                
                Fonte: zenmarket.jp/it/fees.aspx
                """;
        JOptionPane.showMessageDialog(this, msg, "Info Tariffe ZenMarket",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
