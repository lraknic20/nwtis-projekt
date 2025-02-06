package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;

/**
 * Poslužitelj za registraciju radara služi za registiranje ili brisanje radara.
 */
public class PosluziteljZaRegistracijuRadara implements Runnable {

  /** Mrežna vrata. */
  private int mreznaVrata;

  /** Centralni sustav. */
  private CentralniSustav centralniSustav;

  /** Predložak registracija radara. */
  private Pattern predlozakRegistracijaRadara = Pattern.compile(
      "^RADAR (?<id>\\d+) (?<adresa>.*) (?<mreznaVrata>\\d+) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+) (?<maksUdaljenost>\\d+)$");

  /** Predložak za brisanje radara. */
  private Pattern predlozakZaBrisanjeRadara = Pattern.compile("^RADAR OBRIŠI (?<id>\\d+)$");

  /** Predložak za brisanje svih radara. */
  private Pattern predlozakZaBrisanjeSvihRadara = Pattern.compile("^RADAR OBRIŠI SVE$");

  private Pattern predlozakZaProvjeruRadara = Pattern.compile("^RADAR (?<id>\\d+)$");

  private Pattern predlozakZaResetRadara = Pattern.compile("^RADAR RESET$");

  private Pattern predlozakZaSveRadare = Pattern.compile("^RADAR SVI$");

  /** Poklapanje registracije radara. */
  private Matcher poklapanjeRegistracijeRadara;

  /** Poklapanje za brisanje radara. */
  private Matcher poklapanjeZaBrisanjeRadara;

  /** Poklapanje za brisanje svih radara. */
  private Matcher poklapanjeZaBrisanjeSvihRadara;

  private Matcher poklapanjeZaProvjeruRadara;

  private Matcher poklapanjeZaResetRadara;

  private Matcher poklapanjeZaSveRadare;

  /**
   * Instancira novog poslužitelja za registraciju radara.
   *
   * @param mreznaVrata Mrežna vrata
   * @param centralniSustav Centralni sustav
   */
  public PosluziteljZaRegistracijuRadara(int mreznaVrata, CentralniSustav centralniSustav) {
    super();
    this.mreznaVrata = mreznaVrata;
    this.centralniSustav = centralniSustav;
  }

