package com.zenmarket.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility per la formattazione dei valori numerici nell'interfaccia grafica.
 */
public final class FormatUtils {

    private static final NumberFormat EUR_FORMAT = NumberFormat.getCurrencyInstance(Locale.ITALY);
    private static final NumberFormat NUM_FORMAT = NumberFormat.getNumberInstance(Locale.ITALY);

    static {
        NUM_FORMAT.setMinimumFractionDigits(2);
        NUM_FORMAT.setMaximumFractionDigits(2);
    }

    private FormatUtils() {}

    /** Formatta un valore come valuta EUR: "€ 12,50" */
    public static String eur(double valore) {
        return EUR_FORMAT.format(valore);
    }

    /** Formatta un numero con 2 decimali */
    public static String num(double valore) {
        return NUM_FORMAT.format(valore);
    }

    /** Formatta grammi: "3.500 g" */
    public static String grammi(double grammi) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ITALY);
        nf.setMaximumFractionDigits(0);
        return nf.format(grammi) + " g";
    }

    /**
     * Formatta un guadagno con segno: "+€ 45,00" o "-€ 12,00"
     * Il colore viene gestito dalla UI.
     */
    public static String guadagno(double valore) {
        return (valore >= 0 ? "+" : "") + eur(valore);
    }

    /** Restituisce "N/D" se il valore è 0 e la flag è false */
    public static String eurONd(double valore, boolean disponibile) {
        return disponibile ? eur(valore) : "N/D";
    }
}
