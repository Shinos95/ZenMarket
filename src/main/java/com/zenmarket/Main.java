package com.zenmarket;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zenmarket.ui.MainFrame;

import javax.swing.*;

/**
 * Entry point dell'applicazione.
 */
public class Main {

    public static void main(String[] args) {
        // Imposta FlatLaf Dark come Look & Feel moderno
        // Se non è disponibile, usa il L&F di sistema come fallback
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // Avvia la UI nel thread Event Dispatch di Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
