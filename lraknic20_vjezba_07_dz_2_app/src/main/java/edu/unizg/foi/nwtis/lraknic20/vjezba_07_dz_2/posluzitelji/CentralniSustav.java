package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji;

import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.PodaciRadara;

/**
 * Centralni sustav pokreće poslužitelje PosluziteljZaRegistracijuRadara i PosluziteljZaVozila kao
 * dvije zasebne dretve.
 */
public class CentralniSustav {

  /** Mrežna vrata radara. */
  public int mreznaVrataRadara;

  /** Mrežna vrata vozila. */
  public int mreznaVrataVozila;

  /** Mrežna vrata nadzora. */
  private int mreznaVrataNadzora;

  /** Maksimalan broj vozila. */
  private int maksVozila;

  /** Tvornica dretvi. */
  private ThreadFactory tvornicaDretvi = Thread.ofVirtual().factory();

  /** Svi radari. */
  public ConcurrentMap<Integer, PodaciRadara> sviRadari =
      new ConcurrentHashMap<Integer, PodaciRadara>();

  /** Sva vozila. */
  public ConcurrentMap<Integer, Integer> svaVozila = new ConcurrentHashMap<Integer, Integer>();

  // public ConcurrentMap<Integer, RedPodaciVozila> svaVozila = new ConcurrentHashMap<Integer,
  // RedPodaciVozila>();

  /**
   * Glavna metoda u kojoj se provjerava broj argumenata. Preuzima postavke iz txt datoteke i sprema
   * ih u varijable. Zatim pokreće poslužitelje: PosluziteljZaRegistracijuRadara i
   * PosluziteljZaVozila.
   *
   * @param argumenti konfiguracija
   */
  public static void main(String[] argumenti) {
    if (argumenti.length != 1) {
      System.out.println("Broj argumenata nije 1.");
      return;
    }

    CentralniSustav centralniSustav = new CentralniSustav();
    try {
      centralniSustav.preuzmiPostavke(argumenti);

      centralniSustav.pokreniPosluzitelje();

    } catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
      System.out.println(e.getMessage());
      return;
    }
  }

  /**
   * Pokreću se PosluziteljZaRegistracijuRadara i PosluziteljZaVozila.
   */
  private void pokreniPosluzitelje() {
    PosluziteljZaRegistracijuRadara prr =
        new PosluziteljZaRegistracijuRadara(this.mreznaVrataRadara, this);
    PosluziteljZaVozila pzv = new PosluziteljZaVozila(this.mreznaVrataVozila, this);

    var dretvaPrr = this.tvornicaDretvi.newThread(prr);
    dretvaPrr.start();

    var dretvaPzv = this.tvornicaDretvi.newThread(pzv);
    dretvaPzv.start();
    try {
      dretvaPrr.join();
      dretvaPzv.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Preuzimanje postavki iz txt te spremanje u varijable.
   *
   * @param argumenti konfiguracija
   * @throws NeispravnaKonfiguracija the neispravna konfiguracija
   * @throws NumberFormatException the number format exception
   * @throws UnknownHostException the unknown host exception
   */
  public void preuzmiPostavke(String[] argumenti)
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(argumenti[0]);

    this.mreznaVrataRadara = Integer.valueOf(konfig.dajPostavku("mreznaVrataRadara"));
    this.mreznaVrataVozila = Integer.valueOf(konfig.dajPostavku("mreznaVrataVozila"));
    this.mreznaVrataNadzora = Integer.valueOf(konfig.dajPostavku("mreznaVrataNadzora"));
    this.maksVozila = Integer.valueOf(konfig.dajPostavku("maksVozila"));
  }
}
