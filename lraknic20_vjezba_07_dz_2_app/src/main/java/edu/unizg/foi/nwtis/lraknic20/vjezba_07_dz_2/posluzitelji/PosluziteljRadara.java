package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.BrzoVozilo;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.posluzitelji.radnici.RadnikZaRadare;

/**
 * Poslužitelj radara je poslužitelj koji registrira radare i kreira radnike za radare.
 */
public class PosluziteljRadara {

  /** Tvornica dretvi. */
  private ThreadFactory tvornicaDretvi = Thread.ofVirtual().factory();

  /** Podaci radara. */
  private PodaciRadara podaciRadara;

  /** Sva brza vozila. */
  public ConcurrentMap<Integer, BrzoVozilo> svaBrzaVozila =
      new ConcurrentHashMap<Integer, BrzoVozilo>();

  /**
   * Glavna metoda koja provjerava ulazne argumente, preuzima postavke, registrira radare i pokrece
   * radnike za radare.
   *
   * @param argumenti ulazni argumenti
   */
  public static void main(String[] argumenti) {
    if (argumenti.length != 1 && argumenti.length != 3) {
      System.out.println("Broj argumenata nije 1 ili 3.");
      return;
    }

    PosluziteljRadara posluziteljRadara = new PosluziteljRadara();
    try {
      posluziteljRadara.preuzmiPostavke(argumenti);

      if (argumenti.length == 1) {
        posluziteljRadara.registrirajPosluzitelja();

        posluziteljRadara.pokreniPosluzitelja();
      }
      if (argumenti.length == 3) {
        posluziteljRadara.brisiRadar(argumenti[2]);
      }
    } catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
      System.out.println(e.getMessage());
      return;
    }
  }

  /**
   * Metoda šalje brisanje radara poslužitelju za radare.
   *
   * @param zahtjev zahtjev klijenta
   * @return true, if successful
   */
  public boolean brisiRadar(String zahtjev) {
    var komanda = new StringBuilder();
    var razmak = " ";
    if (zahtjev.matches("^\\d+$") || zahtjev.equals("SVE")) {
      komanda.append("RADAR").append(razmak).append("OBRIŠI").append(razmak).append(zahtjev)
          .append("\n");
    } else
      return false;

    var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaRegistracije(),
        this.podaciRadara.mreznaVrataRegistracije(), komanda.toString());

    if (odgovor != null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Metoda šalje registriranje radara poslužitelju za radare.
   *
   * @return true, if successful
   */
  public boolean registrirajPosluzitelja() {
    var komanda = new StringBuilder();
    var razmak = " ";
    komanda.append("RADAR").append(razmak).append(this.podaciRadara.id()).append(razmak)
        .append(this.podaciRadara.adresaRadara()).append(razmak)
        .append(this.podaciRadara.mreznaVrataRadara()).append(razmak)
        .append(this.podaciRadara.gpsSirina()).append(razmak).append(this.podaciRadara.gpsDuzina())
        .append(razmak).append(this.podaciRadara.maksUdaljenost()).append("\n");

    try {
      Socket mreznaUticnica = new Socket(this.podaciRadara.adresaRegistracije(),
          this.podaciRadara.mreznaVrataRegistracije());
      mreznaUticnica.close();
    } catch (Exception e) {
      System.out.println("ERROR 39 PoslužiteljZaRegistracijuRadara nije aktivan");
      return false;
    }

    var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaRegistracije(),
        this.podaciRadara.mreznaVrataRegistracije(), komanda.toString());

    if (odgovor != null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Preuzimanje postavka iz konfiguracije.
   *
   * @param argumenti argumenti
   * @throws NeispravnaKonfiguracija the neispravna konfiguracija
   * @throws NumberFormatException the number format exception
   * @throws UnknownHostException the unknown host exception
   */
  public void preuzmiPostavke(String[] argumenti)
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(argumenti[0]);

    this.podaciRadara = new PodaciRadara(Integer.valueOf(konfig.dajPostavku("id")),
        InetAddress.getLocalHost().getHostName(),
        Integer.valueOf(konfig.dajPostavku("mreznaVrataRadara")),
        Integer.valueOf(konfig.dajPostavku("maksBrzina")),
        Integer.valueOf(konfig.dajPostavku("maksTrajanje")),
        Integer.valueOf(konfig.dajPostavku("maksUdaljenost")),
        konfig.dajPostavku("adresaRegistracije"),
        Integer.valueOf(konfig.dajPostavku("mreznaVrataRegistracije")),
        konfig.dajPostavku("adresaKazne"), Integer.valueOf(konfig.dajPostavku("mreznaVrataKazne")),
        konfig.dajPostavku("postanskaAdresaRadara"),
        Double.valueOf(konfig.dajPostavku("gpsSirina")),
        Double.valueOf(konfig.dajPostavku("gpsDuzina")));
  }

  /**
   * Pokretanje radnika za radare.
   */
  public void pokreniPosluzitelja() {
    boolean kraj = false;

    try (ServerSocket mreznaUticnicaPosluzitelja =
        new ServerSocket(this.podaciRadara.mreznaVrataRadara())) {
      while (!kraj) {
        var mreznaUticnica = mreznaUticnicaPosluzitelja.accept();
        RadnikZaRadare rr = new RadnikZaRadare(mreznaUticnica, podaciRadara, this);
        var dretva = tvornicaDretvi.newThread(rr);
        dretva.start();
      }
    } catch (NumberFormatException | IOException e) {
      e.printStackTrace();
    }
  }
}
