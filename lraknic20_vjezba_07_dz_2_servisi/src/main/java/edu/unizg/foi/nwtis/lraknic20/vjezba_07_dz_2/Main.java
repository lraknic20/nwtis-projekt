package edu.unizg.foi.nwtis.lraknic20.vjezba_07_dz_2;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.core.Application;

/**
 * Main class.
 *
 */
@ApplicationPath("")
public class Main extends Application {

  public static void main(String[] args) throws Exception {
    Application app = new Main();
    SeBootstrap.Configuration config =
        SeBootstrap.Configuration.builder().rootPath("").port(8080).host("0.0.0.0").build();

    SeBootstrap.start(app, config).thenAccept(instance -> {
      instance.stopOnShutdown(stopResult -> stopResult.unwrap(Object.class));
      System.out.printf("\nREST servis na adresi: %s\n", instance.configuration().baseUri());
    });

    Thread.currentThread().join();
  }
}

