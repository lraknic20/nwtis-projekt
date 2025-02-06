/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.kontroler;

import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.model.RestKlijentRadari;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Radar;
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
@Path("radari")
@RequestScoped
public class KontrolerRadari {

  @Inject
  private Models model;

  @Inject
  private BindingResult bindingResult;

  @GET
  @View("radari.jsp")
  public void pocetak() {}

  @POST
  @Path("sviRadari")
  @View("prikazRadara.jsp")
  public void json_radari() {
    RestKlijentRadari r = new RestKlijentRadari();
    List<Radar> radari = r.getRadari();
    model.put("radari", radari);
  }

  @GET
  @Path("resetRadara")
  public String json_reset_radara() {
    RestKlijentRadari r = new RestKlijentRadari();
    boolean uspjeh = r.getReset();
    String poruka = uspjeh ? "Radari su uspješno resetirani" : "Radari nisu resetirani";
    model.put("porukaReset", poruka);
    return "radari.jsp";
  }

  @POST
  @Path("pretrazivanjeRadaraPoId")
  @View("prikazRadara.jsp")
  public void json_id(@FormParam("id") String id) {
    RestKlijentRadari r = new RestKlijentRadari();
    Radar radar = r.getRadar(id);
    List<Radar> radari = new ArrayList<Radar>();
    if (radar != null)
      radari.add(radar);
    model.put("radari", radari);
  }

  @POST
  @Path("provjeriRadaraPoId")
  public String json_id_provjeri(@FormParam("id") String id) {
    RestKlijentRadari r = new RestKlijentRadari();
    boolean uspjeh = r.getProvjeriRadar(id);
    String poruka =
        uspjeh ? "Radar s id: " + id + " je aktivan" : "Radar s id: " + id + " nije aktivan";
    model.put("porukaRadarAktivan", poruka);
    return "radari.jsp";
  }

  @POST
  @Path("obrisiSveRadare")
  public String json_obrisi_sve_radare() {
    RestKlijentRadari r = new RestKlijentRadari();
    boolean uspjeh = r.deleteRadari();
    String poruka = uspjeh ? "Radari su uspješno obrisani" : "Radari nisu obrisani";
    model.put("porukaObrisiRadare", poruka);
    return "radari.jsp";
  }

  @POST
  @Path("obrisiRadaraPoId")
  public String json_id_obrisi(@FormParam("id") String id) {
    RestKlijentRadari r = new RestKlijentRadari();
    boolean uspjeh = r.deleteRadar(id);
    String poruka = uspjeh ? "Radar s id:" + id + " je uspješno obrisan" : "Radar nije obrisan";
    model.put("porukaObrisiRadar", poruka);
    return "radari.jsp";
  }
}
