# Calcolatore Zenmarket × Mercari

Applicazione Java desktop per calcolare i costi e i guadagni degli acquisti su **Mercari** (e altri marketplace giapponesi) tramite il servizio proxy **ZenMarket**, con rivendita in Italia.

---

## Requisiti

| Tool | Versione minima |
|------|----------------|
| Java | 17+ |
| Maven | 3.8+ |

---

## Avvio rapido

```bash
# 1. Clona / scarica il progetto
cd zenmarket-calculator

# 2. Compila e crea il JAR fat (con tutte le dipendenze incluse)
mvn package

# 3. Lancia l'applicazione
java -jar target/zenmarket-calculator.jar
```

---

## Eseguire i test

```bash
mvn test
```

---

## Struttura del progetto

```
zenmarket-calculator/
├── pom.xml
└── src/
    ├── main/java/com/zenmarket/
    │   ├── Main.java                    ← Entry point
    │   ├── config/
    │   │   └── AppConfig.java           ← ⭐ TUTTE le costanti (tariffe, IVA, ecc.)
    │   ├── model/
    │   │   ├── Marketplace.java         ← Enum marketplace con tariffe associate
    │   │   ├── Articolo.java            ← Dati grezzi di un articolo
    │   │   ├── RisultatoCalcolo.java    ← Risultato immutabile (Builder pattern)
    │   │   └── TotaliSessione.java      ← Totali aggregati sessione
    │   ├── service/
    │   │   ├── CalcolatoreService.java  ← Logica di calcolo pura (testabile)
    │   │   └── SessioneService.java     ← CRUD articoli + salvataggio JSON
    │   ├── ui/
    │   │   ├── MainFrame.java           ← Finestra principale
    │   │   ├── ArticoloTableModel.java  ← Swing TableModel
    │   │   ├── ArticoloDialog.java      ← Dialog aggiungi/modifica articolo
    │   │   └── SummaryPanel.java        ← Card riepilogo (costi/guadagno/dogana)
    │   └── util/
    │       └── FormatUtils.java         ← Formattazione valute e numeri
    └── test/java/com/zenmarket/
        └── service/
            └── CalcolatoreServiceTest.java  ← Test unitari
```

---

## Come modificare le tariffe

Tutte le costanti sono centralizzate in **`AppConfig.java`**. Non serve toccare altro.

```java
// Esempio: aggiornare la tariffa Mercari privati
public static final int ZENMARKET_FEE_MERCARI_JPY = 800;  // ← cambia qui

// Esempio: aggiornare la commissione deposito
public static final double COMMISSIONE_DEPOSITO = 0.035;  // ← 3,5%

// Esempio: aggiungere una fascia di spedizione EMS
public static final double[][] TARIFFE_EMS = {
    { 499, 10.30 },
    // ...aggiunta nuova fascia:
    { 9999, 55.00 },
};
```

---

## Logica di calcolo (fonte: zenmarket.jp)

| Voce | Formula |
|------|---------|
| IVA | `costoAcquisto × 22%` |
| Tariffa ZenMarket | `tariffaMarketplace(¥) × cambio` |
| Spedizione EMS | Tabella per fascia di peso (200g – 8kg) |
| Commissione deposito | `(costo + IVA + ZenMarket + spedizione) × 3,5%` |
| Stoccaggio extra | `giorniExtra × 50¥ × cambio` (60 giorni gratuiti) |
| Totale costi | somma di tutte le voci sopra |
| Base doganale | `costo + IVA + spedizione` (soglia: €150) |
| Guadagno netto | `nettoVendita − totaleCosti` |
| Costo/pezzo | `totaleCosti ÷ quantità` |

### Tariffe servizio ZenMarket (aggiornate 2025)

| Marketplace | Tariffa |
|-------------|---------|
| Mercari (venditori privati) | **800 ¥** |
| Mercari Shops, Amazon JP, Rakuten, Yahoo! Auctions | **500 ¥** |
| ZenPlus, negozi consigliati | **300 ¥** |

---

## Funzionalità UI

- **Cambio ¥→€ in tempo reale**: spinner in alto a destra, ricalcola tutto istantaneamente
- **Aggiungi / Modifica / Rimuovi** articoli tramite dialog dedicato
- **Doppio click** su una riga per modificarla
- **Salva / Apri** sessione in formato JSON (`File → Salva…`)
- **Card riepilogo**: totale costi, netto vendita, guadagno netto, base doganale
- **Avvisi visivi**: ⚠ se la base doganale supera €150, ⚠ se il peso è < 200g
- **Dati di esempio** pre-caricati dal file Excel originale

---

## Dipendenze

| Libreria | Uso |
|----------|-----|
| [FlatLaf 3.4](https://www.formdev.com/flatlaf/) | Look & Feel moderno dark/light per Swing |
| [Gson 2.10](https://github.com/google/gson) | Serializzazione JSON per salvataggio sessione |
| JUnit Jupiter 5.10 | Test unitari |
