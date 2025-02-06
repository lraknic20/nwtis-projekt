/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.kontroler;

import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.model.RestKlijentKazne;
import edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.podaci.Kazna;
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
@Path("kazne")
@RequestScoped
public class KontrolerKazne {

  @Inject
  private Models model;

  @Inject
  private BindingResult bindingResult;

  @GET
  @View("kazne.jsp")
  public void pocetak() {}

  @GET
  @Path("ispisKazni")
  @View("prikazKazni.jsp")
  public void json() {
    RestKlijentKazne k = new RestKlijentKazne();
    List<Kazna> kazne = k.getKazneJSON();
    model.put("kazne", kazne);
  }

  @POST
  @Path("pretrazivanjeKazni")
  @View("prikazKazni.jsp")
  public void json_pi(@FormParam("odVremena") long odVremena,
      @FormParam("doVremena") long doVremena) {
    RestKlijentKazne k = new RestKlijentKazne();
    List<Kazna> kazne = k.getKazneJSON_od_do(odVremena, doVremena);
    model.put("kazne", kazne);
  }

  @POST
  @Path("pretrazivanjeKazniPoRb")
  @View("prikazKazni.jsp")
  public void json_rb(@FormParam("rb") String rb) {
    RestKlijentKazne k = new RestKlijentKazne();
    Kazna kazna = k.getKaznaJSON_rb(rb);
    List<Kazna> kazne = new ArrayList<Kazna>();
    if (kazna != null)
      kazne.add(kazna);
    model.put("kazne", kazne);
  }

  @POST
  @Path("pretrazivanjeKazniPoId")
  @View("prikazKazni.jsp")
  public void json_id(@FormParam("id") String id) {
    RestKlijentKazne k = new RestKlijentKazne();
    List<Kazna> kazne = k.getKazneJSON_vozilo(id);
    model.put("kazne", kazne);
  }

  @POST
  @Path("pretrazivanjeKazniPoIdOdDo")
  @View("prikazKazni.jsp")
  public void json_id_od_do(@FormParam("id") String id, @FormParam("odVremena") long odVremena,
      @FormParam("doVremena") long doVremena) {
    RestKlijentKazne k = new RestKlijentKazne();
    List<Kazna> kazne = k.getKazneJSON_vozilo_od_do(id, odVremena, doVremena);
    model.put("kazne", kazne);
  }

  @POST
  @Path("provjeriPosluzitelja")
  public String provjeriPosluzitelja() {
    RestKlijentKazne k = new RestKlijentKazne();
    boolean uspjeh = k.headProvjeriPosluzitelj();
    String poruka = uspjeh ? "Poslužitelj kazni je aktivan" : "Poslužitelj kazni nije aktivan";
    model.put("porukaPosluzitelj", poruka);
    return "kazne.jsp";
  }

}
