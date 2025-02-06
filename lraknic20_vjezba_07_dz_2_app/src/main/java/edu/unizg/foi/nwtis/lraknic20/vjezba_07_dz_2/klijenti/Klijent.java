package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.klijenti;

import java.net.UnknownHostException;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;

/**
 * Klijent šalje upite poslužitelju za kazne.
 */
public class Klijent {

  /** Adresa kazne. */
  private String adresaKazne;

  /** Mrežna vrata kazne. */
  private int mreznaVrataKazne;

  /**
   * Glavna metoda u kojoj se provjerava broj argumenata, preuzimaju postavke i šalju naredbe
   * poslužitelju za kazne.
   *
   * @param argumenti argumenti
   */
  public static void main(String[] argumenti) {
    if (argumenti.length != 3 && argumenti.length != 4) {
      System.out.println("Broj argumenata nije 3 ili 4.");
      return;
    }

    Klijent klijent = new Klijent();
    try {
      klijent.preuzmiPostavke(argumenti);

      klijent.posaljiPosluziteljuKazni(argumenti);

    } catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
      System.out.println(e.getMessage());
      return;
    }
  }

  /**
   * Metoda za slanje naredbe poslužitelju kazni.
   *
   * @param argumenti argumenti
   */
  private void posaljiPosluziteljuKazni(String[] argumenti) {
    var komanda = new StringBuilder();
    var razmak = " ";

    if (argumenti.length == 3) {
      komanda.append("STATISTIKA").append(razmak).append(argumenti[1]).append(razmak)
          .append(argumenti[2]);
    }
    if (argumenti.length == 4) {
      komanda.append("VOZILO").append(razmak).append(argumenti[1]).append(razmak)
          .append(argumenti[2]).append(razmak).append(argumenti[3]);
    }

    MrezneOperacije.posaljiZahtjevPosluzitelju(this.adresaKazne, this.mreznaVrataKazne,
        komanda.toString());
  }

  /**
   * Preuzmanje postavki iz konfiguracije.
   *
   * @param argumenti the args
   * @throws NeispravnaKonfiguracija the neispravna konfiguracija
   * @throws NumberFormatException the number format exception
   * @throws UnknownHostException the unknown host exception
   */
  public void preuzmiPostavke(String[] argumenti)
      throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(argumenti[0]);

    this.adresaKazne = konfig.dajPostavku("adresaKazne");
    this.mreznaVrataKazne = Integer.valueOf(konfig.dajPostavku("mreznaVrataKazne"));
  }
}
