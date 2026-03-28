package com.zenmarket.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Renderer dark-themed per la tabella articoli.
 * Gestisce colori per colonna, righe alternate e selezione multipla.
 */
public class DarkTableRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int col) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        setFont(Theme.FONT_TABLE);

        if (isSelected) {
            setBackground(Theme.ROW_SELECTED);
            setForeground(Theme.TEXT);
            return this;
        }

        // Righe alternate
        setBackground(row % 2 == 0 ? Theme.ROW_EVEN : Theme.ROW_ODD);

        // Colore per colonna
        String txt = value != null ? value.toString() : "";
        ArticoloTableModel.Colonna colName = ArticoloTableModel.Colonna.values()[col];

        switch (colName) {
            case NOME -> {
                setForeground(Theme.TEXT);
                setFont(Theme.FONT_TABLE.deriveFont(Font.BOLD));
            }
            case MARKETPLACE -> {
                setForeground(Theme.ACCENT);
                setFont(Theme.FONT_SMALL.deriveFont(11f));
            }
            case COSTO_ACQ, IVA -> setForeground(new Color(0xff8060));
            case ZENMARKET, DEPOSITO, STOCCAGGIO -> setForeground(Theme.TEXT_DIM);
            case SPEDIZIONE -> {
                if (txt.contains("N/D")) setForeground(Theme.RED);
                else setForeground(Theme.TEXT_DIM);
            }
            case TOT_COSTI -> {
                setForeground(Theme.ACCENT);
                setFont(Theme.FONT_TABLE.deriveFont(Font.BOLD));
            }
            case BASE_DOGANA -> {
                if (txt.contains("⚠")) setForeground(new Color(0xffaa00));
                else setForeground(Theme.MUTED);
            }
            case NETTO_VENDITA -> {
                setForeground(Theme.ACCENT2);
                setFont(Theme.FONT_TABLE.deriveFont(Font.BOLD));
            }
            case GUADAGNO -> {
                if (txt.startsWith("+")) setForeground(Theme.GREEN);
                else if (txt.startsWith("-")) setForeground(Theme.RED);
                else setForeground(Theme.MUTED);
                setFont(Theme.FONT_TABLE.deriveFont(Font.BOLD));
            }
            case COSTO_PZ -> setForeground(Theme.MUTED);
            default -> setForeground(Theme.TEXT);
        }

        // Allineamento
        setHorizontalAlignment(col <= 1 ? SwingConstants.LEFT : SwingConstants.RIGHT);
        return this;
    }
}
