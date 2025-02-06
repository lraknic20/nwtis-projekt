package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Kazna;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Klasa RestKlijentKazne.
 */
public class RestKlijentKazne {

  /**
   * Kopntruktor klase.
   */
  public RestKlijentKazne() {}

  /**
   * Vraća sve kazne
   *
   * @return kazne
   */
  public List<Kazna> getKazneJSON() {
    RestKazne rk = new RestKazne();
    List<Kazna> kazne = rk.getJSON();

    return kazne;
  }

  /**
   * Vraća kaznu.
   *
   * @param rb redni broj kazne
   * @return kazna
   */
  public Kazna getKaznaJSON_rb(String rb) {
    RestKazne rk = new RestKazne();
    Kazna k = rk.getJSON_rb(rb);
    return k;
  }

  /**
   * Vraća kazne u intervalu od do.
   *
   * @param odVremena početak intervala
   * @param doVremena kraj intervala
   * @return kazne
   */
  public List<Kazna> getKazneJSON_od_do(long odVremena, long doVremena) {
    RestKazne rk = new RestKazne();
    List<Kazna> kazne = rk.getJSON_od_do(odVremena, doVremena);

    return kazne;
  }

  /**
   * Vraća kazne za vozilo.
   *
   * @param id id vozila
   * @return kazne
   */
  public List<Kazna> getKazneJSON_vozilo(String id) {
    RestKazne rk = new RestKazne();
    List<Kazna> kazne = rk.getJSON_vozilo(id);
    return kazne;
  }

  /**
   * Vraća kazne za vozilo u intervalu od do..
   *
   * @param id id vozila
   * @param odVremena početak intervala
   * @param doVremena kraj intervala
   * @return kazne
   */
  public List<Kazna> getKazneJSON_vozilo_od_do(String id, long odVremena, long doVremena) {
    RestKazne rk = new RestKazne();
    List<Kazna> kazne = rk.getJSON_vozilo_od_do(id, odVremena, doVremena);

    return kazne;
  }

  public boolean headProvjeriPosluzitelj() {
    RestKazne rk = new RestKazne();
    boolean odgovor = rk.headProvjeriPosluzitelj();

    return odgovor;
  }

  /**
   * Dodaje kazna.
   *
   * @param kazna kazna
   * @return true, ako je uspješno
   */
  public boolean postKaznaJSON(Kazna kazna) {
    RestKazne rk = new RestKazne();
    var odgovor = rk.postJSON(kazna);
    return odgovor;
  }

  /**
   * Klasa RestKazne.
   */
  static class RestKazne {

    /** web target. */
    private final WebTarget webTarget;

    /** client. */
    private final Client client;

    /** knstanta BASE_URI. */
    private static final String BASE_URI = "http://localhost:9080/";

    /**
     * Konstruktor klase.
     */
    public RestKazne() {
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("nwtis/v1/api/kazne");
    }

    /**
     * Vraća kazne.
     *
     * @return kazne
     * @throws ClientErrorException iznimka kod poziva klijenta
     */
    public List<Kazna> getJSON() throws ClientErrorException {
      WebTarget resource = webTarget;
      List<Kazna> kazne = new ArrayList<Kazna>();

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        String odgovor = restOdgovor.readEntity(String.class);
        var jb = JsonbBuilder.create();
        var pkazne = jb.fromJson(odgovor, Kazna[].class);
        kazne.addAll(Arrays.asList(pkazne));
      }

      return kazne;
    }

    /**
     * Vraća kaznu.
     *
     * @param rb redni broj kazne
     * @return kazna
     * @throws ClientErrorException iznimka kod poziva klijenta
     */
    public Kazna getJSON_rb(String rb) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {rb}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        String odgovor = restOdgovor.readEntity(String.class);
        var jb = JsonbBuilder.create();
        var kazna = jb.fromJson(odgovor, Kazna.class);
        return kazna;
      }

      return null;
    }

    /**
     * Vraća kazne u intervalu od do.
     *
     * @param odVremena početak intervala
     * @param doVremena kraj intervala
     * @return kazne
     * @throws ClientErrorException iznimka kod poziva klijentan
     */
    public List<Kazna> getJSON_od_do(long odVremena, long doVremena) throws ClientErrorException {
      WebTarget resource = webTarget;
      List<Kazna> kazne = new ArrayList<Kazna>();

      resource = resource.queryParam("od", odVremena);
      resource = resource.queryParam("do", doVremena);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        String odgovor = restOdgovor.readEntity(String.class);
        var jb = JsonbBuilder.create();
        var pkazne = jb.fromJson(odgovor, Kazna[].class);
        kazne.addAll(Arrays.asList(pkazne));
      }

      return kazne;
    }

    /**
     * Vraća kazne za vozilo.
     *
     * @param id id vozila
     * @return kazne
     * @throws ClientErrorException iznimka kod poziva klijentaon
     */
    public List<Kazna> getJSON_vozilo(String id) throws ClientErrorException {
      WebTarget resource = webTarget;
      List<Kazna> kazne = new ArrayList<Kazna>();

      resource = resource.path(java.text.MessageFormat.format("vozilo/{0}", new Object[] {id}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        String odgovor = restOdgovor.readEntity(String.class);
        var jb = JsonbBuilder.create();
        var pkazne = jb.fromJson(odgovor, Kazna[].class);
        kazne.addAll(Arrays.asList(pkazne));
      }

      return kazne;
    }

    /**
     * Vraća kazne za vozilo u intervalu od do..
     *
     * @param id id vozila
     * @param odVremena početak intervala
     * @param doVremena kraj intervala
     * @return kazne
     * @throws ClientErrorException iznimka kod poziva klijenta
     */
    public List<Kazna> getJSON_vozilo_od_do(String id, long odVremena, long doVremena)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      List<Kazna> kazne = new ArrayList<Kazna>();

      resource = resource.path(java.text.MessageFormat.format("vozilo/{0}", new Object[] {id}));
      resource = resource.queryParam("od", odVremena);
      resource = resource.queryParam("do", doVremena);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        String odgovor = restOdgovor.readEntity(String.class);
        var jb = JsonbBuilder.create();
        var pkazne = jb.fromJson(odgovor, Kazna[].class);
        kazne.addAll(Arrays.asList(pkazne));
      }

      return kazne;
    }

    public boolean headProvjeriPosluzitelj() throws ClientErrorException {
      WebTarget resource = webTarget;

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().head();
      if (restOdgovor.getStatus() == 200) {
        return true;
      }

      return false;
    }

    /**
     * Dodaje kazna.
     *
     * @param kazna kazna
     * @return true, ako je uspješno
     * @throws ClientErrorException iznimka kod poziva klijenta
     */
    public boolean postJSON(Kazna kazna) throws ClientErrorException {
      WebTarget resource = webTarget;
      if (kazna == null) {
        return false;
      }
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      var odgovor =
          request.post(Entity.entity(kazna, MediaType.APPLICATION_JSON), String.class).toString();
      if (odgovor.trim().length() > 0) {
        return true;
      }

      return false;
    }

    /**
     * Close.
     */
    public void close() {
      client.close();
    }
  }
}
