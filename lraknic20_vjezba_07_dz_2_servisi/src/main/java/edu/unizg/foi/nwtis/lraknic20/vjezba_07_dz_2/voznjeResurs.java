package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Vozilo;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.VoznjeDAO;
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

@Path("nwtis/v1/api/simulacije")
public class voznjeResurs extends SviResursi {
  private VoznjeDAO voznjeDAO = null;
  private String adresaVozila;
  private String mreznaVrataVozila;

  @PostConstruct
  private void pripremiVoznjeDAO() {
    System.out.println("Pokrećem REST: " + this.getClass().getName());

    try {
      preuzmiPostavke("NWTiS_REST_S.txt");
      var vezaBP = this.vezaBazaPodataka.getVezaBazaPodataka();
      this.voznjeDAO = new VoznjeDAO(vezaBP);
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
        .entity(voznjeDAO.dohvatiVoznje(odVremena, doVremena).toArray()).build();
  }

  @Path("/vozilo/{id}")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getJsonPracenoVozilo(@HeaderParam("Accept") String tipOdgovora,
      @PathParam("id") int id, @QueryParam("od") long odVremena, @QueryParam("do") long doVremena) {
    if (odVremena <= 0 || doVremena <= 0) {
      return Response.status(Response.Status.OK)
          .entity(voznjeDAO.dohvatiVoznjeOdVozila(id).toArray()).build();
    } else {
      return Response.status(Response.Status.OK)
          .entity(voznjeDAO.dohvatiVoznjeOdVozilaOdDo(id, odVremena, doVremena).toArray()).build();
    }
  }

  @POST
  @Produces({MediaType.APPLICATION_JSON})
  public Response postJsonDodajPracenuVoznju(@HeaderParam("Accept") String tipOdgovora,
      Vozilo novoVozilo) {
    var odgovor = voznjeDAO.dodajVozilo(novoVozilo);
    if (odgovor) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Neuspješni upis vožnje u bazu podataka.").build();
    }
  }

  private void preuzmiPostavke(String nazivDatoteke) throws Exception {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);

    this.adresaVozila = konfig.dajPostavku("adresaVozila");
    this.mreznaVrataVozila = konfig.dajPostavku("mreznaVrataVozila");
  }
}
