package com.zenmarket.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zenmarket.model.Articolo;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestisce la lista di articoli della sessione corrente
 * e il salvataggio/caricamento su file JSON.
 */
public class SessioneService {

    private final List<Articolo> articoli = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // ─── CRUD articoli ─────────────────────────────────────────────────────

    public void aggiungi(Articolo articolo) {
        articoli.add(articolo);
    }

    public void rimuovi(int indice) {
        if (indice >= 0 && indice < articoli.size()) {
            articoli.remove(indice);
        }
    }

    public void aggiorna(int indice, Articolo articolo) {
        if (indice >= 0 && indice < articoli.size()) {
            articoli.set(indice, articolo);
        }
    }

    public void sposta(int da, int a) {
        if (da >= 0 && da < articoli.size() && a >= 0 && a < articoli.size()) {
            Collections.swap(articoli, da, a);
        }
    }

    public Articolo get(int indice) {
        return articoli.get(indice);
    }

    public List<Articolo> getArticoli() {
        return Collections.unmodifiableList(articoli);
    }

    public int size() { return articoli.size(); }

    public void svuota() { articoli.clear(); }

    // ─── Persistenza JSON ──────────────────────────────────────────────────

    public void salva(File file) throws IOException {
        String json = gson.toJson(articoli);
        Files.writeString(file.toPath(), json, StandardCharsets.UTF_8);
    }

    public void carica(File file) throws IOException {
        String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        Type listType = new TypeToken<List<Articolo>>(){}.getType();
        List<Articolo> caricati = gson.fromJson(json, listType);
        articoli.clear();
        if (caricati != null) {
            articoli.addAll(caricati);
        }
    }

    // ─── Dati di esempio (pre-caricati dal file Excel) ─────────────────────

    public void caricaEsempi() {
        articoli.clear();
        articoli.add(new Articolo("Ichiban Whitebeard's Pirates",
                com.zenmarket.model.Marketplace.MERCARI_PRIVATI, 3, 3500, 81, 230));
        articoli.add(new Articolo("Ichiban Whitebeard's Pirates (set 2)",
                com.zenmarket.model.Marketplace.MERCARI_PRIVATI, 4, 4000, 122, 290));
        articoli.add(new Articolo("Lucci e Luffy",
                com.zenmarket.model.Marketplace.MERCARI_PRIVATI, 12, 4000, 75, 240));
        articoli.add(new Articolo("3 set Ammiragli",
                com.zenmarket.model.Marketplace.MERCARI_PRIVATI, 3, 3500, 106, 300));
        articoli.add(new Articolo("4-set Ammiragli",
                com.zenmarket.model.Marketplace.MERCARI_PRIVATI, 4, 4000, 154, 405));
    }
}
