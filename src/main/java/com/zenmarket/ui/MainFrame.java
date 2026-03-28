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
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Finestra principale — dark theme, layout responsivo, multi-selezione,
 * auto-save alla chiusura e al riavvio.
 */
public class MainFrame extends JFrame {

    // ─── Servizi ───────────────────────────────────────────────────────────
    private final CalcolatoreService calcolatore = new CalcolatoreService();
    private final SessioneService    sessione    = new SessioneService();

    // ─── Tabella ──────────────────────────────────────────────────────────
    private final ArticoloTableModel tableModel = new ArticoloTableModel();
    private final JTable             tabella    = new JTable(tableModel);

    // ─── Componenti UI principali ─────────────────────────────────────────
    private final SummaryPanel summaryPanel = new SummaryPanel();
    private final JLabel       lblStatus    = new JLabel(" ");
    private final JLabel       lblAutoSave  = new JLabel();

    // ─── Cambio JPY → EUR ─────────────────────────────────────────────────
    private final SpinnerNumberModel cambioModel =
            new SpinnerNumberModel(AppConfig.DEFAULT_CAMBIO_JPY_EUR, 0.0001, 1.0, 0.0001);
    private final JSpinner spinCambio = new JSpinner(cambioModel);

    // ─── Pulsanti toolbar ─────────────────────────────────────────────────
    private JButton btnModifica;
    private JButton btnRimuovi;

    // ─────────────────────────────────────────────────────────────────────

    public MainFrame() {
        super("Calcolatore Zenmarket × Mercari");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        buildUI();
        configuraTabella();
        buildMenu();
        registraChiusura();

        // Carica auto-save o esempi
        boolean daAutoSave = sessione.caricaAutoSaveOEsempi();
        ricalcola();
        if (daAutoSave) {
            setStatus("Sessione ripristinata (" + sessione.size() + " articoli)", Theme.GREEN);
        } else {
            setStatus("Benvenuto! Dati di esempio caricati.", Theme.ACCENT2);
        }
        lblAutoSave.setText("Auto-save: " + sessione.getAutoSavePath());

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
    }

