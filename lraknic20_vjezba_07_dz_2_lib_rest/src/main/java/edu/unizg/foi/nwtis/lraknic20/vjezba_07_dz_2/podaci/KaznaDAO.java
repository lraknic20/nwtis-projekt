package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Klasa KaznaDAO za rad s tablicom Kazne
 *
 * @author Dragutin Kermek
 */
public class KaznaDAO {

  /** Veza na bazu podataka. */
  private Connection vezaBP;

  /**
   * Instantiates a new kazna DAO.
   *
   * @param vezaBP veza na bazu podataka
   */
  public KaznaDAO(Connection vezaBP) {
    super();
    this.vezaBP = vezaBP;
  }

  /**
   * Dohvati sve kazne.
   *
   * @return lista kazni
   */
  public List<Kazna> dohvatiSveKazne() {
    String upit =
        "SELECT id, vrijemePocetak, vrijemeKraj, brzina, gpsSirina, gpsDuzina, gpsSirinaRadar, gpsDuzinaRadar "
            + "FROM kazne";

    List<Kazna> kazne = new ArrayList<>();

    try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
      ResultSet rs = s.executeQuery();

      while (rs.next()) {
        var kazna = kreirajObjektKazna(rs);
        kazne.add(kazna);
      }
    } catch (SQLException ex) {
      Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return kazne;
  }


  /**
   * Dohvati kaznu.
   *
   * @param rb redni broj kazne
   * @return kazna
   */
  public Kazna dohvatiKaznu(int rb) {
    String upit =
        "SELECT id, vrijemePocetak, vrijemeKraj, brzina, gpsSirina, gpsDuzina, gpsSirinaRadar, gpsDuzinaRadar "
            + "FROM kazne WHERE rb = ?";

    try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
      s.setInt(1, rb);
      ResultSet rs = s.executeQuery();

      while (rs.next()) {
        var kazna = kreirajObjektKazna(rs);
        return kazna;
      }
    } catch (SQLException ex) {
      Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  /**
   * Dohvati kazne.
   *
   * @param odVremena početak interva
   * @param doVremena kraj interva
   * @return lista kazni
   */
  public List<Kazna> dohvatiKazne(long odVremena, long doVremena) {
    String upit =
        "SELECT id, vrijemePocetak, vrijemeKraj, brzina, gpsSirina, gpsDuzina, gpsSirinaRadar, gpsDuzinaRadar "
            + "FROM kazne WHERE vrijemeKraj >= ? AND vrijemeKraj <= ?";

    List<Kazna> kazne = new ArrayList<>();

    try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
      s.setLong(1, odVremena);
      s.setLong(2, doVremena);
      ResultSet rs = s.executeQuery();

      while (rs.next()) {
        var kazna = kreirajObjektKazna(rs);
        kazne.add(kazna);
      }
    } catch (SQLException ex) {
      Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return kazne;
  }

  /**
   * Dohvati kazne vozila.
   *
   * @param id id vozila
   * @return lista kazni
   */
  public List<Kazna> dohvatiKazneVozila(int id) {
    String upit =
        "SELECT id, vrijemePocetak, vrijemeKraj, brzina, gpsSirina, gpsDuzina, gpsSirinaRadar, gpsDuzinaRadar "
            + "FROM kazne WHERE id = ?";

    List<Kazna> kazne = new ArrayList<>();

    try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
      s.setInt(1, id);
      ResultSet rs = s.executeQuery();

      while (rs.next()) {
        var kazna = kreirajObjektKazna(rs);
        kazne.add(kazna);
      }
    } catch (SQLException ex) {
      Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return kazne;
  }


  /**
   * Dohvati kazne vozila.
   *
   * @param odVremena početak interva
   * @param doVremena kraj interva
   * @return lista kazni
   */
  public List<Kazna> dohvatiKazneVozila(int id, long odVremena, long doVremena) {
    String upit =
        "SELECT id, vrijemePocetak, vrijemeKraj, brzina, gpsSirina, gpsDuzina, gpsSirinaRadar, gpsDuzinaRadar "
            + "FROM kazne WHERE id = ? AND vrijemeKraj >= ? AND vrijemeKraj <= ?";

    List<Kazna> kazne = new ArrayList<>();

    try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
      s.setInt(1, id);
      s.setLong(2, odVremena);
      s.setLong(3, doVremena);
      ResultSet rs = s.executeQuery();

      while (rs.next()) {
        var kazna = kreirajObjektKazna(rs);
        kazne.add(kazna);
      }
    } catch (SQLException ex) {
      Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return kazne;
  }

  /**
   * Dodaj kaznu.
   *
   * @param kazna kazna
   * @return true, ako je uspješno dodavanje
   */
  public boolean dodajKaznu(Kazna kazna) {
    String upit =
        "INSERT INTO kazne (id, vrijemePocetak, vrijemeKraj, brzina, gpsSirina, gpsDuzina, gpsSirinaRadar, gpsDuzinaRadar) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {

      s.setInt(1, kazna.getId());
      s.setLong(2, kazna.getVrijemePocetak());
      s.setLong(3, kazna.getVrijemeKraj());
      s.setDouble(4, kazna.getBrzina());
      s.setDouble(5, kazna.getGpsSirina());
      s.setDouble(6, kazna.getGpsDuzina());
      s.setDouble(7, kazna.getGpsSirinaRadar());
      s.setDouble(8, kazna.getGpsDuzinaRadar());

      int brojAzuriranja = s.executeUpdate();

      return brojAzuriranja == 1;

    } catch (Exception ex) {
      Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  /**
   * Kreiraj objekt kazna.
   *
   * @param rs skup rezultata SQL upisa
   * @return kazna
   * @throws SQLException SQL iznimka
   */
  private Kazna kreirajObjektKazna(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    long vrijemePocetak = rs.getLong("vrijemePocetak");
    long vrijemeKraj = rs.getLong("vrijemeKraj");
    double brzina = rs.getDouble("brzina");
    double gpsSirina = rs.getDouble("gpsSirina");
    double gpsDuzina = rs.getDouble("gpsDuzina");
    double gpsSirinaRadar = rs.getDouble("gpsSirinaRadar");
    double gpsDuzinaRadar = rs.getDouble("gpsDuzinaRadar");

    Kazna k = new Kazna(id, vrijemePocetak, vrijemeKraj, brzina, gpsSirina, gpsDuzina,
        gpsSirinaRadar, gpsDuzinaRadar);
    return k;
  }
}
