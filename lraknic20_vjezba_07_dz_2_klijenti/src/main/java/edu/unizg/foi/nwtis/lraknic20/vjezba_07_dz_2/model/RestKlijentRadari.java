package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Radar;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RestKlijentRadari {

  public RestKlijentRadari() {}

  public List<Radar> getRadari() {
    RestRadari rr = new RestRadari();
    List<Radar> radari = rr.getJSON();

    return radari;
  }

  public boolean getReset() {
    RestRadari rr = new RestRadari();
    boolean odgovor = rr.getReset();

    return odgovor;
  }

  public Radar getRadar(String id) {
    RestRadari rr = new RestRadari();
    Radar radar = rr.getJSON_radar(id);

    return radar;
  }

  public boolean getProvjeriRadar(String id) {
    RestRadari rr = new RestRadari();
    boolean odgovor = rr.getProvjeriRadar(id);

    return odgovor;
  }

  public boolean deleteRadar(String id) {
    RestRadari rr = new RestRadari();
    boolean odgovor = rr.deleteRadar(id);

    return odgovor;
  }

  public boolean deleteRadari() {
    RestRadari rr = new RestRadari();
    boolean odgovor = rr.deleteRadari();

    return odgovor;
  }

  static class RestRadari {
    /** web target. */
    private final WebTarget webTarget;

    /** client. */
    private final Client client;

    /** knstanta BASE_URI. */
    private static final String BASE_URI = "http://localhost:9080/";

    /**
     * Konstruktor klase.
     */
    public RestRadari() {
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("nwtis/v1/api/radari");
    }

    public List<Radar> getJSON() throws ClientErrorException {
      WebTarget resource = webTarget;
      List<Radar> radari = new ArrayList<Radar>();

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        String odgovor = restOdgovor.readEntity(String.class);
        var jb = JsonbBuilder.create();
        var pradari = jb.fromJson(odgovor, Radar[].class);
        radari.addAll(Arrays.asList(pradari));
      }

      return radari;
    }

    public boolean getReset() throws ClientErrorException {
      WebTarget resource = webTarget.path("reset");

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        return true;
      }

      return false;
    }

    public Radar getJSON_radar(String id) throws ClientErrorException {
      WebTarget resource = webTarget;

      resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {id}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        String odgovor = restOdgovor.readEntity(String.class);
        var jb = JsonbBuilder.create();
        var radar = jb.fromJson(odgovor, Radar.class);
        return radar;
      }

      return null;
    }

    public boolean getProvjeriRadar(String id) throws ClientErrorException {
      WebTarget resource = webTarget;

      resource = resource.path(java.text.MessageFormat.format("{0}/provjeri", new Object[] {id}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        return true;
      }

      return false;
    }

    public boolean deleteRadari() throws ClientErrorException {
      WebTarget resource = webTarget;

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().delete();
      if (restOdgovor.getStatus() == 200) {
        return true;
      }

      return false;
    }

    public boolean deleteRadar(String id) throws ClientErrorException {
      WebTarget resource = webTarget;

      resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {id}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().delete();
      if (restOdgovor.getStatus() == 200) {
        return true;
      }

      return false;
    }
  }
}
