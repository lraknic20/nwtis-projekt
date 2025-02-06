package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoznjeDAO {

  private Connection vezaBP;

  public VoznjeDAO(Connection vezaBP) {
    super();
    this.vezaBP = vezaBP;
  }

  public List<Vozilo> dohvatiVoznje(long odVremena, long doVremena) {
    String upit = "SELECT * FROM voznje WHERE vrijeme >= ? AND vrijeme <= ?";

    List<Vozilo> voznje = new ArrayList<>();

    try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
      s.setLong(1, odVremena);
      s.setLong(2, doVremena);
      ResultSet rs = s.executeQuery();

      while (rs.next()) {
        var vozilo = kreirajObjektVozilo(rs);
        voznje.add(vozilo);
      }
    } catch (SQLException ex) {
      Logger.getLogger(VoznjeDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return voznje;
  }

  public List<Vozilo> dohvatiVoznjeOdVozila(int id) {
    String upit = "SELECT * FROM voznje WHERE id = ?";

    List<Vozilo> voznje = new ArrayList<>();

    try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
      s.setInt(1, id);
      ResultSet rs = s.executeQuery();

      while (rs.next()) {
        var vozilo = kreirajObjektVozilo(rs);
        voznje.add(vozilo);
      }
    } catch (SQLException ex) {
      Logger.getLogger(VoznjeDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return voznje;
  }

  public List<Vozilo> dohvatiVoznjeOdVozilaOdDo(int id, long odVremena, long doVremena) {
    String upit = "SELECT * FROM voznje WHERE id = ? AND vrijeme >= ? AND vrijeme <= ?";

    List<Vozilo> voznje = new ArrayList<>();

    try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
      s.setInt(1, id);
      s.setLong(2, odVremena);
      s.setLong(3, doVremena);
      ResultSet rs = s.executeQuery();

      while (rs.next()) {
        var vozilo = kreirajObjektVozilo(rs);
        voznje.add(vozilo);
      }
    } catch (SQLException ex) {
      Logger.getLogger(VoznjeDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return voznje;
  }

  public boolean dodajVozilo(Vozilo novoVozilo) {
    String upit = "INSERT INTO voznje (id, broj, vrijeme, brzina, snaga, struja, visina,"
        + "gpsBrzina, tempVozila, postotakBaterija, naponBaterija, kapacitetBaterija,"
        + "tempBaterija, preostaloKm, ukupnoKm, gpsSirina, gpsDuzina) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {

      s.setInt(1, novoVozilo.getId());
      s.setInt(2, novoVozilo.getBroj());
      s.setLong(3, novoVozilo.getVrijeme());
      s.setDouble(4, novoVozilo.getBrzina());
      s.setDouble(5, novoVozilo.getSnaga());
      s.setDouble(6, novoVozilo.getStruja());
      s.setDouble(7, novoVozilo.getVisina());
      s.setDouble(8, novoVozilo.getGpsBrzina());
      s.setInt(9, novoVozilo.getTempVozila());
      s.setInt(10, novoVozilo.getPostotakBaterija());
      s.setDouble(11, novoVozilo.getNaponBaterija());
      s.setInt(12, novoVozilo.getKapacitetBaterija());
      s.setInt(13, novoVozilo.getTempBaterija());
      s.setDouble(14, novoVozilo.getPreostaloKm());
      s.setDouble(15, novoVozilo.getUkupnoKm());
      s.setDouble(16, novoVozilo.getGpsSirina());
      s.setDouble(17, novoVozilo.getGpsDuzina());

      int brojAzuriranja = s.executeUpdate();

      return brojAzuriranja == 1;

    } catch (Exception ex) {
      Logger.getLogger(VoznjeDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  private Vozilo kreirajObjektVozilo(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    int broj = rs.getInt("broj");
    long vrijeme = rs.getLong("vrijeme");
    double brzina = rs.getDouble("brzina");
    double snaga = rs.getDouble("snaga");
    double struja = rs.getDouble("struja");
    double visina = rs.getDouble("visina");
    double gpsBrzina = rs.getDouble("gpsBrzina");
    int tempVozila = rs.getInt("tempVozila");
    int postotakBaterija = rs.getInt("postotakBaterija");
    double naponBaterija = rs.getDouble("naponBaterija");
    int kapacitetBaterija = rs.getInt("kapacitetBaterija");
    int tempBaterija = rs.getInt("tempBaterija");
    double preostaloKm = rs.getDouble("preostaloKm");
    double ukupnoKm = rs.getDouble("ukupnoKm");
    double gpsSirina = rs.getDouble("gpsSirina");
    double gpsDuzina = rs.getDouble("gpsDuzina");

    Vozilo vozilo = new Vozilo(id, broj, vrijeme, brzina, snaga, struja, visina, gpsBrzina,
        tempVozila, postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm,
        ukupnoKm, gpsSirina, gpsDuzina);
    return vozilo;
  }
}