    // ─── Costruzione UI ────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        getContentPane().setBackground(Theme.BG);
        setContentPane(root);
    }

    // ── Header ────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(Theme.SURFACE2);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
            new EmptyBorder(12, 20, 12, 20)
        ));

        // Titolo + sottotitolo
        JPanel titleBlock = new JPanel(new BorderLayout(0, 2));
        titleBlock.setOpaque(false);

        JLabel lblTag = new JLabel("CALCOLATORE ACQUISTI JP");
        lblTag.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD, 9f));
        lblTag.setForeground(Theme.ACCENT);
        lblTag.setBorder(new EmptyBorder(0, 0, 2, 0));

        JLabel lblTitolo = new JLabel("Zenmarket × Mercari");
        lblTitolo.setFont(Theme.FONT_TITLE.deriveFont(22f));
        lblTitolo.setForeground(Theme.TEXT);

        titleBlock.add(lblTag,    BorderLayout.NORTH);
        titleBlock.add(lblTitolo, BorderLayout.CENTER);
        header.add(titleBlock, BorderLayout.WEST);

        // Destra: cambio + pulsanti
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        // Spinner cambio
        JPanel cambioBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        cambioBox.setOpaque(false);
        cambioBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.ACCENT2, 1),
            new EmptyBorder(4, 10, 4, 10)
        ));
        JLabel lblCambio = new JLabel("¥ → €");
        lblCambio.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        lblCambio.setForeground(Theme.ACCENT2);
        spinCambio.setEditor(new JSpinner.NumberEditor(spinCambio, "0.00000"));
        spinCambio.setPreferredSize(new Dimension(110, 26));
        spinCambio.setBackground(Theme.SURFACE2);
        spinCambio.setForeground(Theme.ACCENT2);
        ((JSpinner.NumberEditor) spinCambio.getEditor()).getTextField()
            .setFont(Theme.FONT_TABLE.deriveFont(Font.BOLD, 14f));
        ((JSpinner.NumberEditor) spinCambio.getEditor()).getTextField()
            .setForeground(Theme.ACCENT2);
        spinCambio.addChangeListener(e -> ricalcola());
        cambioBox.add(lblCambio);
        cambioBox.add(spinCambio);

        // Pulsanti azione
        JButton btnAggiungi = Theme.accentButton("＋  Aggiungi", Theme.ACCENT);
        btnAggiungi.addActionListener(e -> apriDialogAggiungi());
        btnAggiungi.setToolTipText("Aggiungi un nuovo articolo (Ins)");

        btnModifica = Theme.accentButton("✎  Modifica", new Color(0x4488cc));
        btnModifica.addActionListener(e -> modificaSelezionato());
        btnModifica.setEnabled(false);
        btnModifica.setToolTipText("Modifica articolo selezionato (F2 o doppio click)");

        btnRimuovi = Theme.accentButton("✕  Rimuovi", Theme.RED);
        btnRimuovi.addActionListener(e -> rimuoviSelezionati());
        btnRimuovi.setEnabled(false);
        btnRimuovi.setToolTipText("Rimuovi articolo/i selezionati (Canc)");

        right.add(cambioBox);
        right.add(Box.createHorizontalStrut(6));
        right.add(btnAggiungi);
        right.add(btnModifica);
        right.add(btnRimuovi);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Centro: summary + tabella + legenda ───────────────────────────────

    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Theme.BG);
        panel.setBorder(new EmptyBorder(16, 20, 0, 20));

        panel.add(summaryPanel,     BorderLayout.NORTH);
        panel.add(buildTablePanel(), BorderLayout.CENTER);
        panel.add(buildLegenda(),   BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.SURFACE);
        panel.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        // Intestazione tabella
        JPanel tHeader = new JPanel(new BorderLayout());
        tHeader.setBackground(Theme.SURFACE2);
        tHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
            new EmptyBorder(8, 16, 8, 16)
        ));
        JLabel lblTable = new JLabel("Articoli");
        lblTable.setFont(Theme.FONT_TABLE.deriveFont(Font.BOLD, 13f));
        lblTable.setForeground(Theme.TEXT);
        tHeader.add(lblTable, BorderLayout.WEST);

        JLabel hint = new JLabel("Ctrl+Click = multi-selezione  |  Shift+Click = range  |  F2 = modifica  |  Canc = rimuovi");
        hint.setFont(Theme.FONT_SMALL);
        hint.setForeground(Theme.MUTED);
        tHeader.add(hint, BorderLayout.EAST);

        panel.add(tHeader, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabella);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Theme.SURFACE);
        scroll.getVerticalScrollBar().setBackground(Theme.SURFACE2);
        scroll.getHorizontalScrollBar().setBackground(Theme.SURFACE2);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLegenda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        p.setBackground(Theme.SURFACE2);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
            new EmptyBorder(4, 16, 4, 16)
        ));

        String[] voci = {
            "⚠ Base Dogana > €150 → possibili dazi doganali",
            "⚠ Spedizione N/D → peso < 200g, contattare ZenMarket",
            "Mercari privati: 800¥  |  Standard: 500¥  |  ZenPlus: 300¥",
            "Deposito: 3,5% (carta/PayPal)  |  SWIFT: ~4.500¥"
        };
        for (String v : voci) {
            JLabel l = new JLabel(v);
            l.setFont(Theme.FONT_SMALL);
            l.setForeground(Theme.MUTED);
            p.add(l);
        }
        return p;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(0x0d0d14));
        bar.setBorder(new EmptyBorder(4, 20, 4, 20));

        lblStatus.setFont(Theme.FONT_SMALL);
        lblStatus.setForeground(Theme.MUTED);
        bar.add(lblStatus, BorderLayout.WEST);

        lblAutoSave.setFont(Theme.FONT_SMALL.deriveFont(9f));
        lblAutoSave.setForeground(new Color(0x44445a));
        bar.add(lblAutoSave, BorderLayout.EAST);
        return bar;
    }

    // ─── Menu ──────────────────────────────────────────────────────────────

    private void buildMenu() {
        JMenuBar mb = new JMenuBar();
        mb.setBackground(Theme.SURFACE2);
        mb.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        JMenu mFile = new JMenu("File");
        addMenuItem(mFile, "Nuova sessione",   "Ctrl+N",  e -> nuovaSessione());
        addMenuItem(mFile, "Apri…",            "Ctrl+O",  e -> apriFile());
        addMenuItem(mFile, "Salva…",           "Ctrl+S",  e -> salvaFile());
        mFile.addSeparator();
        addMenuItem(mFile, "Esci",             null,      e -> chiudiApp());

        JMenu mModifica = new JMenu("Modifica");
        addMenuItem(mModifica, "Aggiungi articolo",    "Ins",    e -> apriDialogAggiungi());
        addMenuItem(mModifica, "Modifica selezionato", "F2",     e -> modificaSelezionato());
        addMenuItem(mModifica, "Rimuovi selezionati",  "Canc",   e -> rimuoviSelezionati());
        mModifica.addSeparator();
        addMenuItem(mModifica, "Carica esempi", null, e -> caricaEsempi());

        JMenu mAiuto = new JMenu("Aiuto");
        addMenuItem(mAiuto, "Tariffe ZenMarket", null, e -> mostraInfoTariffe());

        mb.add(mFile);
        mb.add(mModifica);
        mb.add(mAiuto);
        setJMenuBar(mb);

        // Scorciatoie globali
        getRootPane().registerKeyboardAction(e -> apriDialogAggiungi(),
            KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> modificaSelezionato(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> rimuoviSelezionati(),
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void addMenuItem(JMenu menu, String text, String shortcut, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        if (shortcut != null) {
            item.setAccelerator(KeyStroke.getKeyStroke(shortcut.replace("Ctrl+", "control ").replace("Ins", "INSERT").replace("F2", "F2").replace("Canc", "DELETE")));
        }
        item.addActionListener(action);
        menu.add(item);
    }

    // ─── Tabella ──────────────────────────────────────────────────────────

    private void configuraTabella() {
        // Multi-selezione con Ctrl e Shift
        tabella.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabella.setBackground(Theme.SURFACE);
        tabella.setForeground(Theme.TEXT);
        tabella.setGridColor(Theme.GRID_COLOR);
        tabella.setRowHeight(30);
        tabella.setShowGrid(true);
        tabella.setIntercellSpacing(new Dimension(0, 1));
        tabella.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tabella.getTableHeader().setReorderingAllowed(false);
        tabella.setFillsViewportHeight(true);

        // Header dark
        JTableHeader header = tabella.getTableHeader();
        header.setBackground(Theme.SURFACE2);
        header.setForeground(Theme.MUTED);
        header.setFont(Theme.FONT_HEADER);
        header.setPreferredSize(new Dimension(0, 32));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        // Renderer colori dark
        tabella.setDefaultRenderer(Object.class, new DarkTableRenderer());
        tabella.setDefaultRenderer(Integer.class, new DarkTableRenderer());

        // Larghezze colonne proporzionali
        impostaLarghezzeColonne();

        // Double-click → modifica
        tabella.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabella.getSelectedRowCount() == 1) {
                    modificaSelezionato();
                }
            }
        });

        // Aggiorna pulsanti in base alla selezione
        tabella.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) aggiornaStatoPulsanti();
        });

        // Context menu tasto destro
        tabella.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) mostraContextMenu(e);
            }
            @Override public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) mostraContextMenu(e);
            }
        });
    }

    private void impostaLarghezzeColonne() {
        // Larghezze minime preferite; AUTO_RESIZE_LAST_COLUMN gestirà il resto
        int[] widths = { 180, 150, 42, 75, 82, 72, 82, 88, 88, 72, 95, 98, 98, 95, 80 };
        TableColumnModel cm = tabella.getColumnModel();
        for (int i = 0; i < Math.min(widths.length, cm.getColumnCount()); i++) {
            cm.getColumn(i).setMinWidth(widths[i]);
            cm.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void mostraContextMenu(MouseEvent e) {
        int row = tabella.rowAtPoint(e.getPoint());
        if (row >= 0 && !tabella.isRowSelected(row)) {
            tabella.setRowSelectionInterval(row, row);
        }
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(Theme.SURFACE2);

        JMenuItem mAgg = new JMenuItem("＋ Aggiungi nuovo");
        mAgg.addActionListener(x -> apriDialogAggiungi());
        menu.add(mAgg);
        menu.addSeparator();

        if (tabella.getSelectedRowCount() == 1) {
            JMenuItem mMod = new JMenuItem("✎ Modifica");
            mMod.addActionListener(x -> modificaSelezionato());
            menu.add(mMod);
        }

        JMenuItem mDel = new JMenuItem("✕ Rimuovi selezionati (" + tabella.getSelectedRowCount() + ")");
        mDel.setForeground(Theme.RED);
        mDel.addActionListener(x -> rimuoviSelezionati());
        menu.add(mDel);

        menu.show(tabella, e.getX(), e.getY());
    }

    // ─── Calcolo ──────────────────────────────────────────────────────────

    private void ricalcola() {
        double cambio = (double) spinCambio.getValue();
        List<RisultatoCalcolo> risultati = new ArrayList<>();
        for (Articolo a : sessione.getArticoli()) {
            risultati.add(calcolatore.calcola(a, cambio));
        }
        tableModel.setRighe(risultati);
        if (!risultati.isEmpty()) {
            summaryPanel.aggiorna(calcolatore.calcolaTotali(risultati));
        } else {
            summaryPanel.reset();
        }
        aggiornaStatoPulsanti();
        // Auto-save ad ogni modifica
        sessione.autoSalva();
    }

    private void aggiornaStatoPulsanti() {
        int sel = tabella.getSelectedRowCount();
        btnModifica.setEnabled(sel == 1);
        btnRimuovi.setEnabled(sel > 0);
        if (sel > 1) {
            btnRimuovi.setText("✕  Rimuovi (" + sel + ")");
        } else {
            btnRimuovi.setText("✕  Rimuovi");
        }
    }

    // ─── Azioni ───────────────────────────────────────────────────────────

    private void apriDialogAggiungi() {
        ArticoloDialog dialog = new ArticoloDialog(this, "Aggiungi articolo", null);
        dialog.setVisible(true);
        if (dialog.isConfermato()) {
            sessione.aggiungi(dialog.getArticolo());
            ricalcola();
            int lastRow = tabella.getRowCount() - 1;
            tabella.setRowSelectionInterval(lastRow, lastRow);
            tabella.scrollRectToVisible(tabella.getCellRect(lastRow, 0, true));
            setStatus("Articolo aggiunto: " + dialog.getArticolo().getNome(), Theme.GREEN);
        }
    }

    private void modificaSelezionato() {
        int idx = tabella.getSelectedRow();
        if (idx < 0) return;
        Articolo corrente = sessione.get(idx);
        ArticoloDialog dialog = new ArticoloDialog(this, "Modifica articolo", corrente);
        dialog.setVisible(true);
        if (dialog.isConfermato()) {
            sessione.aggiorna(idx, dialog.getArticolo());
            ricalcola();
            tabella.setRowSelectionInterval(idx, idx);
            setStatus("Articolo aggiornato: " + dialog.getArticolo().getNome(), Theme.ACCENT2);
        }
    }

    private void rimuoviSelezionati() {
        int[] selectedRows = tabella.getSelectedRows();
        if (selectedRows.length == 0) return;

        String msg = selectedRows.length == 1
            ? "Rimuovere \"" + sessione.get(selectedRows[0]).getNome() + "\"?"
            : "Rimuovere " + selectedRows.length + " articoli selezionati?";

        int r = JOptionPane.showConfirmDialog(this, msg, "Conferma rimozione",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            List<Integer> indici = Arrays.stream(selectedRows).boxed().collect(Collectors.toList());
            sessione.rimuoviIndici(indici);
            ricalcola();
            setStatus(selectedRows.length + " articol" + (selectedRows.length == 1 ? "o rimosso" : "i rimossi"), Theme.RED);
        }
    }

    private void nuovaSessione() {
        int r = JOptionPane.showConfirmDialog(this,
                "Cancellare tutti gli articoli e iniziare una nuova sessione?",
                "Nuova sessione", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            sessione.svuota();
            ricalcola();
            setStatus("Nuova sessione avviata.", Theme.MUTED);
        }
    }

    private void caricaEsempi() {
        int r = JOptionPane.showConfirmDialog(this,
                "Sostituire gli articoli attuali con i dati di esempio?",
                "Carica esempi", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            sessione.caricaEsempi();
            ricalcola();
            setStatus("Dati di esempio caricati.", Theme.ACCENT2);
        }
    }

    private void apriFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON (*.json)", "json"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                sessione.carica(fc.getSelectedFile());
                ricalcola();
                setStatus("File caricato: " + fc.getSelectedFile().getName(), Theme.GREEN);
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
                setStatus("Sessione salvata: " + f.getName(), Theme.GREEN);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore salvataggio:\n" + ex.getMessage(),
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostraInfoTariffe() {
        String msg = """
                TARIFFE ZENMARKET (2025)
                ────────────────────────────────────────
                Mercari (venditori privati)      800 ¥ / articolo
                Mercari Shops / Amazon JP / Rakuten  500 ¥ / articolo
                ZenPlus / Negozi partner         300 ¥ / articolo

                COMMISSIONE DEPOSITO FONDI
                ────────────────────────────────────────
                Carta di credito / PayPal        3,5%
                Bonifico bancario SWIFT          ~4.500 ¥

                STOCCAGGIO
                ────────────────────────────────────────
                Primi 60 giorni                  GRATUITO
                Oltre il 60° giorno              50 ¥ / giorno per articolo

                SOGLIA DOGANALE ITALIA
                ────────────────────────────────────────
                Valore dichiarato > €150  →  possibili dazi doganali
                                                                 
                Fonte: zenmarket.jp/it/fees.aspx
                """;
        JOptionPane.showMessageDialog(this, msg, "Info Tariffe ZenMarket", JOptionPane.INFORMATION_MESSAGE);
    }

    // ─── Chiusura ─────────────────────────────────────────────────────────

    private void registraChiusura() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                chiudiApp();
            }
        });
    }

    private void chiudiApp() {
        sessione.autoSalva();
        dispose();
        System.exit(0);
    }

    // ─── Utility ──────────────────────────────────────────────────────────

    private void setStatus(String msg, Color color) {
        lblStatus.setText(msg);
        lblStatus.setForeground(color);
        // Torna a muted dopo 4 secondi
        Timer t = new Timer(4000, e -> {
            lblStatus.setText(" ");
            lblStatus.setForeground(Theme.MUTED);
        });
        t.setRepeats(false);
        t.start();
    }
}
