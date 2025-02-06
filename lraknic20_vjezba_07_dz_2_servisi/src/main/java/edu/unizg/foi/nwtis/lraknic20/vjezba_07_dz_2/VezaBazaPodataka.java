/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Podrška za rad s bazom podataka.
 *
 * @author Dragutin Kermek
 */
@ApplicationScoped
public class VezaBazaPodataka {

  /** korisnicko ime baze podataka. */
  private String korisnickoImeBazaPodataka;

  /** lozinka baze podataka. */
  private String lozinkaBazaPodataka;

  /** url baze podataka. */
  private String urlBazaPodataka;

  /** upravljac baze podataka. */
  private String upravljacBazaPodataka;

  /** veza za bazu podataka. */
  private Connection vezaBazaPodataka;

  /**
   * Kreira novu instancu klase KorisnikResource.
   */
  public VezaBazaPodataka() {
    System.out.println("Otvaram vezu na bazu podataka");
    try {
      preuzmiPostavke("NWTiS_REST_1.txt");
      this.vezaBazaPodataka = otvoriVezuBP();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
  }

  /**
   * Dohvaća vezu na bazu podataka.
   *
   * @return vezu na bazu podataka
   */
  public Connection getVezaBazaPodataka() {
    return vezaBazaPodataka;
  }

  /**
   * Otvori vezu na bazu podataka.
   *
   * @return vezu na bazu podataka
   * @throws Exception iznimka
   */
  private Connection otvoriVezuBP() throws Exception {
    Class.forName(this.upravljacBazaPodataka);
    var vezaBazaPodataka = DriverManager.getConnection(this.urlBazaPodataka,
        this.korisnickoImeBazaPodataka, this.lozinkaBazaPodataka);
    return vezaBazaPodataka;
  }

  /**
   * Zatvori vezu na bazu podataka.
   */
  @PreDestroy
  public void zatvoriVezuBP() {
    System.out.println("Zatvaram vezu na bazu podataka");
    try {
      if (this.vezaBazaPodataka != null && !this.vezaBazaPodataka.isClosed()) {
        this.vezaBazaPodataka.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Preuzmi postavke.
   *
   * @param nazivDatoteke naziv datoteke
   * @throws Exception iznimka
   */
  private void preuzmiPostavke(String nazivDatoteke) throws Exception {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);

    this.korisnickoImeBazaPodataka = konfig.dajPostavku("korisnickoImeBazaPodataka");
    this.lozinkaBazaPodataka = konfig.dajPostavku("lozinkaBazaPodataka");
    this.urlBazaPodataka = konfig.dajPostavku("urlBazaPodataka");
    this.upravljacBazaPodataka = konfig.dajPostavku("upravljacBazaPodataka");
  }
}
