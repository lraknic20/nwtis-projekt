package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.klijenti;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

/**
 * Simulator Vozila čita podatke iz csv te ih putem kanala šalje poslužitelju za vozila.
 */
public class SimulatorVozila {

  /** Adresa vozila. */
  private String adresaVozila;

  /** Mrežna vrata vozila. */
  private int mreznaVrataVozila;

  /** Trajanje sek. */
  private int trajanjeSek;

  /** Trajanje pauze. */
  private int trajanjePauze;

  /** Staro vrijeme. */
  private long staroVrijeme = 0;

  /** Broj početnog retka. */
  private int brojPocetnogRetka = 2;

  /** Predložak za vozilo. */
  private Pattern predlozakZaVozilo =
      Pattern.compile("^(?<vrijeme>\\d+) (?<brzina>-?\\d+([.]\\d+)?) (?<snaga>-?\\d+([.]\\d+)?) "
          + "(?<struja>-?\\d+([.]\\d+)?) (?<visina>-?\\d+([.]\\d+)?) (?<gpsBrzina>-?\\d+([.]\\d+)?) "
          + "(?<tempVozila>\\d+) (?<postotakBaterija>\\d+) (?<naponBaterija>-?\\d+([.]\\d+)?) "
          + "(?<kapacitetBaterija>\\d+) (?<tempBaterija>\\d+) (?<preostaloKm>-?\\d+([.]\\d+)?) "
          + "(?<ukupnoKm>-?\\d+([.]\\d+)?) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+)$");

  /** Poklapanje vozila. */
  private Matcher poklapanjeVozila;

  /**
   * Glavna metoda koja provjerava broj argumenata, te pokreće simulator vozila.
   *
   * @param argumenti argumenti
   */
  public static void main(String[] argumenti) {
    if (argumenti.length != 3) {
      System.out.println("Broj argumenata nije 3.");
      return;
    }

    SimulatorVozila simulatorVozila = new SimulatorVozila();
    try {
      simulatorVozila.preuzmiPostavke(argumenti);

      try {
        simulatorVozila.posaljiRadniku(argumenti);
      } catch (IOException e) {
        e.printStackTrace();
      }

    } catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
      System.out.println(e.getMessage());
      return;
    }
  }

  /**
   * Metoda za slanje naredbe radniku za vozila.
   *
   * @param argumenti argumenti
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void posaljiRadniku(String[] argumenti) throws IOException {
    AsynchronousSocketChannel klijentskiKanal = AsynchronousSocketChannel.open();
    SocketAddress adresaPosluzitelj =
        new InetSocketAddress(this.adresaVozila, this.mreznaVrataVozila);

    klijentskiKanal.connect(adresaPosluzitelj);

    try {
      BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(argumenti[1])));
      String linija = null;

      while ((linija = br.readLine()) != null) {
        String[] podaciRed = linija.split(",");
        String red = String.join(" ", podaciRed);

        String zahtjev = provjeriPredlozak(red, argumenti[2]);
        if (!zahtjev.isEmpty()) {
          ByteBuffer buffer = ByteBuffer.wrap(zahtjev.getBytes());
          klijentskiKanal.write(buffer);
        }
      }

      br.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Metoda za provjeru predloška i stvaranje naredbe.
   *
   * @param red red
   * @param id id
   * @return the string
   */
  private String provjeriPredlozak(String red, String id) {
    var komanda = new StringBuilder();
    var razmak = " ";

    this.poklapanjeVozila = this.predlozakZaVozilo.matcher(red);
    var statusPoklapanja = poklapanjeVozila.matches();
    if (statusPoklapanja) {
      if (staroVrijeme != 0) {
        var novoVrijeme = Long.valueOf(poklapanjeVozila.group("vrijeme"));
        var vrijednostSpavanja = (novoVrijeme - staroVrijeme) * (this.trajanjeSek / 1000);

        try {
          Thread.sleep((long) vrijednostSpavanja);
        } catch (InterruptedException e) {
          System.out.println(e.getMessage());
        }
      }

      komanda.append("VOZILO").append(razmak).append(id).append(razmak).append(brojPocetnogRetka++)
          .append(razmak).append(red).append("\n");

      try {
        Thread.sleep(this.trajanjePauze);
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }
      staroVrijeme = Long.valueOf(poklapanjeVozila.group("vrijeme"));
    }
    return komanda.toString();
  }

  /**
   * Preuzmianje postavki iz konfiguracije.
   *
   * @param argumenti argumenti
   * @throws NeispravnaKonfiguracija the neispravna konfiguracija
   * @throws NumberFormatException the number format exception
   * @throws UnknownHostException the unknown host exception
   */
  public void preuzmiPostavke(String[] argumenti)
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(argumenti[0]);

    this.adresaVozila = konfig.dajPostavku("adresaVozila");
    this.mreznaVrataVozila = Integer.valueOf(konfig.dajPostavku("mreznaVrataVozila"));
    this.trajanjeSek = Integer.valueOf(konfig.dajPostavku("trajanjeSek"));
    this.trajanjePauze = Integer.valueOf(konfig.dajPostavku("trajanjePauze"));
  }
}
