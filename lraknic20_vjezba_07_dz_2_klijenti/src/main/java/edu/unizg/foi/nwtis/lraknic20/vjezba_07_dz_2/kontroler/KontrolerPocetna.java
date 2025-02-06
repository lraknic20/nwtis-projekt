/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2.kontroler;

import jakarta.enterprise.context.RequestScoped;
import jakarta.mvc.Controller;
import jakarta.mvc.View;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 *
 * @author NWTiS
 */
@Controller
@Path("pocetna")
@RequestScoped
public class KontrolerPocetna {

  @GET
  @View("index.jsp")
  public void pocetak() {}

}
