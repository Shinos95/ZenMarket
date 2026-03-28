package com.zenmarket.model;

import com.zenmarket.config.AppConfig;

/**
 * Marketplace supportati con la relativa tariffa di servizio ZenMarket.
 * Fonte: https://zenmarket.jp/it/fees.aspx
 */
public enum Marketplace {

    MERCARI_PRIVATI("Mercari (venditori privati)", AppConfig.ZENMARKET_FEE_MERCARI_JPY),
    MERCARI_SHOPS("Mercari Shops", AppConfig.ZENMARKET_FEE_STANDARD_JPY),
    YAHOO_AUCTIONS("Yahoo! Auctions", AppConfig.ZENMARKET_FEE_STANDARD_JPY),
    AMAZON_JP("Amazon Japan", AppConfig.ZENMARKET_FEE_STANDARD_JPY),
    RAKUTEN("Rakuten", AppConfig.ZENMARKET_FEE_STANDARD_JPY),
    RAKUMA("Rakuma", AppConfig.ZENMARKET_FEE_STANDARD_JPY),
    ZENPLUS("ZenPlus (store partner)", AppConfig.ZENMARKET_FEE_SCONTATA_JPY),
    ALTRO("Altro negozio", AppConfig.ZENMARKET_FEE_STANDARD_JPY);

    private final String label;
    private final int tariffaJpy;

    Marketplace(String label, int tariffaJpy) {
        this.label = label;
        this.tariffaJpy = tariffaJpy;
    }

    public String getLabel() { return label; }

    /** Commissione di servizio ZenMarket in JPY per questo marketplace */
    public int getTariffaJpy() { return tariffaJpy; }

    @Override
    public String toString() { return label; }
}
