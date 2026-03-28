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
import java.util.logging.Logger;

/**
 * Gestisce la lista di articoli della sessione corrente,
 * il salvataggio/caricamento su file JSON manuale,
 * e il salvataggio automatico (auto-save) alla chiusura.
 *
 * AUTO-SAVE: i dati vengono salvati automaticamente in
 *   {user.home}/.zenmarket/autosave.json
 * e ricaricati al prossimo avvio. Se il file non esiste,
 * vengono caricati i dati di esempio.
 */
public class SessioneService {

    private static final Logger LOG = Logger.getLogger(SessioneService.class.getName());

    private static final Path AUTO_SAVE_DIR  = Path.of(System.getProperty("user.home"), ".zenmarket");
    private static final Path AUTO_SAVE_FILE = AUTO_SAVE_DIR.resolve("autosave.json");

    private final List<Articolo> articoli = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // ─── CRUD articoli ─────────────────────────────────────────────────────

    public void aggiungi(Articolo articolo) {
        articoli.add(articolo);
    }

    /** Rimuove più articoli per indice in una sola operazione */
    public void rimuoviIndici(List<Integer> indici) {
        indici.stream()
              .sorted(Collections.reverseOrder())
              .forEach(i -> { if (i >= 0 && i < articoli.size()) articoli.remove((int) i); });
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

    public Articolo get(int indice) { return articoli.get(indice); }

    public List<Articolo> getArticoli() { return Collections.unmodifiableList(articoli); }

    public int size() { return articoli.size(); }

    public void svuota() { articoli.clear(); }

    // ─── Auto-save ─────────────────────────────────────────────────────────

    /**
     * Salva automaticamente la sessione in ~/.zenmarket/autosave.json.
     * Chiamato alla chiusura dell'app e dopo ogni modifica.
     */
    public void autoSalva() {
        try {
            Files.createDirectories(AUTO_SAVE_DIR);
            Files.writeString(AUTO_SAVE_FILE, gson.toJson(articoli), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.warning("Auto-save fallito: " + e.getMessage());
        }
    }

    /**
     * Carica l'auto-save se esiste, altrimenti carica i dati di esempio.
     * @return true se è stato caricato l'auto-save
     */
    public boolean caricaAutoSaveOEsempi() {
        if (Files.exists(AUTO_SAVE_FILE)) {
            try {
                String json = Files.readString(AUTO_SAVE_FILE, StandardCharsets.UTF_8);
                Type listType = new TypeToken<List<Articolo>>(){}.getType();
                List<Articolo> caricati = gson.fromJson(json, listType);
                articoli.clear();
                if (caricati != null) articoli.addAll(caricati);
                LOG.info("Auto-save caricato: " + articoli.size() + " articoli da " + AUTO_SAVE_FILE);
                return true;
            } catch (IOException e) {
                LOG.warning("Lettura auto-save fallita, carico esempi: " + e.getMessage());
            }
        }
        caricaEsempi();
        return false;
    }

    public String getAutoSavePath() { return AUTO_SAVE_FILE.toString(); }

    // ─── Persistenza JSON manuale ──────────────────────────────────────────

    public void salva(File file) throws IOException {
        Files.writeString(file.toPath(), gson.toJson(articoli), StandardCharsets.UTF_8);
    }

    public void carica(File file) throws IOException {
        String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        Type listType = new TypeToken<List<Articolo>>(){}.getType();
        List<Articolo> caricati = gson.fromJson(json, listType);
        articoli.clear();
        if (caricati != null) articoli.addAll(caricati);
    }

    // ─── Dati di esempio ───────────────────────────────────────────────────

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
