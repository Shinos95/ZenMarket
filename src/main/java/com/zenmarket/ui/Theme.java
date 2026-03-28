package com.zenmarket.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Tema dark centralizzato — ispirato all'HTML generato in precedenza.
 * Modifica qui colori, font e spaziature senza toccare i componenti.
 */
public final class Theme {

    private Theme() {}

    // ─── Palette ───────────────────────────────────────────────────────────
    public static final Color BG           = new Color(0x0a0a0f);
    public static final Color SURFACE      = new Color(0x13131a);
    public static final Color SURFACE2     = new Color(0x1c1c28);
    public static final Color BORDER       = new Color(0x2a2a3a);
    public static final Color ACCENT       = new Color(0xff5c35);   // arancio-rosso
    public static final Color ACCENT2      = new Color(0xffb800);   // giallo-oro
    public static final Color GREEN        = new Color(0x00e896);
    public static final Color RED          = new Color(0xff3b6b);
    public static final Color MUTED        = new Color(0x7070a0);
    public static final Color TEXT         = new Color(0xf0f0f5);
    public static final Color TEXT_DIM     = new Color(0xaaaacc);

    // Card accent bars
    public static final Color CARD_COST    = ACCENT;
    public static final Color CARD_REV     = ACCENT2;
    public static final Color CARD_PROFIT  = GREEN;
    public static final Color CARD_DOGANA  = MUTED;
    public static final Color CARD_COUNT   = new Color(0x5599dd);

    // Table rows
    public static final Color ROW_EVEN     = SURFACE;
    public static final Color ROW_ODD      = SURFACE2;
    public static final Color ROW_SELECTED = new Color(0x2a2a50);
    public static final Color GRID_COLOR   = new Color(0x22223a);

    // ─── Font ──────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 10);
    public static final Font FONT_MONO    = new Font("JetBrains Mono", Font.PLAIN, 12) ;
    public static final Font FONT_CARD_VAL= new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_CARD_LBL= new Font("Segoe UI", Font.BOLD, 9);
    public static final Font FONT_TABLE   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD, 10);
    public static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD, 12);

    // ─── Borders ──────────────────────────────────────────────────────────
    public static Border borderSurface() {
        return BorderFactory.createLineBorder(BORDER, 1);
    }
    public static Border borderAccent() {
        return BorderFactory.createLineBorder(ACCENT, 1);
    }
    public static Border padded(int v, int h) {
        return BorderFactory.createEmptyBorder(v, h, v, h);
    }

    // ─── UIManager setup ──────────────────────────────────────────────────
    /**
     * Applica il tema dark a tutti i componenti Swing via UIManager.
     * Chiamare PRIMA di creare qualsiasi componente.
     */
    public static void apply() {
        UIManager.put("Panel.background",          SURFACE);
        UIManager.put("Frame.background",          BG);
        UIManager.put("Label.foreground",          TEXT);
        UIManager.put("Label.disabledForeground",  MUTED);
        UIManager.put("Button.background",         SURFACE2);
        UIManager.put("Button.foreground",         TEXT);
        UIManager.put("Button.border",             BorderFactory.createLineBorder(BORDER));
        UIManager.put("TextField.background",      SURFACE2);
        UIManager.put("TextField.foreground",      TEXT);
        UIManager.put("TextField.caretForeground", TEXT);
        UIManager.put("TextField.border",          BorderFactory.createLineBorder(BORDER));
        UIManager.put("FormattedTextField.background",      SURFACE2);
        UIManager.put("FormattedTextField.foreground",      TEXT);
        UIManager.put("FormattedTextField.caretForeground", TEXT);
        UIManager.put("FormattedTextField.border",          BorderFactory.createLineBorder(BORDER));
        UIManager.put("Spinner.background",        SURFACE2);
        UIManager.put("Spinner.foreground",        TEXT);
        UIManager.put("ComboBox.background",       SURFACE2);
        UIManager.put("ComboBox.foreground",       TEXT);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("List.background",           SURFACE2);
        UIManager.put("List.foreground",           TEXT);
        UIManager.put("List.selectionBackground",  ACCENT);
        UIManager.put("Table.background",          SURFACE);
        UIManager.put("Table.foreground",          TEXT);
        UIManager.put("Table.selectionBackground", ROW_SELECTED);
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("Table.gridColor",           GRID_COLOR);
        UIManager.put("TableHeader.background",    SURFACE2);
        UIManager.put("TableHeader.foreground",    MUTED);
        UIManager.put("ScrollPane.background",     SURFACE);
        UIManager.put("ScrollBar.background",      SURFACE2);
        UIManager.put("ScrollBar.thumb",           new Color(0x3a3a5a));
        UIManager.put("ScrollBar.thumbHighlight",  MUTED);
        UIManager.put("MenuBar.background",        SURFACE2);
        UIManager.put("MenuBar.foreground",        TEXT);
        UIManager.put("Menu.background",           SURFACE2);
        UIManager.put("Menu.foreground",           TEXT);
        UIManager.put("MenuItem.background",       SURFACE2);
        UIManager.put("MenuItem.foreground",       TEXT);
        UIManager.put("MenuItem.selectionBackground", ACCENT);
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);
        UIManager.put("PopupMenu.background",      SURFACE2);
        UIManager.put("PopupMenu.border",          BorderFactory.createLineBorder(BORDER));
        UIManager.put("Separator.foreground",      BORDER);
        UIManager.put("ToolTip.background",        SURFACE2);
        UIManager.put("ToolTip.foreground",        TEXT);
        UIManager.put("OptionPane.background",     SURFACE);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("TextArea.background",       SURFACE2);
        UIManager.put("TextArea.foreground",       TEXT);
        UIManager.put("TextArea.caretForeground",  TEXT);
        UIManager.put("CheckBox.background",       SURFACE);
        UIManager.put("CheckBox.foreground",       TEXT);
        UIManager.put("TitledBorder.titleColor",   MUTED);
        UIManager.put("TitledBorder.border",       BorderFactory.createLineBorder(BORDER));
    }

    // ─── Factory helpers ──────────────────────────────────────────────────

    /** Crea un pulsante styled con colore accent specifico */
    public static JButton accentButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        // Hover effect
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            final Color orig = bg;
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(orig.brighter());
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(orig);
            }
        });
        return b;
    }

    /** Label piccola stile "tag" con lettering maiuscolo */
    public static JLabel tagLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(FONT_CARD_LBL);
        l.setForeground(MUTED);
        return l;
    }
}
