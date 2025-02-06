package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Radar;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("nwtis/v1/api/radari")
public class RadariResurs extends SviResursi {

  private String adresaRegistracije;
  private String mreznaVrataRegistracije;

  private Pattern predlozakSviRadari = Pattern.compile("\\[(.*?)\\]");
  private Pattern predlozakRadar = Pattern.compile(
      "^(?<id>\\d+) (?<adresa>.*) (?<mreznaVrata>\\d+) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+) (?<maksUdaljenost>\\d+)$");

  @PostConstruct
  private void pripremi() {
    System.out.println("Pokrećem REST: " + this.getClass().getName());

    try {
      preuzmiPostavke("NWTiS_REST_R.txt");
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJsonRadari(@HeaderParam("Accept") String tipOdgovora) {
    String poruka = posaljiPosluzitelju("RADAR SVI");
    if (poruka != null) {
      List<Radar> radari = obradiRadare(poruka);
      return Response.status(Response.Status.OK).entity(radari).build();
    } else
      return Response.status(Response.Status.NOT_FOUND).build();
  }

  @Path("/reset")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getRadariReset(@HeaderParam("Accept") String tipOdgovora) {
    String poruka = posaljiPosluzitelju("RADAR RESET");
    if (poruka != null) {
      Odgovor odgovor = new Odgovor(poruka);
      return Response.status(Response.Status.OK).entity(odgovor).build();
    } else
      return Response.status(Response.Status.NOT_FOUND).build();
  }

  @Path("{id}")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getRadar(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id) {
    String poruka = posaljiPosluzitelju("RADAR SVI");
    if (poruka != null) {
      List<Radar> radari = obradiRadare(poruka);
      for (Radar radar : radari) {
        if (radar.getId() == id)
          return Response.status(Response.Status.OK).entity(radar).build();
      }
      Odgovor odgovor = new Odgovor("ERROR 33 nema ovog radara u kolekciji");
      return Response.status(Response.Status.NOT_FOUND).entity(odgovor).build();
    } else
      return Response.status(Response.Status.NOT_FOUND).build();
  }

  @Path("{id}/provjeri")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getRadarProvjeri(@HeaderParam("Accept") String tipOdgovora,
      @PathParam("id") int id) {
    String poruka = posaljiPosluzitelju("RADAR " + id);
    if (poruka.contains("OK")) {
      Odgovor odgovor = new Odgovor(poruka);
      return Response.status(Response.Status.OK).entity(odgovor).build();
    } else {
      Odgovor odgovor = new Odgovor("Radar s id: " + id + " nije aktivan");
      return Response.status(Response.Status.NOT_FOUND).entity(odgovor).build();
    }

  }

  @DELETE
  @Produces({MediaType.APPLICATION_JSON})
  public Response deleteRadari(@HeaderParam("Accept") String tipOdgovora) {
    String poruka = posaljiPosluzitelju("RADAR OBRIŠI SVE");
    if (poruka.contains("OK")) {
      Odgovor odgovor = new Odgovor(poruka);
      return Response.status(Response.Status.OK).entity(odgovor).build();
    } else {
      Odgovor odgovor = new Odgovor("Nema radara za brisanje");
      return Response.status(Response.Status.NOT_FOUND).entity(odgovor).build();
    }
  }

  @Path("{id}")
  @DELETE
  @Produces({MediaType.APPLICATION_JSON})
  public Response deleteRadar(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id) {
    String poruka = posaljiPosluzitelju("RADAR OBRIŠI " + id);
    if (poruka.contains("OK")) {
      Odgovor odgovor = new Odgovor(poruka);
      return Response.status(Response.Status.OK).entity(odgovor).build();
    } else {
      Odgovor odgovor = new Odgovor("ERROR 33 nema ovog radara u kolekciji");
      return Response.status(Response.Status.NOT_FOUND).entity(odgovor).build();
    }
  }

  private List<Radar> obradiRadare(String poruka) {
    List<Radar> radari = new ArrayList<>();
    Matcher poklapanjeZagrada = predlozakSviRadari.matcher(poruka);

    while (poklapanjeZagrada.find()) {
      String radarInfo = poklapanjeZagrada.group(1);
      Matcher poklapanjeRadar = predlozakRadar.matcher(radarInfo);

      if (poklapanjeRadar.matches()) {
        int id = Integer.parseInt(poklapanjeRadar.group("id"));
        String adresa = poklapanjeRadar.group("adresa");
        int mreznaVrata = Integer.parseInt(poklapanjeRadar.group("mreznaVrata"));
        double gpsSirina = Double.parseDouble(poklapanjeRadar.group("gpsSirina"));
        double gpsDuzina = Double.parseDouble(poklapanjeRadar.group("gpsDuzina"));
        int maksUdaljenost = Integer.parseInt(poklapanjeRadar.group("maksUdaljenost"));

        Radar radar = new Radar(id, adresa, mreznaVrata, maksUdaljenost, gpsSirina, gpsDuzina);
        radari.add(radar);
      }
    }

    return radari;
  }

  private String posaljiPosluzitelju(String poruka) {
    var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.adresaRegistracije,
        Integer.parseInt(this.mreznaVrataRegistracije), poruka.toString());

    if (odgovor != null) {
      return odgovor;
    } else {
      return null;
    }
  }

  private void preuzmiPostavke(String nazivDatoteke) throws Exception {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);

    this.adresaRegistracije = konfig.dajPostavku("adresaRegistracije");
    this.mreznaVrataRegistracije = konfig.dajPostavku("mreznaVrataRegistracije");
  }

  public class Odgovor {
    private String odgovor;

    public Odgovor(String odgovor) {
      this.odgovor = odgovor;
    }

    public String getOdgovor() {
      return odgovor;
    }

    public void setOdgovor(String odgovor) {
      this.odgovor = odgovor;
    }
  }
}
