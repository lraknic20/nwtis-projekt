/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.kontroler;

import java.util.List;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.model.RestKlijentSimulacije;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Vozilo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.mvc.binding.BindingResult;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

/**
 *
 * @author NWTiS
 */
@Controller
@Path("simulacije")
@RequestScoped
public class KontrolerSimulacije {

  @Inject
  private Models model;

  @Inject
  private BindingResult bindingResult;

  @GET
  @View("simulacije.jsp")
  public void pocetak() {}

  @POST
  @Path("pretrazivanjeVoznji")
  @View("prikazVoznji.jsp")
  public void json_pv(@FormParam("odVremena") long odVremena,
      @FormParam("doVremena") long doVremena) {
    RestKlijentSimulacije v = new RestKlijentSimulacije();
    List<Vozilo> vozila = v.getVoznjeJSON_od_do(odVremena, doVremena);
    model.put("vozila", vozila);
  }

  @POST
  @Path("pretrazivanjeVoznjiPoId")
  @View("prikazVoznji.jsp")
  public void json_id(@FormParam("id") String id) {
    RestKlijentSimulacije v = new RestKlijentSimulacije();
    List<Vozilo> vozila = v.getJSON_voznje(id);
    model.put("vozila", vozila);
  }

  @POST
  @Path("pretrazivanjeVoznjiPoIdOdDo")
  @View("prikazVoznji.jsp")
  public void json_id_od_do(@FormParam("id") String id, @FormParam("odVremena") long odVremena,
      @FormParam("doVremena") long doVremena) {
    RestKlijentSimulacije v = new RestKlijentSimulacije();
    List<Vozilo> vozila = v.getJSON_voznje_od_do(id, odVremena, doVremena);
    model.put("vozila", vozila);
  }

  @POST
  @Path("voziloSpremi")
  public String voziloSpremi(@FormParam("id") int id, @FormParam("broj") int broj,
      @FormParam("vrijeme") long vrijeme, @FormParam("brzina") double brzina,
      @FormParam("snaga") double snaga, @FormParam("struja") double struja,
      @FormParam("visina") double visina, @FormParam("gpsBrzina") double gpsBrzina,
      @FormParam("tempVozila") int tempVozila, @FormParam("postotakBaterija") int postotakBaterija,
      @FormParam("naponBaterija") double naponBaterija,
      @FormParam("kapacitetBaterija") int kapacitetBaterija,
      @FormParam("tempBaterija") int tempBaterija, @FormParam("preostaloKm") double preostaloKm,
      @FormParam("ukupnoKm") double ukupnoKm, @FormParam("gpsSirina") double gpsSirina,
      @FormParam("gpsDuzina") double gpsDuzina) {
    RestKlijentSimulacije v = new RestKlijentSimulacije();
    Vozilo vozilo = new Vozilo();
    vozilo.setId(id);
    vozilo.setBroj(broj);
    vozilo.setVrijeme(vrijeme);
    vozilo.setBrzina(brzina);
    vozilo.setSnaga(snaga);
    vozilo.setStruja(struja);
    vozilo.setVisina(visina);
    vozilo.setGpsBrzina(gpsBrzina);
    vozilo.setTempVozila(tempVozila);
    vozilo.setPostotakBaterija(postotakBaterija);
    vozilo.setNaponBaterija(naponBaterija);
    vozilo.setKapacitetBaterija(kapacitetBaterija);
    vozilo.setTempBaterija(tempBaterija);
    vozilo.setPreostaloKm(preostaloKm);
    vozilo.setUkupnoKm(ukupnoKm);
    vozilo.setGpsSirina(gpsSirina);
    vozilo.setGpsDuzina(gpsDuzina);
    boolean uspjeh = v.postVoziloJSON(vozilo);
    String poruka = uspjeh ? "Vozilo uspješno dodano" : "Greška pri spremanju vozila";
    model.put("porukaSpremi", poruka);
    return "praceneVoznje.jsp";
  }
}
