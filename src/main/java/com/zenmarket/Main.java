package com.zenmarket;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zenmarket.ui.MainFrame;

import javax.swing.*;

/**
 * Entry point dell'applicazione.
 */
public class Main {

    public static void main(String[] args) {
        // Applica il tema dark personalizzato alle proprietà UIManager
        com.zenmarket.ui.Theme.apply();

        // Imposta FlatLaf Dark — sovrascrive il sistema ma eredita le nostre UIManager props
        try {
            FlatDarkLaf.setup();
            // Re-applica le override dopo FlatLaf (alcune potrebbero essere sovrascritte)
            com.zenmarket.ui.Theme.apply();
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                com.zenmarket.ui.Theme.apply();
            } catch (Exception ignored) {}
        }

        // Avvia la UI nel thread Event Dispatch di Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
