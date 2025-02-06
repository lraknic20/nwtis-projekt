package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji.radnici;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.BrzoVozilo;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji.PosluziteljRadara;

/**
 * Radnik za radare prima podatke o vozilu te ovisno o njegovoj brzini i maksimalnom trajanju
 * generira i šalje kaznu poslužitelju za kazne.
 */
public class RadnikZaRadare implements Runnable {

  /** Mrežna utičnica. */
  private Socket mreznaUticnica;

  /** Podaci radara. */
  private PodaciRadara podaciRadara;

  /** Poslužitelj radara. */
  public PosluziteljRadara posluziteljRadara;

  /** Predložak brzine. */
  private Pattern predlozakBrzine = Pattern.compile(
      "^VOZILO (?<id>\\d+) (?<vrijeme>\\d+) (?<brzina>-?\\d+([.]\\d+)?) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+)$");

  private Pattern predlozakZaReset = Pattern.compile("^RADAR RESET$");

  private Pattern predlozakZaRadar = Pattern.compile("^RADAR (?<id>\\d+)$");

  /**
   * Instancira novi radnik za radare.
   *
   * @param mreznaUticnica Mrežna utičnica
   * @param podaciRadara Podaci radara
   * @param posluziteljRadara Poslužitelj radara
   */
  public RadnikZaRadare(Socket mreznaUticnica, PodaciRadara podaciRadara,
      PosluziteljRadara posluziteljRadara) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.podaciRadara = podaciRadara;
    this.posluziteljRadara = posluziteljRadara;
  }

  /**
   * Glavna metoda u kojoj se pokreće poslužitelj.
   */
  @Override
  public void run() {
    try {
      BufferedReader citac =
          new BufferedReader(new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"));
      OutputStream out = mreznaUticnica.getOutputStream();
      PrintWriter pisac = new PrintWriter(new OutputStreamWriter(out, "utf8"), true);
      var redak = citac.readLine();

      mreznaUticnica.shutdownInput();
      pisac.println(obradaZahtjeva(redak));

      pisac.flush();
      mreznaUticnica.shutdownOutput();
      mreznaUticnica.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Obrada zahtjeva klijenta. Ako je zahtjev neispravan vraća se ERROR.
   *
   * @param zahtjev the zahtjev
   * @return the string
   */
  public String obradaZahtjeva(String zahtjev) {
    if (zahtjev == null) {
      return "ERROR 30 Neispravna sintaksa komande.";
    }
    var odgovor = obradaZahtjevaBrzine(zahtjev);
    if (odgovor != null) {
      return odgovor;
    }

    return "ERROR 30 Neispravna sintaksa komande.";
  }

  /**
   * Obrada zahtjeva u kojem se provjerava poklapanje zahtjeva klijenta.
   *
   * @param zahtjev the zahtjev
   * @return the string
   */
  public String obradaZahtjevaBrzine(String zahtjev) {
    var poklapanje = this.predlozakBrzine.matcher(zahtjev);
    var statusPoklapanja = poklapanje.matches();

    if (statusPoklapanja) {
      return poklapanjeVozila(poklapanje);
    }

    // novo
    var poklapanjeZaReset = this.predlozakZaReset.matcher(zahtjev);
    var statusPoklapanjaZaReset = poklapanjeZaReset.matches();

    if (statusPoklapanjaZaReset) {
      return resetRadara(poklapanjeZaReset);
    }

    // novo
    var poklapanjeZaRadar = this.predlozakZaRadar.matcher(zahtjev);
    var statusPoklapanjaZaRadar = poklapanjeZaRadar.matches();

    if (statusPoklapanjaZaRadar) {
      return provjeriRadar(poklapanjeZaRadar);
    }
    return null;
  }

  private String poklapanjeVozila(Matcher poklapanje) {
    int id = Integer.valueOf(poklapanje.group("id"));
    long vrijeme = Long.valueOf(poklapanje.group("vrijeme"));
    double brzina = Double.valueOf(poklapanje.group("brzina"));
    double gpsSirina = Double.valueOf(poklapanje.group("gpsSirina"));
    double gpsDuzina = Double.valueOf(poklapanje.group("gpsDuzina"));

    return obradiVozilo(id, vrijeme, brzina, gpsSirina, gpsDuzina);
  }

  // dovrseno
  private String resetRadara(Matcher poklapanjeZaReset) {
    try {
      Socket mreznaUticnica = new Socket(this.podaciRadara.adresaRegistracije(),
          this.podaciRadara.mreznaVrataRegistracije());
      mreznaUticnica.close();
    } catch (Exception e) {
      return "ERROR 32 PosluziteljZaRegistracijuRadara nije aktivan";
    }

    String komanda = "RADAR " + this.podaciRadara.id();

    String odgovor =
        MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaRegistracije(),
            this.podaciRadara.mreznaVrataRegistracije(), komanda);

    if (odgovor.equals("OK")) {
      return "OK";
    } else if (odgovor.contains("ERROR 12")) {
      var komanda2 = new StringBuilder();
      var razmak = " ";
      komanda2.append("RADAR").append(razmak).append(this.podaciRadara.id()).append(razmak)
          .append(this.podaciRadara.adresaRadara()).append(razmak)
          .append(this.podaciRadara.mreznaVrataRadara()).append(razmak)
          .append(this.podaciRadara.gpsSirina()).append(razmak)
          .append(this.podaciRadara.gpsDuzina()).append(razmak)
          .append(this.podaciRadara.maksUdaljenost()).append("\n");

      var odgovor2 =
          MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaRegistracije(),
              this.podaciRadara.mreznaVrataRegistracije(), komanda2.toString());

      if (odgovor2.equals("OK"))
        return "OK";
    }
    return null;
  }

  // dovrseno
  private String provjeriRadar(Matcher poklapanjeZaRadar) {
    int id = Integer.valueOf(poklapanjeZaRadar.group("id"));
    if (id != this.podaciRadara.id())
      return "ERROR 33 id ne odgovara identifikatoru radara";

    try {
      Socket mreznaUticnica =
          new Socket(this.podaciRadara.adresaKazne(), this.podaciRadara.mreznaVrataKazne());
      mreznaUticnica.close();
    } catch (Exception e) {
      return "ERROR 34 PosluziteljKazni nije aktivan";
    }

    String odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaKazne(),
        this.podaciRadara.mreznaVrataKazne(), "TEST");

    if (odgovor.contains("OK"))
      return "OK";

    return null;
  }

  /**
   * Metoda u kojoj se određuje je li vozilo brzo vozilo te šalje kaznu.
   *
   * @param id id
   * @param vrijeme vrijeme
   * @param brzina brzina
   * @param gpsSirina gps sirina
   * @param gpsDuzina gps duzina
   * @return the string
   */
  private String obradiVozilo(int id, long vrijeme, double brzina, double gpsSirina,
      double gpsDuzina) {
    if (brzina > this.podaciRadara.maksBrzina()) {
      Boolean postojiStatus = provjeriStatusZaBrzoVozilo(id);

      if (!postojiStatus) {
        BrzoVozilo brzoVozilo = new BrzoVozilo(id, -1, vrijeme, brzina, gpsSirina, gpsDuzina, true);

        this.posluziteljRadara.svaBrzaVozila.put(brzoVozilo.id(), brzoVozilo);

        return "OK";
      }

      long pocetnoVrijeme = dohvatiVrijemeZaBrzoVozilo(id);

      if ((vrijeme - pocetnoVrijeme) / 1000 > this.podaciRadara.maksTrajanje() * 2) {
        ponistiBrzoVozilo(id);
        return "OK";
      }

      if ((vrijeme - pocetnoVrijeme) / 1000 > this.podaciRadara.maksTrajanje()) {
        BrzoVozilo brzoVozilo = new BrzoVozilo(id, -1, vrijeme, brzina, gpsSirina, gpsDuzina, true);

        ponistiBrzoVozilo(id);
        var odgovor = posaljiKaznu(brzoVozilo, pocetnoVrijeme);
        if (odgovor) {
          return "OK";
        } else
          return "ERROR 31 PoslužiteljKazni nije aktivan";
      }
    }

    if (this.posluziteljRadara.svaBrzaVozila.containsKey(id) && provjeriStatusZaBrzoVozilo(id)
        && brzina < this.podaciRadara.maksBrzina()) {
      ponistiBrzoVozilo(id);
    }

    return "OK";
  }

  /**
   * Provjeri status za brzo vozilo.
   *
   * @param id id
   * @return the boolean
   */
  private Boolean provjeriStatusZaBrzoVozilo(int id) {
    for (BrzoVozilo brzoVozilo : this.posluziteljRadara.svaBrzaVozila.values()) {
      if (brzoVozilo.id() == id && brzoVozilo.status() == true) {
        return true;
      }
    }
    return false;
  }

  /**
   * Dohvati vrijeme za brzo vozilo.
   *
   * @param id id
   * @return the long
   */
  private long dohvatiVrijemeZaBrzoVozilo(int id) {
    for (BrzoVozilo brzoVozilo : this.posluziteljRadara.svaBrzaVozila.values()) {
      if (brzoVozilo.id() == id && brzoVozilo.status() == true) {
        return brzoVozilo.vrijeme();
      }
    }
    return 0;
  }

  /**
   * Poništi brzo vozilo.
   *
   * @param id id
   */
  private void ponistiBrzoVozilo(int id) {
    BrzoVozilo vozilo = null;
    for (BrzoVozilo brzoVozilo : this.posluziteljRadara.svaBrzaVozila.values()) {
      if (brzoVozilo.id() == id && brzoVozilo.status() == true) {
        vozilo = brzoVozilo.postaviStatus(false);
      }
    }
    this.posluziteljRadara.svaBrzaVozila.replace(id, vozilo);
  }

  /**
   * Pošalji kaznu poslužitelju za kazne.
   *
   * @param vozilo vozilo
   * @param vrijemePocetak vrijeme početak
   * @return true, if successful
   */
  private boolean posaljiKaznu(BrzoVozilo vozilo, long vrijemePocetak) {
    var komanda = new StringBuilder();
    var razmak = " ";
    komanda.append("VOZILO").append(razmak).append(vozilo.id()).append(razmak)
        .append(vrijemePocetak).append(razmak).append(vozilo.vrijeme()).append(razmak)
        .append(vozilo.brzina()).append(razmak).append(vozilo.gpsSirina()).append(razmak)
        .append(vozilo.gpsDuzina()).append(razmak).append(this.podaciRadara.gpsSirina())
        .append(razmak).append(this.podaciRadara.gpsDuzina()).append("\n");

    try {
      Socket mreznaUticnica =
          new Socket(this.podaciRadara.adresaKazne(), this.podaciRadara.mreznaVrataKazne());
      mreznaUticnica.close();
    } catch (Exception e) {
      System.out.println("ERROR 31 PoslužiteljKazni nije aktivan");
      return false;
    }

    String odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaKazne(),
        this.podaciRadara.mreznaVrataKazne(), komanda.toString());

    if (odgovor != null) {
      return true;
    } else {
      return false;
    }
  }
}
