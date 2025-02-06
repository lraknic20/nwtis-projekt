package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.PraceneVoznjeDAO;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Vozilo;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("nwtis/v1/api/vozila")
public class PraceneVoznjeResurs extends SviResursi {
  private PraceneVoznjeDAO praceneVoznjeDAO = null;
  private String adresaVozila;
  private String mreznaVrataVozila;

  @PostConstruct
  private void pripremiPraceneVoznjeDAO() {
    System.out.println("Pokrećem REST: " + this.getClass().getName());

    try {
      preuzmiPostavke("NWTiS_REST_V.txt");
      var vezaBP = this.vezaBazaPodataka.getVezaBazaPodataka();
      this.praceneVoznjeDAO = new PraceneVoznjeDAO(vezaBP);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJsonPracenaVozila(@HeaderParam("Accept") String tipOdgovora,
      @QueryParam("od") long odVremena, @QueryParam("do") long doVremena) {
    return Response.status(Response.Status.OK)
        .entity(praceneVoznjeDAO.dohvatiPraceneVoznje(odVremena, doVremena).toArray()).build();
  }

  @Path("/vozilo/{id}")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJsonPracenoVozilo(@HeaderParam("Accept") String tipOdgovora,
      @PathParam("id") int id, @QueryParam("od") long odVremena, @QueryParam("do") long doVremena) {
    if (odVremena <= 0 || doVremena <= 0) {
      return Response.status(Response.Status.OK)
          .entity(praceneVoznjeDAO.dohvatiPracenoVozilo(id).toArray()).build();
    } else {
      return Response.status(Response.Status.OK)
          .entity(praceneVoznjeDAO.dohvatiPracenoVoziloOdDo(id, odVremena, doVremena).toArray())
          .build();
    }
  }

  @Path("/vozilo/{id}/start")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJsonPokreniPracenje(@HeaderParam("Accept") String tipOdgovora,
      @PathParam("id") int id) {
    if (pracenjeVozila("START", id)) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Neuspješno praćenje vozila - PoslužiteljZaVozila nije pokrenut.").build();
    }
  }

  @Path("/vozilo/{id}/stop")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJsonPrekiniPracenje(@HeaderParam("Accept") String tipOdgovora,
      @PathParam("id") int id) {
    if (pracenjeVozila("STOP", id)) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Neuspješno praćenje vozila - PoslužiteljZaVozila nije pokrenut.").build();
    }
  }

  private boolean pracenjeVozila(String stanje, int id) {
    var poruka = new StringBuilder();
    poruka.append("VOZILO ").append(stanje + " ").append(id).append("\n");

    var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.adresaVozila,
        Integer.parseInt(this.mreznaVrataVozila), poruka.toString());

    if (odgovor != null) {
      return true;
    } else {
      return false;
    }
  }

  @POST
  @Produces({MediaType.APPLICATION_JSON})
  public Response postJsonDodajPracenuVoznju(@HeaderParam("Accept") String tipOdgovora,
      Vozilo novoVozilo) {
    var odgovor = praceneVoznjeDAO.dodajVozilo(novoVozilo);
    if (odgovor) {
      Odgovor odgovorPoruka = new Odgovor("Uspješni upis praćene vožnje u bazu podataka");
      return Response.status(Response.Status.OK).entity(odgovorPoruka).build();
    } else {
      Odgovor odgovorPoruka = new Odgovor("Neuspješni upis praćene vožnje u bazu podataka");
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(odgovorPoruka).build();
    }
  }

  private void preuzmiPostavke(String nazivDatoteke) throws Exception {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);

    this.adresaVozila = konfig.dajPostavku("adresaVozila");
    this.mreznaVrataVozila = konfig.dajPostavku("mreznaVrataVozila");
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
