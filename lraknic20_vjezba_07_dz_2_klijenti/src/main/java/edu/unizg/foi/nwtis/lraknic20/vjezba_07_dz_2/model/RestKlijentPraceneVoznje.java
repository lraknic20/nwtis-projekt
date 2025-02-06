package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Vozilo;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RestKlijentPraceneVoznje {

  public RestKlijentPraceneVoznje() {}

  public List<Vozilo> getPraceneVoznjeJSON_od_do(long odVremena, long doVremena) {
    RestPracenaVozila rpv = new RestPracenaVozila();
    List<Vozilo> vozila = rpv.getJSON_od_do(odVremena, doVremena);

    return vozila;
  }

  public List<Vozilo> getJSON_voznje(String id) {
    RestPracenaVozila rpv = new RestPracenaVozila();
    List<Vozilo> vozilo = rpv.getJSON_voznje(id);
    return vozilo;
  }

  public List<Vozilo> getJSON_voznje_od_do(String id, long odVremena, long doVremena) {
    RestPracenaVozila rpv = new RestPracenaVozila();
    List<Vozilo> vozilo = rpv.getJSON_voznje_od_do(id, odVremena, doVremena);

    return vozilo;
  }

  public boolean getVoziloStart(String id) {
    RestPracenaVozila rpv = new RestPracenaVozila();
    return rpv.getVoziloStart(id);
  }

  public boolean getVoziloStop(String id) {
    RestPracenaVozila rpv = new RestPracenaVozila();
    return rpv.getVoziloStop(id);
  }

  public boolean postVoziloJSON(Vozilo vozilo) {
    RestPracenaVozila rpv = new RestPracenaVozila();
    var odgovor = rpv.postJSON(vozilo);
    return odgovor;
  }

  static class RestPracenaVozila {
    /** web target. */
    private final WebTarget webTarget;

    /** client. */
    private final Client client;

    /** knstanta BASE_URI. */
    private static final String BASE_URI = "http://localhost:9080/";

    /**
     * Konstruktor klase.
     */
    public RestPracenaVozila() {
      client = ClientBuilder.newClient();
      webTarget = client.target(BASE_URI).path("nwtis/v1/api/vozila");
    }

    public List<Vozilo> getJSON_od_do(long odVremena, long doVremena) throws ClientErrorException {
      WebTarget resource = webTarget;
      List<Vozilo> vozila = new ArrayList<Vozilo>();

      resource = resource.queryParam("od", odVremena);
      resource = resource.queryParam("do", doVremena);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        String odgovor = restOdgovor.readEntity(String.class);
        var jb = JsonbBuilder.create();
        var pvozila = jb.fromJson(odgovor, Vozilo[].class);
        vozila.addAll(Arrays.asList(pvozila));
      }

      return vozila;
    }

    public List<Vozilo> getJSON_voznje(String id) throws ClientErrorException {
      WebTarget resource = webTarget;
      List<Vozilo> vozilo = new ArrayList<Vozilo>();

      resource = resource.path(java.text.MessageFormat.format("vozilo/{0}", new Object[] {id}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        String odgovor = restOdgovor.readEntity(String.class);
        var jb = JsonbBuilder.create();
        var pvozilo = jb.fromJson(odgovor, Vozilo[].class);
        vozilo.addAll(Arrays.asList(pvozilo));
      }

      return vozilo;
    }

    public List<Vozilo> getJSON_voznje_od_do(String id, long odVremena, long doVremena)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      List<Vozilo> vozilo = new ArrayList<Vozilo>();

      resource = resource.path(java.text.MessageFormat.format("vozilo/{0}", new Object[] {id}));
      resource = resource.queryParam("od", odVremena);
      resource = resource.queryParam("do", doVremena);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        String odgovor = restOdgovor.readEntity(String.class);
        var jb = JsonbBuilder.create();
        var pvozilo = jb.fromJson(odgovor, Vozilo[].class);
        vozilo.addAll(Arrays.asList(pvozilo));
      }

      return vozilo;
    }

    public boolean getVoziloStart(String id) throws ClientErrorException {
      WebTarget resource = webTarget;

      resource =
          resource.path(java.text.MessageFormat.format("vozilo/{0}/start", new Object[] {id}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        return true;
      } else
        return false;
    }

    public boolean getVoziloStop(String id) throws ClientErrorException {
      WebTarget resource = webTarget;

      resource =
          resource.path(java.text.MessageFormat.format("vozilo/{0}/stop", new Object[] {id}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      Response restOdgovor = resource.request().get();
      if (restOdgovor.getStatus() == 200) {
        return true;
      } else
        return false;
    }

    public boolean postJSON(Vozilo vozilo) throws ClientErrorException {
      WebTarget resource = webTarget;
      if (vozilo == null) {
        return false;
      }
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      Response restOdgovor =
          resource.request().post(Entity.entity(vozilo, MediaType.APPLICATION_JSON));
      if (restOdgovor.getStatus() == 200) {
        return true;
      }

      return false;
    }

  }
}