  /**
   * Glavna metoda koja pokreće poslužitelj.
   */
  @Override
  public void run() {
    boolean kraj = false;

    try (ServerSocket mreznaUticnicaPosluzitelja = new ServerSocket(this.mreznaVrata)) {
      while (!kraj) {
        var mreznaUticnica = mreznaUticnicaPosluzitelja.accept();
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
      }
    } catch (NumberFormatException | IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Prima zahtjeva klijenta te ga obrađuje, ako je neispravan vraća ERROR.
   *
   * @param zahtjev the zahtjev
   * @return the string
   */
  public String obradaZahtjeva(String zahtjev) {
    if (zahtjev == null) {
      return "ERROR 10 Neispravna sintaksa komande.";
    }
    var odgovor = obradaZahtjevaRegistracijeRadara(zahtjev);
    if (odgovor != null) {
      return odgovor;
    }

    return "ERROR 10 Neispravna sintaksa komande.";
  }

  /**
   * Obrada zahtjeva registracije ili brisanja radara.
   *
   * @param zahtjev zahtjev klijenta
   * @return the string
   */
  public String obradaZahtjevaRegistracijeRadara(String zahtjev) {
    this.poklapanjeRegistracijeRadara = this.predlozakRegistracijaRadara.matcher(zahtjev);
    var statusKazna = poklapanjeRegistracijeRadara.matches();

    if (statusKazna) {
      return dodajKaznu();
    }

    this.poklapanjeZaBrisanjeRadara = this.predlozakZaBrisanjeRadara.matcher(zahtjev);
    var statusBrisanja = poklapanjeZaBrisanjeRadara.matches();

    if (statusBrisanja) {
      return obrisiRadar();
    }

    this.poklapanjeZaBrisanjeSvihRadara = this.predlozakZaBrisanjeSvihRadara.matcher(zahtjev);
    var statusBrisanjaSvih = poklapanjeZaBrisanjeSvihRadara.matches();

    if (statusBrisanjaSvih) {
      return obrisiSveRadare();
    }

    // novo predlozakZaProvjeruRadara
    this.poklapanjeZaProvjeruRadara = this.predlozakZaProvjeruRadara.matcher(zahtjev);
    var statusProvjeriRadar = poklapanjeZaProvjeruRadara.matches();

    if (statusProvjeriRadar) {
      return provjeriRadar();
    }

    // novo predlozakZaResetRadara
    this.poklapanjeZaResetRadara = this.predlozakZaResetRadara.matcher(zahtjev);
    var statusResetRadara = poklapanjeZaResetRadara.matches();

    if (statusResetRadara) {
      return resetRadara();
    }

    // novo predlozakZaSveRadare
    this.poklapanjeZaSveRadare = this.predlozakZaSveRadare.matcher(zahtjev);
    var statusSviRadari = poklapanjeZaSveRadare.matches();

    if (statusSviRadari) {
      return sviRadari();
    }

    return null;
  }

  /**
   * Metoda za dodavanje kazne.
   *
   * @return the string
   */
  private String dodajKaznu() {
    var radar = new PodaciRadara(Integer.valueOf(this.poklapanjeRegistracijeRadara.group("id")),
        this.poklapanjeRegistracijeRadara.group("adresa"),
        Integer.valueOf(this.poklapanjeRegistracijeRadara.group("mreznaVrata")), -1, -1,
        Integer.valueOf(this.poklapanjeRegistracijeRadara.group("maksUdaljenost")), null, -1, null,
        -1, null, Double.valueOf(this.poklapanjeRegistracijeRadara.group("gpsSirina")),
        Double.valueOf(this.poklapanjeRegistracijeRadara.group("gpsDuzina")));

    var postojiRadar = this.centralniSustav.sviRadari.containsKey(radar.id());

    if (postojiRadar)
      return "ERROR 11 Radar s ID: " + radar.id() + " već postoji";

    this.centralniSustav.sviRadari.put(radar.id(), radar);

    return "OK";
  }

  /**
   * Metoda za brisanje jednog radara.
   *
   * @return the string
   */
  private String obrisiRadar() {
    var id = Integer.valueOf(this.poklapanjeZaBrisanjeRadara.group("id"));

    if (this.centralniSustav.sviRadari.containsKey(id)) {
      this.centralniSustav.sviRadari.remove(id);
      return "OK";
    } else
      return "ERROR 12 Radar s ID: " + id + " ne postoji";
  }

  /**
   * Metoda za brisanje svih radara.
   *
   * @return the string
   */
  private String obrisiSveRadare() {
    if (!this.centralniSustav.sviRadari.isEmpty()) {
      this.centralniSustav.sviRadari.clear();
      return "OK";
    }
    return "ERROR 19 Ne postoji niti jedan radar";
  }

  // dovrseno
  private String provjeriRadar() {
    var id = Integer.valueOf(this.poklapanjeZaProvjeruRadara.group("id"));

    if (this.centralniSustav.sviRadari.containsKey(id))
      return "OK";
    else
      return "ERROR 12 Radar s ID: " + id + " ne postoji";
  }

  // dovrseno
  private String resetRadara() {
    int brojRadaraPrije = this.centralniSustav.sviRadari.size();

    for (Entry<Integer, PodaciRadara> radar : this.centralniSustav.sviRadari.entrySet()) {
      try {
        Socket mreznaUticnica =
            new Socket(radar.getValue().adresaRadara(), radar.getValue().mreznaVrataRadara());
        mreznaUticnica.close();

        MrezneOperacije.posaljiZahtjevPosluzitelju(radar.getValue().adresaRadara(),
            radar.getValue().mreznaVrataRadara(), "RADAR " + radar.getValue().id());
      } catch (Exception e) {
        this.centralniSustav.sviRadari.remove(radar.getKey());
      }
    }

    int brojRadaraNakon = this.centralniSustav.sviRadari.size();

    return "OK " + brojRadaraPrije + " " + brojRadaraNakon;
  }

  // dovrseno
  private String sviRadari() {
    var poruka = new StringBuilder();
    var razmak = " ";
    poruka.append("OK {");

    int brojRadara = this.centralniSustav.sviRadari.size();
    int brojanje = 0;

    for (Entry<Integer, PodaciRadara> radar : this.centralniSustav.sviRadari.entrySet()) {
      brojanje++;
      poruka.append("[").append(radar.getValue().id()).append(razmak)
          .append(radar.getValue().adresaRadara()).append(razmak)
          .append(radar.getValue().mreznaVrataRadara()).append(razmak)
          .append(radar.getValue().gpsSirina()).append(razmak).append(radar.getValue().gpsDuzina())
          .append(razmak).append(radar.getValue().maksUdaljenost()).append("]");
      if (brojanje != brojRadara)
        poruka.append(", ");
    }

    poruka.append("}");
    return poruka.toString();
  }
}
